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
    
    private static class HashSetImpl extends HashSet<Integer> {

        public HashSetImpl() {
        }
        {
            add(Chromosome.N);
            add(Chromosome.S);
            add(Chromosome.W);
            add(Chromosome.E);
            add(Chromosome.NW);
            add(Chromosome.NE);
            add(Chromosome.SW);
            add(Chromosome.SE);
            add(Chromosome.C);
        }
    }
    
    private static class ZoneSet<Integer> {
        private Set<Integer> dangerZones;
        private Set<Integer> neutralZones;
        private Set<Integer> resourceZones;
        
        public ZoneSet(Set<Integer> dangerZones, 
                       Set<Integer> neutralZones, 
                       Set<Integer> resourceZones) {
            
            this.dangerZones   = dangerZones;
            this.neutralZones  = neutralZones;
            this.resourceZones = resourceZones;
        }
        
        public void addToDZ(Integer relativePosition) {
            dangerZones.add(relativePosition);
        } 
        
        public void addToNZ(Integer relativePosition) {
            neutralZones.add(relativePosition);
        }
        
        public void addToRZ(Integer relativePosition) {
            resourceZones.add(relativePosition);
        }
        
        public Set<Integer> getDZ() {
            return dangerZones;
        }
        
        public Set<Integer> getNZ() {
            return neutralZones;
        }
        
        public Set<Integer> getRZ() {
            return resourceZones;
        }
        
        public void removeFromNZ(Integer relativePosition) {
            neutralZones.remove(relativePosition);
        }
    }
    
    private static final Set<Integer> ZONES = new HashSetImpl();
    
    private static final int THREAT_THRES = 0;
    private static final int FRIEND_THRES = 1;
    private static final int FOOD_THRES   = 2;
    
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
        Set<Integer> dangerZones, neutralZones, resourceZones;
        
        ZoneSet zonesIntel     = new ZoneSet(new HashSet<>(), ZONES, new HashSet<>()); 
        Set<Integer> outcomes  = new HashSet<>();
        
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
            if(monsterNearby && foodNearby) System.out.println("somthing is amist!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            //System.out.println(monsterNearby + " " + creatureNearby + " " +foodNearby );
        
            // determine fffs.
            if(monsterNearby) {
                int fffStatusM = determineFFFReaction(chromosome.getFFFVal(0));
                zonesIntel = fffValToActions(zonesIntel, fffStatusM, i);
            }
            else if(creatureNearby) {
                int fffStatusC = determineFFFReaction(chromosome.getFFFVal(1));
                zonesIntel = fffValToActions(zonesIntel, fffStatusC, i);                
            }
            else if(foodNearby) {
                int fffStatusF = determineFFFReaction(chromosome.getFFFVal(2));
                zonesIntel = fffValToActions(zonesIntel, fffStatusF, i);                
            } 
        }
        
        // determine final possible moves.
        dangerZones   = zonesIntel.dangerZones;
        neutralZones  = zonesIntel.neutralZones;
        resourceZones = zonesIntel.resourceZones;

        if(neutralZones.size() == 0 && 
           dangerZones.size() == 0 &&
           resourceZones.size() == 0) {
            
            actions[Chromosome.RND_ACT] = chromosome.getActionSens(Chromosome.RND_WT);        
        } else {
            for(int relativeDir : neutralZones) {                     
                if(!dangerZones.contains(relativeDir)) {
                    outcomes.add(relativeDir);
                }
            }
            for(int relativeDir : resourceZones) {
                if(!dangerZones.contains(relativeDir)) {
                    outcomes.add(relativeDir);
                }            
            }
            for(int relativeDir : outcomes) {
                
                if(relativeDir == Chromosome.C) {
                    actions[Chromosome.EAT_ACT] = 
                            chromosome.getActionSens(Chromosome.EAT_WT);
                } else {
                    actions[chromosome.dirValToPerceptLoc(relativeDir)] = 
                            chromosome.getDirToActWeights(relativeDir);
                }
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
    private ZoneSet fffValToActions(ZoneSet zoneIntel, int fffValStatus, int perceptLoc) {
        
        int relativePosition = chromosome.getDirectionVal(perceptLoc);
        
        // foe
        switch (fffValStatus) {
            case -1:
                if(relativePosition == Chromosome.N) {
                    zoneIntel.addToDZ(Chromosome.N);
                    //zoneIntel.addToDZ(Chromosome.NW);
                    //zoneIntel.addToDZ(Chromosome.NE);
                }
                else if(relativePosition == Chromosome.S) {
                    zoneIntel.addToDZ(Chromosome.S);
                    //zoneIntel.addToDZ(Chromosome.SW);
                    //zoneIntel.addToDZ(Chromosome.SE);
                }
                else if(relativePosition == Chromosome.E) {
                    zoneIntel.addToDZ(Chromosome.E);
                    //zoneIntel.addToDZ(Chromosome.NE);
                    //zoneIntel.addToDZ(Chromosome.SE);
                }
                else if(relativePosition == Chromosome.W) {
                    zoneIntel.addToDZ(Chromosome.W);
                    //zoneIntel.addToDZ(Chromosome.NW);
                    //zoneIntel.addToDZ(Chromosome.SW);
                }
                else if(relativePosition == Chromosome.NW) {
                    zoneIntel.addToDZ(Chromosome.NW);
                    //zoneIntel.addToDZ(Chromosome.N);
                    //zoneIntel.addToDZ(Chromosome.W);
                }
                else if(relativePosition == Chromosome.NE) {
                    zoneIntel.addToDZ(Chromosome.NE);
                    //zoneIntel.addToDZ(Chromosome.N);
                    //zoneIntel.addToDZ(Chromosome.E);
                }
                else if(relativePosition == Chromosome.SW) {
                    zoneIntel.addToDZ(Chromosome.SW);
                    //zoneIntel.addToDZ(Chromosome.S);
                    //zoneIntel.addToDZ(Chromosome.W);
                }
                else if(relativePosition == Chromosome.SE) {
                    zoneIntel.addToDZ(Chromosome.SE);
                    //zoneIntel.addToDZ(Chromosome.S);
                    //zoneIntel.addToDZ(Chromosome.E);
                }   break;
            case 0:
                zoneIntel.removeFromNZ(relativePosition); // just remove area occupied by other entity.
                break;
            case 1:
                zoneIntel.addToRZ(relativePosition);
                break; 
        }
        
        return zoneIntel;
    }
    
    private int determineFFFReaction(int fffVal) {

        switch(fffVal) {
            case THREAT_THRES:
                return -1;
                
            case FRIEND_THRES:
                return 0;
                
            case FOOD_THRES:
                return 1;
        }
        
        System.out.println("WRONG REACTION");
        return 10000;
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