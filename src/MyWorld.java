import cosc343.assig2.World;
import cosc343.assig2.Creature;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.Shape;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import util.RandomTool;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

/**
* The MyWorld extends the cosc343 assignment 2 World.  Here you can set 
* some variables that control the simulations and override functions that
* generate populations of creatures that the World requires for its
* simulations.
*
* @author  Jung-Woo (Noel) Park.
* @version 1.0
* @since   2017-04-05 
*/
public class MyWorld extends World {
    
    private static final Random RAND       = RandomTool.random;    
    private static final float ALPHA_BLEND = 0.5f;  // blend crossover alpha value
    private static final float STEP_SIZE   = 0.01f; // standard deviation
    private static final int TOURN_SIZE    = 10;    // competitors
    private static final int NUM_TURNS     = 300;   // turn size
    private static final int NUM_GENS      = 440;   // number of generations
    
    private MyCreature currentFittestCreature;
    private double[]  averageFitnessPerGen = new double[NUM_GENS];
    
    private double previousAvgFit = 0.0;

    private int avgFitIdx = 0;
    
    private class ParentCouple {
        private MyCreature parent1, parent2;
        
        public ParentCouple(MyCreature parent1, MyCreature parent2) {
            this.parent1 = parent1;
            this.parent2 = parent2;
        }
        
        public MyCreature getParent1() {
            return this.parent1;
        }
        
        public MyCreature getParent2() {
            return this.parent2;
        }
    }    
    
    private double determineFitness(MyCreature creature) {
        double fitness = 0;
        
        // take account of death time
        fitness = creature.isDead() ? creature.timeOfDeath() : NUM_TURNS;
        
        // take account of current energy and death time
        fitness = creature.getEnergy() * fitness / (double)NUM_TURNS;
        
        return fitness;
    }
    
    private MyCreature[] breed(Creature[] oldPopulationCt, int numCreatures) {

        List<MyCreature> elitists = new ArrayList();
        
        MyCreature[] oldPopulation = (MyCreature[]) oldPopulationCt;
        MyCreature[] newGeneration = new MyCreature[numCreatures];
        
        double totalFitness = 0.0;
        double maxFitness = 0.0;
        
        // obtain previous fitness
        for(int i = 0; i < numCreatures; i++) {
            
            MyCreature currCreature = oldPopulation[i];
            
            double currFitness = determineFitness(currCreature);
            currCreature.setFitness(currFitness);
            
            // find fittest
            if(maxFitness < currFitness) {
                currentFittestCreature = currCreature;
                maxFitness = currFitness;
            }
            
            // elitists
            if(currCreature.getFitness() >= previousAvgFit) {
                elitists.add(currCreature);
            }
            
            // accumulate fittness total
            totalFitness += currFitness;
        }

        // average fitness
        averageFitnessPerGen[avgFitIdx] = totalFitness/numCreatures;
        
        // display status.
        showStatus(oldPopulation, numCreatures);

        int newGen = 0;
        
        // elitism
        for(MyCreature s : elitists) {
            newGeneration[newGen++] = s;
        }
        
        // generated new generation
        while(newGen < numCreatures) {
           
            // select parents
            ParentCouple parents = tournamentSelection(oldPopulation);
            
            // crossover
            float[] offspring = blendCrossOver(parents.getParent1().getChromosome(),
                                               parents.getParent2().getChromosome(),
                                               ALPHA_BLEND);
            
            // mutation
            for(int i = 0; i < offspring.length; i++) {
                offspring[i] = gaussianMutation(offspring[i], STEP_SIZE); 
            }
            
            // new offspring
            newGeneration[newGen++] = new MyCreature(offspring);
        }
        
        previousAvgFit = totalFitness/numCreatures;
        
        return newGeneration;
    }
    
    private ParentCouple tournamentSelection(MyCreature[] oldPopulation) {

        int p1, p2, competitor;
        
        // compete for first parent
        p1 = RAND.nextInt(oldPopulation.length);
        for (int i = 0; i < TOURN_SIZE; ++i) {
            competitor = RAND.nextInt(oldPopulation.length);
            
            if (oldPopulation[competitor].getFitness() > oldPopulation[p1].getFitness()) {
                p1 = competitor;
            }
        }
        
        // compete for second parent
        p2 = RAND.nextInt(oldPopulation.length);
        for (int i = 0; i < TOURN_SIZE; ++i) {
            competitor = RAND.nextInt(oldPopulation.length);
            
            if (oldPopulation[competitor].getFitness() > oldPopulation[p2].getFitness()) {
                p2 = competitor;
            }
        }
        
        return new ParentCouple(oldPopulation[p1], oldPopulation[p2]);
    }
    
    private float[] blendCrossOver(float[] p1, float[] p2, float alpha) {

        float[] offspring = new float[p1.length];
        
        for(int i = 0; i < p1.length; i++) {
            float min = Math.min(p1[i], p2[i]);
            float max = Math.max(p1[i], p2[i]);
            float diff = max - min;
            
            min -= diff * alpha;
            max += diff * alpha;
            
            offspring[i] = min + RAND.nextFloat() * 2 * diff;
        }
        
        return offspring;
    }

    private float gaussianMutation(float mean, float stdv) {

        float x1;
        float x2;
       
        do {
            x1 = RAND.nextFloat();
            x2 = RAND.nextFloat();    
        } while(x1 <= 1e-11);
        
        // box muller transformation
        float y = (float) (Math.sqrt(-2.0f * Math.log(x1)) * Math.cos(2.0f * Math.PI * x2));
        
        return y * stdv + mean;
    }
    
    /**
     * Prints out population status of generation.
     */
    private int showStatus(MyCreature[] old_population, int numCreatures) {
       float avgLifeTime = 0f;
       int nSurvivors = 0;
       
       for(MyCreature creature : old_population) {
           
          boolean dead = creature.isDead();
          
          if(dead) {
             avgLifeTime += (float) creature.timeOfDeath();
          } else {
             nSurvivors++;
             avgLifeTime += (float) NUM_TURNS;
          }
       }

       avgLifeTime /= (float) numCreatures;
       
       // display status.
       System.out.println("Simulation stats:");
       System.out.println("  Fitness      : " + averageFitnessPerGen[avgFitIdx++]);
       System.out.println("  Survivors    : " + nSurvivors + " out of " + numCreatures);
       System.out.println("  Avg life time: " + avgLifeTime + " turns");
       
       return nSurvivors;
    }
    
    /**
     * Constructor for Simulation World.
     * 
     * @param gridSize       - the size of the world.
     * @param windowWidth    - the width in pixels of the visualization window.
     * @param windowHeight   - the height in pixels of the visualization window.
     * @param repeatableMode - if set true every sim in each generation starts from the same state.
     */
    public MyWorld(int gridSize, int windowWidth, int windowHeight, boolean repeatableMode,
                                                                    int perceptFormat) {
        
        super(gridSize, windowWidth,  windowHeight, repeatableMode, perceptFormat);

        // setup simulation environment.
        this.setNumTurns(NUM_TURNS);
        this.setNumGenerations(NUM_GENS);
    }
 
    /**
     * 
     * 
     * @param numCreatures
     * @return 
     */
    @Override
    public MyCreature[] firstGeneration(int numCreatures) {
    
        int numPercepts = this.expectedNumberofPercepts();
        int numActions = this.expectedNumberofActions();

        MyCreature[] population = new MyCreature[numCreatures];
      
        for(int i = 0; i < numCreatures; i++) {
            population[i] = new MyCreature(numPercepts, numActions, NUM_TURNS);     
        }

        return population;
    }

    /**
     * 
     * 
     * @param old_population_btc
     * @param numCreatures
     * @return 
     */
    @Override
    public MyCreature[] nextGeneration(Creature[] old_population_btc, int numCreatures) {
        
        // Typcast old_population of Creatures to array of MyCreatures
        MyCreature[] old_population = (MyCreature[]) old_population_btc;
        MyCreature[] new_population = breed(old_population_btc, numCreatures); // Create a new array for the new population
        
        if(avgFitIdx == NUM_GENS) {
           
            // aggregate fitness data into data set.
            XYSeriesCollection fitnessDataSet = new XYSeriesCollection();
            XYSeries dataSet = new XYSeries("Fitness");
        
            for (int i = 0; i < NUM_GENS; i++) {
                dataSet.add(i, averageFitnessPerGen[i]);
            }
        
            fitnessDataSet.addSeries(dataSet);
            
            // create chart.
            JFreeChart jfreechart = ChartFactory.createScatterPlot("Fitness Scatter", 
                                                                   "Generations", 
                                                                   "Fitness", 
                                                                   fitnessDataSet,
                                                                   PlotOrientation.VERTICAL, 
                                                                   true, 
                                                                   true, 
                                                                   false);
            
            // define shape of plots.
            Shape cross = ShapeUtilities.createDiagonalCross(3, 1);

            // setup chart.
            XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
            xyPlot.setDomainCrosshairVisible(true);
            xyPlot.setRangeCrosshairVisible(true);
            XYItemRenderer renderer = xyPlot.getRenderer();
            renderer.setSeriesShape(0, cross);
            renderer.setSeriesPaint(0, Color.red);
       
            // set domain and range.
            NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
            range.setRange(0, 100);
            range.setTickUnit(new NumberTickUnit(2.0));
            
            // enable AA.
            jfreechart.getRenderingHints().put(RenderingHints.KEY_ANTIALIASING, 
                                               RenderingHints.VALUE_ANTIALIAS_ON);

            // create and display a frame...
            ChartFrame frame = new ChartFrame("COSC 343 Assignment", jfreechart);
            frame.setPreferredSize(new Dimension(2450, 1440));
            frame.pack();
            frame.setVisible(true);
            
            // save
            try {
                ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
                File file = new File("./Charts/FitnessChart.png");
                ChartUtilities.saveChartAsPNG(file, jfreechart, 2450, 1440, info);
                
            } catch(Exception e) {
                System.err.println("Chart couldn't be saved properly.");
            }
            System.out.println();
            System.out.println("Best Solution:");
            System.out.println("Best Fitness: " + currentFittestCreature.getFitness());
            System.out.println("Best Chromosome State: " + currentFittestCreature);
        }
        
        return new_population;
    }
    
    /**
     * Main Method.
     * 
     * @param args 
     */
    public static void main(String[] args) {

       boolean repeatableMode = false;
       
       int gridSize = 50;
       int perceptFormat = 2;
       int windowWidth =  2456;
       int windowHeight = 1440;

       // Instantiate MyWorld object.  The rest of the application is driven
       // from the window that will be displayed.
       World sim = new MyWorld(gridSize, windowWidth, windowHeight, repeatableMode, 
                                                                    perceptFormat);
    }  
}