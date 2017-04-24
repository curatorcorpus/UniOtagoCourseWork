import cosc343.assig2.Creature;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        this.chromosome = new Chromosome();
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
        
        Map<Integer, Float> actionMapping = new HashMap<>();
        List<Integer>       sensory       = new ArrayList<>();
        
        // default actions would be determined by genotypes of individuals.
        float actions[] = new float[numExpectedActions];
        
        boolean monsterNearby  = false;
        boolean creatureNearby = false;
        boolean foodNearby     = false;
        
        int perceptSections = percepts.length / 3;
        int perceptOffset   = 9;
        
        for(int i = 0; i < perceptSections; i++) {
            
            int sensingMons = percepts[i];
            int sensingCrea = percepts[i + perceptOffset];
            int sensingFood = percepts[i + perceptOffset * 2];
            
            // add sensory information
            sensory.add(sensingMons);
            sensory.add(sensingCrea);
            sensory.add(sensingFood);
            
            // deduce sensory information
            if(sensingMons == 1) monsterNearby  = true;
            if(sensingCrea == 1) creatureNearby = true;
            if(sensingFood == 1) foodNearby     = true;
        }
        
        for(int location : sensory) {
            
            float dirGenSens = chromosome.getDirectionalSens(location);            
            int dirVal = chromosome.getDirectionVal(location);
           
            String direction      = chromosome.getDirectionString(dirVal);
            int inverseDir        = chromosome.getInverseDirection(direction);
            int inversePerceptDir = chromosome.dirValToPerceptLoc(inverseDir);
            
            if(monsterNearby) {
                
            }
            if(creatureNearby) {
                
            }
            if(foodNearby) {
                
            }
            
            actions[location] = dirGenSens;
        }
        
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