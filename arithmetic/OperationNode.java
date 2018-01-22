
public class OperationNode {

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
		this.children = new OperationNode[2];
		this.operation = operation;
	}

	public OperationNode[] getChildren() {
		return children;
	}

	public void subdivide() {

		this.children[0] = new OperationNode("+", depth+1);
		this.children[1] = new OperationNode("x", depth+1);
	}

	public String evaluateNodeL(String[] inputs, int target, int currentTotal) {
		System.out.println(depth);
		if(depth == inputs.length) {

		}
		return children[1].evaluateNodeL(inputs, target, currentTotal);
	}
}