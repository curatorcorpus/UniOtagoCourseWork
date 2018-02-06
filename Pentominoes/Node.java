/**
* @Author: Jung Woo (Noel) Park
* Student ID: 1162424.
*/
public class Node {

	public String columnName;
	public int nodeCount;
	public int row, col;

	public Node columnNode;
	public Node up;
	public Node down;
	public Node left;
	public Node right;

	public Node() {
	}

	public void print(Node original, Node next) {
		if(next == null) {
			return;
		}
		if(original.equals(next)) {
			return;
		}else {
			System.out.println(next.nodeCount);
		}
		next.print(original, next.right);
	}
	public void printDown(Node original, Node next) {
		if(original.equals(next)) {
			return;
		}else {
			System.out.println(next.row+" "+next.col);
		}
		next.printDown(original, next.down);
	}

	public void printAll(Node original, Node next) {
		if(original.equals(next)) {
			return;
		}
		next.printDown(next,next.down);
		next.printAll(original, next.right);
	}
}