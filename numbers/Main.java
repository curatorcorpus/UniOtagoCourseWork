/**
*	@Author: Jung Woo (Noel) Park 
*	Student ID: 1162424
*/
public class Main {

	public static void main(String[] args) {

		for(int i = 1; i != 10000000; i*=10) {			
		System.out.println("n = " + i);
		//System.out.println("Harmonic Numbers Float: \t" + NumbersMath.harmonicNumbersF(i));
		//System.out.println("Harmonic Numbers Double: \t" + NumbersMath.harmonicNumbersD(i));

	//	System.out.println("Reverse Harmonic Numbers Float: \t" + NumbersMath.reverseHarmonicNumbersF(i));
	//	System.out.println("Reverse Harmonic Numbers Double: \t" + NumbersMath.reverseHarmonicNumbersD(i));

		System.out.println("Standard Deviation Float: " + NumbersMath.stdDevMeanF(i));
		System.out.println("Standard Deviation Non-Mean Float:  " + NumbersMath.stdDevNonMeanF(i));
		System.out.println("Standard Deviation Double: " + NumbersMath.stdDevMeanD(i));
		System.out.println("Standard Deviation Non-Mean Double: " + NumbersMath.stdDevNonMeanD(i));
		

			for(int y = 1; y < 1000; y++) {
				boolean s = NumbersMath.identity(1,y);
				if(!s) {
					System.out.println(1 + " " + y + " " + 1/(double)y + " " + 1*y);
				}
			}
		}
	}
}