/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/** 
 * An abstraction of a TCP/IP socket for sending and receiving game messages.
 * It allows two players to communicate with each other to play the game.
 * It is assumed that a connection is already established between the peers
 * with the socket.
 * 
 * This class supports several types of messages. 
 * Each message is a single line of text---a sequence of characters
 * ended by the end-of-line character---and consists of a header and a body.
 * A message header identifies a message type and ends with a ":", e.g.,
 * "move:". A message body contains the content of a message, and
 * if it contains more than one element, they are separated by a ",",
 * e.g., "4,5". There are five different messages as listed below.
 *
 * <ul>
 *     <li>play: - request a new play</li>
 *     <li>play_ack: m, n - acknowledge a play request, where m (response)
 *         and n (turn) are either 0 or 1.
 *         If m is 1, the request is accepted; otherwise, it is rejected.
 *         If n is 1, the client goes first; otherwise, the server goes
 *         first.</li>
 *     <li>move: i - place a checker at slot i (0-based index).</li>
 *     <li>move_ack: i - acknowledge a move message.</li>
 *     <li>quit: - quit a play.</li>
 * </ul>
 *
 * The communication protocol between peers is very simple.
 * The client makes a request to the server for a new play.
 * If the request is accepted by the server, the server determines
 * the turn and the play proceeds by sending and receiving a series
 * of move and move_ack messages until either the game becomes over 
 * or one of the players quits.
 *
 * <pre>
 * 1. Client has the first turn.
 *  Client        Server
 *    |------------>| play: - request for a new play
 *    |<------------| play_ack:1,1 - ack the play request
 *    |------------>| move:3 - client move
 *    |<------------| move_ack:3 - server ack
 *    |<------------| move:2 - server move
 *    |------------>| move_ack:2 - client ack
 *    ...
 * </pre>
 *
 *  <pre>
 *  2. Server has the first turn.
 *  Client        Server
 *    |------------>| play: - request for a new play
 *    |<------------| play_ack:1,0 - ack the play request
 *    |<------------| move:3 - server move
 *    |------------>| move_ack:3 - client ack
 *    |------------>| move:2 - client move
 *    |<------------| move_ack:2 - server ack
 *    ...
 * </pre>
 * <pre>
 *  3. A play request is declined.
 *  Client        Server
 *    |------------>| play: - request for a new play
 *    |<------------| play_ack:0,0 - reject the play request
 * </pre> 
 * 
 * <p>
 * To receive messages from the peer, register a MessageListener 
 * and then call the receiveMessages() method as shown below. 
 * Note that the receiveMessages() method runs on the caller's
 * thread and thus blocks the caller; create a new thread to receive 
 * messages asynchronously.
 * 
 * <pre>
 *  Socket socket = ...;
 *  NetworkAdapter network = new NetworkAdapter(socket);
 *  network.setMessageListener(new NetworkAdapter.MessageListener() {
 *      public void messageReceived(MessageType type, int x, int y) {
 *        switch (type) {
 *          case PLAY: ...
 *          case PLAY_ACK: ...
 *          ...
 *        }
 *      }
 *    });
 *    
 *  // receive messages asynchronously  
 *  new Thread(() -> network.receiveMessages()).start();
 * </pre>
 * 
 * To send messages to the peer, call the writeXXX messages. These
 * methods run asynchronously.
 * 
 * <pre>
 *  network.writePlay();
 *  network.writeMove(1);
 *  ...
 *  network.close();
 * </pre>
 * 
 * @author cheon
 */
public class NetworkAdapter {

    /** Different type of game messages. */
    public enum MessageType { 
        
        /** Quit the game. This message has the form, "quit:". */
        QUIT ("quit:"), 
        
        /** Request a new play. This message has the form, "play:". */
        PLAY ("play:"), 
        
        /** 
         * Acknowledgement of a play request. This message has the form, 
         * "play_ack: m, n", where m (response) and n (turn) are either 0 
         * or 1. If m is 1, the request is accepted; otherwise, it is 
         * declined. If n is 1, the client plays first; otherwise, 
         * the server plays first.
         */
        PLAY_ACK ("play_ack:"), 
        
        /** 
         * Player's move. This message has the form, "move: i",
         * where i is a 0-based slot (column) index. It means dropping a
         * checker at slot i. 
         */
        MOVE ("move:"), 
        
        /** 
         * Acknowledgement of a player's move. 
         * This message has the form, "move_ack: i", where i is a 
         * 0-based slot (column) index. 
         */
        MOVE_ACK ("move_ack:"), 
        
        /** Connection closed. To be notified when the socket is closed. */
        CLOSE (null), 
        
        /** Unknown message received. */
        UNKNOWN (null);
        
        /** Message header. */
        private final String header;
        
        MessageType(String header) {
            this.header = header;
        }

    };

    /** Listener to be called when a message is received. */
    public interface MessageListener {

        /** 
         * To be called when a message is received. 
         * The type of the received message along with optional contents,
         * say x and y, are provided as arguments.
         */
        void messageReceived(MessageType type, int x, int y);
    }

    /** Listener to be called when a message is received. */
    private MessageListener listener;
    
    /** Asynchronous message writer. */
    private MessageWriter messageWriter;
    
    /** Reader connected to the peer to read messages from it. */
    private BufferedReader in;
    
    /** Writer connected to the peer to write messages to it. */
    private PrintWriter out;
    
    /** If not null, log all messages sent and received. */
    private PrintStream logger;

    /** 
     * Create a new network adapter to read messages from and to write
     * messages to the given socket. 
     */
    public NetworkAdapter(Socket socket) {
        this(socket, null);
    }
    
    /** 
     * Create a new network adapter. Messages are to be read from and 
     * written to the given socket. All incoming and outgoing 
     * messages will be logged on the given logger.
     */
    public NetworkAdapter(Socket socket, PrintStream logger) {
        this.logger = logger;
        messageWriter = new MessageWriter();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
        }
    }
    
    /** Close the IO streams of this adapter; the socket is not closed.*/
    public void close() {
        try {
            // close "out" first to break the circular dependency
            // between peers.
            out.close();  
            in.close();
            messageWriter.stop();
        } catch (Exception e) {
        }
    }

    /** 
     * Read messages from this network adapter and notify them to the
     * registered listener. This method blocks the calling method.
     * To receive messages asynchronously, call this method on a new
     * (background) thread.
     */
    public void receiveMessages() {
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                if (logger != null) {
                    logger.format(" < %s\n", line);
                }
                parseMessage(line);
            }
        } catch (IOException e) {
        }
        notifyMessage(MessageType.CLOSE);
    }

    /** Parse the given message and notify to the registered listener. */
    private void parseMessage(String msg) {
        if (msg.startsWith(MessageType.QUIT.header)) {
                notifyMessage(MessageType.QUIT);
        } else if (msg.startsWith(MessageType.PLAY_ACK.header)) {
            parsePlayAckMessage(msgBody(msg));
        } else if (msg.startsWith(MessageType.PLAY.header)) {
            notifyMessage(MessageType.PLAY);
        } else if (msg.startsWith(MessageType.MOVE_ACK.header)) {
            parseMoveMessage(MessageType.MOVE_ACK, msgBody(msg));
        } else if (msg.startsWith(MessageType.MOVE.header)){
            parseMoveMessage(MessageType.MOVE, msgBody(msg));
        } else {
            notifyMessage(MessageType.UNKNOWN);
        }
    }

    /** Parse and return the body of the given message. */
    private String msgBody(String msg) {
        int i = msg.indexOf(':');
        if (i > -1) {
            msg = msg.substring(i + 1);
        }
        return msg;
    }

    /** Parse and notify the given play_ack message body. */
    private void parsePlayAckMessage(String msgBody) {
        String[] m = msgBody.split(",");
        int response = parseInt(m[0].trim()) == 0 ? 0 : 1;
        int turn = 0;
        if (response == 1) {
            turn = parseInt(m[1].trim()) == 0 ? 0 : 1;
        }
        notifyMessage(MessageType.PLAY_ACK, response, turn);
    }

    /** 
     * Parse the given string as an int; return 0 if the input
     * is not well-formed. 
     */
    private int parseInt(String txt) {
        try {
            return Integer.parseInt(txt);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /** Parse and notify the given move or move_ack message. */
    private void parseMoveMessage(MessageType type, String msgBody) {
        try {
            int x = Integer.parseInt(msgBody.trim());
            notifyMessage(type, x);
            return;
        } catch (NumberFormatException e) {
        }
        notifyMessage(MessageType.UNKNOWN);
    }

    /** Write the given message asynchronously. */
    private void writeMsg(String msg) {
        messageWriter.write(msg);
    }
    
    /** Write a quit (gg) message (to quit the game) asynchronously. */
    public void writeQuit() {
        writeMsg(MessageType.QUIT.header);
    }

    /** Write a play message asynchronously. */
    public void writePlay() {
        writeMsg(MessageType.PLAY.header);
    }

    /** 
     * Write a play_ack message asynchronously. If turn is true,
     * the opponent plays first. 
     */
    public void writePlayAck(boolean response, boolean turn) {
        writeMsg(MessageType.PLAY_ACK.header
                 + toInt(response) + "," + toInt(turn));
    }
    
    /** Convert the given boolean flag to an int. */
    private int toInt(boolean flag) {
        return flag ? 1: 0;
    }

    /** Write a move message asynchronously. */
    public void writeMove(int x) {
        writeMsg(MessageType.MOVE.header + x);
    }

    /** Write a move_ack message asynchronously. */
    public void writeMoveAck(int slot) {
        writeMsg(MessageType.MOVE_ACK.header + slot);
    }

    /** 
     * Register the given messageListener to be notified when a message
     * is received.
     */
    public void setMessageListener(MessageListener listener) {
        this.listener = listener;
    }

    /** Notify the listener the receipt of the given message type. */
    private void notifyMessage(MessageType type) {
        listener.messageReceived(type, 0, 0);
    }

    /** Notify the listener the receipt of the given message. */
    private void notifyMessage(MessageType type, int x, int y) {
        listener.messageReceived(type, x, y);
    }

    /** Notify the listener the receipt of the given message. */
    private void notifyMessage(MessageType type, int x) {
        listener.messageReceived(type, x, 0);
    }
    
    /** 
     * Write messages asynchronously. This class uses a single 
     * background thread to write messages asynchronously in a FIFO
     * fashion. To stop the background thread, call the stop() method.
     */
    private class MessageWriter {
        
        /** Background thread to write messages asynchronously. */
        private Thread writerThread;
        
        /** Store messages to be written asynchronously. */
        private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

        /** Write the given message asynchronously on a new thread. */
        public void write(final String msg) {
            if (writerThread == null) {
                writerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                String m = messages.take();
                                out.println(m);
                                out.flush();
                            } catch (InterruptedException e) {
                                return;
                            }
                        }
                    }
                });
                writerThread.start();
            }

            synchronized (messages) {
                try {
                    messages.put(msg);
                    if (logger != null) {
                        logger.format(" > %s\n", msg);
                    }
                } catch (InterruptedException e) {
                }
            }
        }
        
        /** Stop this message writer. */
        public void stop() {
            if (writerThread != null) {
                writerThread.interrupt();
            }
        }
    }
}