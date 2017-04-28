import cosc343.assig2.Creature;

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
    
    private static final int ENTITY0 = 0;
    private static final int ENTITY1 = 0;
    private static final int ENTITY2 = 0;
    
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
                int fffStatusM;
                        
                if(i < 3) {    
                    fffStatusM = chromosome.getFFFValZone1(ENTITY0);
                } else if(3 <= i && i < 6) {     
                    fffStatusM = chromosome.getFFFValZone2(ENTITY0);
                } else {
                    fffStatusM = chromosome.getFFFValZone3(ENTITY0);
                }
                    
                actions = fffValToActions(actions, fffStatusM, i, sensingMons, perceptSections);
            }
            if(creatureNearby) {
                int fffStatusM;
                        
                if(i < 3) {    
                    fffStatusM = chromosome.getFFFValZone1(ENTITY1);
                } else if(3 <= i && i < 6) {     
                    fffStatusM = chromosome.getFFFValZone2(ENTITY1);
                } else {
                    fffStatusM = chromosome.getFFFValZone3(ENTITY1);
                }
                    
                actions = fffValToActions(actions, fffStatusM, i, sensingCrea, perceptSections);              
            }
            if(foodNearby) {
                int fffStatusM;
                        
                if(i < 3) {    
                    fffStatusM = chromosome.getFFFValZone1(ENTITY2);
                } else if(3 <= i && i < 6) {     
                    fffStatusM = chromosome.getFFFValZone2(ENTITY2);
                } else {
                    fffStatusM = chromosome.getFFFValZone3(ENTITY2);
                }
                    
                actions = fffValToActions(actions, fffStatusM, i, sensingFood, perceptSections);               
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
    private float[] fffValToActions(float[] actions, int fffValStatus, int perceptLoc,
                                                                       int perceptVal,
                                                                       int perceptSections) {

        if(perceptLoc < 3) {            
            switch (fffValStatus) {
                case THREAT_THRES:
                    actions[perceptLoc] -= 
                                chromosome.getZone1ActSensVal(Chromosome.AWAY);
                    break;

                case NEUTRAL_THRES:
                    actions[perceptLoc] += 
                                chromosome.getZone1ActSensVal(Chromosome.TOWARDS);
                    actions[Chromosome.RND_ACT] += 
                                chromosome.getZone1ActSensVal(Chromosome.RND);
                    break;

                case FOOD_THRES:
                    actions[perceptLoc] += 
                                chromosome.getZone1ActSensVal(Chromosome.TOWARDS);
                    if(perceptVal == 1) {
                        actions[perceptLoc] += chromosome.getZone1ActSensVal(Chromosome.WAIT);
                    } else {
                        actions[Chromosome.EAT_ACT] += chromosome.getZone1ActSensVal(Chromosome.EAT);
                    }
                    break; 
            }
        } else if(3 <= perceptLoc && perceptLoc < 6) {           
            switch (fffValStatus) {
                case THREAT_THRES:
                    actions[perceptLoc] -= 
                                chromosome.getZone2ActSensVal(Chromosome.AWAY);
                    break;

                case NEUTRAL_THRES:
                    actions[perceptLoc] += 
                                chromosome.getZone2ActSensVal(Chromosome.TOWARDS);
                    actions[Chromosome.RND_ACT] += 
                                chromosome.getZone2ActSensVal(Chromosome.RND);
                    break;

                case FOOD_THRES:
                    actions[perceptLoc] += 
                                chromosome.getZone2ActSensVal(Chromosome.TOWARDS);
                    if(perceptVal == 1) {
                        actions[perceptLoc] += chromosome.getZone2ActSensVal(Chromosome.WAIT);
                    } else {
                        actions[Chromosome.EAT_ACT] += chromosome.getZone2ActSensVal(Chromosome.EAT);
                    }
                    break; 
            }            
        } else {
            switch (fffValStatus) {
                case THREAT_THRES:
                    actions[perceptLoc] -= 
                                chromosome.getZone3ActSensVal(Chromosome.AWAY);
                    break;

                case NEUTRAL_THRES:
                    actions[perceptLoc] += 
                                chromosome.getZone3ActSensVal(Chromosome.TOWARDS);
                    actions[Chromosome.RND_ACT] += 
                                chromosome.getZone3ActSensVal(Chromosome.RND);
                    break;

                case FOOD_THRES:
                    actions[perceptLoc] += 
                                chromosome.getZone3ActSensVal(Chromosome.TOWARDS);
                    if(perceptVal == 1) {
                        actions[perceptLoc] += chromosome.getZone3ActSensVal(Chromosome.WAIT);
                    } else {
                        actions[Chromosome.EAT_ACT] += chromosome.getZone3ActSensVal(Chromosome.EAT);
                    }
                    break; 
            }
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