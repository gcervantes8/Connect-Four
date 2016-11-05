/**CS3331 Mondays and Wednesdays 1:30-3:20 PM
//@author Gerardo Cervantes
//Assignment: HW #5 Implement P2P, Object Oriented Design
//Instructor: Yoonsik Cheon
//Last modification: 07/29/2016
//Purpose: Implement connect four*/

package connectFour;

public interface Strategy{
		
	/**Part of the strategy design*/
	
	/**The AI's algorithm that returns the slot number the AI wants to
	 * drop the disc onto*/
	abstract public int algorithm();
	
	/**The AI's name, which the player will see*/
	abstract public String getStrategyName();
}
