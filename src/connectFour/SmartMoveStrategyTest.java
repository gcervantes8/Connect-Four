/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.awt.Color;

public class SmartMoveStrategyTest {

	/**Unit Tests the SmartMove class*/
	
	/**Board is the board we will use to test, initialized at setUp()*/
	private Board board;

	/**player1 is used to insert into the board, initialized at setUp()
	 * Integer board representation of opponent is 1*/
	private Player computer;

	/**Opponent is used to insert into the board, initialized at setUp()
	 * Integer board representation of opponent is 2*/
	private Player opponent;
	
	/**The strategy being tested*/
	private Strategy smartMove;


	@Before
	/**Initializes player and makes a new board at the start*/
	public void setUp() throws Exception {
		board = new Board();
		computer = new Player(1, "Player 1", "red", new Color(0,0,0), new Color(40,40,40));
		opponent  = new Player(2, "Player 2", "blue", new Color(0,0,0) ,new Color(40,40,40));
	}

	@After
	/**Tears down all global variables*/
	public void tearDown() throws Exception {
		board = null;
		computer = null;
		opponent = null;
		smartMove = null;
	}

	@Test
	/**Makes sure that AI will go for the winning move*/
	public void testAlgorithm1() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,1},
				{0,0,0,0,0,0,1},
				{0,0,2,0,0,0,1}});
		
		Assert.assertEquals(6, smartMove.algorithm());
	}
	
	@Test
	/**Makes sure that AI prevents opponent from winning*/
	public void testAlgorithm2() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,1,2,2,2,0}});
		
		Assert.assertEquals(6, smartMove.algorithm());
	}
	
	@Test
	/**If it's impossible to prevent other player from winning next turn,
	 * makes sure AI still blocks 1 win method*/
	public void testAlgorithm3() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,2,2,2,0}});
		
		int slot = smartMove.algorithm();
		Assert.assertTrue(slot == 6 || slot == 2);
	}
	
	@Test
	/**Makes sure AI won't ever try to drop disc in a full slot*/
	public void testAlgorithm4() {
		toBoard(new int[][]{
				{0,0,0,2,0,0,0},
				{0,0,0,2,0,0,0},
				{0,0,0,2,0,0,0},
				{0,0,0,1,0,0,0},
				{0,0,0,2,0,0,0},
				{0,0,0,1,0,0,0}});
		
		/*For loop used because of the randomness of the AI*/
		for(int i = 0; i < 1000; i++){
			int slot = smartMove.algorithm();
			Assert.assertFalse(slot == 3);
		}
	}
	
	@Test
	/**Makes sure AI won't drop disc in slot 0 or 4
	 * if AI drops in slot 0 or 4, other player can win next turn*/
	public void testAlgorithm5() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,2,2,2,0,0,0},
				{0,1,2,1,0,0,0}});
		
		/*For loop used because of the randomness of the AI*/
		for(int i = 0; i < 10000; i++){
			int slot = smartMove.algorithm();
			Assert.assertFalse(slot == 0);
			Assert.assertFalse(slot == 4);
		}
	}
	
	@Test
	/**Makes sure AI won't drop disc in slot 0 or 4
	 * if AI drops in slot 0 or 4, then other player can block AI's winning move*/
	public void testAlgorithm6() {
		toBoard(new int[][]{
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0},
				{0,1,1,1,0,0,0},
				{0,2,1,2,0,0,0}});
		
		/*For loop used because of the randomness of the AI*/
		for(int i = 0; i < 10000; i++){
			int slot = smartMove.algorithm();
			Assert.assertFalse(slot == 0);
			Assert.assertFalse(slot == 4);
		}
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
		board = new Board(intBoard, computer, opponent);
		smartMove = new SmartMoveStrategy(board, computer, opponent);
	}
}
