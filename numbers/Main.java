/**
*	@Author: Jung Woo (Noel) Park 
*	Student ID: 1162424
*/

public class Main {

	public static void main(String[] args) {

		System.out.println("Harmonic Numbers Float: \t" + NumbersMath.harmonicNumbersF(50));
		System.out.println("Harmonic Numbers Double: \t" + NumbersMath.harmonicNumbersD(50));

		System.out.println("Reverse Harmonic Numbers Float: \t" + NumbersMath.reverseHarmonicNumbersF(50));
		System.out.println("Reverse Harmonic Numbers Double: \t" + NumbersMath.reverseHarmonicNumbersD(50));

		System.out.println("Standard Deviation Float: " + NumbersMath.stdDevMeanF(2000000));
		System.out.println("Standard Deviation Double: " + NumbersMath.stdDevMeanD(2000000));

		System.out.println("Standard Deviation Non-Mean Float:  " + NumbersMath.stdDevNonMeanF(2000000));
		System.out.println("Standard Deviation Non-Mean Double: " + NumbersMath.stdDevNonMeanD(2000000));
		double x = 1, y = 100000;
		for(double i = 0; i <= 100; i++) {
		System.out.println("Is Idenity: x: " + x + " y: " + i + " " + NumbersMath.identity(x, i));
		//if(!NumbersMath.identity(x, i)) {
			System.out.println( x/i );
			System.out.println((( x / i) - x * i));
			System.out.println(((( x / i) - x * i) * i));
			System.out.println(x * i * i);
			System.out.println(((( x / i) - x * i) * i) + " " + (x * i * i));
			System.out.println((((( x / i) - x * i) * i) + x * i * i));
					System.out.println();

		//}
		}
	}
}