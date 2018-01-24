/**
*	@Author: Jung Woo (Noel) Park 
*	Student ID: 1162424
*/

public class NumbersMath {


	public static float harmonicNumbersF(int n) {
		float harmonicNumber = 0.0;

		for(float i = 1.0f; i <= (float)n; i++) {
			harmonicNumber += 1.0f/i;
		}

		return harmonicNumber;
	}

	public static double harmonicNumbersD(int n) {

		double harmonicNumber = 0.0;

		for(double i = 1.0; i <= (double)n; i++) {
			harmonicNumber += 1.0/i;
		}

		return harmonicNumber;
	}	

	public static float stdDevMean(int n) {
		float mean = 0.0f;

		for(float i = 1.0f; i <= (float)n; i++) {
			mean += (float) i;
		}

		mean /= (float)n;

		float stdDev = 0.0;

		for(float i = 1.0; i <= ((float)n); i++) {
			stdDev += (float)Math.pow((i - mean), 2);
		}

		stdDev = (float)Math.sqrt(stdDev / (float)n);

		return stdDev;
	}

	public static float stdDevNonMean() {
		float mean = 0.0;

		for(float i = 1; i <= (float)n; i++) {
			mean += (float) i;
		}

		float sqrSum = 0;

		for(int i = 1; i <= n; i++) {
			sqrSum += (float)Math.pow(i, 2);
		}

		mean = (float)Math.pow(mean, 2);
		mean /= (float)n;

		float stdDev = (float)Math.sqrt(((sqrSum - mean) / (float)n));

		return stdDev;
	}

	public static double stdDevMean(int n) {

		double mean = 0.0;

		for(double i = 1.0; i <= (double)n; i++) {
			mean += (double) i;
		}

		mean /= (double)n;

		double stdDev = 0.0;

		for(double i = 1.0; i <= ((double)n); i++) {
			stdDev += Math.pow((i - mean), 2);
		}

		stdDev = Math.sqrt(stdDev / (double)n);

		return stdDev;
	}

	public static double stdDevNonMean(int n) {
		double mean = 0.0;

		for(double i = 1.0; i <= (double)n; i++) {
			mean += (double) i;
		}

		double sqrSum = 0;

		for(int i = 1; i <= n; i++) {
			sqrSum += Math.pow(i, 2);
		}

		mean = Math.pow(mean, 2);
		mean /= (double)n;

		double stdDev = Math.sqrt(((sqrSum - mean) / (double)n));

		return stdDev;
	}

	public static boolean identity(int x, int y) {

		if(y != 0) {
			return false;
		}

		int identity = x;

		double result = (((double) x / (double)y) - x * y) * y + x * y *y;

		return (identity == result);
	}
}