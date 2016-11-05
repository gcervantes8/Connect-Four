/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

public class GameManager {

	/**Game manager keeps track of turns, and game status*/
	/**Game manager uses the singleton design*/
	
	
	/**Shows whose turn it is*/
	private int turn = 1;

	/**Contains status on if a player won*/
	private boolean playerWon = false;

	/**If Board is full, then true*/
	private boolean fullBoard = false;
	
	/***/
	private int yourTurn = 1;

	/**An instance of GameManager*/
	private static GameManager gameInfo = new GameManager();
	
	/**Constructor is private so only 1 instance of this class can be made, part of singleton desgin*/
	private GameManager(){
		turn = 1;
	}
	
	/**Returns the only instance of the GameManager class*/
	public static GameManager getInstance(){
		return gameInfo;
	}

	/**If it's player 1's turn, changes to player 2's turn, if player 2's turn changes to player 1*/
	public void changeTurns(){
		if(turn == 1){
			turn = 2;
		}
		else{
			turn = 1;
		}
	}

	public void setTurn(int newTurn){
		yourTurn = newTurn;
	}
	
	public boolean isYourTurn(){
		return turn == yourTurn;
	}
	
	/**Sets that the board is full*/
	public void setBoardFull(){
		fullBoard = true;
	}

	/**Sets that a player won*/
	public void setPlayerWon(){
		playerWon = true;
	}

	/**Returns true if board is full*/
	public boolean isBoardFull(){
		return fullBoard;
	}

	/**Returns true if a player won*/
	public boolean hasPlayerWon(){
		return playerWon;
	}

	/**Gets current Player's turn*/
	public int getPlayersTurn(){
		return turn;
	}
	
	/**Sets the players turn to 
	 * @param turn*/
	public void setPlayersTurn(int newTurn){
		turn = newTurn;
	}

	/**Resets game and sets the players turn so that player 1 goes first, resets fields*/
	public void resetGameInfo(){
		playerWon = false;
		fullBoard = false;
		setPlayersTurn(1);
	}
	public static void test(){
		System.out.println("Game Manager");
	}

}
