/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import static org.junit.Assert.fail;

import java.awt.Color;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class BoardTest {

	/**Tests the board class*/
	
	
	/**Board is the board we are testing, initialized at setUp()*/
	private Board board;

	/**player1 is used to insert into the board, initialized at setUp()*/
	private Player player1;

	/**player2 is used to insert into the board, initialized at setUp()*/
	private Player player2;

	@Before
	/**Sets up empty board, and creates 2 Players*/
	public void setUp(){
		board = new Board();
		player1 = new Player(1, "Player 1", "red", new Color(0,0,0), new Color(40,40,40));
		player2  = new Player(2, "Player 2", "blue", new Color(0,0,0) ,new Color(40,40,40));
	}

	@After
	/**Sets all variables to null after test is done*/
	public void tearDown(){
		board = null;
		player1 = null;
		player2  = null;
	}

	@Test
	/**Tests the default constructor*/
	public void testBoardConstructorDefault(){
		board = new Board();
		Assert.assertEquals(board.numOfSlots(), 7);
		Assert.assertEquals(board.slotHeight(), 6);
	}

	@Test
	/**Tests constructor which makes board from integer 2D array*/
	public void testBoardConstructorIntBoard(){
		board = new Board(new int[][]{
				{0,0,0,0,0,0},
				{1,1,1,1,1,1},
				{1,1,1,1,1,1},
				{1,1,1,1,1,1},
				{1,1,1,1,1,1},
				{1,1,1,1,1,1}}, player1, player2);
		Assert.assertEquals(board.numOfSlots(), 6);
		Assert.assertEquals(board.slotHeight(), 6);
		Assert.assertNotNull(board);
		Assert.assertTrue(board.playerAt(0,1) == player1);
		Assert.assertFalse(board.playerAt(0,0) == player1);
	}

	@Test
	/**Inserts disc into first slot, basic functionality test*/
	public void testDropInSlot1() {

		board.dropInSlot(0, player1);
		Assert.assertNotNull(board.playerAt(0, board.slotHeight()-1));
	}

	@Test
	/**Checks that top row works, and last slot (top-right corner of board)*/
	public void testDropInSlot2() {
		for(int i = 0; i < board.slotHeight(); i++){
			board.dropInSlot(board.numOfSlots()-1, player1);	
		}
		Assert.assertNotNull(board.playerAt(board.numOfSlots()-1, 0));
	}

	@Test
	/**Checks that empty board works and is not detected as full board*/
	public void testIsFull1() {
		Assert.assertFalse(board.isFull());
	}

	@Test
	/**Checks that board with almost all discs except for last row isn't detected as full*/
	public void testIsFull2() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1},
				{1,1,1,1,1,1,1}});
		Assert.assertFalse(board.isFull());
	}

	@Test
	/**Checks that board with almost all discs except for last slot isn't detected as full*/
	public void testIsFull3() {
		toBoard(new int[][]{
				{1,1,1,1,1,1,0},
				{1,1,1,1,1,1,0},
				{1,1,1,1,1,1,0},
				{1,1,1,1,1,1,0},
				{1,1,1,1,1,1,0},
				{1,1,1,1,1,1,0}});

		/**Fills board except for last slot, shouldn't detect board being full*/
		Assert.assertFalse(board.isFull());
	}
	@Test
	/**Checks full board works*/
	public void testIsFull4() {
		toBoard(new int[][]{
				{1,1,1,1,1,1},
				{1,1,1,1,1,1},
				{1,1,1,1,1,1},
				{1,1,1,1,1,1}});
		Assert.assertTrue(board.isFull());
	}

	@Test
	/**Tests empty board*/
	public void testIsSlotFull1() {
		Assert.assertFalse(board.isSlotFull(0));
	}
	/**Check to make sure X and Y coordinates are not reversed*/
	@Test
	public void testIsSlotFull2() {
		toBoard(new int[][]{
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{1,1,1,1,1,1}});
		Assert.assertFalse(board.isSlotFull(board.numOfSlots()-1));
	}
	/**Almost filled slot, using last slot*/
	@Test
	public void testIsSlotFull3() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1}});
		Assert.assertFalse(board.isSlotFull(board.numOfSlots()-1));
	}

	@Test
	/**Tests a filled slot*/
	public void testIsSlotFull4() {
		toBoard(new int[][]{
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0}});
		Assert.assertTrue(board.isSlotFull(3));
	}

	@Test
	/**Empty slot test*/
	public void testIsSlotOpen1() {
		Assert.assertTrue(board.isSlotOpen(0));
	}

	@Test
	/**Checks to make sure X and Y coordinates are not reversed*/
	public void testIsSlotOpen2() {
		toBoard(new int[][]{
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{0,0,0,0,0,0},
				{1,1,1,1,1,1}});
		Assert.assertTrue(board.isSlotOpen(board.numOfSlots()-1));
	}

	@Test
	/**Almost filled slot, uses last slot*/
	public void testIsSlotOpen3() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1}});
		Assert.assertTrue(board.isSlotOpen(board.numOfSlots()-1));
	}

	@Test
	/**tests a full slot*/
	public void testIsSlotOpen4() {
		toBoard(new int[][]{
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0}});
		Assert.assertFalse(board.isSlotOpen(3));
	}

	@Test
	/**Tests to see if board clears*/
	public void testClear() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{1,0,0,0,0,0,1}});

		board.reset();

		/**Checks bottom-left and top-right corners of board to check that discs were reset*/
		Assert.assertNull(board.playerAt(0, board.slotHeight()-1));
		Assert.assertNull(board.playerAt(board.numOfSlots()-1, 0));
	}

	@Test
	/**Tests the number of slots is returns correctly*/
	public void testNumOfSlots1() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0}});

		Assert.assertEquals(board.numOfSlots(), 7);
	}

	@Test
	/**Tests the number of slots is returns correctly when there is 0 slots*/
	public void testNumOfSlots2() {
		toBoard(new int[][]{
				{},
				{},
				{},
				{},
				{},
				{}});

		Assert.assertEquals(board.numOfSlots(), 0);
	}

	@Test
	/**Tests the height of cells*/
	public void testSlotHeight1() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0}});

		Assert.assertEquals(board.slotHeight(), 6);
	}
	@Test
	/**Tests the height of cells when cell height is 1*/
	public void testSlotHeight2() {
		toBoard(new int[][]{{0}});

		Assert.assertEquals(board.slotHeight(), 1);
	}

	@Test
	/**Makes sure empty Board works*/
	public void testIsEmpty1(){
		Assert.assertTrue(board.isEmpty(3, 0));
	}

	@Test
	/**Makes sure it detects that the disc is not empty*/
	public void testIsEmpty2(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0}});

		Assert.assertFalse(board.isEmpty(0,5));
	}

	@Test
	/**Checks to make sure x and y coordinates are not backwards*/
	public void testIsEmpty3(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1}});

		Assert.assertFalse(board.isEmpty(6,0));
	}

	@Test
	/**Makes sure empty board works*/
	public void testIsOccupied1(){
		Assert.assertFalse(board.isOccupied(5, 0));
	}

	@Test
	/**Makes sure it detects that the bottom-left corner of board is occupied*/
	public void testIsOccupied2(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0}});

		Assert.assertTrue(board.isOccupied(0,5));
	}

	@Test
	/**Checks to make sure x and y coordinates are not backwards*/
	public void testIsOccupied3(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1}});

		Assert.assertTrue(board.isOccupied(6,0));
	}

	@Test
	/**Makes sure empty board works*/
	public void testIsOccupiedBy1(){
		Assert.assertFalse(board.isOccupiedBy(5, 0, player1));
	}

	@Test
	/**Makes sure it detects that the bottom-left corner of board has a Player*/
	public void testIsOccupiedBy2(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0}});

		Assert.assertTrue(board.isOccupiedBy(0,5, player1));
	}

	@Test
	/**Checks to make sure x and y coordinates are not backwards*/
	public void testIsOccupiedBy3(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1}});

		Assert.assertTrue(board.isOccupiedBy(6,0, player2));
	}

	@Test
	/**Tests to see if it will remove disc*/
	public void testRemove1(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1}});
		
		Assert.assertTrue(board.isOccupied(6, 0));
		board.remove(6);
		Assert.assertFalse(board.isOccupied(6, 0));
	}
	
	@Test
	/**Test to make sure no error occur if you try to remove from empty*/
	public void testRemove2(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0}});
		
		board.remove(3);
		Assert.assertFalse(board.isOccupied(3, 5));
	}
	
	@Test
	/**Test to make sure it finds there are no discs in an empty slot*/
	public void testGetDiscs1(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0}});
		
		Assert.assertEquals(board.getDiscs(3), 0);
	}
	
	
	@Test
	/**Test to make sure it finds 6 discs when the slot is full*/
	public void testGetDiscs2(){
		toBoard(new int[][]{
				{0,0,0,2,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,2,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,2,0,0,0},
				{0,0,0,1,0,0,0}});
		
		Assert.assertEquals(board.getDiscs(3), 6);
	}
	
	@Test
	/**Makes sure empty board works*/
	public void testPlayerAt1(){
		Assert.assertNull(board.playerAt(5, 0));
	}

	@Test
	/**Makes sure it detects that the bottom-left corner of board has Player*/
	public void testPlayerAt2(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0}});

		Assert.assertNotNull(board.playerAt(0,5));
	}

	@Test
	/**Checks to make sure x and y coordinates are not backwards*/
	public void testPlayerAt3(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,0,2}});

		Assert.assertNotNull(board.playerAt(6,0));
	}

	@Test
	/**Checks a vertical win*/
	public void testIsWonBy1(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,2,0,1,0,0,0},
				{0,2,2,1,2,0,0}});
		Assert.assertTrue(board.isWonBy(player1)); 
		Assert.assertFalse(board.isWonBy(player2)); 
	}
	@Test
	/**Checks horizontal win*/
	public void testIsWonBy2(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,2,2,0,0},
				{0,0,1,1,1,1,1}});
		Assert.assertTrue(board.isWonBy(player1)); 
		Assert.assertFalse(board.isWonBy(player2)); 
	}
	@Test
	/**Checks negative diagonal win*/
	public void testIsWonBy3(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{1,0,0,0,0,0,0},
				{2,1,0,0,0,0,0},
				{2,2,1,0,0,0,0},
				{2,2,2,1,0,0,0}});
		Assert.assertTrue(board.isWonBy(player1)); 
		Assert.assertFalse(board.isWonBy(player2)); 
	}
	@Test
	/**Checks positive diagonal win*/
	public void testIsWonBy4(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,2},
				{0,0,0,0,0,2,1},
				{0,0,0,0,2,1,2},
				{0,0,0,2,2,1,2},
				{0,0,0,1,1,2,1},
				{0,0,0,1,2,1,1}});
		Assert.assertTrue(board.isWonBy(player2)); 
		Assert.assertFalse(board.isWonBy(player1)); 
	}
	@Test
	/**Checks no winner*/
	public void testIsWonBy5(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,1,2,0,0,0}});
		Assert.assertFalse(board.isWonBy(player1)); 
		Assert.assertFalse(board.isWonBy(player2)); 
	}
	@Test
	/**Full board that has no wins, no win check*/
	public void testIsWonBy6(){
		toBoard(new int[][]{
				{2,2,2,1,2,2,2},
				{1,1,1,2,1,1,1},
				{2,2,2,1,2,2,2},
				{1,1,1,2,1,1,1},
				{2,2,2,1,2,2,2},
				{1,1,1,2,1,1,1}});
		Assert.assertFalse(board.isWonBy(player1)); 
		Assert.assertFalse(board.isWonBy(player2)); 
	}
	@Test
	/**Tests to make sure getWinningMove contains 0 as default if no player has won*/
	public void testGetWinningMove1(){
		board.isWonBy(player1);
		int[][] winMove= board.getWinningMove();

		Assert.assertNull(winMove);
	}
	@Test
	/**Tests to make sure getWinningMove contains the correct winning moves*/
	public void testGetWinningMove2(){
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,1,0,0,0},
				{0,2,0,1,0,0,0},
				{0,2,2,1,2,0,0}});
		board.isWonBy(player1);
		int[][] winMove= board.getWinningMove();

		Assert.assertEquals(5, winMove[1][0]);
		Assert.assertEquals(3, winMove[0][0]);
		Assert.assertEquals(4, winMove[1][1]);
		Assert.assertEquals(3, winMove[0][1]);
	}

	@Test
	/**Gets Y coordinate of where disc should be inserted, with nothing inserted*/
	public void testGetInsertY1(){
		Assert.assertEquals(5, board.findDropDiscYCoordinate(0));
	}
	@Test
	/**Gets Y coordinate of where disc should be inserted, inserts to slot 1 then checks y coordinate*/
	public void testGetInsertY2(){
		board.dropInSlot(1, player1);
		Assert.assertEquals(4, board.findDropDiscYCoordinate(1));
	}

	/**Given an integer 2D array representation of board, turns it into an updated board object
	 * @param integer array you want to convert to board object, changes the global variable board*/
	private void toBoard(int[][] intBoard){
		if(intBoard == null){
			fail("Trying to convert null intBoard to Board");
		}
		int intBoardHeight = intBoard.length;
		int intBoardSlots = intBoard[0].length;
		for(int i = 0; i < intBoardHeight; i++){
			if(intBoardSlots != intBoard[i].length){
				fail("Test method made wrong, intBoard should be a rectangle to convert");
			}
		}
		board = new Board(intBoard, player1, player2);
	}
}
