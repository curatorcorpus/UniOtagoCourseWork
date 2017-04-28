
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
    
    public static final int AWAY    = 0;
    public static final int TOWARDS = 1;
    public static final int EAT     = 2;
    public static final int WAIT    = 3;
    public static final int RND     = 4;
    
    public static final int NUM_ACTIONS     = 5;
    public static final int NUM_ENTITY_TYPE = 3;

    private int[]   fffSensitivityZone;    
    private float[] zone1ActionSensitivity;
    
    public Chromosome() {}
    
    public Chromosome(int numPercepts, int numActions) {
        List<Integer> lockNum = new ArrayList<>(); 
        Random rand = new Random();

        // initialize traits.
        zone1ActionSensitivity = new float[NUM_ACTIONS];
        fffSensitivityZone     = new int[NUM_ENTITY_TYPE]; 
        
        // initialize action sensitivity genes.
        for(int idx = 0; idx < NUM_ACTIONS; idx++) {
           zone1ActionSensitivity[idx]   = rand.nextFloat();
        }
        
        // initialize fff sensitivity genes.
        int i = 0;
        while(i < NUM_ENTITY_TYPE) {
            int estimateLocationIdx = rand.nextInt(NUM_ENTITY_TYPE);            
            
            // if num is not locked, then we can use it for chromosome.
            if(!lockNum.contains(estimateLocationIdx)) {
                lockNum.add(estimateLocationIdx); // lock num.
                fffSensitivityZone[i++] = estimateLocationIdx;
            } else {
                continue;
            }
        }       
    }
    
    public float[] getZone1ActSens() {
        return this.zone1ActionSensitivity;
    }
    
    public void setZone1ActSens(float[] zone1ActionSensitivity) {
        this.zone1ActionSensitivity = zone1ActionSensitivity;
    }
    
    public float getZone1ActSensVal(int idx) {
        return this.zone1ActionSensitivity[idx];
    }
    
    public int[] getFFFSensGenesZone1() {
        return fffSensitivityZone;
    }
    
    public void setFFFSensGenesZone1(int[] fffSensitivity) {
        this.fffSensitivityZone = fffSensitivity;
    }
    
    public int getFFFValZone1(int idx) {
        return fffSensitivityZone[idx];
    }
    
    @Override
    public String toString() {
        return "Chromosome{" + "fffSensitivityZone1=" + Arrays.toString(fffSensitivityZone) +
                               "\n zone1ActionSensitivity=" + Arrays.toString(zone1ActionSensitivity) + '}';
    }
}
