/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		String[] routes = readMazePuzzle();
		Maze maze = new Maze(routes);

	}

	private static String[] readMazePuzzle() {

		String[] routes = new String[Maze.SIZE*Maze.SIZE];

		Scanner sc = new Scanner(System.in);
		int i = 0;
		while(sc.hasNextLine()) {
			routes[i++] = sc.nextLine();
		}
		return routes;
	}
}