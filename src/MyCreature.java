import cosc343.assig2.Creature;

import java.util.Arrays;
import java.util.Random;

import util.RandomTool;

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

    private static final Random RAND      = RandomTool.random;
    private static final int HID_PERCEPTS = 5;
    private static final int BIAS         = -1;
    
    private int noInHidWeights;
    private int noOutputWeights;
    
    private float[] chromosome;
    private float[] hiddenLayOuts; 
    
    private double fitness = 0.0;
   
    public MyCreature(float[] chromosome) {
        this.chromosome = chromosome;
        this.hiddenLayOuts = new float[HID_PERCEPTS];
        
        for(int i = 0; i < HID_PERCEPTS; i++) {
            hiddenLayOuts[i] = 0;
        }
    }
    
    /**
     * 
     * @param numPercepts - number of percepts this creature will be receiving.
     * @param numActions  - the number of action output vector that creature will 
     *                      need to produce every turn.
     */
    public MyCreature(int numPercepts, int numActions, int numTurns) {
        
        // plus 2 for bias weight and hidden layer weight.
        this.noInHidWeights  = (numPercepts + 2) * HID_PERCEPTS;
        
        // plus 1 for bias weight
        this.noOutputWeights = numActions * (HID_PERCEPTS + 1);
        
        this.chromosome = new float[noInHidWeights + noOutputWeights];
        this.hiddenLayOuts = new float[HID_PERCEPTS]; 

        for(int i = 0; i < chromosome.length; i++) {
            chromosome[i] = -0.7f + 1.4f * RAND.nextFloat();
        }        
        
        for(int i = 0; i < HID_PERCEPTS; i++) {
            hiddenLayOuts[i] = 0;
        }
    }
    
    private float tanhActivation(float hLayerOut) {    
        return (float) Math.tanh(hLayerOut);
    }
    
    private float[] softmaxActivation(float[] output) {
        float maxActivation = output[0];
        
        // find max
        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxActivation) {
                maxActivation = output[i];
            }
        }

        // sum all activation
        float sumActivation = 0;
        for (int i = 0; i < output.length; i++) {
            sumActivation += (output[i] = (float) Math.exp(output[i] - maxActivation));
        }

        // determine activation probability distribution
        for (int i = 0; i < output.length; i++) {
            output[i] /= sumActivation;
        }
        
        return output;
    }
    
    @Override
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) { 
        
        float[] output = new float[numExpectedActions];
        
        int weightIdx = 0; // pointer into weight array for the next edge weight

        // Weighs previous output of hidden layer with the weights of bias 
        // and hidden layer weight. 
        for (int i = 0; i < hiddenLayOuts.length; i++) {
            hiddenLayOuts[i] *= chromosome[weightIdx++];      // context from previous iteration
            hiddenLayOuts[i] += chromosome[weightIdx++] * BIAS;

            for (int j = 0; j < percepts.length; j++) {
                hiddenLayOuts[i] += chromosome[weightIdx++] * percepts[j];
            }
        }

        // hidden layer activation
        for (int i = 0; i < hiddenLayOuts.length; i++) {
            hiddenLayOuts[i] = tanhActivation(hiddenLayOuts[i]);
        }

        // now, pass hidden layer outputs to output layer and add the bias term
        for (int i = 0; i < output.length; i++) {
            output[i] = chromosome[weightIdx++] * BIAS;

            for (int j = 0; j < hiddenLayOuts.length; j++) { 
                output[i] += chromosome[weightIdx++] * hiddenLayOuts[j];
            }
        }
        
        // use softmax to determine outputs for output layer.
        output = softmaxActivation(output);
        
        // the resulting output is interpreted as the probability of performing each action --- return this!
        return output;
    }
    
    public void setChromosome(float[] chromosome) {
        this.chromosome = chromosome;
    }
    
    public float[] getChromosome() {
        return chromosome;
    }
    
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public double getFitness() {
        return fitness;
    }

    @Override
    public String toString() {
        return "MyCreature{" + "chromosome=" + Arrays.toString(chromosome) + '}';
    }
    
    
}