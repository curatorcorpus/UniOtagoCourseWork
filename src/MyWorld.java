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

    /**
     * The number of turns in each simulation.
     */
    private final int numTurns = 100;
    
    /**
     * The number of generations the genetic algorithm will iterate through.
     */
    private final int numGenerations = 300;
    
    private double[] averageFitnessPerGen = new double[numGenerations];
    
    private double previousFitnessAvg = 0.0;
    
    private int avgFitIdx = 0;
    private int avgEnergy = 0;   
    
    private MyCreature currentFittestCreature;
    double previousAvgFit = 0.0;
    
    private double determineFitness(MyCreature creature) {
        double fitness = 0;
        
        avgEnergy += creature.getEnergy();
        fitness = creature.getEnergy() * ((double)((double)numTurns - (double)creature.timeOfDeath()) /(double) numTurns);
        return fitness;
    }
    
    private MyCreature[] breed(Creature[] oldPopulationCt, int numCreatures) {
        List<MyCreature> aboveAvg = new ArrayList<>();
        MyCreature[] oldPopulation = (MyCreature[]) oldPopulationCt;
        MyCreature[] newGeneration = new MyCreature[numCreatures];
        
        double avgFitness = 0.0;
        double maxFitness = 0.0;
        
        // obtain previous fitness
        for(int i = 0; i < numCreatures; i++) {
            
            MyCreature currCreature = oldPopulation[i];
            double currFitness = determineFitness(currCreature);
            currCreature.setFitness(currFitness);
            
            if(maxFitness < currFitness) {
                currentFittestCreature = currCreature;
                maxFitness = currFitness;
                System.out.println(maxFitness);
            }
            /*
            if(currFitness > previousAvgFit + 12) {
                aboveAvg.add(currCreature);
            }*/
            
            avgFitness += currFitness;
        }

                System.out.println("above avg " + aboveAvg.size());
        averageFitnessPerGen[avgFitIdx] = avgFitness/numCreatures;
        
        // display status.
        showStatus(oldPopulation, numCreatures);

        int newGen = 0;
        /*for(MyCreature c : aboveAvg) {
            newGeneration[newGen++] = c;
        }
        */
        while(newGen < numCreatures) {
           newGeneration[newGen++] = tournamentSelection(oldPopulation);
        }
        
        previousAvgFit = avgFitness/numCreatures;
        
        return newGeneration;
    }
    
    private MyCreature tournamentSelection(MyCreature[] oldPopulation) {
        Random rand = new Random();
        
        int left  = 0;
        int right = 0;
        
        do {
           left  = rand.nextInt(oldPopulation.length);
           right = rand.nextInt(oldPopulation.length);
        } while((right - left) < 24);

        List<MyCreature> oldPopSubset = new ArrayList<>();
        
        while(left < right) {
            oldPopSubset.add(oldPopulation[left++]);
        }
        
        // sort array by energy
        Collections.sort(oldPopSubset, (a, b) -> ((Double) a.getFitness()).
                                                 compareTo(((Double) b.getFitness())));
        
        MyCreature parent1 = oldPopSubset.get(oldPopSubset.size() - 1);
        MyCreature parent2 = oldPopSubset.get(oldPopSubset.size() - 2);
        
        Chromosome newChromo = crossOver(parent1.getChromosome(),
                                         parent2.getChromosome());
        
        return new MyCreature(newChromo);
    }
    
    /**
     * 
     * 
     * @param male
     * @param female
     * @return 
     */
    private Chromosome crossOver(Chromosome firstBest, Chromosome scndBest) {
        int[]   directionIntelP1 = firstBest.getDirectionIntel(), 
                directionIntelP2 = scndBest.getDirectionIntel();
        
        float[] actionSensitivityP1 = firstBest.getActionSensGenes(),
                actionSensitivityP2 = scndBest.getActionSensGenes();
        
        float[] fffSensitivityP1 = firstBest.getFFFSensGenes(), 
                fffSensitivityP2 = scndBest.getFFFSensGenes();
     
        Chromosome newGenes = new Chromosome();
   
        newGenes.setDirectionIntel(directionIntelP1);               // always get best parent's direction awareness genes.
        newGenes.setDirectionToPcptMap(firstBest.getDirectionToPcptMap());   // always get best parent's direction to percept mapping.
        newGenes.setActionSensGenes(onePointCrossOver(actionSensitivityP1, 
                                                      actionSensitivityP2)); // cross over action sensitivity genes.
                                                       newGenes.setFFFSensGenes(firstBest.getFFFSensGenes());
//newGenes.setFFFSensGenes(onePointCrossOver(fffSensitivityP1, 
          //                                         fffSensitivityP2));       // cross over fff sensitivity genes.
        
        return newGenes;
    }
    
    public float[] onePointCrossOver(float[] genes1, float[] genes2) {
        Random rand = new Random();
        float[] newSubTraits = new float[genes1.length];
        
        int xoverPoint = rand.nextInt(genes1.length);
        int i = 0;
        while(i < genes1.length) {
            if(i < xoverPoint) {
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
        
        int mutate = rand.nextInt(4000);
        
        if(mutate < subTraits.length) {
            System.out.println("mutated!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            subTraits[mutate] = rand.nextFloat();
        }
/*
        mutate = rand.nextInt(1000);
        
        if(mutate < subTraits.length) {
            subTraits[mutate] = rand.nextFloat();
        }
*/
        return subTraits;
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
            population[i] = new MyCreature(numPercepts, numActions);     
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
            range.setRange(0, 100);
            range.setTickUnit(new NumberTickUnit(5.0));
            
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