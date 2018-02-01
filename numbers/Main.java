/**
*	@Author: Jung Woo (Noel) Park 
*	Student ID: 1162424
*/

public class Main {

	public static void main(String[] args) {

		for(int i = 1; i != 10000000; i*=10) {			
		System.out.println("n = " + i);
		System.out.println("Harmonic Numbers Float: \t" + NumbersMath.harmonicNumbersF(i));
		System.out.println("Harmonic Numbers Double: \t" + NumbersMath.harmonicNumbersD(i));

		System.out.println("Reverse Harmonic Numbers Float: \t" + NumbersMath.reverseHarmonicNumbersF(i));
		System.out.println("Reverse Harmonic Numbers Double: \t" + NumbersMath.reverseHarmonicNumbersD(i));

		System.out.println("Standard Deviation Float: " + NumbersMath.stdDevMeanF(2000000));
		System.out.println("Standard Deviation Double: " + NumbersMath.stdDevMeanD(2000000));

		System.out.println("Standard Deviation Non-Mean Float:  " + NumbersMath.stdDevNonMeanF(2000000));
		System.out.println("Standard Deviation Non-Mean Double: " + NumbersMath.stdDevNonMeanD(2000000));
		System.out.println(1000000.1f+0.01f);
	}
		/*double x = 1, y = 100000;
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
		}*/
	}
}