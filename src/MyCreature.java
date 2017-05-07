import cosc343.assig2.Creature;
import java.util.Arrays;
import java.util.Random;

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

    private static final int NUM_HID_PERCEPTS = 5;
    
    private int noInHidWeights;
    private int noOutputWeights;
    
    private float[] chromosome;
    private float[] hiddenLayOuts; 
    
    private double fitness = 0.0;
   
    public MyCreature(float[] chromosome) {
        this.chromosome = chromosome;
        this.hiddenLayOuts = new float[NUM_HID_PERCEPTS];
        
        for(int i = 0; i < NUM_HID_PERCEPTS; i++) {
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
       
        Random rand = new Random();
        
        // plus 2 for bias weight and hidden layer weight.
        this.noInHidWeights  = (numPercepts + 2) * NUM_HID_PERCEPTS;
        
        // plus 1 for bias weight
        this.noOutputWeights = numActions * (NUM_HID_PERCEPTS + 1);
        
        this.chromosome = new float[noInHidWeights + noOutputWeights];
        this.hiddenLayOuts = new float[NUM_HID_PERCEPTS]; 

        for(int i = 0; i < chromosome.length; i++) {
            chromosome[i] = rand.nextFloat();
        }        
        
        for(int i = 0; i < NUM_HID_PERCEPTS; i++) {
            hiddenLayOuts[i] = 0;
        }
    }

    private float sigmoidActivation(float hLayerOut) {
        
        return (float)((float) 1 / (1 + Math.exp(-hLayerOut)));
    }
    
    private float tanActivation(float hLayerOut) {
        
        return (float) Math.tanh(hLayerOut);
    }
    
    private float[] softmaxActivation(float[] output) {
        
        float maxActivation = output[0];
        
        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxActivation) {
                maxActivation = output[i];
            }
        }

        float sumActivation = 0;
        for (int i = 0; i < output.length; i++) {
            sumActivation += (output[i] = (float) Math.exp(output[i] - maxActivation));
        }

        for (int i = 0; i < output.length; i++) {
            output[i] /= sumActivation;
        }
        
        return output;
    }
    
    @Override
    public float[] AgentFunction(int[] percepts, int numPercepts, int numExpectedActions) { 
        
        float[] output = new float[numExpectedActions];
        
        int weightIdx = 0; // pointer into weight array for the next edge weight

        // first, weight the hidLayerOuput layer's context from the previous iteration
        // then, add the bias term and pass the (weighted) percepts layer forward 
        // into the hidden layer output.
        
        // first weigh previous output of hidden layer with the weights of bias 
        // and hidden layer weight. 
        for (int i = 0; i < hiddenLayOuts.length; i++) {
            hiddenLayOuts[i] *= chromosome[weightIdx++];      // context from previous iteration
            hiddenLayOuts[i] += chromosome[weightIdx++] * -1; //bias

            for (int j = 0; j < percepts.length; j++) {
                hiddenLayOuts[i] += chromosome[weightIdx++] * percepts[j];
            }
        }

        // activate the hidden layer using sigmoid activation function
        for (int i = 0; i < hiddenLayOuts.length; i++) {
            hiddenLayOuts[i] = tanActivation(hiddenLayOuts[i]);
        }

        // now, pass hidden layer outputs to output layer and add the bias term
        for (int i = 0; i < output.length; i++) {
            output[i] = chromosome[weightIdx++] * -1; // bias

            for (int j = 0; j < hiddenLayOuts.length; j++) { 
                output[i] += chromosome[weightIdx++] * hiddenLayOuts[j];
            }
        }
        
        // use softmax to determine outputs for output layer.
        //output = softmaxActivation(output);
        for (int i = 0; i < output.length; i++) {
            output[i] = sigmoidActivation(output[i]);
        }
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