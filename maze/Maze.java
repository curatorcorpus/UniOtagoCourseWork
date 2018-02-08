/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/

import java.util.ArrayList;
import java.util.List;

public class Maze {

	public static final int SIZE = 3;

	private Spot[][] maze;

	private Penny p0,p1;	

	public Maze(String[] routes) {

		this.maze = new Spot[SIZE][SIZE];

		this.p0 = new Penny(0,0, "p0");
		this.p1 = new Penny(2,2, "p1");

		setupSpots();
		setupDirections(routes);
	}

	public void bfs() {

		// queue
		// list of states


		// while queue is not empty

		// 		check if a penny is finished:
					//return or print all states.

			// if move is even, then its penny 0 turn.


			// else if move is odd, the its penny 1 turn. 
	}

	private void setupSpots() {

		int totalSize = SIZE*SIZE;

		for(int x = 0; x < SIZE; x++) {
			for(int y = 0; y < SIZE; y++) {
				maze[y][x] = new Spot(x,y);
			}		
		}
	}

	private void setupDirections(String[] routes) {

		int totalSize = SIZE*SIZE;
		int x = 0, y = 0;
		for(int i = 0; i < totalSize; i++) {

			String[] directions = routes[i].split(" ");
			maze[y][x].addDirections(directions);
			
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

				List<Direction> moves = s.getMoves();

				for(Direction d : moves) {

					System.out.println(s + " " + d);
				}
			}
		}
	}*/

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