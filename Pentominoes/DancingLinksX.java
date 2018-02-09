/**
* @Author: Jung Woo (Noel) Park
* Student ID: 1162424.
*/
import java.util.ArrayDeque;
import java.util.Deque;

public class DancingLinksX {

	private static boolean isSolution = false;

	private int rows, cols;

	private boolean[][] problemMatrix;

	private Node root;
	public static ArrayDeque<Node> aSolution = new ArrayDeque<Node>();
	private ArrayDeque<Node> solution;
	private Node[][] matrix;

	public DancingLinksX(boolean[][] problemMatrix) {

		// generate DLL sparse matrix.
		this.rows = problemMatrix.length+1;
		this.cols = problemMatrix[0].length;

		this.solution = new ArrayDeque<Node>();

		this.root = new Node();
		this.matrix = new Node[rows][cols];
		this.problemMatrix = problemMatrix;

		setupColumnNodes();
		setupObjectNodes();
		setupColumnNodeNeighbours();
		setupObjectNodeNeighbours();

		// Link the root right reference as the first element in the row matrix.
		root.right = matrix[0][0];

		// Link roots left reference as the last element in the row matrix.
		root.left = matrix[0][cols-1];

		// Assign the right and left neighbours of the header nodes as the root.
		matrix[0][0].left = root;
		matrix[0][cols-1].right = root;
		//checkForNullNeighbours();
		//root.printAll(root, root.right);
		//printAllColumnNodeCounts(root, root.right);
	}

	// Setup Methods.

	private void setupColumnNodes() {

		// Construct column nodes.
		for(int col = 0; col < cols; col++) {
			matrix[0][col] = new Node();
		}
	}

	private void setupObjectNodes() {

		// Construct nodes at 1 in the problem matrix.
		for(int row = 1; row < rows; row++) {
			for(int col = 0; col < cols; col++) {

				// If true at position row and col then we create a node.
				if(problemMatrix[row-1][col]) {
					matrix[row][col] = new Node();
					matrix[row][col].row = row;
					matrix[row][col].col = col;
				}
			}
		}
	}

	private void setupColumnNodeNeighbours() {

		// Assign column head neighbours
		for(int col = 0; col < cols; col++) {
			matrix[0][col].columnName = String.valueOf(col);
			matrix[0][col].columnNode = matrix[0][col];
			matrix[0][col].row = 0;
			matrix[0][col].col = col;
			matrix[0][col].nodeCount = 0;
			matrix[0][col].left = matrix[0][getLeft(col)];
			matrix[0][col].right = matrix[0][getRight(col)];

			// UP
			int rowIdx = rows-1;
			while(matrix[rowIdx][col] == null && rowIdx != 0) {
				--rowIdx;
			}
			// Assign closest neighbour.
			matrix[0][col].up = matrix[rowIdx][col];

			// DOWN
			rowIdx = 1;
			while(matrix[rowIdx][col] == null && rowIdx != 0) {
				if(++rowIdx >= rows) {
					rowIdx = 0;
					break;
				}
			}
			// Assign closest neighbour.
			matrix[0][col].down = matrix[rowIdx][col];
		}
	}

	private void setupObjectNodeNeighbours() {
		// An additional row for the column headers.
		// Assign neighbours to nodes.
		for(int row = 1; row < rows; row++) {
			for(int col = 0; col < cols; col++) {

				// If true at position row and col then we create a node.
				if(matrix[row][col] != null) {

					matrix[0][col].nodeCount += 1; 				  // increment node count of a column header.
					matrix[row][col].columnNode = matrix[0][col]; // Link the column head to this row node.

					// Link all row neighbours.

					// LEFT
					int rowIdx = row, colIdx = col;
					do {
						colIdx = getLeft(colIdx);
					} while(matrix[rowIdx][colIdx] == null && colIdx != col);
					// Assign closest neighbour.
					matrix[row][col].left = matrix[row][colIdx];

					// RIGHT
					rowIdx = row; colIdx = col;
					do {
						colIdx = getRight(colIdx);
					} while(matrix[rowIdx][colIdx] == null && colIdx != col);
					// Assign closest neighbour.
					matrix[row][col].right = matrix[row][colIdx];

					// UP
					rowIdx = row; colIdx = col;
					do {
						rowIdx = getUp(rowIdx);
					} while(matrix[rowIdx][colIdx] == null && rowIdx != row);
					// Assign closest neighbour.
					matrix[row][col].up = matrix[rowIdx][col];

					// DOWN
					rowIdx = row; colIdx = col;
					do {
						rowIdx = getDown(rowIdx);
					} while(matrix[rowIdx][colIdx] == null && rowIdx != row);
					// Assign closest neighbour.
					matrix[row][col].down = matrix[rowIdx][col];
				}
			}
		}
	}

	public Deque<Node> getSolution() {

		return aSolution.clone();
	}

	public void clearSolution() {

		solution = new ArrayDeque<Node>();
		isSolution = false;
	}

	public void searchSolution(int n) {

		// If all columns are covered, then we must have found a solution.
		if(root.equals(root.right)) {
			aSolution = solution.clone();
			isSolution = true;
			return;
		}
		Node minColumn = getMinColumn();
		cover(minColumn);
		//	System.out.println(matrix.length + " " matrix[0].length);
		for(Node rowNode = minColumn.down; !rowNode.equals(minColumn); rowNode = rowNode.down) {
			solution.push(rowNode);

			// Remove all overlapping column and rows.
			for(Node rightNode = rowNode.right; !rightNode.equals(rowNode); rightNode = rightNode.right) {
				cover(rightNode);
			}

			// search another level down the nodes.
			searchSolution(n+1);
			if(isSolution) {
				break;
			}

			// if solution is not possible, backtrack and uncover rowNode. Plus remove row Node from solution.
			solution.pop();

			minColumn = rowNode.columnNode;
			for(Node leftNode = rowNode.left; !leftNode.equals(rowNode); leftNode = leftNode.left) {
				uncover(leftNode);
			}
		}
		uncover(minColumn);
	}

	// DANCING LINK SEARCH METHODS.

	/**
	*	Method that covers the given node.
	*/
	private void cover(Node targetNode) {

		Node colNode = targetNode.columnNode;

		// unlink column header form it's neighbour.
		colNode.left.right = colNode.right; // assigns target node, left's right neigbour to target's right.
		colNode.right.left = colNode.left;  // assigns target node, right's left neigbour to target's left.

		// Move down the column and remove each row by traversing right.
		for(Node row = colNode.down; !row.equals(colNode); row = row.down) {

			for(Node rightNode = row.right; !rightNode.equals(row); rightNode = rightNode.right) {
				rightNode.up.down = rightNode.down;
				rightNode.down.up = rightNode.up;

				// decrement node counter.
				rightNode.columnNode.nodeCount--;
			}
		}
	}

	private void uncover(Node targetNode) {

		// get column node of the target node.
		Node colNode = targetNode.columnNode;

		// Move down the column and link back each row by traversing left.
		for(Node rowNode = colNode.up; !rowNode.equals(colNode); rowNode = rowNode.up) {

			for(Node leftNode = rowNode.left; !leftNode.equals(rowNode); leftNode = leftNode.left) {

				leftNode.up.down = leftNode;
				leftNode.down.up = leftNode;

				leftNode.columnNode.nodeCount++;
			}
		}

		// Link the column header from its neighbours.
		colNode.left.right = colNode;
		colNode.right.left = colNode;
	}

	private Node getMinColumn() {

		Node minColumn = root.right;
		Node currentNode = root.right.right;

		do {
			if(currentNode.nodeCount < minColumn.nodeCount) {
				minColumn = currentNode;
			}
			currentNode = currentNode.right;
		} while(!currentNode.equals(root));

		return minColumn;
	}

	// Getters and setters for the neigbours in circular nature.

	private int getRight(int idx) {
		return (idx+1) % cols;
	}

	private int getLeft(int idx) {

		return (idx-1 < 0) ? cols-1 : idx-1;
	}

	private int getUp(int idx) {

		return (idx-1 < 0) ? rows : idx-1; 
	}

	private int getDown(int idx) {

		return (idx+1) % (rows);
	}

	private void checkForNullNeighbours() {

		int nullCount = 0;

		for(int row = 0; row < rows; row++) {
			for(int col = 0; col < cols; col++) {

				// If true at position row and col then we create a node.
				if(matrix[row][col] != null) {
					if(matrix[row][col].right == null) nullCount++;
					if(matrix[row][col].left == null) nullCount++;
					if(matrix[row][col].up == null) nullCount++;
					if(matrix[row][col].down == null) nullCount++;
				}
			}
		}

		System.out.println(nullCount);
	}

	private void printAllColumnNodeCounts(Node original, Node next) {

		if(original.equals(next)) {
			return;
		}
		else {
			System.out.println(next.columnName +": " +next.nodeCount);
		}
		printAllColumnNodeCounts(original,next.right);
	}
}