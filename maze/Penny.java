/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/

public class Penny {
	
	public String name;

	public Position p;
	
	public Penny(int x, int y, String name) { 

		this.p = new Position(x,y);
		this.name = name;
	}

	public boolean isFinished() {

		if(p.x == 1 && p.y == 1) {
			return true;
		}
		return false;
	}
}