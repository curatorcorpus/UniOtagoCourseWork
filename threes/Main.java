/**
* @Author: Jung Woo (Noel) Park.
* Student ID: 1162424.
*/

public class Main {

	public static void main(String[] args) {

		int iterations = 10000;

		for(int z = 1; z < iterations; z++) {
			for(int x = z+1; x < iterations; x++) {
				for(int y = x+1; y < iterations; y++) {

				}
			}
		}

		System.out.println(hasCommonFactor(4,8));
	}

	private static boolean hasCommonFactor(int a, int b) {

		if(a > b) {
			for(int i = 2; i <= b; i++) {
				if(a%i == 0 && b%i == 0) {
					return true;
				}
			}
		}
		else if(a < b) {
			for(int i = 2; i <= a; i++){
				if(a%i == 0 && b%i == 0) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isSet(int x, int y, int z) {

		int lhs = LHS(x,y);
		int rhs = RHS(z);

		return lhs == rhs;
	}

	private int LHS(int x, int y) {

		int power = 2;

		return (int)Math.pow(x, power) + (int)Math.pow(y, power);
	}

	private int RHS(int z) {
		return (int)Math.pow(z, 4);
	}
}