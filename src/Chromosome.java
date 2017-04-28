
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
    public static final int NUM_ENTITY_TYPE = 4;

    private int[]   fffSensitivityZone1; 
    private int[]   fffSensitivityZone2;    
    private int[]   fffSensitivityZone3;    
    private float[] zone1ActionSensitivity;
    private float[] zone2ActionSensitivity;
    private float[] zone3ActionSensitivity;
    
    public Chromosome() {}
    
    public Chromosome(int numPercepts, int numActions) {
        List<Integer> lockNum = new ArrayList<>(); 
        Random rand = new Random();

        // initialize traits.
        zone1ActionSensitivity = new float[NUM_ACTIONS];
        zone2ActionSensitivity = new float[NUM_ACTIONS];
        zone3ActionSensitivity = new float[NUM_ACTIONS];
        fffSensitivityZone1    = new int[NUM_ENTITY_TYPE]; 
        fffSensitivityZone2    = new int[NUM_ENTITY_TYPE]; 
        fffSensitivityZone3    = new int[NUM_ENTITY_TYPE]; 
        
        // initialize action sensitivity genes.
        for(int idx = 0; idx < NUM_ACTIONS; idx++) {
           zone1ActionSensitivity[idx]   = rand.nextFloat();
           zone2ActionSensitivity[idx]   = rand.nextFloat();
           zone3ActionSensitivity[idx++] = rand.nextFloat();
        }
        
        // initialize fff sensitivity genes.
        int i = 0;
        while(i < NUM_ENTITY_TYPE) {
            int estimateLocationIdx = rand.nextInt(NUM_ENTITY_TYPE);            
            
            // if num is not locked, then we can use it for chromosome.
            if(!lockNum.contains(estimateLocationIdx)) {
                lockNum.add(estimateLocationIdx); // lock num.
                fffSensitivityZone1[i++] = estimateLocationIdx;
            } else {
                continue;
            }
        }
        
        lockNum.clear();
        
        i = 0;
        while(i < NUM_ENTITY_TYPE) {
            int estimateLocationIdx = rand.nextInt(NUM_ENTITY_TYPE);            
            
            // if num is not locked, then we can use it for chromosome.
            if(!lockNum.contains(estimateLocationIdx)) {
                lockNum.add(estimateLocationIdx); // lock num.
                fffSensitivityZone2[i++] = estimateLocationIdx;
            } else {
                continue;
            }
        }        

        lockNum.clear();        
        
        i = 0;
        while(i < NUM_ENTITY_TYPE) {
            int estimateLocationIdx = rand.nextInt(NUM_ENTITY_TYPE);            
            
            // if num is not locked, then we can use it for chromosome.
            if(!lockNum.contains(estimateLocationIdx)) {
                lockNum.add(estimateLocationIdx); // lock num.
                fffSensitivityZone3[i++] = estimateLocationIdx;
            } else {
                continue;
            }
        }        
    }
    
    public float[] getZone1ActSens() {
        return this.zone1ActionSensitivity;
    }
    
    public float[] getZone2ActSens() {
        return this.zone2ActionSensitivity;
    }
    
    public float[] getZone3ActSens() {
        return this.zone3ActionSensitivity;
    }  
    
    public void setZone1ActSens(float[] zone1ActionSensitivity) {
        this.zone1ActionSensitivity = zone1ActionSensitivity;
    }
    
    public void setZone2ActSens(float[] zone2ActionSensitivity) {
        this.zone2ActionSensitivity = zone2ActionSensitivity;
    }
    
    public void setZone3ActSens(float[] zone3ActionSensitivity) {
        this.zone3ActionSensitivity = zone3ActionSensitivity;
    }        
    
    public float getZone1ActSensVal(int idx) {
        return this.zone1ActionSensitivity[idx];
    }
    
    public float getZone2ActSensVal(int idx) {
        return this.zone2ActionSensitivity[idx];
    }
    
    public float getZone3ActSensVal(int idx) {
        return this.zone3ActionSensitivity[idx];
    }      
    
    public int[] getFFFSensGenesZone1() {
        return fffSensitivityZone1;
    }
    
    public void setFFFSensGenesZone1(int[] fffSensitivity) {
        this.fffSensitivityZone1 = fffSensitivity;
    }
    
    public int getFFFValZone1(int idx) {
        return fffSensitivityZone1[idx];
    }
    
   public int[] getFFFSensGenesZone2() {
        return fffSensitivityZone2;
    }
    
    public void setFFFSensGenesZone2(int[] fffSensitivity) {
        this.fffSensitivityZone2 = fffSensitivity;
    }
    
    public int getFFFValZone2(int idx) {
        return fffSensitivityZone2[idx];
    }

   public int[] getFFFSensGenesZone3() {
        return fffSensitivityZone3;
    }
    
    public void setFFFSensGenesZone3(int[] fffSensitivity) {
        this.fffSensitivityZone3 = fffSensitivity;
    }
    
    public int getFFFValZone3(int idx) {
        return fffSensitivityZone3[idx];
    }    
    
    @Override
    public String toString() {
        return "Chromosome{" + "fffSensitivityZone1=" + Arrays.toString(fffSensitivityZone1) + 
                               "\n fffSensitivityZone2=" + Arrays.toString(fffSensitivityZone2) + 
                               "\n fffSensitivityZone3=" + Arrays.toString(fffSensitivityZone3) +
                               "\n zone1ActionSensitivity=" + Arrays.toString(zone1ActionSensitivity) + 
                               "\n zone2ActionSensitivity=" + Arrays.toString(zone2ActionSensitivity) + 
                               "\n zone3ActionSensitivity=" + Arrays.toString(zone3ActionSensitivity) + '}';
    }
}
