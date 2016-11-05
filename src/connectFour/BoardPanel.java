/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;


/** A special panel to display a connect-four board. */

@SuppressWarnings("serial")
public class BoardPanel extends JPanel implements Runnable{

	/** Callback interface to listen to board click events. */
	public interface BoardClickListener {

		/** Called when a slot is clicked. */
		void slotClicked(int slot);
	}

	/** Background color of the board. */
	private final Color boardColor = new Color(245, 212, 0);

	/**The color of the highlighted disc, preferably should be transparent*/
	final private Color highlightedDiscColors = new Color(240,240,240, 110); 

	/**Winning move highlight color for disc, preferably should be transparent*/
	final private Color winningDiscColors = new Color(0,0,0,75); 

	/** Listen to board click events. */
	private BoardClickListener listener;

	/**The game model*/
	private GameModel model;

	/**If animation is playing, then set to true*/
	private boolean animationPlaying = false;

	/**Height of disc when dropping animation is being played, reset to 0 after animation is done*/
	private int discHeight;

	/**Width of 1 cell in the board, all cells are same size, used for drawing*/
	private int cellWidth;

	/**Height of 1 cell in the board, all cells are same size, used for drawing*/
	private int cellHeight;

	/**Saves slot to insert to, used to insert to board after animation*/
	private int slotInsert;

	/**Used for double buffering*/
	private Image image;

	/**Used for double buffering*/
	private Graphics offscreen;

	/**Thread delay before thread runs again*/
	private int delay = 31;
	
	/**Thread used for animation*/
	private Thread mainThread = null;
	

	/** Create a new board panel. */
	public BoardPanel(Control control) {
		this.model = GameModel.getInstance();
		if(mainThread == null){
			mainThread = new Thread(this);
			mainThread.start();
		}
		
		addMouseListener(new MouseAdapter() {          
			public void mouseReleased(MouseEvent e) {
				int slotClicked = locateSlot(e.getX());
				control.slotSelected(slotClicked);
			}
		});
	}

	/**Called when you want to drop a disc in a slot*/
	public void dropSlot(int slot){
		if (slot > 0 && !animationPlaying) {
			/*Clicking on a slot will start the animation and will insert the disc
			 * on the board after the animation finishes*/
			discHeight = 0;
			animationPlaying = true;
			slotInsert = slot;
			}
	}


	/** Register the given board click listener. */
	public void setBoardClickListener(BoardClickListener listener) {
		this.listener = listener;
	}

	/**
	 * Given a screen coordinate <code>x</code>, 
	 * locate the corresponding slot (column) of the board
	 */
	public int locateSlot(int x) {
		/*Given the x coordinate clicked on, returns slot number 1-based index*/
		int slot = (int) (x/ cellWidth) + 1;

		/*Handles pixels using integers, including cellWidth, so could be off by a pixel*/
		if(slot >= model.getSlots()){
			return model.getSlots();
		}

		return slot;
	}

	/** Overridden here to draw the board along with placed checkers. */
	@Override
	public void paint(Graphics g) {
		update(g);
	}
	/**Update called every time repaint() is invoked or whenever it needs to update*/
	@Override
	public void update(Graphics g){
		cellWidth = getWidth()/model.getSlots();
		cellHeight = getHeight()/(model.getHeight() + 1);  //board.getHeight() + 1, to add extra row to top of board

		image = createImage(getWidth(), getHeight());
		offscreen = image.getGraphics();

		super.paint(offscreen); //clears the background

		drawBoard(offscreen);

		drawPlacedCheckers(offscreen);

		highlightLastPlacedDisc(offscreen); 

		boolean animationEnded = dropDiscAnimation(offscreen);

		insertDisc(animationEnded);

		topRow(offscreen);

		showWinningMove(offscreen);

		//double buffering
		g.drawImage(image,  0 , 0, this); 
	}
	

	/**Animates the dropping disc onto the board, when the animation ends, the disc will be inserted onto the board.
	 * Returns true when the animation just ended*/
	private boolean dropDiscAnimation(Graphics g){

		if(!animationPlaying || model.isGameOver()){
			return false;
		}

		int x = slotInsert - 1;  /*x and y contain the location of the cell where the disc is being inserted to*/
		int y = model.newDiscY(x);

		if(cellHeight * (y+1) > discHeight){
			g.setColor(model.getColorDisc(true));
			drawDisc(g, x * cellWidth, discHeight, 5);
			g.setColor(model.getColorDisc(false));
			drawDisc(g, x * cellWidth, discHeight, 12);
			discHeight += (getHeight() / 20); /*Height determines how many pixels it moves every repaint, height can be raised to raise the speed*/
		}
		else{
			animationPlaying = false;
			return true;
		}
		return false;
	}

	/**Draws the top row of board filled with discs, that change color depending on players turn*/
	private void topRow(Graphics g){

		for(int i = 0; i < model.getSlots(); i++){
			g.setColor(model.getColorDisc(true));
			drawDisc(g, i * cellWidth,  0, 5);
			g.setColor(model.getColorDisc(false));
			drawDisc(g, i * cellWidth,  0, 12);
		}
	}

	/**If the animation is not playing, and it just ended playing, then it inserts onto the board*/
	private void insertDisc(boolean animationEnded){
		if(!animationPlaying && animationEnded){
			listener.slotClicked(slotInsert);
			
		}

	}

	/**Draws the disc, in given x-y coordinate, given the size of the disc
		The smaller the sizeOffSet the smaller disc, should not be negative to prevent overlapping*/
	private void drawDisc(Graphics g, int x, int y, int sizeOffSet){
		g.fillOval(x + sizeOffSet, y + sizeOffSet, cellWidth - (sizeOffSet * 2 ), cellHeight - (sizeOffSet * 2 ));
	}

	/**Draws yellow/orange square, which will represent the board*/
	private void drawBoard(Graphics g){
		g.setColor(boardColor);
		g.fillRect(0, cellHeight, getWidth(), getHeight());
	}

	/**Draws discs and empty slots on the board*/
	private void drawPlacedCheckers(Graphics g){

		for(int i = 0; i < model.getSlots(); i++){
			for(int j = 0; j < model.getHeight() ; j++){
				Player disc = model.getPlace(i, j);
				g.setColor(model.getColorDisc(disc, true));
				drawDisc(g, i * cellWidth, (j+1) * cellHeight, 5); //Outline around disc  // j+1, to add extra row at top
				if(disc != null){ 		//If disc is empty, don't draw outline
					g.setColor(model.getColorDisc(disc, false));
					drawDisc(g, i * cellWidth, (j+1) * cellHeight, 12); //Inner disc  // j+1, to add extra row at top
				}
			}
		}
	}

	/**If someone has won the game
	 * then shows the winning connect four 4 discs*/
	private void showWinningMove(Graphics g){
		if(model.getPlayerWon()){
			g.setColor(winningDiscColors);
			int[][] winDiscs = model.getWinningMove();
			for(int i = 0; i < winDiscs[0].length ; i++){
				drawDisc(g, winDiscs[0][i] * cellWidth, (winDiscs[1][i]+1) * cellHeight, 0 );
			}	
		}
	}

	/**Last placed disc will be highlighted by a white/transparent circle
	 * nothing is highlighted if no disc is on the board*/
	private void highlightLastPlacedDisc(Graphics g){

		if(!model.isGameOver()){
			int x = model.getLastPlacedDisc(true);
			int y = model.getLastPlacedDisc(false);
			if(x == -1){
				/**There is an empty board is x coordinate and y coordinate are -1*/
				return;
			}

			g.setColor(highlightedDiscColors);
			drawDisc(g, x * cellWidth, (y+1) * cellHeight, 0);  // y+1, to add extra row at top

		}
	}
	
	/**If drop disc animation is happening at the moment, then returns true
	 * Slot clicks disabled as long as animation is playing*/
	public boolean isAnimating(){
		return animationPlaying;
	}
	
	/**Runs the thread, used for animation repainting*/
	public void run(){
		while(true){
			repaint();
			try{
				Thread.currentThread();
				Thread.sleep(delay);
			}
			catch (InterruptedException e){}
		}
	}
	
	/**Resets the board panel*/
	public void resetBoardPanel(){
		discHeight = 0;
		animationPlaying = false;
	}
}

