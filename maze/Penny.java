/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/

public class Penny {
	
	private Position p;
	
	public Penny(Position p) { 

		this.p = p; 
	}
	
	public Penny clone() {
		
		return new Penny(p);
	}
}