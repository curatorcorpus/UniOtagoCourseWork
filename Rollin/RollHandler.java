import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class RollHandler extends Rollin {

    public RollHandler(int[] dice) {
        super(dice);
    }

    public int handleRoll(int roll) {
        // Replace each die with roll, check if complete
		for (int i = 0; i < dice.length; i++) {
			int temp = dice[i];
			dice[i] = roll;
			if (isComplete()) 
				return i;
			else 
				dice[i] = temp;
		}
		/* Score each die. Increment each die's score for every other die 
		 * that is one less, equal, or one greater than it.
		 */
		int[] die_scores = new int[6];
		for (int i = 0; i < dice.length; i++) 
			for (int j = 0; j < dice.length; j++) 
				if (dice[i] == dice[j] || dice[i] == dice[j]+1 || dice[i] == dice[j]-1)
					die_scores[i]++;
					
		// Return index of die with the smallest score.
		int minIndex = 0;
		for (int i = 1; i < die_scores.length; i++)
			if (die_scores[i] < die_scores[minIndex]) 
				minIndex = i;
		
		return minIndex;
    }
}
