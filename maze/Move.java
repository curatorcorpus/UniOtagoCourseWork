/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/

public enum Move {

	N("N",	 0,-1),
	NE("NE", 1,-1),
	E("E", 	 1, 0),
	SE("SE", 1, 1),
	S("S", 	 0, 1),
	SW("SW",-1, 1),
	W("W",  -1, 0),
	NW("NW",-1,-1),
	F("F",   0, 0);

	public String moveName;
	public int xOffset;
	public int yOffset;

	Move(String moveName, int xOffset, int yOffset) { 
		
		this.moveName = moveName; 
		this.xOffset  = xOffset;
		this.yOffset  = yOffset;
	}	
}