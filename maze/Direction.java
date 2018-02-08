/**
*	@author Jung Woo (Noel) Park
*	Student ID: 1162424
*/

public class Direction {

	public Penny penny;
	public String name;
	public Position p;

	public Direction(String name, Position p, Penny penny) {

		this.name = name;
		this.p = p;
		this.penny = penny;
	}

	public String toString() {

		return penny.name + " moved " + name + " to position " + p.toString();
	}
}