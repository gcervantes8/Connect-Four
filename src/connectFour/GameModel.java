/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import java.awt.Color;

public class GameModel{
	

	/**The game model which consists of the game operators, takes care of all the internal game logic
	 * and business logic.  Uses the singleton design*/
	
	/**Makes a board*/ 
	private Board board;

	/**Makes GameManager to keep track of information about current game to help manage game*/
	private GameManager gameInfo;

	/**A Player array that contains all the players participating in the game*/
	private Player[] players;

	/**Makes an array with all the possible AI strategies*/
	private Strategy[] computer;

	/**Indicates which strategy to use, player vs player has index of -1, all AI
	 * strategies have an index of 0 or greater*/
	private int strategy;

	/**Strategy name for player vs player*/
	final private String playerStrategyName = "Player";
	
	/**Strategy name for online player vs player*/
	final private String onlineStrategyName = "Online Player";

	/**Color of the blue disc*/
	final private Color blueDisc = new Color(40, 40, 255);
	
	/**Color of the blue disc outline*/
	final private Color blueDiscOutline = new Color(0, 0, 170);
	
	/**Color of the red disc*/
	final private Color redDisc = new Color(230, 0, 0);
	
	/**Color of the red disc outline*/
	final private Color redDiscOutline = new Color(180, 0, 0);
	
	/**Color when there is no disc*/
	final private Color noDisc = new Color(250, 235, 210);

	/**The game model instance, consists of all the 'business logic' for connect four game model*/
	private static GameModel model = new GameModel();
	
	private Player player;
	
	/**The GameModel initializes with 2 Players, makes objects of all strategies, Board and GameManager objects*/
	private GameModel(){

		players = new Player[3];
		players[1] = new Player(1, "Player 1", "blue", blueDisc, blueDiscOutline);
		players[2] = new Player(2, "Player 2", "red", redDisc, redDiscOutline);
		
		strategy = -1; /**When game is started, starts with player vs player*/
		
		gameInfo = GameManager.getInstance();
		board = new Board();
		
		computer = new Strategy[2];
		computer[0] = new RandomMove(board);
		computer[1] = new SmartMoveStrategy(board, players[2], players[1]);
	}
	
	/**Sets player who is playing
	 * @param i should be either 1 or 2 depending on who you want to be playing to be*/
	public void setPlayer(int i){
		player = players[i];
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public String getOnlineStrategy(){
		return onlineStrategyName;
	}
	
	public boolean isYourTurn(){
		return gameInfo.isYourTurn();
	}
	
	public void setTurn(int turn){
		gameInfo.setTurn(turn);
	}
	
	public boolean isSlotFull(int slot){
		return board.isSlotFull(slot);
	}
	
	/**Returns the only instance of the GameModel class*/
	public static GameModel getInstance(){
		return model;
	}
	
	public boolean isBoardEmpty(){
		return board.isEmpty();
	}
	
	/**Returns a string array with all the possible opponents*/
	public String[] getPossibleOpponents(){
		String[] opponents = new String[computer.length+2];
		opponents[0] = playerStrategyName;
		opponents[1] = onlineStrategyName;
		for(int i = 0; i < computer.length; i++){
			opponents[i+2] = computer[i].getStrategyName();
		}
		return opponents;
	}

	/**Sets opponent given the string name of strategy/opponent
	 * @param opponent should be a String containing the name of the strategy*/
	public void setOpponent(Object opponent){
		if(opponent != null){

			if( opponent instanceof String){
				String opponentString = (String) opponent;

				if(opponentString.equals(playerStrategyName)){
					strategy = -1;
				}
				if(opponentString.equals(onlineStrategyName)){
					strategy = -2;
				}
				for(int i = 0; i < computer.length; i++){

					if( opponentString.equals(computer[i].getStrategyName())){
						strategy = i;
					}
				}
				
			}
		}
	}
	
	public int getOpponent(){
		return strategy;
	}

	/**If you are facing a computer opponent, and it's their turn, and game isn't over
	 * then returns true*/
	public boolean isComputersTurn(){
		/*Strategy indicated by -1 is, player vs player strategy, so should return false*/
		if(strategy == -1){
			return false;
		}
		return getPlayersTurn().equals(players[2]) && !isGameOver();
	}

	/**Returns slot that computer strategy want to drop slot to
	 * @return 0 based-index*/
	public int computerTurn(){
		return computer[strategy].algorithm();
	}

	/**Inserts disc in given slot, on whoever's turn it is
	 * returns true if slot is full (did not insert disc), returns false if slot wasn't full and disc was inserted
	 * Checks if board is full, sets board to full in gameManager if board was full
	 * Checks if player who just inserted won, if they did, sets it to true in gameManager
	 * @param slot is 0-based index*/
	public boolean insertToBoard(int slot){
		boolean slotFull = board.dropInSlot(slot, getPlayersTurn());
		if(board.isWonBy(getPlayersTurn())){
			gameInfo.setPlayerWon();
		}
		if(board.isFull()){
			gameInfo.setBoardFull();
		}
		return slotFull;
	}

	/**If it's player 1's turn, changes to player 2's turn, if player 2's turn changes to player 1*/
	public void changeTurns(){
		gameInfo.changeTurns();
	}

	/**Returns true if a player won, stored in gameInfo object*/
	public boolean getPlayerWon(){
		return gameInfo.hasPlayerWon();
	}

	/**Resets GameManager and Board to default settings*/
	public void resetGame(){
		gameInfo.resetGameInfo();
		board.reset();
		for(int i = 1; i < players.length; i++){
			players[i].gameReset();
		}
	}

	/**Returns the number of slots in board*/
	public int getSlots(){
		return board.numOfSlots();
	}

	/**Returns height of the board*/
	public int getHeight(){
		return board.slotHeight();
	}

	/**Returns string that contains the color of whoever turn it is
	 * ex. If red disc has to go next, returns "red"*/
	public String getColorName(){
		return getPlayersTurn().getColorDisc();
	}

	/**Returns Y-coordinate where new disc will be inserted to, used for animation
	 * @param slot you want to find Y coordinate of, slot is 0-based index*/
	public int newDiscY(int slot){
		return board.findDropDiscYCoordinate(slot);
	}

	/**Returns coordinates of the winning discs, coordinates are stored in 2D int array
	 * winningMove[0] is y axis
	 * winningMove[1] is x axis*/
	public int[][] getWinningMove(){
		return board.getWinningMove();
	}

	/**Returns Coordinates of board that shows where the last placed disc was
	 * @param If boolean parameter is true, returns x coordinate, if false returns y coordinate
	 * Returns -1 if board is empty*/
	public int getLastPlacedDisc(boolean xCoordinate){
		int turn = gameInfo.getPlayersTurn();
		int[] coordinates;
		
		if(turn == 1){
			coordinates = players[2].getLastPlacedDisc();
		}
		else{
			coordinates = players[1].getLastPlacedDisc();
		}
		
		if(xCoordinate){
			return coordinates[0];
		}
		return coordinates[1];
	}

	/**Returns the player that is on the board given y and x coordinates, returns null if empty
	 * @param x is x-coordinate of board and has 0-based index
	 * @param y is y-coordinate of board and has 0-based index*/
	public Player getPlace(int x, int y){
		return board.playerAt(x, y);
	}

	/**Returns the color of the disc
	 * @param if outline is true, then returns outline color, otherwise returns disc color*/
	public Color getColorDisc(boolean outline){

		return getPlayersTurn().getPlayerColor(outline);
	}

	/**Returns the Color disc of given Player
	 * @param player is the players you want color's disc of
	 * @param boolean outline if true returns color of discs's outline, otherwise just the color disc*/
	public Color getColorDisc(Player player, boolean outline){
		if(player == null){
			return noDisc;
		}
		return player.getPlayerColor(outline);
	}

	/**Returns name of player whose turn it is*/
	public String getPlayersName(){
		Player currentPlayer = getPlayersTurn();
		return currentPlayer.getName();
	}

	/**Returns true if game is over*/
	public boolean isGameOver(){
		return getPlayerWon() || gameInfo.isBoardFull();
	}

	/**Returns Player whose turn it is*/
	public Player getPlayersTurn(){
		int turn = gameInfo.getPlayersTurn();
		return players[turn];
	}
	
	/**Randomly returns a boolean that is true or false
	 * 50% chance for it to return true, 50% chance for it to return false*/
	public boolean randomBoolean(){
		int randomInt = new java.util.Random().nextInt(2);
		return randomInt == 1;
	}
	
	/**Returns false if a game is in progress*/
	public boolean hasNoGameInProgress(){
		return isGameOver() || isBoardEmpty();
	}
}
