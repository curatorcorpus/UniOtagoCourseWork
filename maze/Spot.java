/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/
import java.util.ArrayList;
import java.util.List;

public class Spot {

	private String[] directions;

	private Position p;

	public Spot(int x, int y) {
		
		this.p = new Position(x,y);
	}

	public List<Direction> computeValidMoves(Penny penny) {

		List<Direction> vaildMoves = new ArrayList<Direction>();

		Position p = penny.p;

		for(int i = 0; i < directions.length; i++) {
			String moveName = directions[i];
			Position newPos = getNewLocation(moveName, p);
			if(newPos != null) {
				vaildMoves.add(new Direction(moveName, new Position(newPos.x, newPos.y),penny));
			}
		}
		return vaildMoves;	
	}

	public void addDirections(String[] directions) {

		this.directions = directions;
	}

	public Position getNewLocation(String moveName, Position p) {

		Position newPos = new Position(p.x, p.y);

		switch(moveName) {
			case "N":
				newPos.x += Move.N.xOffset;
				newPos.y += Move.N.yOffset;
				break;
			case "S":
				newPos.x += Move.S.xOffset;
				newPos.y += Move.S.yOffset;			
				break;
			case "E":
				newPos.x += Move.E.xOffset;
				newPos.y += Move.E.yOffset;			
				break;
			case "W":
				newPos.x += Move.W.xOffset;
				newPos.y += Move.W.yOffset;			
				break;												
			case "NE":
				newPos.x += Move.NE.xOffset;
				newPos.y += Move.NE.yOffset;
				break;
			case "NW":
				newPos.x += Move.NW.xOffset;
				newPos.y += Move.NW.yOffset;			
				break;
			case "SE":
				newPos.x += Move.SE.xOffset;
				newPos.y += Move.SE.yOffset;			
				break;
			case "SW":
				newPos.x += Move.SW.xOffset;
				newPos.y += Move.SW.yOffset;			
				break;
		}

		if(isValidMove(newPos.x, newPos.y)) {
			return newPos;
		} /*else {
			System.out.println("invalid move "+  newPos.x + " " + newPos.y);
		}*/
		return null;
	}

	private boolean isValidMove(int newX, int newY) {

		if(newX < 0 || Maze.SIZE <= newX) return false;
		if(newY < 0 || Maze.SIZE <= newY) return false;
		return true;
	}
	
	public String toString() {

		return p.x + "," + p.y;
	}
}