import cosc343.assig2.World;
import cosc343.assig2.Creature;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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

    public class ParentCouple {
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
    
    /**
     * The number of turns in each simulation.
     */
    private final int numTurns = 300;
    
    /**
     * The number of generations the genetic algorithm will iterate through.
     */
    private final int numGenerations = 150;
    
    private double[] averageFitnessPerGen = new double[numGenerations];
    
    private double previousFitnessAvg = 0.0;
    
    private int avgFitIdx = 0;
    
    private MyCreature currentFittestCreature;
    double previousAvgFit = 0.0;
    
    private double determineFitness(MyCreature creature) {
        double fitness = 0;
        
        fitness = creature.getEnergy() * ((double)numTurns - (double)creature.timeOfDeath());// / (double) numTurns;
        
        return fitness;
    }
    
    private MyCreature[] breed(Creature[] oldPopulationCt, int numCreatures) {

        List<MyCreature> survivors = new ArrayList();
        
        MyCreature[] oldPopulation = (MyCreature[]) oldPopulationCt;
        MyCreature[] newGeneration = new MyCreature[numCreatures];
        
        double totalFitness = 0.0;
        double maxFitness = 0.0;
        
        // obtain previous fitness
        for(int i = 0; i < numCreatures; i++) {
            
            MyCreature currCreature = oldPopulation[i];
            double currFitness = determineFitness(currCreature);
            currCreature.setFitness(currFitness);
            if(maxFitness < currFitness) {
                currentFittestCreature = currCreature;
                maxFitness = currFitness;
            }
            
            if(!currCreature.isDead()) {
                survivors.add(currCreature);
            }
            
            totalFitness += currFitness;
        }

        averageFitnessPerGen[avgFitIdx] = totalFitness/numCreatures;
        
        // display status.
        showStatus(oldPopulation, numCreatures);

        int newGen = 0;
        while(newGen < numCreatures) {
           
            // select parents
            ParentCouple parents = tournamentSelection(oldPopulation);
            
            // crossover
            Chromosome newChromo = crossOver(parents.getParent1().getChromosome(),
                                             parents.getParent2().getChromosome());
            
            newGeneration[newGen++] = new MyCreature(newChromo, numTurns);
        }
        
        previousAvgFit = totalFitness/numCreatures;
        
        return newGeneration;
    }
    
    private ParentCouple tournamentSelection(MyCreature[] oldPopulation) {
        Random rand = new Random();
        
        int left  = 0;
        int right = 0;
        
        do {
           left  = rand.nextInt(oldPopulation.length);
           right = rand.nextInt(oldPopulation.length);
        } while((right - left) < 2);

        List<MyCreature> oldPopSubset = new ArrayList<>();
        
        while(left < right) {
            oldPopSubset.add(oldPopulation[left++]);
        }
        
        // sort array by energy
        Collections.sort(oldPopSubset, (a, b) -> ((Double) a.getFitness()).
                                                 compareTo(((Double) b.getFitness())));
        
        MyCreature parent1 = oldPopSubset.get(oldPopSubset.size() - 1);
        MyCreature parent2 = oldPopSubset.get(oldPopSubset.size() - 2);
              
        return new ParentCouple(parent1, parent2);
    }
    
    /**
     * 
     * 
     * @param male
     * @param female
     * @return 
     */
    private Chromosome crossOver(Chromosome firstBest, Chromosome scndBest) {

        float[] zone1ActSensP1 = firstBest.getZone1ActSens(),
                zone1ActSensP2 = scndBest.getZone1ActSens();             
                
        int[] fffSensitivityZone1P1 = firstBest.getFFFSensGenesZone1(), 
              fffSensitivityZone1P2 = scndBest.getFFFSensGenesZone1();    
        
        Chromosome newGenes = new Chromosome();
        
        newGenes.setZone1ActSens(mutateWeights(
                                        onePointCrossOver(zone1ActSensP1,
                                                          zone1ActSensP2)));

        newGenes.setFFFSensGenesZone1(fffMutation(
                                    orderOneCrossOverFFF(
                                                fffSensitivityZone1P1,
                                                fffSensitivityZone1P2), 5000));
        
        return newGenes;
    }
   
    public int[] orderOneCrossOverFFF(int[] dirGene1, int[] dirGene2) {
        List<Integer> intLocks = new ArrayList<>();
        List<Integer> intLockIndices = new ArrayList<>();
        
        int left  = 0;
        int right = 0;
        
        int[] newDirGenes = new int[dirGene1.length];
        
        do {
           left  = rand.nextInt(dirGene1.length);
           right = rand.nextInt(dirGene1.length);
        } while((right - left) <= 0); // make sure number is not range of randomly choosen numbers are not less than 1.
        
        int i = left;
        while(i < right) {
            newDirGenes[i] = dirGene1[i];
            intLocks.add(dirGene1[i]);
            intLockIndices.add(i++);
        }
        
        int[] remainders = new int[dirGene1.length - intLocks.size()];
        
        // process indicies.
        int remainderIdx = 0;
        for(int remains = 0; remains < dirGene1.length; remains++) {
            if(!intLockIndices.contains(remains)) {
                remainders[remainderIdx++] = remains;
            }
        }
        
        i = 0;
        int z = 0;
        while(i < dirGene1.length) {
            if(!intLocks.contains(dirGene2[i])) {
                newDirGenes[remainders[z++]] = dirGene2[i];
            } 
            
            i++;
        }
        
        return newDirGenes;
    }
    
    public float[] onePointCrossOver(float[] genes1, float[] genes2) {
        Random rand = new Random();
        
        float[] newSubTraits = new float[genes1.length];
        
        int left = rand.nextInt(genes1.length);
        left = genes1.length / 2;
        
        int i = 0;
        while(i < genes1.length) {
            if(i < left) {
                newSubTraits[i] = genes1[i];
            } else {
                newSubTraits[i] = genes2[i]; 
            }            
            
            i++;
        }
        
        mutateWeights(newSubTraits);
        
        return newSubTraits;
    }
    
    private float[] mutateWeights(float[] subTraits) {
        
        Random rand = new Random();
 
        //if(mutate < subTraits.length) {
            for(int i = 0; i < subTraits.length; i++) {
                int mutate = rand.nextInt(50000);
                
                if(mutate < subTraits.length) {
                    System.out.println("mutated");
                    subTraits[mutate] = rand.nextFloat();
                }
            }
        //}

        return subTraits;
    }    
    
    public int[] fffMutation(int[] fffGenes, int alpha) {
        Random rand = new Random();
        
        int mutationRate = rand.nextInt(alpha);
        
        if(mutationRate < fffGenes.length) {
            int idx1 = rand.nextInt(fffGenes.length);
            int idx2 = rand.nextInt(fffGenes.length);
            
            int copy = fffGenes[idx1];
            
            fffGenes[idx1] = fffGenes[idx2];
            fffGenes[idx2] = copy;
        }
        
        return fffGenes;
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
             avgLifeTime += (float) numTurns;
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
        this.setNumTurns(numTurns);
        this.setNumGenerations(numGenerations);
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

        // This is just an example code.  You may replace this code with
        // your own that initialises an array of size numCreatures and creates
        // a population of your creatures
        MyCreature[] population = new MyCreature[numCreatures];
      
        for(int i = 0; i < numCreatures; i++) {
            population[i] = new MyCreature(numPercepts, numActions, numTurns);     
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
        
        if(avgFitIdx == numGenerations) {
           
            // aggregate fitness data into data set.
            XYSeriesCollection fitnessDataSet = new XYSeriesCollection();
            XYSeries dataSet = new XYSeries("Fitness");
        
            for (int i = 0; i < numGenerations; i++) {
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
            range.setRange(0, numTurns * 200);
            range.setTickUnit(new NumberTickUnit(2500.0));
            
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
                File file = new File("FitnessChart.png");
                ChartUtilities.saveChartAsPNG(file, jfreechart, 2450, 1440, info);
                
            } catch(Exception e) {
                System.err.println("Chart couldn't be saved properly.");
            }
            System.out.println();
            System.out.println("Best Solution:");
            System.out.println(currentFittestCreature.getEnergy());
            System.out.println("Best Fitness: " + currentFittestCreature.getFitness());
            System.out.println(currentFittestCreature.getChromosome());
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