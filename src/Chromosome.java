
import java.util.ArrayList;
import java.util.List;
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
 *  North_Wt = 0.
 *  South_Wt = 1.
 *  East_Wt  = 2.
 *  WestWt   = 3.
 *  CenterWt = 4.
 *  NW_Wt    = 5.
 *  NE_Wt    = 6.
 *  SW_Wt    = 7.
 *  SE_Wt    = 8.
 *
 *  Eat_Wt     = 0.
 *  Wait_Wt    = 1.
 *  Forward_Wt = 2.
 *  Back_Wt    = 3.
 *  Random     = 4.
 * 
 * @author curator
 */
public class Chromosome {
    
    private static final int NUM_DIRECITONS  = 9;
    private static final int NUM_ACTIONS     = 6;
    private static final int NUM_ENTITY_TYPE = 3;
    
    /**
     * Direction Awareness Traits.
     */
    private int[] directionIntel;
    
    /**
     * Direciton Sensitivity Traits - used when multiple movements 
     * have to be made.
     */
    private float[] direcitonSensitivity;
    
    /**
     * Action Sensitivity Traits.
     */
    private float[] actionSensitivity;
    
    /**
     * Prey/Predator/Friend Awareness Traits.
     */
    private float[] ppfSensitivity;
    
    private boolean debug = true;
    
    public Chromosome() {
        List<Integer> lockNum = new ArrayList<>(); 
        Random rand = new Random();
        
        // initialize traits.
        directionIntel       = new int[NUM_DIRECITONS];
        direcitonSensitivity = new float[NUM_DIRECITONS];
        actionSensitivity    = new float[NUM_ACTIONS];
        
        int i = 0;
        while(i < NUM_DIRECITONS) {
            float dirSensitivity     = rand.nextFloat();
            int   estimateLocationIdx = rand.nextInt(NUM_DIRECITONS);
            
            // if num is not locked, then we can use it for chromosome.
            if(!lockNum.contains(estimateLocationIdx)) {
                lockNum.add(estimateLocationIdx); // lock num.
                directionIntel[i] = estimateLocationIdx;
                
                // also assign corresponding weight.
                direcitonSensitivity[i] = dirSensitivity;
                
                i++;
            } else {
                continue;
            }
        }
        
        // initialize action sensitivity genes.
        i = 0;
        while(i < NUM_ACTIONS) {
           actionSensitivity[i++] = rand.nextFloat();
        }
        
        if(debug) {
            String info = "";
            for(int geneIdx = 0; geneIdx < NUM_DIRECITONS; geneIdx++) {
                info +=  "G" + geneIdx + ":"  + directionIntel[geneIdx] + ",W:" +
                                          direcitonSensitivity[geneIdx] + " ";
            }
            System.out.println("[DEBUG]: Chromosome: " + info);
            
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
            ppfSensitivity[i] = rand.nextFloat();
        }
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
    
    public float getDirectionalSens(int dirIdx) {
        return direcitonSensitivity[dirIdx];
    }
}
