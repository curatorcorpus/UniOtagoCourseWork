import cosc343.assig2.Creature;

import java.util.Random;

/**
* The MyCreate extends the cosc343 assignment 2 Creature.  Here you implement
* creatures chromosome and the agent function that maps creature percepts to
* actions.  
*
* @author  Jung-Woo (Noel) Park.
* @version 1.0
* @since   2017-04-05 
*/
public class MyCreature extends Creature {

    private static final int CHROMO_SIZE = 20;
    
    private float[] chromosome;
    
    // Random number generator
    private Random rand = new Random();

    /**
     * Initial constructor.
     */
    public MyCreature() {   
        chromosome = new float[CHROMO_SIZE];
        
        for(int i = 0; i < chromosome.length; i++) {
            chromosome[i] = rand.nextFloat();
        }
    }
    
    /**
     * Constructor for creatures.
     * 
     * @param numPercepts - number of percepts this creature will be receiving.
     * @param numActions  - the number of action output vector that creature will 
     *                      need to produce every turn.
     */
    public MyCreature(int numPercepts, int numActions) {
        chromosome = new float[CHROMO_SIZE];
        
        for(int i = 0; i < chromosome.length; i++) {
            chromosome[i] = rand.nextFloat();
        }
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
        
        final int OFFSET      = 9; // number of neighbourhoods + OFFSETs for creature, and food percepts.
        final int RND_MOV_IDX = 10;
        
        // default actions would be determined by phenotypes of individuals.
        float actions[] = new float[numExpectedActions];
        float totalPerceptVal = 0.0f;
        float totalFood       = 0.0f;
        
        int perceptIdx   = 0;
        
        while(perceptIdx < OFFSET) {
            float output = 0.0f;
            
            int pcptMonsters  = percepts[perceptIdx];
            int pcptCreatures = percepts[perceptIdx + OFFSET];
            int pcptFood      = percepts[perceptIdx + OFFSET * 2];
            
            output += -chromosome[perceptIdx] * pcptMonsters;
            output += chromosome[perceptIdx + OFFSET] * (pcptCreatures + pcptFood); // extra offset to access food location.

            // accumulate total percepts
            totalPerceptVal += pcptMonsters;
            totalFood       += pcptFood;
            
            // record action and iterate to next percept value.
            actions[perceptIdx++] = output;
        }

        //actions[RND_MOV_IDX - 1] = chromosome[CHROMO_SIZE - 2] * totalFood;
        
        // if there are no actions to take because no mons or creatures or food.
        // take a random move according to weight of chromosome.
        /*if(totalPerceptVal != 0) {
            actions[RND_MOV_IDX] = chromosome[CHROMO_SIZE - 1] * (1 / totalPerceptVal);
        }*/

        String info = "";
        for(float i : chromosome) {
            info += i + " ";
        }
        System.out.println("chromosome " + info);
        info = "";
        for(float i : actions) {
            info += i + " ";
        }
        System.out.println("actions " + info);
        System.out.println();
        
        return actions;
    }

    public float[] getChromosome() {
        return chromosome;
    }
    
    public void setChromosome(float[] chromosome) {
        this.chromosome = chromosome;
    }
}