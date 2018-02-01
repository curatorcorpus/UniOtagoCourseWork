import java.util.ArrayList;

public class CountUp {

	public static void main(String[] args) {
		
		
		if (args[0].equals(args[1])) {
			System.out.println(1);
			System.exit(0);
		}

		long n = Long.parseLong(args[0]);
		long k = Long.parseLong(args[1]);
        long x = n - k;
        long result;
        long[] nFac;
        long[] kFac;
        long[] xFac;
        
        
        if (k > x) {
            nFac = factorize(n, k);
            xFac = factorize(x, 0);
            result = calculate(nFac, xFac);
        } else {
            nFac = factorize(n, x);
            kFac = factorize(k, 0);
            result = calculate(nFac, kFac);
        }
        
        System.out.println(result);
	}
    
    static long calculate(long[] numerator, long[] denominator) {
    	long total = 1;
    	ArrayList<Long> remainDivisors = getFactors(denominator);
    	while (remainDivisors.size() > 0) {
    		for (int i = 0; i < remainDivisors.size(); i++) {
    			for (int j = 0; j < numerator.length; j++) {
    				if (numerator[j] % remainDivisors.get(i) == 0) {
    					numerator[j] = numerator[j] / remainDivisors.remove(i);
    					break;
    				}
    			}
    		}
    	}
		
    	
    	for (long num : numerator) {
    		if (total > 0) {
    			total *= num;
    		} else {
    			System.out.println("Impossible");
                System.exit(1);
    		}
    	}
    	if (total < 0) {
    		System.out.println("Impossible");
            System.exit(1);
    	}
    	return total;
    	
    }
    
    static void printArray(long[] a) {
        System.out.print("[");
        for (long n : a) System.out.print(n + " ");
        System.out.println("]");
    }
	
	static long factorial(long x) {
		if (x == 1) return x;
		else return x * factorial(x-1);
	}
	
	static ArrayList<Long> getFactors(long[] denominator) {
		ArrayList<Long> factors = new ArrayList<Long>();
		for (long divisor : denominator) {
			long d = divisor;
			long pFactor = 2;
			while (d > 1) {
				if (d % pFactor == 0) {
					d /= pFactor;
					factors.add(pFactor);
					pFactor = 2;
				} else {
					pFactor++;
				}
			}
		}
		return factors;
	}
	
	
	
	
	static long[] factorize(long n, long x) {
		ArrayList<Long> factors = new ArrayList<Long>();
		while (n > x) {
			factors.add(n);
			n--;
		}
        long[] newArray = new long[factors.size()];
        for (int i = 0; i < factors.size(); i++) {
            newArray[i] = factors.get(i);
        }
        return newArray;
	}
	
	
	
	
	static long multiplyFactors(ArrayList<Long> factors) {
		
		long total = 1;
		for (long factor : factors) {
				total *= factor;
		}
		
		return total;
	}
	

}
