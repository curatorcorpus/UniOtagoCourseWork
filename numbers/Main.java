/**
*	@Author: Jung Woo (Noel) Park 
*	Student ID: 1162424
*/

public class Main {

	public static void main(String[] args) {

		System.out.println("Harmonic Numbers Float: \t" + NumbersMath.harmonicNumbersF(10));
		System.out.println("Harmonic Numbers Double: \t" + NumbersMath.harmonicNumbersD(10));

		System.out.println("Standard Deviation Float: " + NumbersMath.stdDevMeanF(2000000));
		System.out.println("Standard Deviation Double: " + NumbersMath.stdDevMeanD(2000000));

		System.out.println("Standard Deviation Non-Mean Float:  " + NumbersMath.stdDevNonMeanF(2000000));
		System.out.println("Standard Deviation Non-Mean Double: " + NumbersMath.stdDevNonMeanD(2000000));
		int x = 2, y = 1;
		System.out.println("Is Idenity: x: " + x + " y: " + y + " " + NumbersMath.identity(x, y));
	}
}