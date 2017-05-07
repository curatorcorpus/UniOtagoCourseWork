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
    private static final int ENTITY1 = 1;
    private static final int ENTITY2 = 2;

    private Chromosome chromosome;

    private double currentFitness = 0.0;
    
    public MyCreature(Chromosome chromosome, int numTurns) {
       this.chromosome = chromosome;     
    }
    
    /**
     * 
     * @param numPercepts - number of percepts this creature will be receiving.
     * @param numActions  - the number of action output vector that creature will 
     *                      need to produce every turn.
     */
    public MyCreature(int numPercepts, int numActions, int numTurns) {
        this.chromosome = new Chromosome(numPercepts, numActions);
    }

    @Override
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) { 

        // default actions would be determined by genotypes of individuals.
        float actions[] = new float[numExpectedActions];
   
        int perceptSections = percepts.length / 3;
        int perceptOffset   = 9;
        
        for(int i = 0; i < perceptSections; i++) {
            
            // assume creature doesn't know friends, foes, food.
            boolean entity1Nearby = false;
            boolean entity2Nearby = false;
            boolean entity3Nearby = false;
            
            int sensingE1 = percepts[i];
            int sensingE2 = percepts[i + perceptOffset];
            int sensingE3 = percepts[i + perceptOffset * 2];
            
            // deduce sensory information
            if(sensingE1 == 1) entity1Nearby  = true;
            if(sensingE2 == 1) entity2Nearby = true;
            if(sensingE3 == 1 || sensingE3 == 2) entity3Nearby = true;

            actions[i] += chromosome.getZone1ActSensVal(Chromosome.MOVE);            
            
            // determine fffs.
            if(entity1Nearby) {
                int fffStatusM = chromosome.getFFFValZone(ENTITY0);
                actions = fffValToActions(actions, fffStatusM, i, sensingE1);
            }
            if(entity2Nearby) {
                int fffStatusM = chromosome.getFFFValZone(ENTITY1);
                actions = fffValToActions(actions, fffStatusM, i, sensingE2);           
            }
            if(entity3Nearby) {
                int fffStatusM = chromosome.getFFFValZone(ENTITY2);
                actions = fffValToActions(actions, fffStatusM, i, sensingE3);              
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
        
        switch (fffValStatus) {
            case THREAT_THRES:
                actions[perceptLoc] -= chromosome.getZone1ActSensVal(Chromosome.AWAY);
                break;
                
            case NEUTRAL_THRES:
                actions[perceptLoc] += chromosome.getZone1ActSensVal(Chromosome.TOWARDS);
                actions[Chromosome.RND_ACT] += chromosome.getZone1ActSensVal(Chromosome.RND);                  
                break;        
                
            case FOOD_THRES:
                actions[perceptLoc] += chromosome.getZone1ActSensVal(Chromosome.TOWARDS);
                //actions[perceptLoc] -= chromosome.getZone1ActSensVal(Chromosome.WAIT);
                
                if(perceptVal == 2) {
                    actions[Chromosome.EAT_ACT] += chromosome.getZone1ActSensVal(Chromosome.EAT);
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