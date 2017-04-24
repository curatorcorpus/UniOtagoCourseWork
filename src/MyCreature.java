import cosc343.assig2.Creature;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

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
    
    private static final boolean DEBUG = true; 
    
    private Chromosome chromosome;
    
    private double currentFitness = 0.0;
    
    public MyCreature(Chromosome chromosome) {
        this.chromosome = chromosome;
    }
    
    /**
     * 
     * @param numPercepts - number of percepts this creature will be receiving.
     * @param numActions  - the number of action output vector that creature will 
     *                      need to produce every turn.
     */
    public MyCreature(int numPercepts, int numActions) {
        this.chromosome = chromosome;
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
        // default actions would be determined by phenotypes of individuals.
        float actions[] = new float[numExpectedActions];
        
        return actions;
    }
    
    public Chromosome getChromosome() {
        return this.chromosome;
    }
    
    public void Chromosome(Chromosome chromosome) {
        this.chromosome = chromosome;
    }
    
    public double getFitness() {
        return currentFitness;
    }
    
    public void setFitness(double currentFitness) {
        this.currentFitness = currentFitness;
    }
}