/**
*	@Author Jung Woo (Noel) Park.
	Student ID: 1162424.
*/

import java.util.ArrayList;
import java.util.Arrays;

public class Arithmetic {
	
	// contains all permutations of + and x operations from numbers 1 - 10.
	private ArrayList<String> operationsPowOne = new ArrayList<String>(Arrays.asList("+","x"));
	private ArrayList<String> operationsPowTwo = new ArrayList<String>(Arrays.asList("++","+x","x+","xx"));
	private ArrayList<String> operationsPowThr = new ArrayList<String>(Arrays.asList("+++","++x","+x+","+xx","x++","x+x","xx+","xxx"));
	private ArrayList<String> operationsPowFor = new ArrayList<String>(Arrays.asList("++++","+++x","++x+","++xx","+x++","+x+x","+xx+","+xxx","x+++","x++x","x+x+","x+xx","xx++","xx+x","xxx+","xxxx"));
	private ArrayList<String> operationsPowFiv = new ArrayList<String>(Arrays.asList("+++++","++++x","+++x+","+++xx","++x++","++x+x","++xx+","++xxx","+x+++","+x++x","+x+x+","+x+xx","+xx++","+xx+x","+xxx+","+xxxx","x++++","x+++x","x++x+","x++xx","x+x++","x+x+x","x+xx+","x+xxx","xx+++","xx++x","xx+x+","xx+xx","xxx++","xxx+x","xxxx+","xxxxx"));
	private ArrayList<String> operationsPowSix = new ArrayList<String>(Arrays.asList("++++++","+++++x","++++x+","++++xx","+++x++","+++x+x","+++xx+","+++xxx","++x+++","++x++x","++x+x+","++x+xx","++xx++","++xx+x","++xxx+","++xxxx","+x++++","+x+++x","+x++x+","+x++xx","+x+x++","+x+x+x","+x+xx+","+x+xxx","+xx+++","+xx++x","+xx+x+","+xx+xx","+xxx++","+xxx+x","+xxxx+","+xxxxx","x+++++","x++++x","x+++x+","x+++xx","x++x++","x++x+x","x++xx+","x++xxx","x+x+++","x+x++x","x+x+x+","x+x+xx","x+xx++","x+xx+x","x+xxx+","x+xxxx","xx++++","xx+++x","xx++x+","xx++xx","xx+x++","xx+x+x","xx+xx+","xx+xxx","xxx+++","xxx++x","xxx+x+","xxx+xx","xxxx++","xxxx+x","xxxxx+","xxxxxx"));
	private ArrayList<String> operationsPowSev = new ArrayList<String>(Arrays.asList("+++++++","++++++x","+++++x+","+++++xx","++++x++","++++x+x","++++xx+","++++xxx","+++x+++","+++x++x","+++x+x+","+++x+xx","+++xx++","+++xx+x","+++xxx+","+++xxxx","++x++++","++x+++x","++x++x+","++x++xx","++x+x++","++x+x+x","++x+xx+","++x+xxx","++xx+++","++xx++x","++xx+x+","++xx+xx","++xxx++","++xxx+x","++xxxx+","++xxxxx","+x+++++","+x++++x","+x+++x+","+x+++xx","+x++x++","+x++x+x","+x++xx+","+x++xxx","+x+x+++","+x+x++x","+x+x+x+","+x+x+xx","+x+xx++","+x+xx+x","+x+xxx+","+x+xxxx","+xx++++","+xx+++x","+xx++x+","+xx++xx","+xx+x++","+xx+x+x","+xx+xx+","+xx+xxx","+xxx+++","+xxx++x","+xxx+x+","+xxx+xx","+xxxx++","+xxxx+x","+xxxxx+","+xxxxxx","x++++++","x+++++x","x++++x+","x++++xx","x+++x++","x+++x+x","x+++xx+","x+++xxx","x++x+++","x++x++x","x++x+x+","x++x+xx","x++xx++","x++xx+x","x++xxx+","x++xxxx","x+x++++","x+x+++x","x+x++x+","x+x++xx","x+x+x++","x+x+x+x","x+x+xx+","x+x+xxx","x+xx+++","x+xx++x","x+xx+x+","x+xx+xx","x+xxx++","x+xxx+x","x+xxxx+","x+xxxxx","xx+++++","xx++++x","xx+++x+","xx+++xx","xx++x++","xx++x+x","xx++xx+","xx++xxx","xx+x+++","xx+x++x","xx+x+x+","xx+x+xx","xx+xx++","xx+xx+x","xx+xxx+","xx+xxxx","xxx++++","xxx+++x","xxx++x+","xxx++xx","xxx+x++","xxx+x+x","xxx+xx+","xxx+xxx","xxxx+++","xxxx++x","xxxx+x+","xxxx+xx","xxxxx++","xxxxx+x","xxxxxx+","xxxxxxx"));
	private ArrayList<String> operationsPowEig = new ArrayList<String>(Arrays.asList("++++++++","+++++++x","++++++x+","++++++xx","+++++x++","+++++x+x","+++++xx+","+++++xxx","++++x+++","++++x++x","++++x+x+","++++x+xx","++++xx++","++++xx+x","++++xxx+","++++xxxx","+++x++++","+++x+++x","+++x++x+","+++x++xx","+++x+x++","+++x+x+x","+++x+xx+","+++x+xxx","+++xx+++","+++xx++x","+++xx+x+","+++xx+xx","+++xxx++","+++xxx+x","+++xxxx+","+++xxxxx","++x+++++","++x++++x","++x+++x+","++x+++xx","++x++x++","++x++x+x","++x++xx+","++x++xxx","++x+x+++","++x+x++x","++x+x+x+","++x+x+xx","++x+xx++","++x+xx+x","++x+xxx+","++x+xxxx","++xx++++","++xx+++x","++xx++x+","++xx++xx","++xx+x++","++xx+x+x","++xx+xx+","++xx+xxx","++xxx+++","++xxx++x","++xxx+x+","++xxx+xx","++xxxx++","++xxxx+x","++xxxxx+","++xxxxxx","+x++++++","+x+++++x","+x++++x+","+x++++xx","+x+++x++","+x+++x+x","+x+++xx+","+x+++xxx","+x++x+++","+x++x++x","+x++x+x+","+x++x+xx","+x++xx++","+x++xx+x","+x++xxx+","+x++xxxx","+x+x++++","+x+x+++x","+x+x++x+","+x+x++xx","+x+x+x++","+x+x+x+x","+x+x+xx+","+x+x+xxx","+x+xx+++","+x+xx++x","+x+xx+x+","+x+xx+xx","+x+xxx++","+x+xxx+x","+x+xxxx+","+x+xxxxx","+xx+++++","+xx++++x","+xx+++x+","+xx+++xx","+xx++x++","+xx++x+x","+xx++xx+","+xx++xxx","+xx+x+++","+xx+x++x","+xx+x+x+","+xx+x+xx","+xx+xx++","+xx+xx+x","+xx+xxx+","+xx+xxxx","+xxx++++","+xxx+++x","+xxx++x+","+xxx++xx","+xxx+x++","+xxx+x+x","+xxx+xx+","+xxx+xxx","+xxxx+++","+xxxx++x","+xxxx+x+","+xxxx+xx","+xxxxx++","+xxxxx+x","+xxxxxx+","+xxxxxxx","x+++++++","x++++++x","x+++++x+","x+++++xx","x++++x++","x++++x+x","x++++xx+","x++++xxx","x+++x+++","x+++x++x","x+++x+x+","x+++x+xx","x+++xx++","x+++xx+x","x+++xxx+","x+++xxxx","x++x++++","x++x+++x","x++x++x+","x++x++xx","x++x+x++","x++x+x+x","x++x+xx+","x++x+xxx","x++xx+++","x++xx++x","x++xx+x+","x++xx+xx","x++xxx++","x++xxx+x","x++xxxx+","x++xxxxx","x+x+++++","x+x++++x","x+x+++x+","x+x+++xx","x+x++x++","x+x++x+x","x+x++xx+","x+x++xxx","x+x+x+++","x+x+x++x","x+x+x+x+","x+x+x+xx","x+x+xx++","x+x+xx+x","x+x+xxx+","x+x+xxxx","x+xx++++","x+xx+++x","x+xx++x+","x+xx++xx","x+xx+x++","x+xx+x+x","x+xx+xx+","x+xx+xxx","x+xxx+++","x+xxx++x","x+xxx+x+","x+xxx+xx","x+xxxx++","x+xxxx+x","x+xxxxx+","x+xxxxxx","xx++++++","xx+++++x","xx++++x+","xx++++xx","xx+++x++","xx+++x+x","xx+++xx+","xx+++xxx","xx++x+++","xx++x++x","xx++x+x+","xx++x+xx","xx++xx++","xx++xx+x","xx++xxx+","xx++xxxx","xx+x++++","xx+x+++x","xx+x++x+","xx+x++xx","xx+x+x++","xx+x+x+x","xx+x+xx+","xx+x+xxx","xx+xx+++","xx+xx++x","xx+xx+x+","xx+xx+xx","xx+xxx++","xx+xxx+x","xx+xxxx+","xx+xxxxx","xxx+++++","xxx++++x","xxx+++x+","xxx+++xx","xxx++x++","xxx++x+x","xxx++xx+","xxx++xxx","xxx+x+++","xxx+x++x","xxx+x+x+","xxx+x+xx","xxx+xx++","xxx+xx+x","xxx+xxx+","xxx+xxxx","xxxx++++","xxxx+++x","xxxx++x+","xxxx++xx","xxxx+x++","xxxx+x+x","xxxx+xx+","xxxx+xxx","xxxxx+++","xxxxx++x","xxxxx+x+","xxxxx+xx","xxxxxx++","xxxxxx+x","xxxxxxx+","xxxxxxxx"));
	private ArrayList<String> operationsPowNin = new ArrayList<String>(Arrays.asList("+++++++++","++++++++x","+++++++x+","+++++++xx","++++++x++","++++++x+x","++++++xx+","++++++xxx","+++++x+++","+++++x++x","+++++x+x+","+++++x+xx","+++++xx++","+++++xx+x","+++++xxx+","+++++xxxx","++++x++++","++++x+++x","++++x++x+","++++x++xx","++++x+x++","++++x+x+x","++++x+xx+","++++x+xxx","++++xx+++","++++xx++x","++++xx+x+","++++xx+xx","++++xxx++","++++xxx+x","++++xxxx+","++++xxxxx","+++x+++++","+++x++++x","+++x+++x+","+++x+++xx","+++x++x++","+++x++x+x","+++x++xx+","+++x++xxx","+++x+x+++","+++x+x++x","+++x+x+x+","+++x+x+xx","+++x+xx++","+++x+xx+x","+++x+xxx+","+++x+xxxx","+++xx++++","+++xx+++x","+++xx++x+","+++xx++xx","+++xx+x++","+++xx+x+x","+++xx+xx+","+++xx+xxx","+++xxx+++","+++xxx++x","+++xxx+x+","+++xxx+xx","+++xxxx++","+++xxxx+x","+++xxxxx+","+++xxxxxx","++x++++++","++x+++++x","++x++++x+","++x++++xx","++x+++x++","++x+++x+x","++x+++xx+","++x+++xxx","++x++x+++","++x++x++x","++x++x+x+","++x++x+xx","++x++xx++","++x++xx+x","++x++xxx+","++x++xxxx","++x+x++++","++x+x+++x","++x+x++x+","++x+x++xx","++x+x+x++","++x+x+x+x","++x+x+xx+","++x+x+xxx","++x+xx+++","++x+xx++x","++x+xx+x+","++x+xx+xx","++x+xxx++","++x+xxx+x","++x+xxxx+","++x+xxxxx","++xx+++++","++xx++++x","++xx+++x+","++xx+++xx","++xx++x++","++xx++x+x","++xx++xx+","++xx++xxx","++xx+x+++","++xx+x++x","++xx+x+x+","++xx+x+xx","++xx+xx++","++xx+xx+x","++xx+xxx+","++xx+xxxx","++xxx++++","++xxx+++x","++xxx++x+","++xxx++xx","++xxx+x++","++xxx+x+x","++xxx+xx+","++xxx+xxx","++xxxx+++","++xxxx++x","++xxxx+x+","++xxxx+xx","++xxxxx++","++xxxxx+x","++xxxxxx+","++xxxxxxx","+x+++++++","+x++++++x","+x+++++x+","+x+++++xx","+x++++x++","+x++++x+x","+x++++xx+","+x++++xxx","+x+++x+++","+x+++x++x","+x+++x+x+","+x+++x+xx","+x+++xx++","+x+++xx+x","+x+++xxx+","+x+++xxxx","+x++x++++","+x++x+++x","+x++x++x+","+x++x++xx","+x++x+x++","+x++x+x+x","+x++x+xx+","+x++x+xxx","+x++xx+++","+x++xx++x","+x++xx+x+","+x++xx+xx","+x++xxx++","+x++xxx+x","+x++xxxx+","+x++xxxxx","+x+x+++++","+x+x++++x","+x+x+++x+","+x+x+++xx","+x+x++x++","+x+x++x+x","+x+x++xx+","+x+x++xxx","+x+x+x+++","+x+x+x++x","+x+x+x+x+","+x+x+x+xx","+x+x+xx++","+x+x+xx+x","+x+x+xxx+","+x+x+xxxx","+x+xx++++","+x+xx+++x","+x+xx++x+","+x+xx++xx","+x+xx+x++","+x+xx+x+x","+x+xx+xx+","+x+xx+xxx","+x+xxx+++","+x+xxx++x","+x+xxx+x+","+x+xxx+xx","+x+xxxx++","+x+xxxx+x","+x+xxxxx+","+x+xxxxxx","+xx++++++","+xx+++++x","+xx++++x+","+xx++++xx","+xx+++x++","+xx+++x+x","+xx+++xx+","+xx+++xxx","+xx++x+++","+xx++x++x","+xx++x+x+","+xx++x+xx","+xx++xx++","+xx++xx+x","+xx++xxx+","+xx++xxxx","+xx+x++++","+xx+x+++x","+xx+x++x+","+xx+x++xx","+xx+x+x++","+xx+x+x+x","+xx+x+xx+","+xx+x+xxx","+xx+xx+++","+xx+xx++x","+xx+xx+x+","+xx+xx+xx","+xx+xxx++","+xx+xxx+x","+xx+xxxx+","+xx+xxxxx","+xxx+++++","+xxx++++x","+xxx+++x+","+xxx+++xx","+xxx++x++","+xxx++x+x","+xxx++xx+","+xxx++xxx","+xxx+x+++","+xxx+x++x","+xxx+x+x+","+xxx+x+xx","+xxx+xx++","+xxx+xx+x","+xxx+xxx+","+xxx+xxxx","+xxxx++++","+xxxx+++x","+xxxx++x+","+xxxx++xx","+xxxx+x++","+xxxx+x+x","+xxxx+xx+","+xxxx+xxx","+xxxxx+++","+xxxxx++x","+xxxxx+x+","+xxxxx+xx","+xxxxxx++","+xxxxxx+x","+xxxxxxx+","+xxxxxxxx","x++++++++","x+++++++x","x++++++x+","x++++++xx","x+++++x++","x+++++x+x","x+++++xx+","x+++++xxx","x++++x+++","x++++x++x","x++++x+x+","x++++x+xx","x++++xx++","x++++xx+x","x++++xxx+","x++++xxxx","x+++x++++","x+++x+++x","x+++x++x+","x+++x++xx","x+++x+x++","x+++x+x+x","x+++x+xx+","x+++x+xxx","x+++xx+++","x+++xx++x","x+++xx+x+","x+++xx+xx","x+++xxx++","x+++xxx+x","x+++xxxx+","x+++xxxxx","x++x+++++","x++x++++x","x++x+++x+","x++x+++xx","x++x++x++","x++x++x+x","x++x++xx+","x++x++xxx","x++x+x+++","x++x+x++x","x++x+x+x+","x++x+x+xx","x++x+xx++","x++x+xx+x","x++x+xxx+","x++x+xxxx","x++xx++++","x++xx+++x","x++xx++x+","x++xx++xx","x++xx+x++","x++xx+x+x","x++xx+xx+","x++xx+xxx","x++xxx+++","x++xxx++x","x++xxx+x+","x++xxx+xx","x++xxxx++","x++xxxx+x","x++xxxxx+","x++xxxxxx","x+x++++++","x+x+++++x","x+x++++x+","x+x++++xx","x+x+++x++","x+x+++x+x","x+x+++xx+","x+x+++xxx","x+x++x+++","x+x++x++x","x+x++x+x+","x+x++x+xx","x+x++xx++","x+x++xx+x","x+x++xxx+","x+x++xxxx","x+x+x++++","x+x+x+++x","x+x+x++x+","x+x+x++xx","x+x+x+x++","x+x+x+x+x","x+x+x+xx+","x+x+x+xxx","x+x+xx+++","x+x+xx++x","x+x+xx+x+","x+x+xx+xx","x+x+xxx++","x+x+xxx+x","x+x+xxxx+","x+x+xxxxx","x+xx+++++","x+xx++++x","x+xx+++x+","x+xx+++xx","x+xx++x++","x+xx++x+x","x+xx++xx+","x+xx++xxx","x+xx+x+++","x+xx+x++x","x+xx+x+x+","x+xx+x+xx","x+xx+xx++","x+xx+xx+x","x+xx+xxx+","x+xx+xxxx","x+xxx++++","x+xxx+++x","x+xxx++x+","x+xxx++xx","x+xxx+x++","x+xxx+x+x","x+xxx+xx+","x+xxx+xxx","x+xxxx+++","x+xxxx++x","x+xxxx+x+","x+xxxx+xx","x+xxxxx++","x+xxxxx+x","x+xxxxxx+","x+xxxxxxx","xx+++++++","xx++++++x","xx+++++x+","xx+++++xx","xx++++x++","xx++++x+x","xx++++xx+","xx++++xxx","xx+++x+++","xx+++x++x","xx+++x+x+","xx+++x+xx","xx+++xx++","xx+++xx+x","xx+++xxx+","xx+++xxxx","xx++x++++","xx++x+++x","xx++x++x+","xx++x++xx","xx++x+x++","xx++x+x+x","xx++x+xx+","xx++x+xxx","xx++xx+++","xx++xx++x","xx++xx+x+","xx++xx+xx","xx++xxx++","xx++xxx+x","xx++xxxx+","xx++xxxxx","xx+x+++++","xx+x++++x","xx+x+++x+","xx+x+++xx","xx+x++x++","xx+x++x+x","xx+x++xx+","xx+x++xxx","xx+x+x+++","xx+x+x++x","xx+x+x+x+","xx+x+x+xx","xx+x+xx++","xx+x+xx+x","xx+x+xxx+","xx+x+xxxx","xx+xx++++","xx+xx+++x","xx+xx++x+","xx+xx++xx","xx+xx+x++","xx+xx+x+x","xx+xx+xx+","xx+xx+xxx","xx+xxx+++","xx+xxx++x","xx+xxx+x+","xx+xxx+xx","xx+xxxx++","xx+xxxx+x","xx+xxxxx+","xx+xxxxxx","xxx++++++","xxx+++++x","xxx++++x+","xxx++++xx","xxx+++x++","xxx+++x+x","xxx+++xx+","xxx+++xxx","xxx++x+++","xxx++x++x","xxx++x+x+","xxx++x+xx","xxx++xx++","xxx++xx+x","xxx++xxx+","xxx++xxxx","xxx+x++++","xxx+x+++x","xxx+x++x+","xxx+x++xx","xxx+x+x++","xxx+x+x+x","xxx+x+xx+","xxx+x+xxx","xxx+xx+++","xxx+xx++x","xxx+xx+x+","xxx+xx+xx","xxx+xxx++","xxx+xxx+x","xxx+xxxx+","xxx+xxxxx","xxxx+++++","xxxx++++x","xxxx+++x+","xxxx+++xx","xxxx++x++","xxxx++x+x","xxxx++xx+","xxxx++xxx","xxxx+x+++","xxxx+x++x","xxxx+x+x+","xxxx+x+xx","xxxx+xx++","xxxx+xx+x","xxxx+xxx+","xxxx+xxxx","xxxxx++++","xxxxx+++x","xxxxx++x+","xxxxx++xx","xxxxx+x++","xxxxx+x+x","xxxxx+xx+","xxxxx+xxx","xxxxxx+++","xxxxxx++x","xxxxxx+x+","xxxxxx+xx","xxxxxxx++","xxxxxxx+x","xxxxxxxx+","xxxxxxxxx"));

	public Arithmetic() {}

	public String search(String input, String target) {
		
		
		int[] num = extractIntFromStringInput(input);

		int result = num[0];
		String correctOperation = "";
		// iterate through opertaion sets.
		for(String operation : operationsPowTwo) {
			// iterate through each operation in the set.
			for(int i = 0; i < operation.length(); i++){
				// extract an operation.
				String op = operation.substring(i,i+1);
				
				// apply operation.
				if(op.equals("+")) {
					result += num[i+1];
				} else {
					result *= num[i+1];
				}
			}
			// if target value.
			if(result == 9) {
				correctOperation = operation;
				break;
			}

			// reset back to starting number.
			//System.out.println(result + " " + operation);
			result = num[0];
		}
		return formatForOutput("L", num, correctOperation);
	}

	//TODO extract input as number.

	private int[] extractIntFromStringInput(String input) {

		String[] numbers = input.split(" ");
		int[] num = new int[9];

		for(int i = 0; i < numbers.length; i++) {
			num[i] = Integer.parseInt(numbers[i]);
		}
		return num;
	}

	//TODO extract order of operation method.

	//TODO extract target.

	//TODO format output public String formatForOutput(operation method, inputs, operations);
	private String formatForOutput(String oooMethod, int[] inputs, String correctOperations) {

		StringBuilder sb = new StringBuilder();

		// append first number and oooMethod
		sb.append(oooMethod);
		sb.append(" ");
		sb.append(inputs[0]);

		for(int i = 0; i < correctOperations.length(); i++) {
			String op = correctOperations.substring(i,i+1);
			sb.append(" ");
			if(op.equals("x")) {
				sb.append("*");
			} else {
				sb.append(op);
			}
			sb.append(" ");
			sb.append(inputs[i+1]);
		}

		return sb.toString();
	}
}