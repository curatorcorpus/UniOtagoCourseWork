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

	public String search(String input) {

		String[] inputs = input.split(" ");
		if(oooMethod.equals("L")) {
			if(evaluateL(inputs)) {
				return formatOutput(oooMethod, OperationNode.expression, inputs);
			}
		} else if(oooMethod.equals("N")) {
			if(evaluateN(inputs)) {
				return formatOutput(oooMethod, OperationNode.expression, inputs);
			}
		}
		return oooMethod + " impossible";
	}

	public boolean evaluateL(String[] inputs) {
		
		OperationNode[] children = root.getChildren();

		if(children[0].evaluateNodeL(inputs, target, Integer.parseInt(inputs[0]), "")) {
			return true;
		}
		return children[1].evaluateNodeL(inputs, target, Integer.parseInt(inputs[0]), "");
	}

	public boolean evaluateN(String[] inputs) {

		OperationNode[] children = root.getChildren();

		if(children[0].evaluateNodeN(inputs, target, Integer.parseInt(inputs[0]), "")) {
			return true;
		}
		return children[1].evaluateNodeN(inputs, target, Integer.parseInt(inputs[0]), "");
	}

	private String formatOutput(String oooMethod, String expression, String[] inputs) {

		String[] expressions = expression.split("");

		StringBuilder sb = new StringBuilder(oooMethod);

		sb.append(" ");
		sb.append(inputs[0]);

		for(int i = 0; i < expressions.length; i++) {
			sb.append(" ");
			sb.append(expressions[i]);
			sb.append(" ");
			sb.append(inputs[i+1]);
		}
		return sb.toString();
	}
}