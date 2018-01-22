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
		String result = "";

		// check if input length is 2.
		if(inputs.length == 2) {
			int firstInput = Integer.parseInt(inputs[0]);
			int secondInput = Integer.parseInt(inputs[1]);

			StringBuilder sb = new StringBuilder(oooMethod);

			sb.append(" ");
			sb.append(firstInput);

			if((firstInput + secondInput) == target) {
				sb.append(" + ");
			}
			else if((firstInput * secondInput) == target) {
				sb.append(" * ");
			}
			else {
				return oooMethod + " impossible";
			}
			sb.append(secondInput);

			return sb.toString();
		}

		if(oooMethod.equals("L")) {
			result = evaluateL(inputs);

			return result;
		}

		return evaluateN(inputs);
	}

	public String evaluateL(String[] inputs) {

		OperationNode[] rootChildren = root.getChildren();

		return "";
	}

	public String evaluateN(String[] inputs) {

		OperationNode[] rootChildren = root.getChildren();

		return "";
	}
}