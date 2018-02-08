import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.HashSet;

public class Maze {

	private final int SIZE = 3;
	private Spot[][] spots = new Spot[SIZE][SIZE];
	
	//ArrayList<String> visited = new ArrayList<String>();
	HashSet<String> visited = new HashSet<String>();


	public static void main(String[] args) {
	
		Maze m = new Maze();
		// Maze 1
		/*m.addSpot(0, 0, new Move[]{Move.N, Move.S});
		m.addSpot(0, 1, new Move[]{Move.SE, Move.S});
		m.addSpot(0, 2, new Move[]{Move.N, Move.S, Move.SW});
		m.addSpot(1, 0, new Move[]{Move.NE, Move.E});
		m.addSpot(1, 1, new Move[]{Move.F});
		m.addSpot(1, 2, new Move[]{Move.N, Move.SW});
		m.addSpot(2, 0, new Move[]{Move.W, Move.SE});
		m.addSpot(2, 1, new Move[]{Move.N, Move.SE, Move.NW});
		m.addSpot(2, 2, new Move[]{Move.S, Move.SW, Move.W});
		*/
		
		// Maze 2
		m.addSpot(0, 0, new Move[]{Move.N, Move.S, Move.SW});
		m.addSpot(0, 1, new Move[]{Move.NE, Move.E, Move.S, Move.SW});
		m.addSpot(0, 2, new Move[]{Move.N, Move.NE, Move.S, Move.SW});
		m.addSpot(1, 0, new Move[]{Move.NE, Move.S, Move.SW, Move.NW});
		m.addSpot(1, 1, new Move[]{Move.F});
		m.addSpot(1, 2, new Move[]{Move.NE, Move.SE, Move.W});
		m.addSpot(2, 0, new Move[]{Move.N, Move.W, Move.NW});
		m.addSpot(2, 1, new Move[]{Move.NE, Move.SW, Move.NW});
		m.addSpot(2, 2, new Move[]{Move.N, Move.E, Move.NW});

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
			int moveCount = sequence.size() - 1;
			
			if (zero.finished() || one.finished()) {
				printLinkedList(sequence);
				break;
			} else if (moveCount % 2 == 0) {
				ArrayList<Move> moves = validMoves(one.spot.getMoves(), zero.getRow(), zero.getCol(), one);
				
				if (moves.isEmpty()) {
					LinkedList<BoardState> newSequence = (LinkedList<BoardState>) sequence.clone();
					Penny newZero = zero.clone();
					Penny newOne = one.clone();	
					newSequence.add(new BoardState(newZero, newOne));
					q.add(newSequence);
				} else {
					for (Move move : moves) {
						LinkedList<BoardState> newSequence = (LinkedList<BoardState>) sequence.clone();
						Penny newZero = zero.clone();
						Penny newOne = one.clone();							
						newZero.moveTo(spots[move.rowOffSet(newZero.getRow())][move.colOffSet(newZero.getCol())]);
						newSequence.add(new BoardState(newZero, newOne));
						
						if (!visited.contains(newSequence.peekLast().toString())) {
							q.add(newSequence);
							visited.add(newSequence.peekLast().toString());
						}
					}
				}
			} else {
				ArrayList<Move> moves = validMoves(zero.spot.getMoves(), one.getRow(), one.getCol(), zero);
				if (moves.isEmpty()) {
					LinkedList<BoardState> newSequence = (LinkedList<BoardState>) sequence.clone();
					Penny newZero = zero.clone();
					Penny newOne = one.clone();	
					newSequence.add(new BoardState(newZero, newOne));
					q.add(newSequence);
				} else {
					for (Move move : moves) {
						LinkedList<BoardState> newSequence = (LinkedList<BoardState>) sequence.clone();
						Penny newZero = zero.clone();
						Penny newOne = one.clone();
						newOne.moveTo(spots[move.rowOffSet(newOne.getRow())][move.colOffSet(newOne.getCol())]);
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
	
	private ArrayList<Move> validMoves(ArrayList<Move> moves, int row, int col, Penny p) {
		ArrayList<Move> validMoves = new ArrayList<Move>();
		for (Move move: moves) {
			if (validMove(move, row, col, p)) 
				validMoves.add(move);
		}
		
		return validMoves;
	}
	
	private boolean validMove(Move move, int row, int col, Penny p) {
	
		int moveRowLoc = move.rowOffSet(row);
		int moveColLoc = move.colOffSet(col);
		
		int otherPennyRow = p.spot.row;
		int otherPennyCol = p.spot.col;
		
		if (moveRowLoc > 2 || moveRowLoc < 0)
			return false;
		if (moveColLoc > 2 || moveColLoc < 0) 
			return false;
		if (otherPennyRow == moveRowLoc && otherPennyCol == moveColLoc)
			return false;
		
		return true;
	}
	
	void addSpot(int row, int col, Move[] moves) {
		spots[row][col] = new Spot(new ArrayList<Move>(Arrays.asList(moves)), row, col);
	}
	
	void printMaze() {
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
	
		private ArrayList<Move> moves;
		int row;
		int col;
	
		Spot(ArrayList<Move> moves, int row, int col) { 
			this.moves = moves; 
			this.row = row;
			this.col = col;
		}
	
	
		ArrayList<Move> getMoves() { return moves; }
		
		public String toString() {
			String rep = "{";
			for (int i = 0; i < moves.size() - 1; i++) {
				rep += moves.get(i) + ", ";
			}
			rep += moves.get(moves.size() - 1) + "}";
			return rep;
		}
		
		boolean finish() { return this.moves.get(0) == Move.F; }
		
	}
	
	class Penny {
	
		Spot spot;
		
		Penny(Spot s) { 
			this.spot = s; 
		}
		
		void moveTo(Spot s) { this.spot = s; }
		
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

enum Move {
	
	N("N", -1, 0),
	NE("NE", -1, 1),
	E("E", 0, 1),
	SE("SE", 1, 1),
	S("S", 1, 0),
	SW("SW", 1, -1),
	W("W", 0, -1),
	NW("NW", -1, -1),
	F("F", 0, 0);
	
	String move;
	int row;
	int col;
	
	Move(String m, int row, int col) { 
		this.move = m; 
		this.row = row;
		this.col = col;
	}
	
	int rowOffSet(int r) { return r + row; }
	int colOffSet(int c) { return c + col; }
}