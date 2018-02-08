/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/
import java.util.ArrayList;
import java.util.List;

public class Spot {

	private String[] moves;

	private Position p;

	public Spot(int x, int y) {
		
		this.p = new Position(x,y);
	}

	public List<Move> computeValidMoves(Penny p0, Penny p1, boolean isZeroMove) {

		List<Move> vaildMoves = new ArrayList<Move>();
		
		Position currentPos;
		if(isZeroMove) {
			currentPos = p0.p;
		} else {
			currentPos = p1.p;
		}
		//System.out.println(currentPos);
		for(int i = 0; i < moves.length; i++) {
			String moveName = moves[i];
			Position newPos = getNewLocation(moveName, currentPos, p0, p1, isZeroMove);
			if(newPos != null) {
				Penny newPenny;
				if(isZeroMove){
					newPenny = p0.clone();
				}	else {
					newPenny = p1.clone();
				}

				newPenny.p.x = newPos.x;
				newPenny.p.y = newPos.y;

				if(isZeroMove) {
					vaildMoves.add(new Move(moveName,newPenny,p1,isZeroMove));
				} else {

					vaildMoves.add(new Move(moveName,p0,newPenny,isZeroMove));
				}
			}
		}
		return vaildMoves;
	}

	public void addMoves(String[] moves) {

		this.moves = moves;
	}

	public Position getNewLocation(String moveName, Position p, Penny p0, Penny p1, boolean isZeroMove) {

		Position newPos = new Position(p.x, p.y);

		switch(moveName) {
			case "N":
				newPos.x += Move.Moves.N.xOffset;
				newPos.y += Move.Moves.N.yOffset;
				break;
			case "S":
				newPos.x += Move.Moves.S.xOffset;
				newPos.y += Move.Moves.S.yOffset;			
				break;
			case "E":
				newPos.x += Move.Moves.E.xOffset;
				newPos.y += Move.Moves.E.yOffset;			
				break;
			case "W":
				newPos.x += Move.Moves.W.xOffset;
				newPos.y += Move.Moves.W.yOffset;			
				break;												
			case "NE":
				newPos.x += Move.Moves.NE.xOffset;
				newPos.y += Move.Moves.NE.yOffset;
				break;
			case "NW":
				newPos.x += Move.Moves.NW.xOffset;
				newPos.y += Move.Moves.NW.yOffset;			
				break;
			case "SE":
				newPos.x += Move.Moves.SE.xOffset;
				newPos.y += Move.Moves.SE.yOffset;			
				break;
			case "SW":
				newPos.x += Move.Moves.SW.xOffset;
				newPos.y += Move.Moves.SW.yOffset;			
				break;
		}

		if(isValidMove(newPos.x, newPos.y, p0,p1, isZeroMove)) {
			return newPos;
		} /*else {
			System.out.println("invalid move "+  newPos.x + " " + newPos.y);
		}*/
		return null;
	}

	private boolean isValidMove(int newX, int newY,Penny p0, Penny p1, boolean isZeroMove) {

		if(newX < 0 || Maze.SIZE <= newX) return false;
		if(newY < 0 || Maze.SIZE <= newY) return false;
		if(isZeroMove) {
			if(newX == p1.p.x && newY == p1.p.y) {
				return false;
			}
		} else {
			if(newX == p0.p.x && newY == p0.p.y) {
				return false;
			}
		}
		return true;
	}

	public String toString() {

		return p.x + "," + p.y;
	}
}