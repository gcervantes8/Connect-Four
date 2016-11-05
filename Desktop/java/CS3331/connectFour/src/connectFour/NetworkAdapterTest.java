/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NetworkAdapterTest {

    private NetworkAdapter network;
    
    private FakeSoket socket;
    
    private boolean messageReceived = false;
    
    @Before
    public void setUp() throws Exception {
        socket = new FakeSoket();
        network = new NetworkAdapter(socket);
        messageReceived = false;
    }

    @After
    public void tearDown() throws Exception {
        socket.close();
        network.close();
    }

    @Test
    public void testWritePlay() {
        network.writePlay();
        checkMessageDelivery("play:");
    }
    
    @Test
    public void testWritePlayAck1() {
        network.writePlayAck(true, true);
        checkMessageDelivery("play_ack:1,1");
    }
    
    @Test
    public void testWritePlayAck2() {
        network.writePlayAck(true, false);
        checkMessageDelivery("play_ack:1,0");
    }
    
    @Test
    public void testWritePlayAck3() {
        network.writePlayAck(false, false);
        checkMessageDelivery("play_ack:0,0");
    }
    
    @Test
    public void testWriteMove() {
        network.writeMove(1);
        checkMessageDelivery("move:1");
    }

    @Test
    public void testWriteMoveAck() {
        network.writeMoveAck(1);
        checkMessageDelivery("move_ack:1");
    }
    
    @Test
    public void testWriteQuit() {
        network.writeQuit();
        checkMessageDelivery("quit:");
    }
    
    @Test
    public void testReceiveMessages1() {
        checkMessageReceipt("play:", NetworkAdapter.MessageType.PLAY);
    }
    
    @Test
    public void testReceiveMessages2() {
        checkMessageReceipt("play_ack:1,1", 
                NetworkAdapter.MessageType.PLAY_ACK, 1, 1);
        checkMessageReceipt("play_ack:1,0", 
                NetworkAdapter.MessageType.PLAY_ACK, 1, 0);        
        checkMessageReceipt("play_ack:0,0", 
                NetworkAdapter.MessageType.PLAY_ACK, 0, 0);
    }
    
    @Test
    public void testReceiveMessages3() {
        checkMessageReceipt("move:1", NetworkAdapter.MessageType.MOVE, 1);
    }
    
    @Test
    public void testReceiveMessages4() {
        checkMessageReceipt("move_ack:1", 
                NetworkAdapter.MessageType.MOVE_ACK, 1);
    }
    
    @Test
    public void testReceiveMessages5() {
        checkMessageReceipt("quit:", 
                NetworkAdapter.MessageType.QUIT);
    }
    
    @Test
    public void testReceiveMessages6() {
        checkMessageReceipt("what?:", 
                NetworkAdapter.MessageType.UNKNOWN);
    }
    
     
    /** Check message receipt synchronously. */
    private void checkMessageReceipt(String msg, 
            NetworkAdapter.MessageType type) {
        checkMessageReceipt(msg, type, 0, 0);
    }
    
    /** Check message receipt synchronously. */
    private void checkMessageReceipt(String msg, 
            NetworkAdapter.MessageType type, int x) {
        checkMessageReceipt(msg, type, x, 0);
    }
    
    /** Check message receipt synchronously. */
    private void checkMessageReceipt(String msg, 
            NetworkAdapter.MessageType type, int x, int y) {
        network.setMessageListener((receivedType, receivedX, receivedY) -> {
            messageReceived = true;
            if (receivedType != NetworkAdapter.MessageType.CLOSE) {
                assertEquals(type, receivedType);
                assertEquals(x, receivedX);
                assertEquals(y, receivedY);
            }
        });
        socket.simulateMessageReceipt(msg); // simulate message
        network.receiveMessages(); // run on the calling (JUnit) thread
        assertTrue(messageReceived);
    }
    
    /** Check that the given (sent) message is delivered. */
    private void checkMessageDelivery(String msg) {
        sleep(1); // coz of asynchronous sending
        assertEquals(addEOL(msg), socket.writtenMessages());        
    }
    
    private String addEOL(String text) {
        return text + System.lineSeparator();
    }
    
    private void sleep(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
        }        
    }
    
    private static class FakeSoket extends Socket {
        
        private InputStream input;
        private PipedOutputStream pipe;
        
        private OutputStream output;
        
        public FakeSoket() {
            output = new ByteArrayOutputStream();
            input = new PipedInputStream();
            pipe = new PipedOutputStream();
            try {
                ((PipedInputStream) input).connect(pipe);
            } catch (IOException e) {
            }
        }
        
        /** Overridden here to read messages from a pipe. */
        @Override
        public InputStream getInputStream() {
            return input;
        }
        
        /** Overridden here to write messages to a byte array
         * whose content can be read using 
         * the <code>message()</code> method. */
        @Override
        public OutputStream getOutputStream() {
            return output;
        } 
        
        /** Return the messages written to this socket. */
        public String writtenMessages() {    
            return output.toString();
        }
        
        /** Simulate receiving of a given one message by writing
         * the message to the pipe connected to the input stream
         * of this socket. */
        public void simulateMessageReceipt(String msg) {
            try {
                pipe.write((msg + System.lineSeparator()).getBytes());
                pipe.flush();
                pipe.close();
            } catch (IOException e) {
            }
        }

        @Override
        public void close() {
            try {
                super.close();
                input.close();
                output.close();
                pipe.close();
            } catch (IOException e) {
            }
        }
    }
}