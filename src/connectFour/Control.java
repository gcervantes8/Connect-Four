/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;


public class Control{

	/**The JFrame to display the buttons, and Board. The C4Frame contains all of the GUI*/
	private C4Frame c4Frame;

	/**The boardPanel is the graphic board, it extends JPanel*/
	private BoardPanel boardPanel;

	/**The game model contains all of the business logic for the connect four game*/
	private GameModel model;

	/**Used for P2P connections with opponent*/
	private WebService web;

	/**A popup menu where Player can insert opponent's connection information*/
	private ConnectPanel connectMenu;


	public Control(C4Frame frame){
		
		this.c4Frame = frame;
		this.model = GameModel.getInstance();
		web = new WebService(this);
		displayGameStatus();

		connectMenu = new ConnectPanel(this);
	}

	/**Sets board panel to given board panel reference*/
	public void setBoardPanel(BoardPanel boardPanel){
		this.boardPanel = boardPanel;
	}

	/** Called when the play button is clicked. If the current play
	 * is over, start a new play; otherwise, prompt the user for
	 * confirmation and then proceed accordingly. Sends Play_ack message
	 * if playing against P2P/online opponent*/
	public void playButtonClicked(ActionEvent event){

		if(!web.writePlay()){
			if(model.hasNoGameInProgress()){
				resetGame("Set to Player vs " + c4Frame.selectedOpponent());
			}
			else{
				if(askMessage("Are you sure you want to reset the board?", "Reset board?")){
					resetGame("Reset, it's your turn. Player vs " + c4Frame.selectedOpponent());
				}
			}
		}
	}

	/**Prompts the user, gives use yes, no, and cancel options
	 * Returns true if they select yes, otherwise returns false
	 * @param message is the message you want ask the user with
	 * @param title is the title of the prompt window*/
	private boolean askMessage(String message, String title){
		int dialog = popupMessage(message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(dialog == JOptionPane.YES_OPTION){
			return true;
		}
		return false;
	}

	/**Resets the game
	 * @param message is message you want to give after the reset*/
	private void resetGame(String message){
		c4Frame.showMessage(message);
		model.setOpponent(c4Frame.selectedOpponent());
		model.resetGame();
		boardPanel.resetBoardPanel();
	}

	/**Called when play request was given by opponent, when playing p2p*/
	public void p2pPlayReceived(){

		connectMenu.enableConnectButton(false);
		String message = web.getIPAddress() + "  player requested to play new Connect Four game with you.\n"
				+ "Would you like to play?";
		if(!model.hasNoGameInProgress()){
			message = message + "\nThis will reset your current game";
		}
		int response = popupMessage(message, "Game Request", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(JOptionPane.YES_OPTION == response){
			c4Frame.setOpponent(model.getOnlineStrategy()); //Sets game to online

			boolean turn = model.randomBoolean();
			web.writePlayAck(true, turn);
			if(turn){
				model.setTurn(2);
				resetGame("It is opponent's turn. Player vs " + c4Frame.selectedOpponent());
			}
			else{
				model.setTurn(1);
				resetGame("It is your turn. Your disc is " +  model.getColorName());
			}
		}
		else{
			web.writePlayAck(false, false);
		}
	}
	
	/**Should be called when player receives a playAck message when playing p2p*/
	public void p2pPlayAckReceived(int x, int y){
		if(y == 1){
			model.setTurn(1);
		}
		else{
			model.setTurn(2);
		}

		if(x == 1){
			c4Frame.setOpponent(model.getOnlineStrategy());
			resetGame("Play request accepted, opponent's turn ");
		}
		else{
			c4Frame.showMessage("Play request rejected");
		}
	}
	
	/**Should be called when player receives a Quit or Close message when playing p2p*/
	public void p2pQuitCloseReceived(){
		web.disconnect();
		connectMenu.enableConnectButton(true);
	}
	

	/**Called when connect settings button is clicked*/
	public void openConnectSettings(ActionEvent event){
		connectMenu.setPlayersPort(Integer.toUnsignedString(web.getPort()));
		connectMenu.setIPText(web.getIPAddress());
		connectMenu.setVisible(true);
		connectMenu.setAlwaysOnTop(true);
	}

	/**Connect buttons is clicked, attempt to connect as long as they aren't already connected*/
	public void connectButton(ActionEvent event){
		/*If wants to reset, then changes to online game and connects*/
		if(!web.isConnected() && 
				(model.hasNoGameInProgress() || askMessage("Reset current game so you can play online?", "Reset board?"))){


			web.connectToServer(connectMenu.getIPText(), connectMenu.getOpponentsPort());

			if(web.isConnected()){
				connectMenu.enableConnectButton(false);
				web.writePlay();
			}
		}
	}

	/**When disconnect button is called, disconnects from online game and disables disconnect button
	 * from being clicked*/
	public void disconnectButton(ActionEvent event){
		web.disconnect();
		connectMenu.enableConnectButton(true);
	}

	/**Displays the game status*/
	private void displayGameStatus(){
		if(model.getPlayerWon()){

			if(web.isConnected()){
				String message = model.isYourTurn() ? "You lost!" : "You won!";
				c4Frame.showMessage(message);
			}

			else{
				c4Frame.showMessage(model.getPlayersName() + " won!");
			}

			c4Frame.playSound("applause.wav");
		}


		else if(model.isGameOver()){
			c4Frame.showMessage("There was a tie!");
		}

		else{
			if(web.isConnected()){

				String message = model.isYourTurn() ? "It is your turn, your " : "It is opponent's turn, their ";

				c4Frame.showMessage(message + "disc is " + model.getColorName());
			}
			else{
				c4Frame.showMessage("It is " + model.getPlayersName() + "'s turn, "
						+ "your disc is " + model.getColorName());
			}
		}
	}

	/**Called when Player closes C4Frame window*/
	public void windowClosed(){
		connectMenu.setVisible(false);
		web.disconnect();
	}

	/**Returns the list of possible opponents*/
	public String[] getOpponents(){
		return model.getPossibleOpponents();
	}

	/**Called by boardPanel listener after the animation is done
	 * @param slot is 1-based index indicating where you want slot to be dropped in*/
	public void slotClicked(int slot) {
		model.insertToBoard(slot-1);
		afterAnimation(slot);
	}

	/**Called if online strategy is selected and player inserted slot*/
	private void againstOnlineOpponent(int slot){
		if(!model.isYourTurn()){
			return;
		}
		dropSlot(slot);
		web.writeMove(slot-1);
	}

	/**If user clicks a slot, then boardPanel calls this method with indicated slot clicked
	 * @param slotClicked is the slot selected by user 1-based index*/
	public void slotSelected(int slotClicked) {
		if(!boardPanel.isAnimating()){
			if(model.isGameOver()){
				c4Frame.showMessage("Reset game to play again");
			}
			else if(model.isSlotFull(slotClicked-1)){
				c4Frame.showMessage("Slot " + slotClicked + " was full, it is " + model.getPlayersName() + 
						"'s turn, your disc is " + model.getColorName());
			}
			else{
				doStrategy(slotClicked);
			}
		}
	}

	/**Drops disc to board in given slot, there is animation*/
	public void dropSlot(int slot){
		boardPanel.dropSlot(slot);
	}

	/**Given slot you want to insert to, finds the strategy last applied to the game, and executes it*/
	private void doStrategy(int slot){

		int strategy = model.getOpponent();
		switch(strategy){
		case -2: //Player vs Player (P2P/Online Game)
			if(web.isConnected()){
				againstOnlineOpponent(slot);
			}
			else{
				c4Frame.showMessage("Connect with someone to play online");
			}
			break;
		case -1: dropSlot(slot); break; //Player vs Player
		case 0: dropSlot(slot); break;//Player vs Computer (Random strategy)
		case 1: dropSlot(slot); break;//Player vs Computer (Smart strategy)
		}
	}

	/**After animation is complete, calling this method will do next part of the strategy*/
	private void afterAnimation(int slot){
		int strategy = model.getOpponent();
		model.changeTurns();
		displayGameStatus();
		switch(strategy){
		case -2: break; //Does nothing after animation, Player vs Player (P2P/Online Game)
		case -1: break; /*Player vs player does not need to change anything after the animation*/
		case 0: afterAnimationComputer(); break; //Player vs Computer (Random strategy)
		case 1: afterAnimationComputer(); break; //Player vs Computer (Smart strategy)
		}
	}

	/**If a computer strategy is chosen, After the animation finishes, drops computer's slot*/
	private void afterAnimationComputer(){
		if(model.isComputersTurn()){
			boardPanel.dropSlot(model.computerTurn()+1);
		}
	}

	/**Pop up message, with 'yes' or 'no' options*/
	public int popupMessage(String msg, String titleBar, int options, int type)
	{
		return JOptionPane.showConfirmDialog(c4Frame, msg, titleBar, options, type);
	}

}
