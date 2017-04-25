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
            else if(creatureNearby) {
                int fffStatusC = determineFFFReaction(chromosome.getFFFVal(1));
                actions = fffValToActions(actions, fffStatusC, location);                
            }
            else if(foodNearby) {
                int fffStatusF = determineFFFReaction(chromosome.getFFFVal(2));
                actions = fffValToActions(actions, fffStatusF, location);                
            } 
            // if all else fails, then we  walk randomly.
            else {
                actions[Chromosome.RND_ACT] = chromosome.getActionSens(chromosome.RND_WT);
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
        
        int relativePosition = chromosome.getDirectionVal(perceptLoc);
        
        // weight variables
        float eatWt  = 0.0f,
              cWt = 0.0f,
              nWt  = 0.0f,
              sWt  = 0.0f,
              wWt  = 0.0f,
              eWt = 0.0f,
              nwWt   = 0.0f,
              neWt   = 0.0f,
              swWt   = 0.0f,
              seWt   = 0.0f;
        
        nWt  = chromosome.getActionSens(Chromosome.N_WT);  // move towards.
        sWt  = chromosome.getActionSens(Chromosome.S_WT);  // move away.
        wWt  = chromosome.getActionSens(Chromosome.W_WT);  // move left.
        eWt  = chromosome.getActionSens(Chromosome.E_WT);  // move right.
        cWt  = chromosome.getActionSens(Chromosome.C_WT);  // move
        nwWt = chromosome.getActionSens(Chromosome.NW_WT); // move top left.
        neWt = chromosome.getActionSens(Chromosome.NE_WT); // move top right.
        swWt = chromosome.getActionSens(Chromosome.SW_WT); // move away left.
        seWt = chromosome.getActionSens(Chromosome.SE_WT); // move away right.
        
        // foe
        if(fffValStatus == -1) {
            if(relativePosition == Chromosome.N) { 
                actionWeights.add(wWt);
                actionWeights.add(eWt);
                actionWeights.add(sWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);

                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT); 
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);                
            }
            else if(relativePosition == Chromosome.S) { 
                actionWeights.add(nWt);
                actionWeights.add(wWt);
                actionWeights.add(eWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);

                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);             
            } 
            else if(relativePosition == Chromosome.E) { 
                actionWeights.add(nWt);
                actionWeights.add(sWt);
                actionWeights.add(wWt);
                actionWeights.add(nwWt);
                actionWeights.add(swWt);

                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);             
            }
            else if(relativePosition == Chromosome.W) { 
                actionWeights.add(nWt);
                actionWeights.add(sWt);
                actionWeights.add(eWt);
                actionWeights.add(neWt);
                actionWeights.add(seWt);

                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);               
            }    
            else if(relativePosition == Chromosome.NW) { 
                actionWeights.add(sWt);
                actionWeights.add(eWt);
                actionWeights.add(neWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);

                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);               
            }
            else if(relativePosition == Chromosome.NE) { 
                actionWeights.add(sWt);
                actionWeights.add(wWt);
                actionWeights.add(nwWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);

                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);               
            }    
            else if(relativePosition == Chromosome.SW) { 
                actionWeights.add(nWt);
                actionWeights.add(eWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);
                actionWeights.add(seWt);

                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);               
            }
            else if(relativePosition == Chromosome.SE) { 
                actionWeights.add(nWt);
                actionWeights.add(wWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);
                actionWeights.add(swWt);

                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);              
            }    
        }
        
        // friend
        else if(fffValStatus == 0) {
            if(relativePosition == Chromosome.N) { 
                actionWeights.add(sWt);
                actionWeights.add(wWt);
                actionWeights.add(eWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);
                actionWeights.add(cWt);
                
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);
                multiActionMapping.put(cWt,  Chromosome.C_WT);
            }
            else if(relativePosition == Chromosome.S) { 
                actionWeights.add(nWt);
                actionWeights.add(wWt);
                actionWeights.add(eWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);
                actionWeights.add(cWt);
                
                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);
                multiActionMapping.put(cWt,  Chromosome.C_WT);               
            } 
            else if(relativePosition == Chromosome.E) { 
                actionWeights.add(nWt);
                actionWeights.add(sWt);
                actionWeights.add(wWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);
                actionWeights.add(cWt);
                
                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);
                multiActionMapping.put(cWt,  Chromosome.C_WT);            
            }
            else if(relativePosition == Chromosome.W) { 
                actionWeights.add(nWt);
                actionWeights.add(sWt);
                actionWeights.add(eWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);
                actionWeights.add(cWt);
                
                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);
                multiActionMapping.put(cWt,  Chromosome.C_WT);               
            }    
            else if(relativePosition == Chromosome.NW) { 
                actionWeights.add(nWt);
                actionWeights.add(sWt);
                actionWeights.add(wWt);
                actionWeights.add(eWt);
                actionWeights.add(neWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);
                actionWeights.add(cWt);
                
                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);
                multiActionMapping.put(cWt,  Chromosome.C_WT);             
            }
            else if(relativePosition == Chromosome.NE) { 
                actionWeights.add(nWt);
                actionWeights.add(sWt);
                actionWeights.add(wWt);
                actionWeights.add(eWt);
                actionWeights.add(nwWt);
                actionWeights.add(swWt);
                actionWeights.add(seWt);
                actionWeights.add(cWt);
                
                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);
                multiActionMapping.put(cWt,  Chromosome.C_WT);             
            }    
            else if(relativePosition == Chromosome.SW) { 
                actionWeights.add(nWt);
                actionWeights.add(sWt);
                actionWeights.add(wWt);
                actionWeights.add(eWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);
                actionWeights.add(seWt);
                actionWeights.add(cWt);
                
                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(seWt, Chromosome.SE_WT);
                multiActionMapping.put(cWt,  Chromosome.C_WT);              
            }
            else if(relativePosition == Chromosome.SE) { 
                actionWeights.add(nWt);
                actionWeights.add(sWt);
                actionWeights.add(wWt);
                actionWeights.add(eWt);
                actionWeights.add(nwWt);
                actionWeights.add(neWt);
                actionWeights.add(swWt);
                actionWeights.add(cWt);
                
                multiActionMapping.put(nWt,  Chromosome.N_WT);
                multiActionMapping.put(sWt,  Chromosome.S_WT);
                multiActionMapping.put(wWt,  Chromosome.W_WT);
                multiActionMapping.put(eWt,  Chromosome.E_WT);
                multiActionMapping.put(nwWt, Chromosome.NW_WT);
                multiActionMapping.put(neWt, Chromosome.NE_WT);
                multiActionMapping.put(swWt, Chromosome.SW_WT);
                multiActionMapping.put(cWt,  Chromosome.C_WT);              
            }   
        }
        
        else if(fffValStatus == 1) {
            
            float actionWeight = 0.0f;
            
            if(relativePosition == Chromosome.N) actionWeight  = nWt;
            if(relativePosition == Chromosome.S) actionWeight  = sWt;
            if(relativePosition == Chromosome.W) actionWeight  = wWt;
            if(relativePosition == Chromosome.E) actionWeight  = eWt;
            if(relativePosition == Chromosome.NW) actionWeight = nwWt;
            if(relativePosition == Chromosome.NE) actionWeight = neWt;
            if(relativePosition == Chromosome.SW) actionWeight = swWt;
            if(relativePosition == Chromosome.SE) actionWeight = seWt;
            
            actions[perceptLoc] = actionWeight;
            
            if(relativePosition == Chromosome.C) {
                actions[Chromosome.EAT_ACT] = eatWt;
            }  
        }
        
        if(actionWeights.size() > 1) {
            Collections.sort(actionWeights);

            float maxWeight = Collections.max(actionWeights);

            int action  = multiActionMapping.get(maxWeight);
            int pcptLoc = chromosome.getActionMapToPcptLocIdx(action);

            actions[pcptLoc] = maxWeight;
        }
        
        return actions;

    }
    
    private int determineFFFReaction(float fffVal) {

        // assume everyone is foe.
        int fffStatus = -1;
        
        // GA determines if this entity is a threat (predator)
        if(fffVal > THREAT_THRES) { 
            // if fffVal is lower than threat threshold, then consider this entity as a threat.
            // GA algorithm confirms threat threshold condition, we  assume friendly.
            fffStatus = 0;    
            
            // GA determine if this entity is food
            if(fffVal > FOOD_THRES) {
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