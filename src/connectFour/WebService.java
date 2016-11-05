/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class WebService extends Thread{

	/**Used to send and receive messages given a socket*/
	private NetworkAdapter connection; 

	/**The control, used to know inserted IP Address and port of opponent and to deal with
	 * commands given by other peer, play, playAck, move, moveAck, quit, and close*/
	private Control control;

	/**The ServerSocket made to become a server, port stays the same*/
	private ServerSocket server;

	/**True if you are connected to someone, false otherwise*/
	private boolean connected = false;

	/**The port number that the server is connect to*/
	private int port = -1;


	/**Constructs Peer2Peer basics*/
	public WebService(Control control){
		this.control = control;
		createServer();
		start();
		setNetworkMessages(); 

	}

	/**Creates the server for the player*/
	private int createServer(){
		try{
			server = createSocket();
			port = server.getLocalPort();
			return port;
		}
		catch(Exception e){
			return -1;
		}
	}

	/**Returns the port number where server was created*/
	public int getPort(){
		return port;
	}

	@Override
	/**Creates a separate thread to handle ServerSocket.accept() which blocks the thread until client connects*/
	public void run(){
		try{
			connection = null;
			while(true){
				Socket socket = server.accept(); 
				connected = true;
				connection = new NetworkAdapter(socket);
				setListener();
			}
		}
		catch(Exception e){}
	}

	/**Connect to a server given the server's IP and port
	 * @param IP the IP can also be a host name, used to connect to server
	 * @param portString is the port in a String, will be converted to integer*/
	public void connectToServer(String IP, String portString){
		try{
			int port = Integer.parseInt(portString);
			Socket socket = new Socket(IP, port); 
			connection = new NetworkAdapter(socket);
			setListener();
			connected = true;
		}
		catch(Exception e){			
			connected = false;
			control.popupMessage("Failed to connect to server", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		}	
	}

	/**Sets it so you receive messages from opponent, even if networkAdapter changes
	 * Should only be called once*/
	private void setNetworkMessages(){ 
		new Thread(){
			@Override
			public void run(){
				while(true){
					if(connection != null){
						connection.receiveMessages();
					}
					try {
						Thread.sleep(35);
					} catch(Exception e) {}
				}
			}
		}.start();
	}

	/**Finds available socket and returns the ServerSocket in the port*/
	private ServerSocket createSocket(){
		for (int port = 8000;; port++) {
			try {
				return new ServerSocket(port);
			} 
			catch (Exception e) {}
		}
	}

	/**Disconnects from opponent if connected to them*/
	public void disconnect(){
		if(connection != null){
			writeQuit();
			if(connection != null){
				connection.close();
			}
			connection = null;
		}
		connected = false;
		setListener();
	}

	/**Sets the new listener, should be called if NetworkAdapter changes*/
	private void setListener(){
		if(connection == null){
			return;
		}
		connection.setMessageListener(new NetworkAdapter.MessageListener() {
			public void messageReceived(NetworkAdapter.MessageType type, int x, int y) {
				switch (type){
				case PLAY: control.p2pPlayReceived(); break;
				case PLAY_ACK: control.p2pPlayAckReceived(x,y);	break;
				case MOVE: control.dropSlot(x+1); connection.writeMoveAck(x);break;
				case MOVE_ACK: break;
				case QUIT: control.p2pQuitCloseReceived(); break;
				case CLOSE: control.p2pQuitCloseReceived(); break; 
				default:
					break;
				}
			}
		});
	}

	/**Returns true if player is connected to opponent*/
	public boolean isConnected(){
		return connected;
	}

	/**Writes play to connected opponent, returns false if not connected*/
	public boolean writePlay(){
		if(connected){
			connection.writePlay();
			return true;
		}
		return false;
	}
	/**Writes quit to connected opponent*/
	public void writeQuit(){
		connection.writeQuit();
	}

	/**Writes playAck to connected opponent*/
	public void writePlayAck(boolean response, boolean turn){
		connection.writePlayAck(response, turn);
	}

	/**Writes move to connected opponent*/
	public void writeMove(int slot){
		connection.writeMove(slot);
	}

	/**Returns IP address as a String*/
	public String getIPAddress(){
		if(server == null){
			return "";
		}
		try{
			return InetAddress.getLocalHost().getHostAddress();
		}
		catch(Exception e){}
		return "";
	}
}
