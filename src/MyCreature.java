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
                actions[location] = chromosome.getActionSens(chromosome.RND_WT);
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
        Map<Float, Integer> multiActionMapping = new HashMap<>();          
        List<Float> actionWeights              = new ArrayList<>();
        
        // weight variables
        float eatWt  = 0.0f,
              waitWt = 0.0f,
              fwdWt  = 0.0f,
              bckWt  = 0.0f,
              lftWt  = 0.0f,
              rghtWt = 0.0f,
              tlWt   = 0.0f,
              trWt   = 0.0f,
              blWt   = 0.0f,
              brWt   = 0.0f,
              rndWt  = 0.0f;
        
        // foe
        if(fffValStatus == -1) {
            
            bckWt = chromosome.getActionSens(Chromosome.BCK_WT); // can move away
            blWt  = chromosome.getActionSens(Chromosome.BL_WT);  // can move away left.
            brWt  = chromosome.getActionSens(Chromosome.BR_WT);  // can move away right.
            
            actionWeights.add(bckWt);
            actionWeights.add(blWt);
            actionWeights.add(brWt);
            
            multiActionMapping.put(bckWt, Chromosome.BCK_WT);
            multiActionMapping.put(blWt, Chromosome.BL_WT);
            multiActionMapping.put(brWt, Chromosome.BR_WT);            
        }
        
        // friend
        else if(fffValStatus == 0) {
            
            fwdWt  = chromosome.getActionSens(Chromosome.FWD_WT);  // can move towards.
            bckWt  = chromosome.getActionSens(Chromosome.BCK_WT);  // can move away.
            lftWt  = chromosome.getActionSens(Chromosome.LFT_WT);  // can move left.
            rghtWt = chromosome.getActionSens(Chromosome.RGT_WT);  // can move right.
            tlWt   = chromosome.getActionSens(Chromosome.TL_WT);   // can move top left.
            trWt   = chromosome.getActionSens(Chromosome.TR_WT);   // can move top right.
            blWt   = chromosome.getActionSens(Chromosome.BL_WT);   // can move away left.
            brWt   = chromosome.getActionSens(Chromosome.BR_WT);   // can move away right.
            waitWt = chromosome.getActionSens(Chromosome.WAIT_WT); // can wait.
            rndWt  = chromosome.getActionSens(Chromosome.RND_WT);  // can move randomly.
            
            actionWeights.add(fwdWt);
            actionWeights.add(bckWt);
            actionWeights.add(lftWt);
            actionWeights.add(rghtWt);
            actionWeights.add(tlWt);
            actionWeights.add(trWt);
            actionWeights.add(blWt);
            actionWeights.add(brWt);
            actionWeights.add(waitWt);
            actionWeights.add(rndWt);
            
            multiActionMapping.put(fwdWt,  Chromosome.FWD_WT);
            multiActionMapping.put(bckWt,  Chromosome.WAIT_WT);
            multiActionMapping.put(lftWt,  Chromosome.LFT_WT);
            multiActionMapping.put(rghtWt, Chromosome.RGT_WT);
            multiActionMapping.put(tlWt,   Chromosome.TL_WT);
            multiActionMapping.put(trWt,   Chromosome.TR_WT);
            multiActionMapping.put(blWt,   Chromosome.BL_WT);
            multiActionMapping.put(brWt,   Chromosome.BR_WT);
            multiActionMapping.put(waitWt, Chromosome.WAIT_WT);
            multiActionMapping.put(rndWt,  Chromosome.RND_WT);
        }
        
        else if(fffValStatus == 1) {
                       
            eatWt  = chromosome.getActionSens(Chromosome.EAT_WT);  // can eat.
            fwdWt  = chromosome.getActionSens(Chromosome.FWD_WT);  // can move towards.
            bckWt  = chromosome.getActionSens(Chromosome.BCK_WT);  // can move away.
            lftWt  = chromosome.getActionSens(Chromosome.LFT_WT);  // can move left.
            rghtWt = chromosome.getActionSens(Chromosome.RGT_WT);  // can move right.
            tlWt   = chromosome.getActionSens(Chromosome.TL_WT);   // can move top left.
            trWt   = chromosome.getActionSens(Chromosome.TR_WT);   // can move top right.
            blWt   = chromosome.getActionSens(Chromosome.BL_WT);   // can move away left.
            brWt   = chromosome.getActionSens(Chromosome.BR_WT);   // can move away right.
            waitWt = chromosome.getActionSens(Chromosome.WAIT_WT); // can wait.
            rndWt  = chromosome.getActionSens(Chromosome.RND_WT);  // can move randomly.
            
            actionWeights.add(eatWt);            
            actionWeights.add(fwdWt);/*
            actionWeights.add(bckWt);
            actionWeights.add(lftWt);
            actionWeights.add(rghtWt);
            actionWeights.add(tlWt);
            actionWeights.add(trWt);
            actionWeights.add(blWt);
            actionWeights.add(brWt);
            actionWeights.add(waitWt);*/
            //actionWeights.add(rndWt);
            
            multiActionMapping.put(eatWt,  Chromosome.EAT_WT);            
            multiActionMapping.put(fwdWt,  Chromosome.FWD_WT);/*
            multiActionMapping.put(bckWt,  Chromosome.WAIT_WT);
            multiActionMapping.put(lftWt,  Chromosome.LFT_WT);
            multiActionMapping.put(rghtWt, Chromosome.RGT_WT);
            multiActionMapping.put(tlWt,   Chromosome.TL_WT);
            multiActionMapping.put(trWt,   Chromosome.TR_WT);
            multiActionMapping.put(blWt,   Chromosome.BL_WT);
            multiActionMapping.put(brWt,   Chromosome.BR_WT);*/
            multiActionMapping.put(waitWt, Chromosome.WAIT_WT);
            //multiActionMapping.put(rndWt,  Chromosome.RND_WT);
            
            // food is ripe.
            if(perceptLoc == 2) {
                
            }
        }

        Collections.sort(actionWeights);

        float maxWeight = Collections.max(actionWeights);

        int action  = multiActionMapping.get(maxWeight);
        int pcptLoc = chromosome.getActionMapToPcptLocIdx(action);

        actions[pcptLoc] = maxWeight;

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