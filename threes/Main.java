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

public class threes {


    private static long gcd(long a, long b) {
        return (b==0) ? a : gcd(b,a%b);
    }
    private static long gcd(long a, long b, long c) {
        return gcd(gcd(a,b),c);
    }

    private static void smallestZ() {
        System.out.println("First 350 Sets in order of Smallest Z");
        int count = 0;
        long z = 1;

        while(count<350){
            for(long y = 2; y<200000; y++) {
            long x = (int)Math.sqrt(z*z + y*y);

            if(x*x == z*z + y*y && x%5==0 && gcd(z,y,x)==1) {
                System.out.println(x + " " + y + " " + z);
                count++;
            }
            y++;
        }
            z++;
        }
    }

    private static void evenY() {
        System.out.println("First 350 Sets in order of Unique Even Y");
        int count =0;

        for (int y2 = 2; count < 140; y2++) {
            int y = y2*2;
            int num = y2*y2;
            if((y*y + (num-1)*(num-1)) == (num+1)*(num+1) && ((num+1)%5==0) && gcd(y,num-1,num+1)==1) {
                System.out.println((num+1) + " " + y + " " + (num-1));
                count++;
            }
        }
    }


    private static void increasingX() {
        System.out.println("First 350 Sets in increasing order of X");
        int count = 0;
        long y;
        long x = 5;

        while(count<350){
            for(long z = 1; z<x; z++) {
                y = (int) Math.sqrt(x*x - z*z);
                if((y*y + z*z) == x*x && z<y && gcd(x,y,z)==1) {
                    System.out.println(x + " " + y + " " + z);
                    count++;
                }
            }
            x+=5;
        }
    }

    public static void main(String[] args) {
        increasingX();
        evenY();
        smallestZ();
    }

}