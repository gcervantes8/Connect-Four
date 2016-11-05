/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

import java.awt.Color;

public class Player {
	
	/**The Player that will be placed in the board, contains properties of the player*/
	
	/**Unique Player number*/
	private int playerNumber;
	
	/**Name of player*/
	private String name;
	
	/**Contains String that has color of disc*/
	private String colorDiscName;
	
	/**Contains color of the disc*/
	private Color colorDisc;
	
	/**Contains color of outline of disc*/
	private Color colorDiscOutline;
	
	/**Last placed disc onto board is saved as x and y coordinates [0] is x, [1] is y
	 * has values of -1 if they haven't placed a disc on to board yet*/
	private int[] lastPlacedDisc;
	
	/**Makes a default player*/
	public Player(){
		playerNumber = 0;
		name = "Player";
		lastPlacedDisc = new int[2];
		gameReset();
		colorDiscName = "Black";
		colorDisc = new Color(0,0,0);
		colorDisc = new Color(50,50,50);
	}
	
	/**Given a Player number, sets default name based on what player number they are*/
	public Player(int playerNumber){
		this.playerNumber = playerNumber;
		name = "Player " + playerNumber;
		
		lastPlacedDisc = new int[2];
		gameReset();
		
		colorDiscName = "Black";
		colorDisc = new Color(0,0,0);
		colorDisc = new Color(50,50,50);
	}
	
	/**Can set a player number, player name, and color to the Player, doesn't use defaults*/
	public Player(int player, String playerName, String colorName, Color playerDiscColor, Color playerDiscOutline){
		name = playerName;
		playerNumber = player;
		
		lastPlacedDisc = new int[2];
		gameReset();
		
		colorDiscName = colorName;
		colorDisc = playerDiscColor;
		colorDiscOutline = playerDiscOutline;
	}
	
	/**Returns the player's name*/
	public String getName(){
		return name;
	}
		
	/**Are same player if same playerNumber, name and colorDisc
	 * @param is Player you want to check if is the same*/
	@Override
	public boolean equals(Object other){
		if(other == null){
			return false;
		}
		if(other instanceof Player){
			Player otherPlayer = (Player) other;
			if(playerNumber != otherPlayer.getPlayerNumber()){
				return false;
			}
			if(!name.equals(otherPlayer.getName())){
				return false;
			}
			if(playerNumber != otherPlayer.getPlayerNumber()){
				return false;
			}
			if(!colorDiscName.equals(otherPlayer.getColorDisc())){
				return false;
			}
			return true;
		}
		return false;
	}

	/**Returns String that contains the player's disc*/
	public String getColorDisc(){
		return colorDiscName;
	}
	
	/**Returns Player number*/
	public int getPlayerNumber(){
		return playerNumber;
	}
	
	/**When inserting, and you are using the player, you should set a new last placed disc*/
	public void setLastPlacedDisc(int x, int y){
		lastPlacedDisc[0] = x;
		lastPlacedDisc[1] = y;
	}
	/**Returns coordinates to the new last placed disc*/
	public int[] getLastPlacedDisc(){
		return lastPlacedDisc;
	}
	
	/**Called when a new game has started*/
	public void gameReset(){
		lastPlacedDisc[0] = -1;
		lastPlacedDisc[1] = -1;
	}
	
	/**Returns color of the player*/
	public Color getPlayerColor(boolean outline){
		if(outline){
			return colorDiscOutline;
		}
		return colorDisc;
	}
	
	@Override
	/**Override of toString(), gives a description of the player*/
	public String toString(){
		return "Player Name: " + name + " Player Number: " + playerNumber + " Color Disc: " + colorDiscName;
	}
}

