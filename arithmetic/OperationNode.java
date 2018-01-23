/**
*	@Author Jung Woo (Noel) Park.
	Student ID: 1162424.
*/
import java.util.ArrayList;

public class OperationNode {

	public static String expression;

	private int depth;
	private OperationNode[] children;
	private String operation;

	public OperationNode(int depth) {

		this.depth = depth;
		this.children = new OperationNode[2];
		subdivide();
	}

	public OperationNode(String operation, int depth) {

		this.depth = depth;
		this.children = null;
		this.operation = operation;
	}

	public OperationNode[] getChildren() {
		return children;
	}

	public void subdivide() {

		this.children[0] = new OperationNode("x", depth+1);
		this.children[1] = new OperationNode("+", depth+1);
	}

	public boolean evaluateNodeL(String[] inputs, int target, int currentTotal, String currentOperations) {

		int rhsInput = Integer.parseInt(inputs[depth]);
		int total = currentTotal;

		currentOperations += operation;

		if(operation.equals("x")) {
			total *= rhsInput;
		} else if(operation.equals("+")) {
			total += rhsInput;
		}
		
		if(total > target) {
			return false;
		}
		if(depth == (inputs.length - 1)) {
			if(total == target) {
				expression = currentOperations;
				return true;
			} else {
				return false;
			}
		}
		if(children == null) {
			this.children = new OperationNode[2];
			subdivide();
		}
		if(children[0].evaluateNodeL(inputs, target, total, currentOperations)) {
			return true;
		}
		return children[1].evaluateNodeL(inputs, target, total, currentOperations);
	}

	public boolean evaluateNodeN(String[] inputs, int target, int currentTotal, String currentOperations, ArrayList<Integer> additionBuffer) {

		ArrayList<Integer> additions = new ArrayList<Integer>(additionBuffer);

		int rhsInput = Integer.parseInt(inputs[depth]);
		int total = currentTotal;

		currentOperations += operation;

		if(operation.equals("x")) {
			if(additions.size() == 0) {
				total *= rhsInput;
			} else {
				int lhsInput = additions.get(additionBuffer.size()-1);
				additions.remove(additions.size()-1);
				lhsInput *= rhsInput;
				for(int i : additions) {
					total += i;
				}
				total += lhsInput;
			}
		} else if(operation.equals("+")) {
			if(depth == (inputs.length - 1)) {
				if(additions.size() == 0) {
					total += rhsInput;
				} else {
					for(int i : additions) {
						total += i;
					}
					total += rhsInput;
				}
			} else {
				additions.add(rhsInput);
			}
		}
		
		if(total > target) {
			return false;
		}
		if(depth == (inputs.length - 1)) {
			if(total == target) {
				expression = currentOperations;
				return true;
			} else {
				return false;
			}
		}
		if(children == null) {
			this.children = new OperationNode[2];
			subdivide();
		}
		if(children[0].evaluateNodeN(inputs, target, total, currentOperations, additions)) {
			return true;
		}
		return children[1].evaluateNodeN(inputs, target, total, currentOperations, additions);
	}
}