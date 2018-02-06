/**
* @Author: Jung Woo (Noel) Park
* Student ID: 1162424.
*/

/**
*	Class the represents the point. The points just encapsulates some 
*	values of x and y. Used in pentominoes x and y could be the starting 
*	point of some pentomino and its respective offsets the define its shape.
*/
public class Point {

	public int x;
	public int y;

	public Point(int x, int y) {

		this.x = x;
		this.y = y;
	}

	public Point add(Point otherPoint) {

		Point newPoint = new Point(x+otherPoint.x,y+otherPoint.y);

		return newPoint;
	}

	public String toString() {
		return "x: " + x + " y: " + y;
	}
}