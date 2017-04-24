
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
 * Indicies:
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
    
    private static final int NUM_DIRECITONS = 9;
    private static final int NUM_ACTIONS    = 6;
    
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
     * Aciton Sensitivity Traits.
     */
    private float[] actionSensitivity;
    
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
    }
}
