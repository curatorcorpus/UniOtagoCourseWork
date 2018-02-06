import java.util.Random;

public class TestRollHandler {

    final int ITERATIONS = 1000;
    final int NUM_DICE = 6;
    
	
	
    /**
     * Call either test() or fixedTest() from to test handleRoll() method.
     */
    public static void main(String[] args) {
        TestRollHandler t = new TestRollHandler();
		
        t.test();
    }

    /** 
     * If you want to start your test with set of pre-set die, give this method
     * an array of 6 integers.
     * 
     * Prints out the number of Rolls it took your method to produce a  complete set. 
     */
    private void fixedTest(int[] dice) {
        int numberOfRolls = 0;
        RollHandler r = new RollHandler(dice);
        while (!r.isComplete()) {
            int roll = new Random().nextInt(6) + 1;
			
            int index = r.handleRoll(roll);
			
            if (index >= 0 && index <= 6) {
                r.dice[index] = roll;
            }
            numberOfRolls++;
        }
        System.out.println("Number of Rolls: " + numberOfRolls);
    }
	
    /** 
     * Use this method if you want to simulate a test that starts off with a 
     * random set of dice.
     * 
     * Prints out the number of Rolls it took your method to produce a  complete set. 
     */
    private void test() {
        int totalNumRolls = 0;

        for (int e = 0; e < ITERATIONS; e++) {

            int numberOfRolls = 0;
            int[] randomDice = new int[NUM_DICE];
		
            for (int i = 0; i < NUM_DICE; i++) {
                randomDice[i] = new Random().nextInt(6) + 1;
            }
            
            RollHandler r = new RollHandler(randomDice);
            while (!r.isComplete()) {
                int roll = new Random().nextInt(6) + 1;
			
                int index = r.handleRoll(roll);
			
                if (index >= 0 && index <= 5) {
                    r.dice[index] = roll;
                }
                numberOfRolls++;
            }
            totalNumRolls += numberOfRolls;
        }
        double averageNumRolls = (float)totalNumRolls/(float)ITERATIONS;
        System.out.println("Average Number of Rolls: " + averageNumRolls);
	
    }

}
