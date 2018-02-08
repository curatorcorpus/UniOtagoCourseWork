import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.HashSet;

public class FalveyMaze {

	private final int SIZE = 3;
	private Spot[][] spots = new Spot[SIZE][SIZE];
	
	//ArrayList<String> visited = new ArrayList<String>();
	HashSet<String> visited = new HashSet<String>();


	public static void main(String[] args) {
	
		FalveyMaze m = new FalveyMaze();
		// FalveyMaze 1
		m.addSpot(0, 0, new FalveyMove[]{FalveyMove.N, FalveyMove.S});
		m.addSpot(0, 1, new FalveyMove[]{FalveyMove.SE, FalveyMove.S});
		m.addSpot(0, 2, new FalveyMove[]{FalveyMove.N, FalveyMove.S, FalveyMove.SW});
		m.addSpot(1, 0, new FalveyMove[]{FalveyMove.NE, FalveyMove.E});
		m.addSpot(1, 1, new FalveyMove[]{FalveyMove.F});
		m.addSpot(1, 2, new FalveyMove[]{FalveyMove.N, FalveyMove.SW});
		m.addSpot(2, 0, new FalveyMove[]{FalveyMove.W, FalveyMove.SE});
		m.addSpot(2, 1, new FalveyMove[]{FalveyMove.N, FalveyMove.SE, FalveyMove.NW});
		m.addSpot(2, 2, new FalveyMove[]{FalveyMove.S, FalveyMove.SW, FalveyMove.W});
		
		// FalveyMaze 2
		/*m.addSpot(0, 0, new FalveyMove[]{FalveyMove.N, FalveyMove.S, FalveyMove.SW});
		m.addSpot(0, 1, new FalveyMove[]{FalveyMove.NE, FalveyMove.E, FalveyMove.S, FalveyMove.SW});
		m.addSpot(0, 2, new FalveyMove[]{FalveyMove.N, FalveyMove.NE, FalveyMove.S, FalveyMove.SW});
		m.addSpot(1, 0, new FalveyMove[]{FalveyMove.NE, FalveyMove.S, FalveyMove.SW, FalveyMove.NW});
		m.addSpot(1, 1, new FalveyMove[]{FalveyMove.F});
		m.addSpot(1, 2, new FalveyMove[]{FalveyMove.NE, FalveyMove.SE, FalveyMove.W});
		m.addSpot(2, 0, new FalveyMove[]{FalveyMove.N, FalveyMove.W, FalveyMove.NW});
		m.addSpot(2, 1, new FalveyMove[]{FalveyMove.NE, FalveyMove.SW, FalveyMove.NW});
		m.addSpot(2, 2, new FalveyMove[]{FalveyMove.N, FalveyMove.E, FalveyMove.NW});
*/
		m.findShortestPath();
	}

	void findShortestPath() {
		Penny zero = new Penny(spots[0][0]);
		Penny one = new Penny(spots[2][2]);
		goToMiddle(zero, one);
	}
	
	void goToMiddle(Penny z, Penny o) {
		PriorityQueue<LinkedList<BoardState>> q = new PriorityQueue<LinkedList<BoardState>>(100, new StateSequenceComparator());
		BoardState initialState = new BoardState(z, o);
		LinkedList<BoardState> initialSequence = new LinkedList<BoardState>();
		initialSequence.add(initialState);
		q.add(initialSequence);
		
		while (!q.isEmpty()) {

			LinkedList<BoardState> sequence = q.poll();
			Penny zero = sequence.peekLast().zero;
			Penny one = sequence.peekLast().one;
			int FalveyMoveCount = sequence.size() - 1;
			
			if (zero.finished() || one.finished()) {
				printLinkedList(sequence);
				break;
			} else if (FalveyMoveCount % 2 == 0) {
				ArrayList<FalveyMove> FalveyMoves = validFalveyMoves(one.spot.getFalveyMoves(), zero.getRow(), zero.getCol(), one);
				
				if (FalveyMoves.isEmpty()) {
					LinkedList<BoardState> newSequence = (LinkedList<BoardState>) sequence.clone();
					Penny newZero = zero.clone();
					Penny newOne = one.clone();	
					newSequence.add(new BoardState(newZero, newOne));
					q.add(newSequence);
				} else {
					for (FalveyMove FalveyMove : FalveyMoves) {
						LinkedList<BoardState> newSequence = (LinkedList<BoardState>) sequence.clone();
						Penny newZero = zero.clone();
						Penny newOne = one.clone();							
						newZero.FalveyMoveTo(spots[FalveyMove.rowOffSet(newZero.getRow())][FalveyMove.colOffSet(newZero.getCol())]);
						newSequence.add(new BoardState(newZero, newOne));
						
						if (!visited.contains(newSequence.peekLast().toString())) {
							q.add(newSequence);
							visited.add(newSequence.peekLast().toString());
						}
					}
				}
			} else {
				ArrayList<FalveyMove> FalveyMoves = validFalveyMoves(zero.spot.getFalveyMoves(), one.getRow(), one.getCol(), zero);
				if (FalveyMoves.isEmpty()) {
					LinkedList<BoardState> newSequence = (LinkedList<BoardState>) sequence.clone();
					Penny newZero = zero.clone();
					Penny newOne = one.clone();	
					newSequence.add(new BoardState(newZero, newOne));
					q.add(newSequence);
				} else {
					for (FalveyMove FalveyMove : FalveyMoves) {
						LinkedList<BoardState> newSequence = (LinkedList<BoardState>) sequence.clone();
						Penny newZero = zero.clone();
						Penny newOne = one.clone();
						newOne.FalveyMoveTo(spots[FalveyMove.rowOffSet(newOne.getRow())][FalveyMove.colOffSet(newOne.getCol())]);
						newSequence.add(new BoardState(newZero, newOne));
						
						if (!visited.contains(newSequence.peekLast().toString())) {
							q.add(newSequence);
							visited.add(newSequence.peekLast().toString());
						}
					}
				}
			}
		}
		
	}
	
	void printLinkedList(LinkedList<BoardState> visited) {
		System.out.println(visited.size());
		for (BoardState s : visited)
			System.out.println(s);
	}
	
	private ArrayList<FalveyMove> validFalveyMoves(ArrayList<FalveyMove> FalveyMoves, int row, int col, Penny p) {
		ArrayList<FalveyMove> validFalveyMoves = new ArrayList<FalveyMove>();
		for (FalveyMove FalveyMove: FalveyMoves) {
			if (validFalveyMove(FalveyMove, row, col, p)) 
				validFalveyMoves.add(FalveyMove);
		}
		
		return validFalveyMoves;
	}
	
	private boolean validFalveyMove(FalveyMove FalveyMove, int row, int col, Penny p) {
	
		int FalveyMoveRowLoc = FalveyMove.rowOffSet(row);
		int FalveyMoveColLoc = FalveyMove.colOffSet(col);
		
		int otherPennyRow = p.spot.row;
		int otherPennyCol = p.spot.col;
		
		if (FalveyMoveRowLoc > 2 || FalveyMoveRowLoc < 0)
			return false;
		if (FalveyMoveColLoc > 2 || FalveyMoveColLoc < 0) 
			return false;
		if (otherPennyRow == FalveyMoveRowLoc && otherPennyCol == FalveyMoveColLoc)
			return false;
		
		return true;
	}
	
	void addSpot(int row, int col, FalveyMove[] FalveyMoves) {
		spots[row][col] = new Spot(new ArrayList<FalveyMove>(Arrays.asList(FalveyMoves)), row, col);
	}
	
	void printFalveyMaze() {
		System.out.println();
		for (Spot[] row : spots) {
			for (Spot spot : row) {
				System.out.printf("%-15s", spot);
			}
			System.out.println();
		}
		System.out.println();
	}

	class Spot {
	
		private ArrayList<FalveyMove> FalveyMoves;
		int row;
		int col;
	
		Spot(ArrayList<FalveyMove> FalveyMoves, int row, int col) { 
			this.FalveyMoves = FalveyMoves; 
			this.row = row;
			this.col = col;
		}
	
	
		ArrayList<FalveyMove> getFalveyMoves() { return FalveyMoves; }
		
		public String toString() {
			String rep = "{";
			for (int i = 0; i < FalveyMoves.size() - 1; i++) {
				rep += FalveyMoves.get(i) + ", ";
			}
			rep += FalveyMoves.get(FalveyMoves.size() - 1) + "}";
			return rep;
		}
		
		boolean finish() { return this.FalveyMoves.get(0) == FalveyMove.F; }
		
	}
	
	class Penny {
	
		Spot spot;
		
		Penny(Spot s) { 
			this.spot = s; 
		}
		
		void FalveyMoveTo(Spot s) { this.spot = s; }
		
		boolean finished() { return this.spot.finish(); }
		
		int getRow() { return this.spot.row; }
		int getCol() { return this.spot.col; }
		
		public String toString() {
			return "{" + spot.row + "," + spot.col + "}";
		}
		
		public Penny clone() {
			return new Penny(spot);
		}
	}
	
	class BoardState {
	
		Penny zero;
		Penny one;
		
		BoardState(Penny zero, Penny one) {
			this.zero = zero;
			this.one = one;
		}
		
		public String toString() {
			return zero + ":" + one;
		}	
	}
	
	class StateSequenceComparator implements Comparator<LinkedList<BoardState>> {

		public int compare(LinkedList<BoardState> o1, LinkedList<BoardState> o2) {
			if (o1.size() > o2.size()) {
				return 1;
			} else if (o1.size() < o2.size()) {
				return -1;
			} 
			return 0;
		} 
	}
}

enum FalveyMove {

	N("N", -1, 0),
	NE("NE", -1, 1),
	E("E", 0, 1),
	SE("SE", 1, 1),
	S("S", 1, 0),
	SW("SW", 1, -1),
	W("W", 0, -1),
	NW("NW", -1, -1),
	F("F", 0, 0);
	
	String FalveyMove;
	int row;
	int col;
	
	FalveyMove(String m, int row, int col) { 
		this.FalveyMove = m; 
		this.row = row;
		this.col = col;
	}
	
	int rowOffSet(int r) { return r + row; }
	int colOffSet(int c) { return c + col; }
}