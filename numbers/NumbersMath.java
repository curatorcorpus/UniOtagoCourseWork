/**
*	@Author: Jung Woo (Noel) Park 
*	Student ID: 1162424
*/
import java.util.Random;
import java.math.BigDecimal;

public class NumbersMath {

	public static float harmonicNumbersF(int n) {
		float harmonicNumber = 0.0f;

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

	public static BigDecimal harmonicNumbersBD(int n) {

		BigDecimal harmonicNumber = new BigDecimal(0.0);

		for(double i = 1.0; i <= (double)n; i++) {
			harmonicNumber = harmonicNumber.add(new BigDecimal(1.0/i));
		}

		return harmonicNumber;
	}	

	public static float reverseHarmonicNumbersF(int n) {
		float harmonicNumber = 0.0f;

		for(float i = n; i > 0; i--) {
			harmonicNumber += 1.0f/i;
		}

		return harmonicNumber;
	}

	public static double reverseHarmonicNumbersD(int n) {

		double harmonicNumber = 0.0;

		for(double i = n; i > 0.0; i--) {
			harmonicNumber += 1.0/i;
		}

		return harmonicNumber;
	}	

	public static BigDecimal reverseHarmonicNumbersBD(int n) {

		BigDecimal harmonicNumber = new BigDecimal(0.0);

		for(double i = n; i > 0.0; i--) {
			harmonicNumber = harmonicNumber.add(new BigDecimal(1.0/i));
		}

		return harmonicNumber;
	}	
	private static float offsetF = 10000.0f;
	private static double offsetD = 10000.0;
	public static float stdDevMeanF(int n) {
		float mean = 0.0f;

		for(float i = 1.0f; i <= (float)n; i++) {
			mean += (i+offsetF);
		}

		mean /= (float)n;

		float stdDev = 0.0f;

		for(float i = 1.0f; i <= ((float)n); i++) {
			stdDev += (float)Math.pow(((i+offsetF) - mean), 2);
		}

		stdDev = (float)Math.sqrt(stdDev / (float)n);

		return stdDev;
	}

	public static float stdDevNonMeanF(int n) {
		float sum = 0.0f;
		float squaredSum = 0.0f;

		for(float i = 1.0f; i <= (float)n; i++) {
			sum += i+offsetF;
			squaredSum += (i+offsetF)*(i+offsetF);
		//			System.out.println(squaredSum + " " + sum);
		}
		sum = sum*sum;
		sum /= (float)n;
		//System.out.println(squaredSum + " " + sum);
		return (float)Math.sqrt((squaredSum - sum) / (float)n);
	}

	public static double stdDevMeanD(int n) {

		double mean = 0.0;

		for(double i = 1.0; i <= (double)n; i++) {
			mean += (i+offsetD);
		}
		mean /= (double)n;

		double stdDev = 0.0;
		for(double i = 1.0; i <= ((double)n); i++) {
			stdDev += Math.pow(((i+offsetD)+ - mean), 2);
		}

		stdDev = Math.sqrt(stdDev / (double)n);

		return stdDev;
	}

	public static double stdDevNonMeanD(int n) {
		double sum = 0.0;
		double squaredSum = 0.0;

		for(double i = 1.0; i <= (double)n; i++) {
			sum += (i+offsetD);
			squaredSum += (i+offsetD)*(i+offsetD);
		}
		sum = sum*sum;
		sum /= (double)n;

		return Math.sqrt((squaredSum - sum) / (double)n);
	}

	public static boolean identity(double x, double y) {

		if(y == 0) {
			return false;
		}

		double identity = x;

		double result = ((x/y)-x*y)*y+x*y*y;

		return (identity == (int)result);
	}
}