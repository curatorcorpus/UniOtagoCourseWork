	/**
*	@Author Jung Woo (Noel) Park.
	Student ID: 1162424.
*/

public class ArithmeticTree {

	private int target;
	private String oooMethod;
	private OperationNode root;

	public ArithmeticTree() {

		this.root = new OperationNode(0);

	}

	public void setExpectedTarget(String expectedTarget) {

		String[] oooMethodAndTarget = expectedTarget.split(" ");

		target = Integer.parseInt(oooMethodAndTarget[0]);
		oooMethod = oooMethodAndTarget[1];
	}

	public String evaluate(String input) {

		String[] inputs = input.split(" ");

		if(oooMethod.equals("L")) {
			if(evaluateL(inputs)) {
				return OperationNode.expression;
			}
		}
		return "L impossible";
	}

	public boolean evaluateL(String[] inputs) {
		
		OperationNode[] children = root.getChildren();

		if(children[0].evaluateNodeL(inputs, target, Integer.parseInt(inputs[0]), "")) {
			return true;
		}
		return children[1].evaluateNodeL(inputs, target, Integer.parseInt(inputs[0]), "");
	}

	public String evaluateN(String[] inputs) {

		OperationNode[] rootChildren = root.getChildren();

		return "";
	}
}