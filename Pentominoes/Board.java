/**
* @Author: Jung Woo (Noel) Park
* Student ID: 1162424.
*/
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Board {

	private static final int DEFAULT_PENTS = 12;
	private List<Pentomino> pents;

	private int width, height;
	private int noOfValidSquares;
	private int noOfPlacements;

	private String stringBoard;
	private String[][] board;

	private List<String> typeRequirement;
	private List<Point> restrictedIndices;
	private List<Pentomino> availablePlacements;
	private Hashtable<Pentomino.Type, Integer> pentoTypeToECIDLookup;
	private Hashtable<String, Integer> pointToSquareIDLookup;

	/**
	*	Constructor for board representation.
	*/
	public Board(String board, int width, int height, List<Pentomino> pents, String[] requirement, boolean isRestricted) {

		this.width = width;
		this.height = height;
		this.stringBoard = board;
		this.board = new String[height][width];
		this.availablePlacements = new ArrayList<Pentomino>();
		this.pents = pents;
		this.restrictedIndices = new ArrayList<Point>();
		this.typeRequirement = new ArrayList<String>();

		// apply pentominoes type requriement filter.
		if(isRestricted) {
			this.typeRequirement = Arrays.asList(requirement);
			List<Pentomino> restrictedList = new ArrayList<Pentomino>();

			for(String t : typeRequirement) {
				for(Pentomino p : pents) {

					if(t.equals(p.getType().name().toLowerCase())) {
						restrictedList.add(p);
					}
				}
			}
			this.pents = restrictedList;
		}

		setupMatrix();
		generateIndexToSquareLookup();
		generatePentoTypeToECIDLookup();
	}

	/**
	*	Method for setting up a matrix of the input string.
	*/
	private void setupMatrix() {

		String[] sBoard = stringBoard.split("");
		int elements = 0;
		for(int row = 0; row < height; row++) {
			for(int col = 0; col < width; col++) {
				String symbol = sBoard[elements++];
				board[row][col] = symbol;

				if(symbol.equals("*")) {
					restrictedIndices.add(new Point(col,row));
				} else {
					++noOfValidSquares;
				}
			}
		}
	}


	public String getMatrix() {

		StringBuilder sb = new StringBuilder();
		for(int row = 0; row < height; row++) {
			for(int col = 0; col < width; col++) {
				sb.append(board[row][col]);
			}
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length()-1);

		return sb.toString();
	}

	public boolean checkBoardValidity() {
		
		return (noOfValidSquares % 5) == 0;
	}

	/**
	*	Method for printing current board matrix.
	*/
	public void printMatrix() {

		for(int row = 0; row < height; row++) {
			for(int col = 0; col < width; col++) {
				System.out.print(board[row][col]);
			}
			System.out.println();
		}
	}

	/**
	*	Method for printing out the string representation of the board.
	*/
	public String toString() {

		return width + "x" + height + "\n" + stringBoard;
	}

	public String getSolution(Deque<Node> solution) {

		if(solution.size() == 0) {
			return "No Solution";
		}
		
		ArrayList<Pentomino> rowSolution = new ArrayList<Pentomino>();

		for(Node n : solution) {
			rowSolution.add(availablePlacements.get(n.row-1)); // get solutions from rows in ecm.
		}

		// Iterate through solution and generate string representation.
		for(Pentomino p : rowSolution) {
			// obtain indices of pentominoes shape.
			Point[] shape = p.getShape();
			Pentomino.Type type = p.getType();

			Point idx0 = shape[0];
			Point idx1 = shape[1];
			Point idx2 = shape[2];
			Point idx3 = shape[3];
			Point idx4 = shape[4];
			/*System.out.println(idx0.y + "," +idx0.x+" "+
							idx1.y+","+idx1.x+" "+
							idx2.y+","+idx2.x+" "+
							idx3.y+","+idx3.x+" "+
							idx4.y+","+idx4.x);*/
			// add it to matrix
			board[idx0.y][idx0.x] = type.name();
			board[idx1.y][idx1.x] = type.name();
			board[idx2.y][idx2.x] = type.name();
			board[idx3.y][idx3.x] = type.name();
			board[idx4.y][idx4.x] = type.name();
		}
		return getMatrix();
	}

	/**
	*	Method that loops through all pentominoes pieces and checks the numbers of 
	* 	times each piece can be placed on the board.
	*/
	public void calculateTotalPlacements() {
		
		for(Pentomino p : pents) {

			// obtain offsets of pentominoes shape.
			Point[] shape = p.getShape();
			Point offset1 = shape[1];
			Point offset2 = shape[2];
			Point offset3 = shape[3];
			Point offset4 = shape[4];		

			// iterate through matrix.
			for(int row = 0; row < height; row++) {
				for(int col = 0; col < width; col++) {

					// obtain index.
					Point start = new Point(col, row);
					Point idx1 = start.add(offset1);
					Point idx2 = start.add(offset2);
					Point idx3 = start.add(offset3);
					Point idx4 = start.add(offset4);

					if(checkBounds(start,idx1,idx2,idx3,idx4)) {
						
						Point[] newShapeIdx = new Point[5];
						newShapeIdx[0] = start;
						newShapeIdx[1] = idx1;
						newShapeIdx[2] = idx2;
						newShapeIdx[3] = idx3;
						newShapeIdx[4] = idx4;

						noOfPlacements++;
						availablePlacements.add(new Pentomino(newShapeIdx, p.getType())); // contains new indices for creating exact cover matrix.
					}
				}
			}
		}
	}

	public boolean[][] generateExactCoverProblem() {

		int ecWidth = DEFAULT_PENTS+width*height-restrictedIndices.size();
		int ecHeight = noOfPlacements;

		if(typeRequirement.size() != 0) {
			ecWidth = typeRequirement.size()+width*height-restrictedIndices.size();
		}

		boolean[][] ecProblemMatrix = new boolean[ecHeight][ecWidth];
		for(int row = 0; row < ecHeight; row++) {

			Pentomino p = availablePlacements.get(row);
			Point[] shape = p.getShape();

			// get the pentomino type and extract index in exact cover matrix.
			Pentomino.Type t = p.getType();
			int ecmPentoConstraint = pentoTypeToECIDLookup.get(t);

			// get the index of available squares based on available pentominoes placement.
			Point idx1 = shape[0];
			Point idx2 = shape[1];
			Point idx3 = shape[2];
			Point idx4 = shape[3];
			Point idx5 = shape[4];

			// lookup square ID for a pentomino square index.
			int square1 = pointToSquareIDLookup.get(idx1.y+" "+idx1.x);
			int square2 = pointToSquareIDLookup.get(idx2.y+" "+idx2.x);
			int square3 = pointToSquareIDLookup.get(idx3.y+" "+idx3.x);
			int square4 = pointToSquareIDLookup.get(idx4.y+" "+idx4.x);
			int square5 = pointToSquareIDLookup.get(idx5.y+" "+idx5.x);

			// assign to exact cover matrix.
			ecProblemMatrix[row][ecmPentoConstraint] = true;
			ecProblemMatrix[row][square1] = true;
			ecProblemMatrix[row][square2] = true;
			ecProblemMatrix[row][square3] = true;
			ecProblemMatrix[row][square4] = true;
			ecProblemMatrix[row][square5] = true;
		}
		return ecProblemMatrix;
	}

	/**
	*
	*/
	private void generateIndexToSquareLookup() {

		this.pointToSquareIDLookup = new Hashtable<String, Integer>();

		int squareID = DEFAULT_PENTS;

		if(typeRequirement.size() != 0) {
			squareID = typeRequirement.size();
		}
		for(int row = 0; row < height; row++) {
			for(int col = 0; col < width; col++) {
				// For restricted indices, don't give square ID.
				if(restrictedIndices.size() != 0) {
					boolean isInvalid = false;
					for(Point p : restrictedIndices) {
						int x = p.x;
						int y = p.y;

						// if indices match, then its restricted.
						if(x == col && y == row) {
							isInvalid = true;
							break;
						}
					}
					if(isInvalid) {
						continue;
					} else {
						pointToSquareIDLookup.put((row+" "+col),squareID++);
					}
				} else {
					pointToSquareIDLookup.put((row+" "+col),squareID++);
				}
			}
		}
	}

	/**
	*
	*/
	private void generatePentoTypeToECIDLookup() {

		this.pentoTypeToECIDLookup = new Hashtable<Pentomino.Type, Integer>();
		int i = 0;
		
		if(typeRequirement.size() == 0) {
			for(Pentomino.Type t : Pentomino.Type.values()) {
				pentoTypeToECIDLookup.put(t,i++);
			}
		} else {
			for(String s : typeRequirement) {
				pentoTypeToECIDLookup.put(Pentomino.getEnumType(s),i++);
			}
		}
	}

	/**
	*	Determines if the pentominoes shape stays inside the board BB.
	*/
	private boolean checkBounds(Point idx1,Point idx2,Point idx3,Point idx4,Point idx5) {

		if(idx1.x < 0 || idx1.x >= width ||
		   idx1.y < 0 || idx1.y >= height) {
			return false;
		}
		if(idx2.x < 0 || idx2.x >= width ||
		   idx2.y < 0 || idx2.y >= height) {
			return false;
		}
		if(idx3.x < 0 || idx3.x >= width ||
		   idx3.y < 0 || idx3.y >= height) {
			return false;
		}
		if(idx4.x < 0 || idx4.x >= width ||
		   idx4.y < 0 || idx4.y >= height) {
			return false;
		}
		if(idx5.x < 0 || idx5.x >= width ||
		   idx5.y < 0 || idx5.y >= height) {
			return false;
		}
		for(Point p : restrictedIndices){
			if(idx1.x == p.x && idx1.y == p.y) return false;
			if(idx2.x == p.x && idx2.y == p.y) return false;
			if(idx3.x == p.x && idx3.y == p.y) return false;
			if(idx4.x == p.x && idx4.y == p.y) return false;
			if(idx5.x == p.x && idx5.y == p.y) return false;
		}
		return true;
	}

	/**
	*	Method for placing a pentominoes piece on the matrix board representation.
	*/
	private void placeOnBoard(Point idx1,Point idx2,Point idx3,Point idx4,Point idx5, Pentomino.Type type) {

		if(checkBounds(idx1,idx2,idx3,idx4,idx5)) {
			board[idx1.y][idx1.x] = type.name();
			board[idx2.y][idx2.x] = type.name();
			board[idx3.y][idx3.x] = type.name();
			board[idx4.y][idx4.x] = type.name();
			board[idx5.y][idx5.x] = type.name();
		}
	}

	private void printPlacements(Point[] shape) {

		for(int i = 0; i < shape.length; i++) {
			System.out.print(shape[i]+" ");
		}
		System.out.println();
	}
/*
	private void printHashTable(Hashtable<T, E> table) {

		Set<T> keys = table.keySet();
		 
		// Obtaining iterator over set entries
		Iterator<T> itr = keys.iterator();

		while(itr.hasNext()) {
			System.out.println(itr.next());
		}		
	}*/
}