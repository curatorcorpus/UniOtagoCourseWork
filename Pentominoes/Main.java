/**
* @Author: Jung Woo (Noel) Park
* Student ID: 1162424.
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

/**
*	Main method class. This pentominoes solver assumes the puzzle will 
*	be a size of 6x10.
*/
public class Main {

	private static final String FILENAME = "pentominoes"; // looks in current directory.
	private static List<Board> boards;
	private static List<Pentomino> pents;
	private static ArrayList<Deque<Node>> solutions;

	/**
	*	Main Method.
	*/
	public static void main(String[] args) {

		boards = new ArrayList<Board>();
		pents = new ArrayList<Pentomino>();
		solutions = new ArrayList<Deque<Node>>();

		readDataFile();
		readBoard();

		for(Board b : boards) {
			b.calculateTotalPlacements();
			boolean[][] ecm = b.generateExactCoverProblem();
			
			DancingLinksX dlx = new DancingLinksX(ecm);

			dlx.searchSolution(0);
			Deque<Node> solution = dlx.getSolution();
			solutions.add(solution);
			dlx.clearSolution();
			DancingLinksX.aSolution = new ArrayDeque<Node>();
		}

		Board b1 = boards.get(0);
		Board b2 = boards.get(1);

		b1.printSolution(solutions.get(0));
		System.out.println();
		b2.printSolution(solutions.get(1));
	}

	/**
	*	Method for reading in the pentomino data file.
	*/
	public static void readDataFile() {

		try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {

			String line;
			while ((line = br.readLine()) != null) {
				if(line.equals("")) { 
					continue;
				}

				String[] pentoData = line.replaceAll(",", " ").split(" ");
				String pentoType = pentoData[0];

				Point[] pentoShape = new Point[5];
				for(int i = 1; i < pentoData.length; i+=2) {
					int x = Integer.parseInt(pentoData[i]);
					int y = Integer.parseInt(pentoData[i+1]);

					pentoShape[i/2] = new Point(x,y);
				}
				pents.add(new Pentomino(pentoShape, Pentomino.Type.valueOf(pentoType)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	*	Method for reading in the puzzle to solve via text file.
	*/
	public static void readBoard() {

		boolean isRestricted = false;

		int height = 0;
		int width = 0;

		StringBuilder board = new StringBuilder();
		String requirement = "";

		Scanner sc = new Scanner(System.in);
		while(sc.hasNextLine()) {

			String line = sc.nextLine();
			// TODO: not the most generic way to read in pentomino requirement.
			if(line.matches(".*[a-z].*")) {
				requirement = line;
				isRestricted = true;
				height = 0;
			}
			else if(line.contains(".") || line.contains("*")) {

				board.append(line);
				++height;
				width = line.length();
			}
			else {
				// This mean a board is finished.
				if(line.contains(" ") || line.contains("")) {
					// check that the board string is not empty.
					if(board.toString().contains(".") || board.toString().contains("*")) {
						boards.add(new Board(board.toString(),width,height, pents, requirement.split(" "),isRestricted));
					}
				}

				width = 0;
				height = 0;
				board = new StringBuilder();
				requirement = "";
				isRestricted = false;
			}
		}
	//	boards.add(new Board(board.toString(),width,height, pents,requirement.split(" "),isRestricted));
	}

	/**
	*
	*/
	private static void printExactCoverMatrixProblem(boolean[][] matrix) {
		for(int row = 0; row < matrix.length; row++) {
			for(int col = 0; col < matrix[0].length; col++) {
				if(matrix[row][col]) {
					System.out.print(1);
				}else {
					System.out.print(0);
				}
			}
			System.out.println();
		}
	}

	/**
	*	Method for printing all pentominoes we read from the data file.
	*/
	private static void printAllPentominoes() {

		for(Pentomino pent : pents) {
			System.out.println(pent);
			System.out.println();
		}
	}

	/**
	*	Method for printing only selected pentomino from the data file.
	*	Mainly for testing purposes.
	*/
	private static void printRestrictedPentominoes(Pentomino.Type type) {

		for(Pentomino pent : pents) {
			if(pent.getType() == type) {
				System.out.println(pent);
				System.out.println();
			}
		}
	}
}
