/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/


package connectFour;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class ConnectPanel extends JFrame{

	/**The Dimension of the connection popup window*/
	private final static Dimension DIMENSION = new Dimension(270, 235);

	/**Contains the player's port number*/
	private JTextField playerPort = new JTextField();
	
	/**Contains the player's IP number*/
	private JTextField playerIP = new JTextField();
	
	/**Contains the opponent's port number, which should have been inserted by player*/
	private JTextField opponentsPortField = new JTextField(5);
	
	/**Contains the opponent's IP address number, which should have been inserted by player*/
	private JTextField opponentsIPField = new JTextField(12);
	
	/**The connect button, used when wanting to connect to opponent in P2P*/
	private final JButton connectButton = new JButton("CONNECT");
	
	/**The disconnect button, used when wanting to disconnect to opponent in P2P*/
	private final JButton disconnectButton = new JButton("DISCONNECT");
	
	/**Used to give control the port and ip that player inserted back to control, so that it can handle
	 * connecting and disconnecting*/
	private Control control;


	public ConnectPanel(Control control){
		setLocationRelativeTo(null);
		this.control = control;
	
		setLayout(new BorderLayout());
		
		setSize(DIMENSION);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		connectionMenu();
		opponentsIPField.setText("localhost");
	}

	/**Returns a JPanel consisting of a connection menu need to connect to opponent*/
	private void connectionMenu(){
		add(userInfo(), BorderLayout.NORTH);
		add(opponentInfoFields(), BorderLayout.CENTER);
		add(connectionButtons(), BorderLayout.SOUTH);
	}
	
	/**Returns JPanel consisting of current player's connection information, including IP Address and port #*/
	private JPanel userInfo(){
		
		JPanel userInfo = new JPanel(new BorderLayout());
		userInfo.setBorder(defaultBorder("Your information"));
		
		JPanel ipTextOutput = new JPanel(new FlowLayout(FlowLayout.LEADING));
		userInfo.add(ipTextOutput, BorderLayout.NORTH);

		JLabel ipText = new JLabel("IP: ");
		ipTextOutput.add(ipText);
		ipTextOutput.add(playerIP);
		playerIP.setEditable(false);
		
		JPanel portTextOutput = new JPanel(new FlowLayout(FlowLayout.LEADING));
		userInfo.add(portTextOutput, BorderLayout.CENTER);
		
		JLabel portText = new JLabel("Port: ");
		portText.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		portTextOutput.add(portText);
		portTextOutput.add(playerPort);
		playerPort.setEditable(false);
		return userInfo;
	}
	
	/**Return JPanel consisting of text fields where you should input your opponent's info to connect to them*/
	private JPanel opponentInfoFields(){
		JPanel fieldMenu = new JPanel(new BorderLayout());
		fieldMenu.setBorder(defaultBorder("Opponents information"));
		
		JPanel ipTextInput = new JPanel(new FlowLayout(FlowLayout.LEADING));
		fieldMenu.add(ipTextInput, BorderLayout.NORTH);
		
		JLabel ipAddress = new JLabel("IP/Host name: ");
		ipTextInput.add(ipAddress);
		ipTextInput.add(opponentsIPField);
		
		
		
		JPanel portTextInput = new JPanel(new FlowLayout(FlowLayout.LEADING));
		fieldMenu.add(portTextInput, BorderLayout.SOUTH);
		
		JLabel port = new JLabel("Port: ");
		portTextInput.add(port);
		portTextInput.add(opponentsPortField);
		return fieldMenu;
	}
	
	/**Returns JPanel that contains all the connection buttons inside of the connect settings*/
	private JPanel connectionButtons(){
		JPanel buttons = new JPanel(new FlowLayout());
		buttons.setBorder(BorderFactory.createEmptyBorder(0,15,0,0));
		buttons.add(connectButton);
		connectButton.addActionListener(control::connectButton);
		
		buttons.setBorder(BorderFactory.createEmptyBorder(0,15,0,0));
		buttons.add(disconnectButton);
		disconnectButton.addActionListener(control::disconnectButton);
		disconnectButton.setEnabled(false);
		return buttons;
	}
	
	/**Disables or enables connect and disconnect button
	 * Connect and disconnect buttons are mutually exclusive, they never have the same state
	 * @param flag if true, enables the connect button and disables disconnect button
	 * If false, disables connect button and enables disconnect button*/
	public void enableConnectButton(boolean flag){
		connectButton.setEnabled(flag);
		disconnectButton.setEnabled(!flag);
	}
	
	/**Sets the current player's port to given String*/
	public void setPlayersPort(String text){
		playerPort.setText(text);
	}
	
	/**Sets the current player's IP address to given String*/
	public void setIPText(String text){
		playerIP.setText(text);
	}
	
	/**Returns the opponent's port as a String*/
	public String getOpponentsPort(){
		return opponentsPortField.getText();
	}
	
	/**Returns the opponent's IP Address as a String*/
	public String getIPText(){
		return opponentsIPField.getText();
	}
	
	/**The default border for JPanels used by JPanels in this class*/
	private Border defaultBorder(String title){
		return new TitledBorder(new BevelBorder(BevelBorder.LOWERED), title);
	}
}
