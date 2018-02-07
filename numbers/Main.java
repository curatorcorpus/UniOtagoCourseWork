/**
*	@Author: Jung Woo (Noel) Park 
*	Student ID: 1162424
*/
import java.util.Random;

public class Main {

	static double offset = 1E6;

	public static void main(String[] args) {

		double[] dataset = new double[] {3,6,12,20,33,40,60,100,30,53};
		double[] dataset1 = new double[] {0,1,12,20,33,40E30,60E9,100E70,30E50,53E60};
		double[] dataset2 = new double[] {13E-50,12E-60,17E-20,33E-15,40E30,60E9,100E70,30E50,53E60};
		double[] dataset3 = new double[] {13,12E60,17E20,33E15,40E30,60E9,100E70,30E50,53E60};
		double[] dataset4 = new double[] {1.3,12E-60,17E-20,33E-15,40E-30,60E-9,100E-70,30E-50,53E-60};
		double[] random = new double[1000000];

		Random rand = new Random(12000);

		for(int i = 0; i < 1000000; i++) {
			//System.out.println(rand.nextDouble()*13E13);
			random[i] = rand.nextDouble();
		}

		System.out.println(stdDevMeanD(dataset));
		System.out.println(stdDevNonMeanD(dataset));
		System.out.println();		
		System.out.println(stdDevMeanD(dataset1));
		System.out.println(stdDevNonMeanD(dataset1));
				System.out.println();		
		System.out.println(stdDevMeanD(dataset2));
		System.out.println(stdDevNonMeanD(dataset2));
				System.out.println();		
		System.out.println(stdDevMeanD(dataset3));
		System.out.println(stdDevNonMeanD(dataset3));
				System.out.println();		
		System.out.println(stdDevMeanD(dataset4));
		System.out.println(stdDevNonMeanD(dataset4));	
				System.out.println();			
		System.out.println(stdDevMeanD(random));
		System.out.println(stdDevNonMeanD(random));		
	}


	public static double stdDevMeanD(double[] dataset) {
		double mean = 0.0;

		for(int i = 0; i < dataset.length; i++) {
			double value = dataset[i];

			mean += (value+offset);
		}
		mean /= (double)dataset.length;

		double stdDev = 0.0;
		for(int i = 0; i < dataset.length; i++) {
			double value = dataset[i];
		//	System.out.println("std "+((value+offset)));
			stdDev += Math.pow(((value+offset)-mean), 2.0);
		}
		//		System.out.println("std "+stdDev);

		stdDev = Math.sqrt(stdDev/(double)dataset.length);

		return stdDev;
	}

	public static double stdDevNonMeanD(double[] dataset) {
		double sum = 0.0;
		double squaredSum = 0.0;

		for(int i = 0; i < dataset.length; i++) {
			double value = dataset[i];
			sum += (value+offset);
			squaredSum += (value+offset)*(value+offset);
		}
		sum = sum*sum;
		sum /= (double)dataset.length;

		return Math.sqrt((squaredSum - sum) / (double)dataset.length);
	}
}