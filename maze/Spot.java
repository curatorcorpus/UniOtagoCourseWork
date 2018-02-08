/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/
import java.util.ArrayList;
import java.util.List;

public class Spot {

	private Position p;

	private List<Direction> vaildMoves;

	public Spot(int x, int y) {
		
		this.p = new Position(x,y);
		this.vaildMoves = new ArrayList<Direction>();
	}

	public void addMove(String moveName) {

		int newX = p.x, newY = p.y;
		switch(moveName) {

			case "N":
				newX += Move.N.xOffset;
				newY += Move.N.yOffset;
				break;
			case "S":
				newX += Move.S.xOffset;
				newY += Move.S.yOffset;			
				break;
			case "E":
				newX += Move.E.xOffset;
				newY += Move.E.yOffset;			
				break;
			case "W":
				newX += Move.W.xOffset;
				newY += Move.W.yOffset;			
				break;												
			case "NE":
				newX += Move.NE.xOffset;
				newY += Move.NE.yOffset;
				break;
			case "NW":
				newX += Move.NW.xOffset;
				newY += Move.NW.yOffset;			
				break;
			case "SE":
				newX += Move.SE.xOffset;
				newY += Move.SE.yOffset;			
				break;
			case "SW":
				newX += Move.SW.xOffset;
				newY += Move.SW.yOffset;			
				break;
		}

		if(isValidMove(newX, newY)) {

			vaildMoves.add(new Direction(moveName, new Position(newX, newY)));
		} /*else {
			System.out.println("invalid move "+  newX + " " + newY);
		}*/
	}

	private boolean isValidMove(int newX, int newY) {

		if(newX < 0 || Maze.SIZE <= newX) return false;
		if(newY < 0 || Maze.SIZE <= newY) return false;
		return true;
	}

	public List<Direction> getMoves() {

		return vaildMoves;
	}

	public String toString() {

		return p.x + "," + p.y;
	}
}