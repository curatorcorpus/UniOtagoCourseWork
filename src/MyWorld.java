import cosc343.assig2.World;
import cosc343.assig2.Creature;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.io.File;

import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
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
    private final int numGenerations = 200;
    
    private int genCounter = 200;
    
    private int prevSurvivors = 0;
    private MyCreature[] prevOldGen = null;
    private double[] averageFitnessPerGen = new double[numGenerations];
        private int avgFitIdx = 0;
        
    private double determineFitness(MyCreature creature) {

        double fitness = 0;
        int state = 0;
        
        if(creature.isDead()) state = 0;
        else state = 1;
            
        fitness = creature.getEnergy() + (state * numTurns - creature.timeOfDeath());
        
        return fitness;
    }
    
    private MyCreature[] breed(Creature[] oldPopulationCt, int numCreatures) {
        
        MyCreature[] oldPopulation = (MyCreature[]) oldPopulationCt;
        MyCreature[] newGeneration = new MyCreature[numCreatures];
        MyCreature fittestCreature = null;
        Deque<MyCreature> survivedCreaturesStack = new ArrayDeque<>();
        List<MyCreature>  survivedCreaturesArray = new ArrayList<>();
        
        double avgFitness = 0;
        
        double maxFitness = 0;
        int fittestCreatureIdx = 0;
        int noEliteCreatures = 0;
        
        // find fittest creature for breeding.
        for(int i = 0; i < numCreatures; i++) {
            double fitness = determineFitness(oldPopulation[i]);
            
            avgFitness += fitness;
            
            if(!oldPopulation[i].isDead()) {
                if(fitness > maxFitness) {
                    maxFitness = fitness;
                    fittestCreature = oldPopulation[i];
                    fittestCreatureIdx = i;
                }
                /*
                survivedCreaturesStack.push(oldPopulation[i]);
                survivedCreaturesArray.add(oldPopulation[i]);
                noEliteCreatures++;*/
            }
        }      
        
        if(fittestCreature == null) {
            System.out.println("couldn't find elite creature");
        }
        
        float[] fittestChromosome = fittestCreature.getChromosome();
        
        for(int i = 0; i < numCreatures; i++) {
            if(i == fittestCreatureIdx) {
                newGeneration[i] = oldPopulation[i];
            } else {
                float[] newGenes = crossOver(fittestChromosome, 
                                             oldPopulation[i].getChromosome());
                newGeneration[i] = new MyCreature(newGenes);                
            }
        }
        
        /*
        int currNoNewGen = 0;
        // breed with fittest creatures
        while(!survivedCreaturesStack.isEmpty()) {     

            try {
                // if there is only on surviver then break - no fit partner to mate.
                if(survivedCreaturesArray.size() == 1) {
                    break;
                // otherwise if the stack has more than one surviver, breed the fit.
                } else if(survivedCreaturesStack.size() > 1) {
                    MyCreature firstParent  = survivedCreaturesStack.pop();
                    MyCreature secondParent = survivedCreaturesStack.pop();

                    float[] newGenes = crossOver(firstParent.getChromosome(), 
                                                 secondParent.getChromosome());

                    newGeneration[currNoNewGen++] = new MyCreature(newGenes);
                // if there is only 1 parent remaining, try randomly breeding with another fit mate.
                } else {
                    Random rand = new Random();
                    MyCreature mate = null;
                    MyCreature remainingParent = survivedCreaturesStack.pop();
                    
                    do {
                        int selection = rand.nextInt(noEliteCreatures);

                        mate = survivedCreaturesArray.get(selection);
                        
                        if(!mate.equals(remainingParent)) {
                            break;
                        } else {
                            mate = null;
                        }
                    } while(mate == null);  
                    
                    float[] newGenes = crossOver(remainingParent.getChromosome(), 
                                                 mate.getChromosome());
                    
                    newGeneration[currNoNewGen++] = new MyCreature(newGenes);
                }
                
            } catch(EmptyStackException e) {}  
        }

        // add the fittest population to new generation
        for(MyCreature fitCreatures : survivedCreaturesArray) {
           newGeneration[currNoNewGen++] = fitCreatures;
        }
        
        // use the fittest to breed with rest of old population
        float[] eliteChromosomes = fittestCreature.getChromosome();

        int oldGenIdx = 0;
        // cross over breeding.
        while(currNoNewGen < numCreatures) {
            
            MyCreature partner = oldPopulation[oldGenIdx++];
            
            if(!survivedCreaturesArray.contains(partner)) {
                float[] newGenes = crossOver(eliteChromosomes, 
                                             partner.getChromosome());
                newGeneration[currNoNewGen++] = new MyCreature(newGenes);
            }
        }
        
        */
        
        averageFitnessPerGen[avgFitIdx++] = (double) (avgFitness/numCreatures);        
        return newGeneration;
    }
    
    private float[] crossOver(float[] dominChrom, float[] altChrom) {
        
        Random ran = new Random();
        
        float[] offspring = new float[dominChrom.length];
        
        int xoverPoint = ran.nextInt(dominChrom.length);
        int i = 0;

        while(i < dominChrom.length) {
            if(i < xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i]; 
            }
            
            i++;
        }
        
        // mutate a gene.
        mutate(offspring);
        
        return offspring;
    }
    
    private void mutate(float[] offspring) {
        
        Random rand = new Random();
        
        // have 11 / 1000 chance of mutation.
        int mutateGene = rand.nextInt(10000);    
        
        if(mutateGene < offspring.length) {
            offspring[mutateGene] = rand.nextFloat();
        } 
    }
    
    /**
     * Prints out population status of generation.
     */
    private int showStatus(MyCreature[] old_population, int numCreatures) {
       // Here is how you can get information about old creatures and how
       // well they did in the simulation
       float avgLifeTime = 0f;
       int nSurvivors = 0;
       int avgEnergy = 0;
       
       for(MyCreature creature : old_population) {
           
          boolean dead = creature.isDead();
          
          if(dead) {
             avgLifeTime += (float) creature.timeOfDeath();
          } else {
             nSurvivors++;
             avgLifeTime += (float) numTurns;
          }
          
          avgEnergy += creature.getEnergy();
       }

       // Right now the information is used to print some stats...but you should
       // use this information to access creatures fitness.  It's up to you how
       // you define your fitness function.  You should add a print out or
       // some visual display of average fitness over generations.
       avgLifeTime /= (float) numCreatures;
       avgEnergy /=  numCreatures;
       System.out.println("Simulation stats:");
       System.out.println("  Fitness      : " + avgEnergy );
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
 
    /* The MyWorld class must override this function, which is
       used to fetch a population of creatures at the beginning of the
       first simulation.  This is the place where you need to  generate
       a set of creatures with random behaviours.

       Input: numCreatures - this variable will tell you how many creatures
                             the world is expecting

       Returns: An array of MyCreature objects - the World will expect numCreatures
                elements in that array     
    */  
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

    /* The MyWorld class must override this function, which is
       used to fetch the next generation of the creatures.  This World will
       proivde you with the old_generation of creatures, from which you can
       extract information relating to how they did in the previous simulation...
       and use them as parents for the new generation.

       Input: old_population_btc - the generation of old creatures before type casting. 
                                The World doesn't know about MyCreature type, only
                                its parent type Creature, so you will have to
                                typecast to MyCreatures.  These creatures 
                                have been simulated over and their state
                                can be queried to compute their fitness
              numCreatures - the number of elements in the old_population_btc
                             array


    Returns: An array of MyCreature objects - the World will expect numCreatures
             elements in that array.  This is the new population that will be
             use for the next simulation.  
    */  
    
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
        MyCreature[] new_population = null;
        
        int survivors = showStatus(old_population, numCreatures);
       
        /*
        if(prevSurvivors < survivors) {
            // Create a new array for the new population
            new_population = breed(old_population_btc, numCreatures);

            prevSurvivors = survivors;
            prevOldGen = old_population;
            
        } else {
            new_population = prevOldGen;
        }*/
       
        genCounter--;
        
        if(genCounter == 0) {
           
            JFreeChart jfreechart = ChartFactory.createScatterPlot(
            "Fitness Scatter", "Generations", "Fitness", samplexydataset2(),
            PlotOrientation.VERTICAL, true, true, false);
            Shape cross = ShapeUtilities.createDiagonalCross(3, 1);
            XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
            xyPlot.setDomainCrosshairVisible(true);
            xyPlot.setRangeCrosshairVisible(true);
            XYItemRenderer renderer = xyPlot.getRenderer();
            renderer.setSeriesShape(0, cross);
            renderer.setSeriesPaint(0, Color.red);
       
            jfreechart.getRenderingHints().put
            (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // create and display a frame...
            ChartFrame frame = new ChartFrame("COSC 343 Assignment", jfreechart);
            frame.pack();
            frame.setVisible(true);
            
            try {
                ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
                File file = new File("FitnessChart.png");
                ChartUtilities.saveChartAsPNG(file, jfreechart, 2450, 1440, info);
                
            } catch(Exception e) {
                
            }
        }
        
        return breed(old_population_btc, numCreatures);
    }
    
    private XYSeriesCollection samplexydataset2() {
        
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        XYSeries series = new XYSeries("Fitness");
        
        for (int i = 0; i < numGenerations - 1; i++) {
                series.add(i, averageFitnessPerGen[i]);
                System.out.println(averageFitnessPerGen[i]);
        }
        
        xySeriesCollection.addSeries(series);
        
        return xySeriesCollection;
    }
    
    /**
     * Main Method.
     * 
     * @param args 
     */
    public static void main(String[] args) {

       boolean repeatableMode = true;
       
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