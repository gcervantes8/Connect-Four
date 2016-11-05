/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import java.util.Random;

public class RandomMove implements Strategy{

	/**Strategy that randomly chooses a slot of the board that isn't full,
	 * uses the strategy design*/
	
	
	/**Strategy name that will appear to the player*/
	final private String strategyName = "Random";
	
	/**The board that AI will use to find AI's next turn*/
	private Board board;
	
	/**Creates the strategy, given the board, board is used to know which slots are full*/
	public RandomMove(Board board){
		this.board = board;
	}
	
	/**Algorithm returns a random slot that is not full
	 * Returns -1 if board is full*/
	public int algorithm(){
		
		if(board.isFull()){
			return -1;
		}
		
		Random rng = new Random();
		int slot;
		
		while(board.isSlotFull(slot = rng.nextInt(board.numOfSlots()))){
		}
		return slot;
	}
	
	/**Returns name of strategy, which will be shown to players that play the game*/
	public String getStrategyName(){
		return strategyName;
	}
}
