/**
* @Author: Jung Woo (Noel) Park.
* Student ID: 1162424.
*/

public class Main {

	public static void main(String[] args) {

		int iterations = 1000;

		for(int z = 1; z < iterations; z++) {
			for(int x = z+1; x < iterations; x++) {
				for(int y = x+1; y < iterations; y++) {

					if(hasCommonFactor(x,y)) {
						continue;
					}
					if(hasCommonFactor(x,z)) {
						continue;
					}
					if(hasCommonFactor(y,z)) {
						continue;
					}
					if(isSet(x, y, z)) {
						System.out.println(z + " " + x + " " + y);
					}
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

	private static boolean isSet(int x, int y, int z) {

		if(z < x && x < y) {

			int lhs = LHS(x,y);
			int rhs = RHS(z);

			return lhs == rhs;
		}
		return false;
	}

	private static int LHS(int x, int y) {

		int power = 2;

		return (int)Math.pow(x, power) + (int)Math.pow(y, power);
	}

	private static int RHS(int z) {
		return (int)Math.pow(z, 4) + 1;
	}
}