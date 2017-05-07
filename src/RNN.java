public class RNN {

    private int numInputs;
    private int numHidden;
    private int numOutputs;

    private float weights[];
    private float hidden[];
    private float output[];

    public RNN(int numInputs, int numHidden, int numOutputs) {

        this.numInputs = numInputs;
        this.numHidden = numHidden;
        this.numOutputs = numOutputs;

        this.hidden = new float[numHidden];
        for (int i = 0; i < this.hidden.length; i++) {
            this.hidden[i] = 0;
        }

        this.output = new float[numOutputs];
        //this.weights = new float[(numHidden + numInputs + 1) * numHidden + (numHidden + 1) * numOutputs];
    }

    public void setWeights(float[] weights) {
        
        this.weights = weights; 
        
        for (int i = 0; i < this.hidden.length; i++) {
            this.hidden[i] = 0;
        }		
    }

    public float getNumberOfWeights() {
        return this.weights.length;
    }
    
    public float[] action(int[] input) {
        int w = 0; // pointer into weight array for the next edge weight

        // first, weight the hidden layer's context from the previous iteration
        // then, add the bias term and pass the (weighted) input layer forward 
        // into the hidden layer
        for (int i = 0; i < this.hidden.length; i++) {
            this.hidden[i] *= this.weights[w++];      // context from previous iteration
            this.hidden[i] += this.weights[w++] * -1; //bias

            for (int j = 0; j < input.length; ++j) {
                this.hidden[i] += this.weights[w++] * input[j];
            }
        }

        // activate the hidden layer using tanh activation
        for (int i = 0; i < this.hidden.length; i++) {
            this.hidden[i] = (float) Math.tanh(this.hidden[i]);
        }

        // now, pass (weighted) inputs forward into the hidden layer and add the bias term
        for (int i = 0; i < this.output.length; i++) {
            this.output[i] = this.weights[w++] * -1; // bias

            for (int j = 0; j < this.hidden.length; ++j) { 
                this.output[i] += this.hidden[j];//this.weights[w++] * this.hidden[j];
            }
        }

        // finally, activate the output layer using softmax activation
        float maxActivation = this.output[0];
        for (int i = 1; i < this.output.length; i++) {
            if (this.output[i] > maxActivation) maxActivation = this.output[i];
        }

        float sumActivation = 0;
        for (int i = 0; i < this.output.length; i++) {
            sumActivation += (this.output[i] = (float) Math.exp(this.output[i] - maxActivation));
        }

        for (int i = 0; i < this.output.length; i++) {
            this.output[i] /= sumActivation;
        }

        // the resulting output is interpreted as the probability of performing each action --- return this!
        return this.output;
    }
}
