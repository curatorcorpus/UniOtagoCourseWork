import cosc343.assig2.Creature;

import java.util.Random;

/**
* The MyCreate extends the cosc343 assignment 2 Creature.  Here you implement
* creatures chromosome and the agent function that maps creature percepts to
* actions.  
*
* @author  
* @version 1.0
* @since   2017-04-05 
*/
public class MyCreature extends Creature {

    // Random number generator
    private Random rand = new Random();

    /* Empty constructor - might be a good idea here to put the code that 
     initialises the chromosome to some random state   */

    /**
     * Initial constructor.
     */
    public MyCreature() {
        
    }
    
    /**
     * Constructor for creatures.
     * 
     * @param numPercepts - number of percepts this creature will be receiving.
     * @param numActions  - the number of action output vector that creature will 
     *                      need to produce every turn.
     */
    public MyCreature(int numPercepts, int numActions) {
    }

    /* This function must be overridden by MyCreature, because it implements
       the AgentFunction which controls creature behavoiur.  This behaviour
       should be governed by a model (that you need to come up with) that is
       parameterise by the chromosome.  

       Input: percepts - an array of percepts
              numPercepts - the size of the array of percepts depend on the percept
                            chosen
              numExpectedAction - this number tells you what the expected size
                                  of the returned array of percepts should bes
       Returns: an array of actions 
    */
    @Override
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) {

        String info = "";
        
        for(int i : percepts) {
            info += i + " ";
        }
        System.out.println("percepts " + info);
        // This is where your chromosome gives rise to the model that maps
        // percepts to actions.  This function governs your creature's behaviour.
        // You need to figure out what model you want to use, and how you're going
        // to encode its parameters in a chromosome.

        // expected number of actions from percepts.
        float actions[] = new float[numExpectedActions];
        
        for(int i=0;i<numExpectedActions;i++) {
           actions[i] = rand.nextFloat();
        } 
        
        info = "";
        
        for(float i : actions) {
            info += i + " ";
        }
        System.out.println("actions " + info);
        return actions;
    }

}