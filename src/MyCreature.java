import cosc343.assig2.Creature;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
   
    private static final float THREAT_THRES = 0.33f;
    private static final float FOOD_THRES   = 0.66f;
    
    private static final int EAT_PCT = 9;
    private static final int RND_PCT = 10;
    
    private static final int EAT_WT  = 0;
    private static final int WAIT_WT = 1;
    private static final int FWD_WT  = 2;
    private static final int BCK_WT  = 3;
    private static final int RND_WT  = 4;
    
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
     * 
     * Direction Weight Indices.
     *  North_Wt = 0.
     *  South_Wt = 1.
     *  East_Wt  = 2.
     *  WestWt   = 3.
     *  CenterWt = 4.
     *  NW_Wt    = 5.
     *  NE_Wt    = 6.
     *  SW_Wt    = 7.
     *  SE_Wt    = 8.
     */
    @Override
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) {
        List<Integer> sensory = new ArrayList<>();
        
        // default actions would be determined by genotypes of individuals.
        float actions[] = new float[numExpectedActions];
        
        // assume creature doesn't know friends, foes, food.
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
                    
            // determine fffs.
            if(monsterNearby) {
                int fffStatusM = determineFFFReaction(chromosome.getFFFVal(0));
                actions = fffValToActions(actions, fffStatusM, location);
            }
            if(creatureNearby) {
                int fffStatusC = determineFFFReaction(chromosome.getFFFVal(1));
                actions = fffValToActions(actions, fffStatusC, location);                
            }
            if(foodNearby) {
                int fffStatusF = determineFFFReaction(chromosome.getFFFVal(2));
                actions = fffValToActions(actions, fffStatusF, location);                
            } 
            // if all else fails, then we can walk randomly.
            else {
                actions[location] = chromosome.getActionSens(RND_WT);
            }
        }
        
        return actions;
    }
    
    /**
     *  Action Sensitivity Indices.
     *  Eat_Wt     = 0.
     *  Wait_Wt    = 1.
     *  Forward_Wt = 2.
     *  Back_Wt    = 3.
     *  Random     = 4.
     * 
     * @param actions
     * @param fffValStatus
     * @param perceptLoc
     * 
     * @return 
     */
    private float[] fffValToActions(float[] actions, int fffValStatus, int perceptLoc) {
        List<Float>         actionWeights = new ArrayList<>();
        Map<Float, Integer> actionMapping = new HashMap<>();          
        
        int dirVal = chromosome.getDirectionVal(perceptLoc);

        String direction      = chromosome.getDirectionString(dirVal);
        int inverseDir        = chromosome.getInverseDirection(direction);
        int inversePerceptDir = chromosome.dirValToPerceptLoc(inverseDir);  
        
        // weight variables
        float eatWt  = 0.0f,
              waitWt = 0.0f,
              fwdWt  = 0.0f,
              bckWt  = 0.0f,
              rndWt  = 0.0f;
        
        
        // foe
        if(fffValStatus == -1) {
            
            bckWt = chromosome.getActionSens(BCK_WT);
            actions[inversePerceptDir] = chromosome.getActionSens(BCK_WT);
        }
        
        // friend
        else if(fffValStatus == 0) {
            
            fwdWt  = chromosome.getActionSens(FWD_WT);
            waitWt = chromosome.getActionSens(WAIT_WT);
            
            actionWeights.add(fwdWt);
            actionWeights.add(waitWt);
            
            actionMapping.put(fwdWt,  FWD_WT);  // can move foward.
            actionMapping.put(waitWt, WAIT_WT); // can wait.
        }
        
        else if(fffValStatus == 1) {
            
            fwdWt  = chromosome.getActionSens(FWD_WT);
            waitWt = chromosome.getActionSens(WAIT_WT);            
            eatWt  = chromosome.getActionSens(EAT_WT); 
            
            actionWeights.add(fwdWt);
            actionWeights.add(waitWt);
            actionWeights.add(eatWt);
            
            actionMapping.put(fwdWt,  FWD_WT);   // can move foward.
            actionMapping.put(waitWt, WAIT_WT);  // can wait.
            actionMapping.put(eatWt,  EAT_WT);   // can eat.
            
            // food is ripe.
            if(perceptLoc == 2) {
                
            }
        }
        
        // if action mapping size is greater than 1, then there are multiple options. 
        if(actionMapping.size() > 1) {
            Collections.sort(actionWeights);
            
            float maxWeight = Collections.max(actionWeights);
            
            int action = actionMapping.get(maxWeight);
            
            // TODO: configure more actions options, eat ripe fruit, MOVE ALL directions, impl wait.
            if(action == EAT_PCT) actions[EAT_PCT] = eatWt;
            if(action == WAIT_WT) actions[WAIT_WT] = waitWt;
            if(action == FWD_WT)  actions[FWD_WT]  = fwdWt;
            if(action == BCK_WT)  actions[BCK_WT]  = bckWt;
            if(action == RND_WT)  actions[RND_WT]  = rndWt;
            //if(action == 0)
                
            return actions;
        }
        
        return actions;
    }
    
    private int determineFFFReaction(float fffVal) {

        // assume everyone is foe.
        int fffStatus = -1;
        
        // GA determines if this entity is a threat (predator)
        if(fffVal < THREAT_THRES) { 
            // if fffVal is lower than threat threshold, then consider this entity as a threat.
            // GA algorithm confirms threat threshold condition, we can assume friendly.
            fffStatus = 0;    
            
            // GA determine if this entity is food
            if(THREAT_THRES <= fffVal && fffVal < FOOD_THRES) {
                fffStatus = 1;
            }
        }
        
        // threat
        return fffStatus;
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