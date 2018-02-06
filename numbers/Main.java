/**
*	@Author: Jung Woo (Noel) Park 
*	Student ID: 1162424
*/
public class Main {

	public static void main(String[] args) {

		/*for(int i = 1; i != 10000000; i*=10) {			
		System.out.println("n = " + i);
		System.out.println("Harmonic Numbers Float: \t" + NumbersMath.harmonicNumbersF(i));
		System.out.println("Harmonic Numbers Double: \t" + NumbersMath.harmonicNumbersD(i));
		System.out.println("Reverse Harmonic Numbers Float: \t" + NumbersMath.reverseHarmonicNumbersF(i));
		System.out.println("Reverse Harmonic Numbers Double: \t" + NumbersMath.reverseHarmonicNumbersD(i));
		System.out.println("Reverse Harmonic DB: \t" + NumbersMath.reverseHarmonicNumbersBD(i));
		}*/
		//System.out.println((3/7 - 3*7)*7 + 3*7*7);
/*
		System.out.println("Standard Deviation Float: " + NumbersMath.stdDevMeanF(i));
		System.out.println("Standard Deviation Non-Mean Float:  " + NumbersMath.stdDevNonMeanF(i));
		System.out.println("Standard Deviation Double: " + NumbersMath.stdDevMeanD(i));
		System.out.println("Standard Deviation Non-Mean Double: " + NumbersMath.stdDevNonMeanD(i));
		*/

			for(int x = 1; x < 1000; x++) {
				for(int y = 1; y < 10; y++) {
					boolean s = NumbersMath.identity(x,y);
					if(!s) {
						//System.out.println(x + " " + y + " " + x/(double)y + " " + x*y);
						int result = (int)((((double)x / (double)y) - x * y) * y + x * y *y);
						System.out.println(result + ", " + x);
					}
				}
			}
	}
}