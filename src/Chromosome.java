
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
    
    public static final int EAT_WT  = 0;
    public static final int WAIT_WT = 1;
    public static final int FWD_WT  = 2;
    public static final int BCK_WT  = 3;
    public static final int LFT_WT  = 4;
    public static final int RGT_WT  = 5;
    public static final int TL_WT   = 6;
    public static final int TR_WT   = 7;
    public static final int BL_WT   = 8;
    public static final int BR_WT   = 9;
    public static final int RND_WT  = 10;
    
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
    
    private boolean debug = true;
    
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
            
            case WAIT_WT:
                return directionMapping.get(C);
            
            case FWD_WT:
                return directionMapping.get(N);
            
            case BCK_WT:
                return directionMapping.get(S);
            
            case LFT_WT:
                return directionMapping.get(W);
            
            case RGT_WT:
                return directionMapping.get(E);
                
            case TL_WT:
                return directionMapping.get(NW);
                
            case TR_WT:
                return directionMapping.get(NE);
                
            case BL_WT:
                return directionMapping.get(SW);
                
            case BR_WT: 
                return directionMapping.get(SE);
                
            case RND_WT:
                return RND_ACT;
                
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
}
