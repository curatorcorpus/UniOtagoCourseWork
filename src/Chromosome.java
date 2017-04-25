
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
    
    public static final int EAT_WT = 0;
    public static final int C_WT   = 1;
    public static final int N_WT   = 2;
    public static final int S_WT   = 3;
    public static final int W_WT   = 4;
    public static final int E_WT   = 5;
    public static final int NW_WT  = 6;
    public static final int NE_WT  = 7;
    public static final int SW_WT  = 8;
    public static final int SE_WT  = 9;
    public static final int RND_WT = 10;
    
    public static final int N  = 0;
    public static final int S  = 1;  
    public static final int E  = 2;
    public static final int W  = 3;  
    public static final int C  = 4;    
    public static final int NW = 5;
    public static final int NE = 6;   
    public static final int SW = 7;
    public static final int SE = 8;   
    
    public static final int NUM_DIRECITONS  = 9;
    public static final int NUM_ACTIONS     = 11;
    public static final int NUM_ENTITY_TYPE = 3;

    private Map<Integer, Integer> directionMapping;
    
    /**
     * Direction Awareness Traits. Maps percept location index to 
     * what the creature thinks the directions are.
     */
    private int[] directionIntel;
   
    /**
     * Action Sensitivity Traits.
     */
    private float[] actionSensitivity;
    
    /**
     * Prey/Predator/Friend Awareness Traits.
     */
    private float[] fffSensitivity;
    
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
        fffSensitivity    = new float[NUM_ENTITY_TYPE]; 
        
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
        
        // initialize ppf sensitivity genes.
        i = 0;
        while(i < NUM_ENTITY_TYPE) {
            fffSensitivity[i++] = rand.nextFloat();
        }
    }
    public int[] getDirectionIntel() {
        return directionIntel;
    }
    
    public void setDirectionIntel(int[] directionIntel) {
        this.directionIntel = directionIntel;
    }
    
    public void redoDirectionToPcptMapping() {
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
    
    public float[] getFFFSensGenes() {
        return fffSensitivity;
    }
    
    public void setFFFSensGenes(float[] fffSensitivity) {
        this.fffSensitivity = fffSensitivity;
    }
    
    public int getDirectionVal(int perceptLoc) {
        return directionIntel[perceptLoc];
    }

    public String getDirectionString(int dirVal) {
        
        String direction = "";
        
        switch(dirVal) {
        case 0:
            direction = "N";
            break;
        case 1:   
            direction = "S";                
            break;
        case 2:
            direction = "E";                
            break;
        case 3:   
            direction = "W";                
            break;
        case 4:
            direction = "C";                
            break;
        case 5:   
            direction = "NW";                
            break;
        case 6:
            direction = "NE";                
            break;
        case 7:   
            direction = "SW";                
            break;
        case 8:
            direction = "SE";                
            break;    
        }
        
        return direction;
    }    
    
    public int getInverseDirection(String dir) {
        int direction = 0;
        
        switch(dir) {
        case "N":
            direction = 0;
            break;
        case "S":   
            direction = 1;                
            break;
        case "E":
            direction = 2;                
            break;
        case "W":   
            direction = 3;                
            break;
        case "C":
            direction = 4;                
            break;
        case "NW":   
            direction = 5;                
            break;
        case "NE":
            direction = 6;                
            break;
        case "SW":   
            direction = 7;                
            break;
        case "SE":
            direction = 8;                
            break;    
        }        
        
        return direction;
    }
    
    public int dirValToPerceptLoc(int dirVal) {
        int perceptLoc = -1;
        
        for(int i = 0; i < directionIntel.length; i++) {
            int chromoDirVal = directionIntel[i];
            
            if(chromoDirVal == dirVal) {
                perceptLoc = i;
                break;
            }
        }
        
        return perceptLoc;
    }
    
    public int getActionMapToPcptLocIdx(int maxActionWtIdx) {

        switch(maxActionWtIdx) {
            case EAT_WT:
                return EAT_ACT;
            
            case C_WT:
                return directionMapping.get(C);
            
            case N_WT:
                return directionMapping.get(N);
            
            case S_WT:
                return directionMapping.get(S);
            
            case W_WT:
                return directionMapping.get(W);
            
            case E_WT:
                return directionMapping.get(E);
                
            case NW_WT:
                return directionMapping.get(NW);
                
            case NE_WT:
                return directionMapping.get(NE);
                
            case SW_WT:
                return directionMapping.get(SW);
                
            case SE_WT: 
                return directionMapping.get(SE);
                
            default:
                return RND_ACT;
        }
    }
    
    public float getActionSens(int actIdx) {
        return actionSensitivity[actIdx];
    }
    
    public float getFFFVal(int idx) {
        return fffSensitivity[idx];
    }
    
    @Override
    public String toString() {
        return "Chromosome{" + "directionIntel=" + Arrays.toString(directionIntel) +
               ", actionSensitivity=" + Arrays.toString(actionSensitivity) + 
               ", fffSensitivity=" + Arrays.toString(fffSensitivity) + '}';
    }
}
