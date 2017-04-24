
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author curator
 */
public class Chromosome {
    
    private static final int NO_DIR_SENS_GENES = 9;
    private static final int NUM_DIRECITONS    = 9;
    private static final int OFFSET = 9; // number of neighbourhoods + OFFSETs for creature, and food percepts.
    
    private boolean debug = true;
    
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
     */
    private int[] directionIntel;
    
    public Chromosome() {
        List<Integer> lockNum = new ArrayList<>(); 
        Random rand = new Random();
        directionIntel = new int[NO_DIR_SENS_GENES];
       
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
        
        if(debug) {
            String info = "";
            for(int j = 0; j < NUM_DIRECITONS; j++) {
                info +=  " "  + directionIntel[j];
            }
            System.out.println("[DEBUG]: Chromosome: " + info);
        }
    }
}
