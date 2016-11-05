/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import java.util.Random;
import java.lang.Math;

public class SmartMoveStrategy implements Strategy{


	/**Strategy that chooses a slot of the board that isn't full, contains smart AI,
	 * uses the strategy design*/
	
	
	/**Slot value is the random chance that a slot will be picked relative to other slots, if no player 
	 can win in the next turn
	 If it has a value of 0, there is no chance it will be picked*/
	private int[] slotValue;

	/**Strategy name that will appear to the player*/
	final private String strategyName = "Smart Computer";

	/**The random number generator*/
	private Random rng = new Random();

	/**The board that AI will use to find AI's next turn*/
	private Board board;

	/**Should be the AI*/
	private Player computer;

	/**Should be player opponent you are playing against*/
	private Player opponent;

	/**Gives the strategy, the board and the 2 Players playing*/
	public SmartMoveStrategy(Board board, Player computer, Player opponent){
		this.board = board;
		this.computer = computer;
		this.opponent = opponent;
	}

	/**Returns integer slot that AI SmartMove would drop disc to
	 * Returns an integer less than 0 if there is an error, or board is full*/
	public int algorithm(){

		slotValue = new int[board.numOfSlots()];
		
		int winSlot = winOrBlockSlot();
		if(winOrBlockSlot() != -1){
			return winSlot;
		}


		/*Inserting a disc potentially gives the other player a chance to win if they put their disc
		 * on top of the one you inserted, which is what canWinNextTurn method checks for, keeps slotValue
		 * array for that slot at 0, if they could have won*/
		
		for(int slot = 0; slot < board.numOfSlots(); slot++){
			if(!canWinNextTurn(opponent, computer, slot) && !canWinNextTurn(computer, opponent, slot)){
				setSlotValue(slot);
			}

		}
		int sum = sum(slotValue);
		/*If there is no good slots left, randomly chooses slot that is not full*/
		if(sum == 0){
			return randomNonFullSlot();
		}
		return slotDecision(sum);

	}

	/**Checks all slots to see if AI can win, if AI can win, returns the slot where it can win in
	 * otherwise, checks whether opponent can win, if opponent can win returns slot that
	 * they can win in, so AI can stop them from winning, returns -1 if opponent or computer couldn't win*/
	private int winOrBlockSlot(){
		int savedSlot = -1;
		for(int slot = 0; slot < board.numOfSlots(); slot++){
			if(canWin(computer, slot)){
				return slot;
			}
			if(canWin(opponent, slot)){
				savedSlot = slot;
			}
		}
		
		return savedSlot;
	}

	/**Given a slot, checks if the given Player can win, returns -1 if given player can't win
	 * @param Player is the player you want to check if they can win
	 * @param slot is the slot you want to check if player can win in*/
	private boolean canWin(Player player, int slot){

		if(board.dropInSlot(slot, player)){

			if(board.isWonBy(player)){
				/*Checks if player can win by inserting disc in that slot,
				 * if they can, removes other player's disc and returns slot number*/
				board.remove(slot);
				return true;
			}
			board.remove(slot);
		}
		return false;
	}

	/**Checks if other player can win next turn if AI places disc in this slot
	 * and other player places on top of your disc, returns true if they can
	 * 
	 * 			{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,1,2,0,0},
				{0,0,0,1,1,0,0},
				{0,2,2,2,1,0,0},
				{0,1,2,1,1,0,1}}
				
				Player 1 should not place disc in slot 1, or else player 2
				can win next turn by also placing it on slot 0
				
				Player 1 should not place a disc in slot 5, or else player 2 can block
				a possible winning move by player 1, by also placing it in slot 5
				
				*
		@param opponent is the player you want to check if they can win next turn
		@param computer is a reference to the opponent's disc
		@param slot is the slot you want to check if they can win next turn in*/
	private boolean canWinNextTurn(Player opponent, Player computer, int slot){
		boolean canWin = false;
		/*Slot needs to have 2 open disc slots, otherwise other player can't win next turn
		 * by placing it on top of your disc*/
		if(board.getDiscs(slot)+1 < board.slotHeight()){
			board.dropInSlot(slot, computer);
			board.dropInSlot(slot, opponent);
			if(board.isWonBy(opponent)){
				canWin = true;
			}
			board.remove(slot);
			board.remove(slot);
		}
		return canWin;
	}

	/**Gives slots that are closer to middle a higher slot value
	 * Slots that are set as bad slots (isSlotBad boolean array) get a slot value of 0
	 * @param slot is slot you want to give a value to*/
	private void setSlotValue(int slot){

		if(board.isSlotOpen(slot)){

			int middleOfBoard = board.numOfSlots()/2;
			int distanceFromMiddle = Math.abs(slot - middleOfBoard);
			/**Formula gives preference to slots in the middle*/
			slotValue[slot] = 1000/(3*distanceFromMiddle+1);
		}

	}

	/**Randomly picks a slot depending on the value given to each slot in array slotValue
	 * @param sum should be the sum of SlotValue array*/
	private int slotDecision(int sum){

		int randomNum = rng.nextInt(sum);
		int counter = 0;

		for(int slot = 0; slot < board.numOfSlots(); slot++){
			counter += slotValue[slot];
			if( randomNum < counter){
				return slot;
			}
		}
		return -2;
	}

	/**Returns sum of integer array
	 * @param arr is the array you want to find sum of*/
	private int sum(int[] arr){
		int sum = 0;
		for(int i = 0; i < arr.length; i++){
			sum += arr[i];
		}
		return sum;
	}

	/**Randomly chooses a slot that is not full, all slots that are not full have equal chance
	 * should be used as a last case scenario, returns -3 if error occured*/
	private int randomNonFullSlot(){
		int randomOpenSlot = rng.nextInt(board.openSlots());
		for(int slot = 0, openSlotIndex = 0; slot < board.numOfSlots(); slot++){

			if( board.isSlotOpen(slot)){
				if(randomOpenSlot == openSlotIndex){
					return slot;
				}
				openSlotIndex++;
			}

		}
		return -3;
	}

	/**Returns name of strategy, which will be shown to players that play the game*/
	public String getStrategyName(){
		return strategyName;
	}
}
