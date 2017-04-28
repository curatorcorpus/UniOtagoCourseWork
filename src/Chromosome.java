
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Data field that encodes chromosomes.
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
 * Action Sensitivity Indices.
 *  Eat_Wt     = 0.
 *  Wait_Wt    = 1.
 *  Forward_Wt = 2.
 *  Back_Wt    = 3.
 *  Random     = 4.
 * 
 * @author curator
 */
public class Chromosome {
    
    public static final int EAT_ACT = 9;
    public static final int RND_ACT = 10;
    
    public static final int TOWARDS = 0;
    public static final int AWAY    = 1;
    public static final int EAT     = 2;
    public static final int WAIT    = 3;
    public static final int RND     = 4;
    
    public static final int N  = 0;
    public static final int S  = 1;  
    public static final int E  = 2;
    public static final int W  = 3;  
    public static final int C  = 4;    
    public static final int NW = 5;
    public static final int NE = 6;   
    public static final int SW = 7;
    public static final int SE = 8;   
    
    public static final int NUM_ACTIONS     = 5;
    public static final int NUM_DIRECITONS  = 9;
    public static final int NUM_ENTITY_TYPE = 3;

    private Map<Integer, Integer> directionMapping;
    
    /**
     * Direction Awareness Traits. Maps percept location index to 
     * what the creature thinks the directions are.
     */
    private int[]   directionIntel;
    private int[]   fffSensitivity;    
    private float[] actionSensitivity;
    
    private boolean debug = false;
    
    public Chromosome() {}
    
    public Chromosome(int numPercepts, int numActions) {
        List<Integer> lockNum = new ArrayList<>(); 
        Random rand = new Random();
        
        // efficiency mapping.
        directionMapping  = new HashMap<>();
        
        // initialize traits.
        directionIntel    = new int[NUM_DIRECITONS];
        actionSensitivity = new float[NUM_ACTIONS];
        fffSensitivity    = new int[NUM_ENTITY_TYPE]; 
        
        // initialize direction awareness traits. Maps percepts to directions.
        int i = 0;
        while(i < NUM_DIRECITONS) {
            int estimateLocationIdx = rand.nextInt(NUM_DIRECITONS);            
            
            // if num is not locked, then we can use it for chromosome.
            if(!lockNum.contains(estimateLocationIdx)) {
                lockNum.add(estimateLocationIdx); // lock num.
                directionIntel[i++] = estimateLocationIdx;
            } else {
                continue;
            }
        }
        
        // initialize action sensitivity genes.
        for(int idx = 0; idx < NUM_ACTIONS; idx++) {
           actionSensitivity[idx++] = rand.nextFloat();
        }
        
        int dir = 0;
        // initialize direction mapping for efficiency. Maps directions back to percept values. (Naive Search).
        while(dir < NUM_DIRECITONS) {
            
            // for all percept locations.
            for(int pcpt = 0; pcpt < NUM_DIRECITONS; pcpt++) {
                
                // if direction is at the pcpt gene location.
                if(dir == directionIntel[pcpt]) {
                    directionMapping.put(dir++, pcpt);
                    break;
                }
            }
        }
        
        lockNum.clear();
        
        // initialize ppf sensitivity genes.
        i = 0;
        while(i < NUM_ENTITY_TYPE) {
            
            int estimateLocationIdx = rand.nextInt(NUM_ENTITY_TYPE);            
            
            // if num is not locked, then we can use it for chromosome.
            if(!lockNum.contains(estimateLocationIdx)) {
                lockNum.add(estimateLocationIdx); // lock num.
                fffSensitivity[i++] = estimateLocationIdx;
            } else {
                continue;
            }
        }
        
        if(debug) {
            String info = "";
            for(int geneIdx = 0; geneIdx < NUM_DIRECITONS; geneIdx++) {
                info +=  "G" + geneIdx + ":"  + directionIntel[geneIdx] + " ";
            }
            System.out.println("[DEBUG]: Chromosome: " + info);
            
            info = "";
            for(int pcpt = 0; pcpt < NUM_DIRECITONS; pcpt++) {
                info +=  "Dir:" + pcpt + " PcptL: " + directionMapping.get(pcpt) + " ";
            } 
            System.out.println("[DEBUG]: ChromoDPMap: " + info);
            
            info = "";
            for(int geneIdx = 0; geneIdx < NUM_ACTIONS; geneIdx++) {
                info +=  "G" + geneIdx + ":W:" + actionSensitivity[geneIdx] + " ";
            }
            System.out.println("[DEBUG]: ChromoAct: " + info);
            System.out.println();
        }
    }
    public int[] getDirectionIntel() {
        return directionIntel;
    }
    
    public void setDirectionIntel(int[] directionIntel) {
        this.directionIntel = directionIntel;
        redoDirectionToPcptMapping();
    }
    
    public void redoDirectionToPcptMapping() {
        this.directionMapping = new HashMap<>();
        
        int dir = 0;
        // initialize direction mapping for efficiency. Maps directions back to percept values. (Naive Search).
        while(dir < NUM_DIRECITONS) {
            
            // for all percept locations.
            for(int pcpt = 0; pcpt < NUM_DIRECITONS; pcpt++) {
                
                // if direction is at the pcpt gene location.
                if(dir == directionIntel[pcpt]) {
                    directionMapping.put(dir++, pcpt);
                    break;
                }
            }
        }
    }
    
    public Map<Integer, Integer> getDirectionToPcptMap() {
        return directionMapping;
    }
    
    public void setDirectionToPcptMap(Map<Integer, Integer> directionMapping) {
        this.directionMapping = directionMapping;
    }
    
    public float[] getActionSensGenes() {
        return actionSensitivity;
    }
    
    public void setActionSensGenes(float[] actionSensitivity) {
        this.actionSensitivity = actionSensitivity;
    }
    
    public int[] getFFFSensGenes() {
        return fffSensitivity;
    }
    
    public void setFFFSensGenes(int[] fffSensitivity) {
        this.fffSensitivity = fffSensitivity;
    }
    
    public int getDirectionVal(int perceptLoc) {
        return directionIntel[perceptLoc];
    } 

    public int dirValToPerceptIdx(int dir) {
        return directionMapping.get(dir);
    }
    
    public float getActionSens(int action) {
        return actionSensitivity[action];
    }
    
    public int getFFFVal(int idx) {
        return fffSensitivity[idx];
    }
    
    @Override
    public String toString() {
        return "Chromosome{" + "directionIntel=" + Arrays.toString(directionIntel) +
               ", actionSensitivity=" + Arrays.toString(actionSensitivity) + 
               ", fffSensitivity=" + Arrays.toString(fffSensitivity) + '}';
    }
}
