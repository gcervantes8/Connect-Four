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
import java.awt.GridLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComboBox;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

@SuppressWarnings("serial")
public class C4Frame extends JFrame{

	/**Dimensions of the frame*/
	private final static Dimension DIMENSION = new Dimension(600, 700);

	/** Button to start a new game. */
	private final JButton playButton = new JButton("Play");

	/**Button that opens a JPanel, where player can insert opponent's connect settings*/
	private final JButton connectButton = new JButton("Connect Settings");

	/** Message bar to display various messages. */
	private JLabel msgBar = new JLabel();

	/**The Combo box to display all of the opponents you can choose from*/
	private JComboBox<String> popupMenu;

	/** Special panel to display the game board. */
	private BoardPanel boardPanel;

	/**Control which controls actions done when a button is clicked*/
	private Control control;


	public C4Frame() {
		setSize(DIMENSION);
		control = new Control(this);

		configureUI();
		control.setBoardPanel(boardPanel);

		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		listenClose();
	}

	/**Plays sound given the file name, name needs to include extension, will do nothing if file is not found
	 * File needs to be inside the resource folder
	 * @param soundName is the name of the audio file, should include file extension*/
	public void playSound(String soundName){
		try{
			Clip sound = AudioSystem.getClip();

			AudioInputStream soundInput = AudioSystem.getAudioInputStream(this.getClass().getResource("/resources/" + soundName));
			sound.open(soundInput);
			sound.start(); 
		}
		catch(Exception e){
		}

	}

	/** Configure UI. */
	private void configureUI() {
		setLayout(new BorderLayout());
		add(makeControlPanel(), BorderLayout.NORTH);
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
		panel.setLayout(new GridLayout(1,1));
		panel.add(makeBoardPanel());
		add(panel, BorderLayout.CENTER);
	}

	/**
	 * Create a control panel consisting of a play button and
	 * a message bar, and combo box that dispays the opponents.
	 */
	private JPanel makeControlPanel() {
		JPanel content = new JPanel(new BorderLayout()); 
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));

		JLabel opponentText = new JLabel("Opponent: ");
		JPanel opponentList = new JPanel(new BorderLayout());

		buttons.setBorder(BorderFactory.createEmptyBorder(0,15,0,0));
		buttons.add(playButton);
		playButton.setFocusPainted(false);
		playButton.addActionListener(control::playButtonClicked);


		buttons.add(connectButton);
		connectButton.addActionListener(control::openConnectSettings);

		content.add(buttons, BorderLayout.NORTH);

		popupMenu = new JComboBox<String>(control.getOpponents());
		content.add(opponentList, BorderLayout.EAST);
		opponentList.add(opponentText, BorderLayout.WEST);
		opponentList.add(popupMenu, BorderLayout.EAST);


		msgBar.setBorder(BorderFactory.createEmptyBorder(5,20,0,0));
		content.add(msgBar, BorderLayout.CENTER);

		return content;
	}

	/** Create a panel to display the game board. */
	private BoardPanel makeBoardPanel() {

		boardPanel = new BoardPanel(control);
		boardPanel.setBoardClickListener(control::slotClicked);        
		return boardPanel;
	}

	/** Display the given string on the message bar. */
	public void showMessage(String msg) {
		Font labelFont = msgBar.getFont();
		msgBar.setFont(new Font(labelFont.getName(), Font.PLAIN, 18));
		msgBar.setText(msg);
	}

	/**Given a String, changes the JComboBox's current selection to the new String given*/
	public void setOpponent(String opponent){
		popupMenu.setSelectedItem(opponent);
		repaint();
	}

	/**Returns the currently selected opponent from the JComboBox
	 * Returns Object data type which can be converted to String*/
	public Object selectedOpponent(){
		return popupMenu.getSelectedItem();
	}

	/**Creates listener to be called when the window/C4Frame is closed*/
	public void listenClose(){
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				control.windowClosed();
				System.exit(0);

			}
		});
	}

	public static void main(String[] args) {
		new C4Frame();
	}

}