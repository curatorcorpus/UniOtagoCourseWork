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
		System.out.println(oooMethod);
		System.out.println(target);
	}

	public String evaluateL(String input, String target) {

		OperationNode[] rootChildren = root.getChildren();

		String result = "";

		return "";
	}
}