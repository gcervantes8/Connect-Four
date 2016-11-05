/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

public class Board{

	/**The game board*/
	
	
	/**The board is an Player 2D array, if cell contains a Player, then that player has a disc on that slot,
	 * if cell has null then cell is empty.*/
	private Player[][] board; 

	/**Contains amount of discs that are in given slot*/
	private int[] discsOnSlot;

	/**Contains x and y coordinates of the all the winning discs
	 * winningDiscs[0] contains x coordinate
	 * winningDiscs[1] contains y coordinate*/
	private int[][] winningDiscs;

	/**Creates board with 7 slots, and 6 vertical cells, board starts empty*/
	public Board(){
		board = new Player[6][7];
		discsOnSlot = new int[numOfSlots()];
		reset();
	}

	/**Used for BoardTest class, converts 2D Integer array into a Player board
	 * 0 is empty, 1 is player 1, and 2 is player 2
	 * @param intBoard is the integer representation of the board
	 * @param is player you want Player 1 to be
	 * @param is player you want player 2 to be*/
	public Board(int[][] intBoard, Player player1, Player player2){
		int playerNum = 0;
		board = new Player[intBoard.length][intBoard[0].length];
		discsOnSlot = new int[numOfSlots()];
		reset();
		
		for(int i = slotHeight()-1; i >= 0; i--){
			for(int slot = 0; slot < numOfSlots(); slot++){
				playerNum = intBoard[i][slot];
				
				if(playerNum == 1){
					dropInSlot(slot, player1);
				}
				else if(playerNum == 2){
					dropInSlot(slot, player2);
				}
			}
		}
	}

	/**Inserts disc in given slot, also saves lastPlacedDisc
	 * Returns false if slot was full or player was null
	 * @param slot is 0-based index
	 * @param player is the Player whose disc you are inserting to board*/
	public boolean dropInSlot(int slot, Player player){
		if(isSlotFull(slot) || player == null){
			return false;
		}
		int y = findDropDiscYCoordinate(slot);
		player.setLastPlacedDisc(slot, y);
		board[y][slot] = player;
		discsOnSlot[slot]++;
		return true;
	}

	/**Given x coordinate of the board, returns the y coordinate where disc should be inserted to
	 * @param slot is 0-based index*/
	public int findDropDiscYCoordinate(int slot){
		int cellsInSlot = slotHeight()-1; //Converts cell height to 0-index based
		return cellsInSlot - discsOnSlot[slot];
	}

	/**Checks if the board is full by checking top row of connect four board
	 * return true if board if full*/
	public boolean isFull(){
		for(int i = 0; i < board[0].length; i++){
			if( board[0][i] == null ){
				return false;
			}
		}
		return true;
	}
	
	/**Checks if the board is empty by checking bottom row of connect four board
	 * return true if board if empty*/
	public boolean isEmpty(){
		for(int i = 0; i < board[0].length; i++){
			if( board[slotHeight()-1][i] != null ){
				return false;
			}
		}
		return true;
	}

	/**Checks if slot is full or not, returns true if full, otherwise false
	 * @param slot is 0-based index*/
	public boolean isSlotFull(int slot){
		for(int i = 0; i < slotHeight(); i++){
			if(board[i][slot] == null){
				return false;
			}
		}
		return true;
	}

	/**Checks if slot is open or not, returns true if it's open, otherwise false 
	 * @param slot is 0-based index*/
	public boolean isSlotOpen(int slot){
		return !isSlotFull(slot);
	}

	/**Removes all discs on the board*/
	private void removeAllDiscs(){
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				board[i][j] = null;
			}
		}
	}

	/**Resets discOnSlot variable to default*/
	private void resetDiscsOnSlot(){
		for(int i = 0; i < discsOnSlot.length; i++){
			discsOnSlot[i] = 0;
		}
	}

	/**Resets the board and fields*/
	public void reset(){
		removeAllDiscs();
		resetDiscsOnSlot();
		winningDiscs = null;
	}

	/**Returns height of the board*/
	public int slotHeight(){
		return board.length;
	
	}

	/**Returns number of slots in board*/
	public int numOfSlots(){
		return board[0].length;
	}

	/**Returns coordinates of the winning discs, coordinates are stored in 2D integer array
	 * winningMove[0] is x axis
	 * winningMove[1] is y axis*/
	public int[][] getWinningMove(){
		return winningDiscs;
	}

	/**Is cell empty?
	 * @param x 0-based slot index
	 * @param y 0-based row index*/
	public boolean isEmpty(int x, int y){
		return board[y][x] == null;
	}

	/**Is cell occupied?
	 * @param x 0-based slot index
	 * @param y 0-based row index*/
	public boolean isOccupied(int x, int y){
		return board[y][x] != null;
	}

	/**Is cell occupied by given player? If player is null, returns false
	 * @param x 0-based slot index
	 * @param y 0-based row index*/
	public boolean isOccupiedBy(int x, int y, Player player){
		if(player == null){
			return false;
		}
		return player.equals(board[y][x]);
	}

	/**returns Player who has disc in that coordinate, null if empty
	 * @param x is coordinate on board is 0-based index
	 * @param y is coordinate on board is 0-based index*/
	public Player playerAt(int x, int y){
		return board[y][x];
	}
	
	/**Removes the top disc on a slot
	 * @param slot you want to remove from, 0-based index*/
	public void remove(int slot){
		if(discsOnSlot[slot] <= 0){
			return;
		}
		discsOnSlot[slot]--;
		int y = findDropDiscYCoordinate(slot);
		board[y][slot] = null;
	}

	/**Given a slot, returns number of discs in that slot
	 * @param slot is 0 based-index*/
	public int getDiscs(int slot){
		return discsOnSlot[slot];
	}
	
	/**Returns the amount of open slots in the board*/
	public int openSlots(){
		int openSlots = 0;
		for(int slot = 0; slot < numOfSlots(); slot++){
			if(isSlotOpen(slot)){
				openSlots++;
			}
		}
		return openSlots;
	}

	/**If Player won returns true, checks if player won using last placed disc and looking around it 
	 * sets initializes winning discs 2D array if player won
	 * @param player you want to check that won*/
	public boolean isWonBy(Player player){
		if(player == null){
			return false;
		}
		
		int[] lastPlacedDisc = player.getLastPlacedDisc();
		
		if(lastPlacedDisc[0] == -1){
			/*No disc has been placed yet*/
			return false;
		}
		
		Player disc = board[lastPlacedDisc[1]][lastPlacedDisc[0]];
		
		if(disc == null || !disc.equals(player)){
			return false;
		}
		
		int x = lastPlacedDisc[0];
		int y = lastPlacedDisc[1];
		
		
		int leftDiscs = checkAllDirections(x, y, -1, 0, disc);
		int rightDiscs = checkAllDirections(x, y, 1, 0, disc);
		int upLeftDiscs = checkAllDirections(x, y, -1, -1, disc);
		int upRightDiscs = checkAllDirections(x, y, 1, -1, disc);
		int downLeftDiscs = checkAllDirections(x, y, -1, 1, disc);
		int downRightDiscs = checkAllDirections(x, y, 1, 1, disc);
		int downDiscs = checkAllDirections(x, y, 0, 1, disc);


		boolean horizontalWin = setWinningDiscs(x, y,  leftDiscs,  rightDiscs,  -1,  0);
		boolean verticalWin = setWinningDiscs(x, y,  downDiscs,  0,  0,  1);
		boolean positiveDiagonalWin = setWinningDiscs(x, y, upLeftDiscs, downRightDiscs, -1, -1);
		boolean negativeDiagonalWin = setWinningDiscs(x, y, downLeftDiscs, upRightDiscs, -1, 1);

		return horizontalWin || verticalWin || positiveDiagonalWin || negativeDiagonalWin;
	}

	/**Can check all directions, given the x and y coordinates of last placed disc, the disc that's  being checked
	 * and the direction that you want checked using dy, dx. Returns integer to indicate amount of discs next to it*/
	private int checkAllDirections(int x, int y, int dx, int dy, Player disc){

		int consecutiveDiscs = 0;
		x += dx;
		y += dy;
	
		for(; withinGameBoard(x, y); x += dx,y += dy, consecutiveDiscs++){

			if(!disc.equals(board[y][x])){
				return consecutiveDiscs;
			}
		}
		return consecutiveDiscs;
	}

	/**Given x and y coordinates of board, returns true if coordinates are inside the board*/
	private boolean withinGameBoard(int x, int y){
		return x >= 0  && y >= 0 && x < numOfSlots() && y < slotHeight();
	}

	/**Given number of discs that are next to the disc from 2 direction (both directions should be opposite of each other) 
	 * finds if they make a connect four,
	 * saves the winning move and returns true if they do*/
	private boolean setWinningDiscs(int x, int y, int direction1Discs, int direction2Discs, int dx, int dy){

		int amountOfWinDiscs = (direction1Discs + direction2Discs) + 1;
		if(amountOfWinDiscs < 4){
			return false;
		}
		/*winningMove[0] contains y axis, winningMove[1], 2D array contains all winning disc coordinates*/
		winningDiscs = new int[2][amountOfWinDiscs];

		/*Goes to 1 end point*/
		x += (dx*direction1Discs);
		y += (dy*direction1Discs);

		/*Changes direction to opposite direction, ex. if it was left, turns to right*/
		dx *= -1;	
		dy *= -1;
		

		/*Goes to other end point 1 disc at a time, and saves the disc locations*/
		for(int i = 0; i < amountOfWinDiscs; i++, x += dx, y += dy){
			winningDiscs[0][i] = x;	
			winningDiscs[1][i] = y;
		}
		return true;
	}

}
