/**
* @Author: Jung Woo (Noel) Park.
* Student ID: 1162424.
*/

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		List<String> zFirst = new ArrayList<String>();

		int count = 0;

		outer: for(long z = 1;; z++) {
			long rhs = z*z*z*z+1;
			for(long x = z+1;; x++) {
				long xSqrd = x*x;

				if((2*xSqrd) >= rhs) {
					break;
				}

				long tmpRHS = rhs-xSqrd;
				long y = (long)Math.sqrt(tmpRHS);

				if((y*y) == tmpRHS) {

					if(gcd(z,x) != 1) continue;
					if(gcd(x,y) != 1) continue;
					if(gcd(z,y) != 1) continue;

					count++;
					System.out.println(count + " " + z + " " + x + " " + y);
				}

				if(count == 70) {
					break outer;
				}

			}
		}
	}

	private static int LHS(int x, int y) {

		int power = 2;

		return (int)Math.pow(x, power) + (int)Math.pow(y, power);
	}

    private static long gcd(long a, long b) {
        return (b==0) ? a : gcd(b,a%b);
    }

 /*   private static void smallestZ() {
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
    }*/
}