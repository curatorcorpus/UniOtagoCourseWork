import cosc343.assig2.Creature;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    
    private static final int THREAT_THRES  = 0;
    private static final int NEUTRAL_THRES = 1;
    private static final int FOOD_THRES    = 2;
    
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
        this.chromosome = new Chromosome(numPercepts, numActions);
    }

    /** Data field that encodes chromosomes.
     * Value:
     *  North  = 0.
     *  South  = 1.
     *  East   = 2.
     *  West   = 3.
     *  Center = 4.
     *  NW     = 5.
     *  NE     = 6.
     *  SW     = 7.
     *  SE     = 8.
     */
    @Override
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) { 
        // default actions would be determined by genotypes of individuals.
        float actions[] = new float[numExpectedActions];
   
        int perceptSections = percepts.length / 3;
        int perceptOffset   = 9;
        
        for(int i = 0; i < perceptSections; i++) {
            
            // assume creature doesn't know friends, foes, food.
            boolean monsterNearby  = false;
            boolean creatureNearby = false;
            boolean foodNearby     = false;
            
            int sensingMons = percepts[i];
            int sensingCrea = percepts[i + perceptOffset];
            int sensingFood = percepts[i + perceptOffset * 2];
            
            // deduce sensory information
            if(sensingMons == 1) monsterNearby  = true;
            if(sensingCrea == 1) creatureNearby = true;
            if(sensingFood == 1 || sensingFood == 2) foodNearby = true;
            
            // determine fffs.
            if(monsterNearby) {
                int fffStatusM = chromosome.getFFFVal(0);
                actions = fffValToActions(actions, fffStatusM, i, sensingMons);
            }
            if(creatureNearby) {
                int fffStatusC = chromosome.getFFFVal(1);
                actions = fffValToActions(actions, fffStatusC, i, sensingCrea);                
            }
            if(foodNearby) {
                int fffStatusF = chromosome.getFFFVal(2);
                actions = fffValToActions(actions, fffStatusF, i, sensingFood);                
            } 
        }
        
        return actions;
    }
    
    /**
     * 
     * @param actions
     * @param fffValStatus
     * @param perceptLoc
     * 
     * @return 
     */
    private float[] fffValToActions(float[] actions, int fffValStatus, int perceptLoc, int perceptVal) {
        
        int relativePosition = chromosome.getDirectionVal(perceptLoc);
        
        // foe
        switch (fffValStatus) {
            case THREAT_THRES:
                actions[perceptLoc] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                
                if(relativePosition == Chromosome.N) {
                    actions[chromosome.dirValToPerceptIdx(Chromosome.NE)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                    actions[chromosome.dirValToPerceptIdx(Chromosome.NW)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                }
                else if(relativePosition == Chromosome.S) {
                    actions[chromosome.dirValToPerceptIdx(Chromosome.SE)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                    actions[chromosome.dirValToPerceptIdx(Chromosome.SW)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);                   
                }
                else if(relativePosition == Chromosome.E) {
                    actions[chromosome.dirValToPerceptIdx(Chromosome.NE)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                    actions[chromosome.dirValToPerceptIdx(Chromosome.SE)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);                 
                }
                else if(relativePosition == Chromosome.W) {
                    actions[chromosome.dirValToPerceptIdx(Chromosome.NW)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                    actions[chromosome.dirValToPerceptIdx(Chromosome.SW)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);                  
                }
                else if(relativePosition == Chromosome.NW) {
                    actions[chromosome.dirValToPerceptIdx(Chromosome.N)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                    actions[chromosome.dirValToPerceptIdx(Chromosome.W)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);             
                }
                else if(relativePosition == Chromosome.NE) {
                    actions[chromosome.dirValToPerceptIdx(Chromosome.N)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                    actions[chromosome.dirValToPerceptIdx(Chromosome.E)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);                 
                }
                else if(relativePosition == Chromosome.SW) {
                    actions[chromosome.dirValToPerceptIdx(Chromosome.S)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                    actions[chromosome.dirValToPerceptIdx(Chromosome.W)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);                  
                }
                else if(relativePosition == Chromosome.SE) {
                    actions[chromosome.dirValToPerceptIdx(Chromosome.S)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);
                    actions[chromosome.dirValToPerceptIdx(Chromosome.E)] -= 
                            chromosome.getActionSens(Chromosome.AWAY);              
                }
                break;
                
            case NEUTRAL_THRES:
                actions[perceptLoc] += 
                            chromosome.getActionSens(Chromosome.TOWARDS);
                actions[Chromosome.RND_ACT] += 
                            chromosome.getActionSens(Chromosome.RND);
                break;
                
            case FOOD_THRES:
                actions[perceptLoc] += 
                            chromosome.getActionSens(Chromosome.TOWARDS);
                if(perceptVal == 1) {
                    actions[perceptLoc] += chromosome.getActionSens(Chromosome.WAIT);
                } else {
                    actions[Chromosome.EAT_ACT] += chromosome.getActionSens(Chromosome.EAT);
                }
                break; 
        }
        
        return actions;
    }
    
    public Chromosome getChromosome() {
        return this.chromosome;
    }
    
    public void setChromosome(Chromosome chromosome) {
        this.chromosome = chromosome;
    }
    
    public double getFitness() {
        return currentFitness;
    }
    
    public void setFitness(double currentFitness) {
        this.currentFitness = currentFitness;
    }
}