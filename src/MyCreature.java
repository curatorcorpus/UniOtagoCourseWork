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
    
    private static final int CHROMO_SIZE = 22;
    private static final int OFFSET = 9; // number of neighbourhoods + OFFSETs for creature, and food percepts.
    
    private int prevAction = 0;
    private List<Integer> prevFoodZones = null;
    
    private double currentFitness = 0.0;
    
    /**
     * Data field that encodes chromosomes.
     * Indicies 0 - 8:  encodes parameters related to danger zones.
     * Indicies 9 - 17: encodes parameters related to friendly zones.
     * Indicies 18:     encodes parameter related to eating food.
     * Indicies 19:     encodes parameter related to random move.
     * Indicies 20:     encodes parameter related to prioritizing food or avoid monsters.
     * Indicies 21:     encodes parameter related to eat or do another move.
     */
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
        
        int[] foodQuality = new int[OFFSET];
        
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
                foodQuality[perceptIdx] = pcptFood;
            }
            
            perceptIdx++;
        }

        boolean isDangerZonesEmpty   = dangerZones.isEmpty();
        boolean isFriendlyZonesEmtpy = friendlyZones.isEmpty();
        boolean isFoodZonesEmpty     = foodZones.isEmpty();
        
        // if there are nearby entities, react.
        /*if(!isFriendlyZonesEmtpy || !isDangerZonesEmpty) {
        
            // determines locations.
            List<Integer> undangerousZones = determineUndangerZones(dangerZones);
            
            // if there are only monster around then use undangerous zones.
            if(!isDangerZonesEmpty && isFriendlyZonesEmtpy) {
                for(int loc : undangerousZones) {
                    actions[loc] = chromosome[loc];
                }
            } 
            
            // if there are friendly zones but no dangerous zones, just use friendly zones.
            else if(isDangerZonesEmpty && !isFriendlyZonesEmtpy) {
                
                for(int loc : friendlyZones) {
                    actions[loc] = chromosome[loc + OFFSET];
                }
            }
            
            // both zones are not empty, then just use friendly zones.
            else if(!isDangerZonesEmpty && !isFriendlyZonesEmtpy) {
                
                // 0.6 threshold has slight bias towards eating food over avoiding monsters.
                /*if(chromosome[OFFSET*2+2] < 0.5) {
                    for(int loc : friendlyZones) {
                        actions[loc] = chromosome[loc + OFFSET];
                    }     
                } else {
                     for(int loc : undangerousZones) {
                        actions[loc] = chromosome[loc + OFFSET];
                    }                      
                }*/
/*
                    if(chromosome[OFFSET*2+2] < 0.6) {
                        for(int loc : friendlyZones) {
                            actions[loc] = chromosome[loc + OFFSET];
                        }     
                    } else {
                        for(int loc : undangerousZones) {
                           actions[loc] = chromosome[loc];
                       }                      
                   }
                //}
                
            }
            
            // if foodzones are not empty
            if(!isFoodZonesEmpty) {
                
                // if prev move as same as index of a food source, then eat.                
                if(prevFoodZones != null) {
                    if(prevFoodZones.contains(prevAction) && chromosome[OFFSET*2+3] < 0.5) {
                        actions[OFFSET] = chromosome[CHROMO_SIZE - 1];
                    }
                }
                prevFoodZones = foodZones;
            }*/

    // if there are nearby entities, react.
        if(!isFriendlyZonesEmtpy || !isDangerZonesEmpty) {
        
            // determines locations.
            List<Integer> undangerousZones = determineUndangerZones(dangerZones);
            
            // if there are only monster around then use undangerous zones.
            if(!isDangerZonesEmpty && isFriendlyZonesEmtpy) {
                for(int loc : undangerousZones) {
                    actions[loc] = chromosome[loc] * 
                                   chromosome[loc + OFFSET];
                }
            } 
            
            // if there are friendly zones but no dangerous zones, just use friendly zones.
            else if(isDangerZonesEmpty && !isFriendlyZonesEmtpy) {
                for(int loc : friendlyZones) {
                    actions[loc] = chromosome[loc] * 
                                   chromosome[loc + OFFSET];
                }
            }
            
            // both zones are not empty, then just use friendly zones.
            else if(!isDangerZonesEmpty && !isFriendlyZonesEmtpy) {
                for(int loc : friendlyZones) {
                    actions[loc] = chromosome[loc] * 
                                   chromosome[loc + OFFSET];
                }                
            }
            
            // if foodzones are not empty
            if(!isFoodZonesEmpty) {
                if(prevFoodZones != null) {
                    if(prevFoodZones.contains(prevAction)) {
                        actions[OFFSET] = chromosome[OFFSET * 2] * 
                                          chromosome[OFFSET * 2 + 1];
                    }

                }
                
                prevFoodZones = foodZones;
            }

            // otherwise does random move.
            
            actions[OFFSET + 1] = chromosome[OFFSET * 2 + 2] * 
                                  chromosome[OFFSET * 2 + 3];
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
    
    public double getFitness() {
        return currentFitness;
    }
    
    public void setFitness(double currentFitness) {
        this.currentFitness = currentFitness;
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