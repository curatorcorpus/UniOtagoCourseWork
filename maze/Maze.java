/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.HashSet;

public class Maze {

	private class StateSequenceComparator implements Comparator<LinkedList<Move>> {

		public int compare(LinkedList<Move> o1, LinkedList<Move> o2) {
			if (o1.size() > o2.size()) {
				return 1;
			} else if (o1.size() < o2.size()) {
				return -1;
			} 
			return 0;
		} 
	}

	public static final int SIZE = 3;
	private Spot[][] maze;
	private Penny p0,p1;	

	public Maze(String[] moves) {

		this.maze = new Spot[SIZE][SIZE];

		this.p0 = new Penny(0,0, "p0");
		this.p1 = new Penny(2,2, "p1");

		setupSpots();
		setupMoves(moves);
	}

	public LinkedList<Move> bfs() {

		LinkedList<Move> bestPath = new LinkedList<Move>();

		PriorityQueue<LinkedList<Move>> q = new PriorityQueue<LinkedList<Move>>(100, new StateSequenceComparator());
		Move initialMove = new Move("Start", p0, p1,true, true);
		LinkedList<Move> initialState = new LinkedList<Move>();
		initialState.add(initialMove);

		q.add(new LinkedList<Move>(initialState));
		while (!q.isEmpty()) {
			LinkedList<Move> sequence = q.poll();
			int moveCount = sequence.size() - 1;

			Penny zero = sequence.peekLast().pennyMoved;
			Penny one = sequence.peekLast().pennyRelative;

			if (zero.isFinished() || one.isFinished()) {
				System.out.println("Finished");
				printSequence(sequence);
				break;
			} 

			List<Move> validMoves;

			if (moveCount % 2 == 0) {

				validMoves = maze[one.p.y][one.p.x].computeValidMoves(zero, one, true);
				
				if (validMoves.isEmpty()) {
					LinkedList<Move> newSequence = (LinkedList<Move>) sequence.clone();
					
					Penny newZero = zero.clone();
					Penny newOne = one.clone();	

					newSequence.add(new Move("passed",newZero, newOne,true, true));
					q.add(newSequence);
				
				}
			} else {

				validMoves = maze[zero.p.y][zero.p.x].computeValidMoves(zero, one, false);
				
				if (validMoves.isEmpty()) {
					LinkedList<Move> newSequence = (LinkedList<Move>) sequence.clone();
					
					Penny newZero = zero.clone();
					Penny newOne = one.clone();	

					newSequence.add(new Move("passed", newZero, newOne,false, true));
					q.add(newSequence);
				
				}
			}

			if(!validMoves.isEmpty()) {
				for (Move move : validMoves) {
					LinkedList<Move> newSequence = (LinkedList<Move>) sequence.clone();

					newSequence.add(move);
					
					q.add(newSequence);
				//	visited.add(newSequence.peekLast().toString());
				}
			}
		}

		return bestPath;
	}

	private void setupSpots() {

		int totalSize = SIZE*SIZE;

		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < SIZE; y++) {
				maze[y][x] = new Spot(x,y);
			}
		}
	}

	private void setupMoves(String[] routes) {

		int totalSize = SIZE*SIZE;
		int x = 0, y = 0;
		for(int i = 0; i < totalSize; i++) {

			String[] moves = routes[i].split(" ");
			maze[y][x].addMoves(moves);
			
			x++;

			if(x == SIZE) {
				x = 0;
				y++;
			}
		}
	}
/*
	public void printAllRoutes() {

		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE; x++) {
				Spot s = maze[y][x];

				List<Direction> movess = s.getmovess();

				for(Direction d : movess) {

					System.out.println(s + " " + d);
				}
			}
		}
	}*/

	private void printSequence(LinkedList<Move> bestPath) {

		for(Move m : bestPath) {
			System.out.println(m);
		}
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();

		for(int y = 0; y < SIZE; y++) {
			for(int x = 0; x < SIZE; x++) {
				sb.append(maze[y][x]);
				sb.append(" ");
			}
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length()-1);

		return sb.toString();
	}
}