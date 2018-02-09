/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/

public class Move {

	static enum Moves {

		N("N",	 0,-1),
		NE("NE", 1,-1),
		E("E", 	 1, 0),
		SE("SE", 1, 1),
		S("S", 	 0, 1),
		SW("SW",-1, 1),
		W("W",  -1, 0),
		NW("NW",-1,-1);

		public String moveName;
		public int xOffset;
		public int yOffset;

		Moves(String moveName, int xOffset, int yOffset) { 
			
			this.moveName = moveName; 
			this.xOffset  = xOffset;
			this.yOffset  = yOffset;
		}	
	}

	public Penny pennyMoved, pennyRelative;
	public String name;
	public boolean isZeroMove,hasStartedorFinished;

	public Move(String name, Penny pennyMoved, Penny pennyRelative, boolean isZeroMove) {

		this(name, pennyMoved, pennyRelative, isZeroMove, false);
	}

	public Move(String name, Penny pennyMoved, Penny pennyRelative, boolean isZeroMove, boolean hasStartedorFinished) {

		this.name = name;
		this.pennyMoved = pennyMoved;
		this.pennyRelative = pennyRelative;
		this.isZeroMove = isZeroMove;
		this.hasStartedorFinished = hasStartedorFinished;
	}

	public String toString() {

		/*if((p.x == -1) && (p.y == -1)) {
			return pennyMoved.name + " stayed at same position " + p.toString();
		}*/

		if(hasStartedorFinished) {
			if(!isZeroMove) {
				return pennyRelative.name + " has " + name;
			} else {
				return pennyMoved.name + " has " + name;
			}
		}

		if(!isZeroMove) {
			return pennyRelative.name + " moved " + name + " to position " + pennyRelative.toString();
		}

		return pennyMoved.name + " moved " + name + " to position " + pennyMoved.toString();
	}
}