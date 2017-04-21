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

    private static final int CHROMO_SIZE = 11;
    private static final int OFFSET = 9; // number of neighbourhoods + OFFSETs for creature, and food percepts.
    
    private int prevAction = 0;
    private List<Integer> prevFoodZones = null;
    
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
    
    public MyCreature(float[] chromosome) {
        this.chromosome = chromosome;
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
        
        // zones
        List<Integer> dangerZones = new ArrayList<>();
        List<Integer> friendlyZones = new ArrayList<>();
        List<Integer> foodZones   = new ArrayList<>();
        List<Integer> foodQuality = new ArrayList<>();
        
        // default actions would be determined by phenotypes of individuals.
        float actions[] = new float[numExpectedActions];
        
        int perceptIdx = 0;

        // sense all locations.
        while(perceptIdx < OFFSET) {
            
            // obtain neighbourhood information
            int pcptMonsters  = percepts[perceptIdx];
            int pcptCreatures = percepts[perceptIdx + OFFSET];
            int pcptFood      = percepts[perceptIdx + OFFSET * 2];
            
            // find locations of danger
            if(pcptMonsters == 1) {
                dangerZones.add(perceptIdx);
            }
            
            // find location of friendly zones (other creatures)
            if(pcptCreatures == 1) {
                friendlyZones.add(perceptIdx);
            }
            
            // find locations of energy sources (food)
            if(pcptFood == 1 || pcptFood == 2) {
                friendlyZones.add(perceptIdx);
                foodZones.add(perceptIdx);
                //foodQuality.add(perceptIdx, pcptFood);
            }
            
            perceptIdx++;
        }

        boolean isDangerZonesEmpty   = dangerZones.isEmpty();
        boolean isFriendlyZonesEmtpy = friendlyZones.isEmpty();
        boolean isFoodZonesEmpty     = foodZones.isEmpty();
        
        // if there are nearby entities, react.
        if(!isFriendlyZonesEmtpy || !isDangerZonesEmpty) {
        
            // determines locations.
            List<Integer> undangerousZones = determineUndangerZones(dangerZones);
            
            /*
            String info = "";
            for(int i : dangerZones) info += i + " ";
            System.out.println("dangerZones " + info);
            
            info = "";
            for(int i : friendZones) info += i + " ";
            System.out.println("friendZones " + info);            
            
            info = "";
            for(int i : foodZones) info += i + " ";
            System.out.println("foodZones " + info);            
            
            info = "";
            for(int i : undangerousZones) info += i + " ";
            System.out.println("UndangerousZones " + info);
            
            info = "";
            for(int i : neighbourZones) info += i + " ";
            System.out.println("neighbourZones " + info);            

            info = "";
            for(int i : energyZones) info += i + " ";
            System.out.println("energyZones " + info);                
            System.out.println();
            */
            
            // if there are only monster around then use undangerous zones.
            if(!isDangerZonesEmpty && isFriendlyZonesEmtpy) {
                
                for(int loc : undangerousZones) {
                    actions[loc] = chromosome[loc];
                }
            } 
            
            // if there are friendly zones but no dangerous zones, just use friendly zones.
            else if(isDangerZonesEmpty && !isFriendlyZonesEmtpy) {
                
                for(int loc : friendlyZones) {
                    actions[loc] = chromosome[loc];
                }
                
                // also have chance to make random move because we are in no danger.
                actions[OFFSET + 1] = chromosome[OFFSET + 1];  
            }
            
            // both zones are not empty, then just use friendly zones.
            else if(!isDangerZonesEmpty && !isFriendlyZonesEmtpy) {
                
                for(int loc : friendlyZones) {
                    actions[loc] = chromosome[loc];
                }
                
                // also have chance to make random move because we are in no danger.
                actions[OFFSET + 1] = chromosome[OFFSET + 1];                
            }
            
            // if foodzones are not empty
            if(!isFoodZonesEmpty) {
                
                // if prev move as same as index of a food source, then eat.                
                if(prevFoodZones != null) {
                    if(prevFoodZones.contains(prevAction)) {
                        actions[OFFSET] = chromosome[OFFSET];
                    }
                }
                
                prevFoodZones = foodZones;
            }
            
            // otherwise does random move.
        } else {
            actions[OFFSET + 1] = chromosome[OFFSET + 1];
        }
        
        int act = 0;
        int maxMoveIdx = 0;
        float maxMove = 0.0f;
        
        // determine action
        while(act < actions.length) {
            
            float determinedAction = actions[act];
            
            if(determinedAction > maxMove) {
                maxMove = determinedAction;
                maxMoveIdx = act;
            }
            
            act++;
        }
        
        prevAction = maxMoveIdx;
        
        return actions;
    }

    public float[] getChromosome() {
        return chromosome;
    }
    
    public void setChromosome(float[] chromosome) {
        this.chromosome = chromosome;
    }
    
    private List<Integer> determineUndangerZones(List<Integer> dangerZones) {
        List<Integer> availableZones = new ArrayList<>();
        
        int i = 0;
        
        while(i < OFFSET) {
            // if the location is considered a danger zone, then skip.
            if(!dangerZones.contains(i)) {
                availableZones.add(i);
            }
            
            i++;
        }
        
        return availableZones;
    }
}