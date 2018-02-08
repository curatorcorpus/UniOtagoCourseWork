import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

enum Color {
    NOCOLOR("No Color"),
    RED("Red"),
    BLUE("Blue"),
    GREEN("Green"),
    GOLD("Gold");
    String color;
    Color(String c) { this.color = c; }
}

public class ColorOfTheDay {
	
    static HashMap<Long,Color> nonPrimeMap = new HashMap<Long,Color>();

    public static void main(String[] args) {
    	
    }
    
    /**
     * Recursively searches for the color of a day and memoises non-prime days to save 
     * search time.  
     * Given a day, it first checks to see if it's a seen non-prime day, and returns the 
     * color if it has been seen.
     * It then checks to see if it's a prime day, in which case it can immediately 
     * return the color.
     * It hasn't seen it before and it's not a prime day, we have to figure out what the 
     * color of the prime day is, so we get the factor days, and count the non-gold days
     * to determine the color of the day.
     */ 
	
    static Color colorOfTheDay(long day) {
        if (nonPrimeMap.containsKey(day)) {
            return nonPrimeMap.get(day);
        } else if (isPrime(day)) {
            return colorOfPrime(day);
        } else {
            int greenCount = 0;
            int redCount = 0;
            int blueCount = 0;
            Color colorOfTheDay;
			
            ArrayList<Long> factorDays = factorsOf(day, false);
			
            for (long factorDay : factorDays) {
                colorOfTheDay = colorOfTheDay(factorDay);
                if (colorOfTheDay == Color.RED) redCount++;
                else if (colorOfTheDay == Color.GREEN) greenCount++;
                else if (colorOfTheDay == Color.GOLD) continue;
                else blueCount++;
            }
			
            colorOfTheDay = colorOfNonPrime(redCount, greenCount, blueCount);
            nonPrimeMap.put(day, colorOfTheDay);
            return colorOfTheDay;
        }
    }
    
    /**
     * Returns the color of a prime day.
     */
	
    static Color colorOfPrime(long day) {
        if (day == 1) return Color.NOCOLOR;
        if (day == 7) return Color.GOLD;
        if (day % 7 == 1 || day % 7 == 4) return Color.RED;
        if (day % 7 == 2 || day % 7 == 5) return Color.GREEN;
        if (day % 7 == 3 || day % 7 == 6) return Color.BLUE;
        return null;
    }	
    
    /**
     * Pass this method red, green and blue counts for a nonPrime day's factor day
     * and this method will return the color of the nonPrime day.
     */ 
	
    static Color colorOfNonPrime(int r, int g, int b) {
        if (r == g && g == b && b == r) {
            return Color.GOLD;
        } else if (r == g && g != b) {
            return Color.BLUE;
        } else if (g == b && b != r) {
            return Color.RED;
        } else if (b == r && r != g) {
            return Color.GREEN;
        } else {
            if (r > g && r > b) {
                return Color.RED;
            } else if (g > b && g > r) {
                return Color.GREEN;
            } else {
                return Color.BLUE;
            }
        }
    }
	
    static boolean isPrime(long n) {
        if (n == 1) return true;
    	if (n < 2) return false;
    	if (n == 2 || n == 3) return true;
    	if( n%2 == 0 || n%3 == 0) return false;
    	long sqrtN = (long)Math.sqrt(n)+1;
    	for (long i = 6L; i <= sqrtN; i += 6) {
            if(n%(i-1) == 0 || n%(i+1) == 0) return false;
    	}
    	return true;
    }
    
    /**
     * Returns an list of factor days for a given day.
     * Set reinterpreted to true for "Breaking news" section of etude.
     */
	
    static ArrayList<Long> factorsOf(long day, boolean reinterpreted) {
        ArrayList<Long> factors = new ArrayList<Long>();
        for (long i = 2; i*i <= day; i++) {
            if (day % i == 0 && reinterpreted) {
                factors.add(day-i);
                factors.add(day-day/i);
            } else if (day % i == 0 && !reinterpreted) {
            	factors.add(i);
                factors.add(day/i);
            }
        }
        Collections.sort(factors);
        return factors;
    }
    
    
}