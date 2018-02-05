/**
* @Author: Jung Woo (Noel) Park.
* Student ID: 1162424.
*/

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		int count = 0;

		outer: for(long x = 1; true; x++) {
			long xSqrd = x*x;
			for(long z = 1; z < x; z++) {
				long rhs = z*z*z*z+1;

				long tmpRHS = rhs-xSqrd;
				long y = (long)Math.sqrt(tmpRHS);

				if((y*y) == tmpRHS) {

					if(gcd(z,x) != 1) continue;
					if(gcd(x,y) != 1) continue;
					if(gcd(z,y) != 1) continue;

					if(z < x && x < y) {
						count++;
						System.out.println(count + " " + x + " " + y + " " + z);
					}
				}

				if(count == 70) {
					break outer;
				}
			}
		}
		System.out.println();
		count = 0;
		outer: for(long z = 1; true; z++) {
			long rhs = z*z*z*z+1;
			for(long x = z+1; true; x++) {
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

					if(z < x && x < y) {
						count++;
						System.out.println(count + " " + x + " " + y + " " + z);
					}
				}

				if(count == 70) {
					break outer;
				}

			}
		}
	}

    private static long gcd(long a, long b) {
        return (b==0) ? a : gcd(b,a%b);
    }
}