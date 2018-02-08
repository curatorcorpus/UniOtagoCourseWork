/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/

public class Maze {

	public static final int SIZE = 3;

	private Spot[][] maze;

	public Maze(String[] routes) {

		this.maze = new Spot[SIZE][SIZE];

		setupSpots();
		setupDirections(routes);
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

			for(int j = 0; j < directions.length; j++) {

				String moveName = directions[j];
				maze[y][x].addMove(moveName);
			}
			x++;

			if(x == SIZE) {
				x = 0;
				y++;
			}
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