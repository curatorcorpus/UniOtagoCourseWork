import cosc343.assig2.World;
import cosc343.assig2.Creature;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.io.File;

import java.util.*;
import java.util.List;
import java.util.ArrayList;

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
    private final int numGenerations = 200;
    
    private int genCounter = numGenerations;
    
    
    private double[] averageFitnessPerGen = new double[numGenerations];
    
    private double previousFitnessAvg = 0.0;
    
    private int avgFitIdx = 0;
    private int avgEnergy = 0;   
    
    private double determineFitness(MyCreature creature) {
        int state = 0;
        double fitness = 0;
        
        if(!creature.isDead()) state = 1;
        
        avgEnergy += creature.getEnergy();
        fitness = creature.getEnergy() * ((double)((double)numTurns - (double)creature.timeOfDeath()) /(double) numTurns);
        
        return fitness;
    }
    
    private MyCreature[] breed(Creature[] oldPopulationCt, int numCreatures) {
        
        MyCreature[] oldPopulation = (MyCreature[]) oldPopulationCt;
        MyCreature[] newGeneration = new MyCreature[numCreatures];
        MyCreature fittestCreature = null;
        
        List<MyCreature> aboveAverageCreatures = new ArrayList<>();
        List<MyCreature> belowAverageCreatures = new ArrayList<>();
        List<MyCreature> sortedAverages        = new ArrayList<>();
        
        double avgFitness = 0;
        
        double maxFitness = 0;
        int fittestCreatureIdx = 0;
        
        // find fittest creature for breeding.
        for(int i = 0; i < numCreatures; i++) {
            MyCreature currentCreature = oldPopulation[i];
            
            double fitness = determineFitness(currentCreature);
            
            currentCreature.setFitness(fitness);
            
            avgFitness += fitness;

            if(fitness > previousFitnessAvg) {      
            
                if(fitness > maxFitness) {
                    maxFitness = fitness;
                    fittestCreature = oldPopulation[i];
                    fittestCreatureIdx = i;
                }
            
                aboveAverageCreatures.add(currentCreature);
            } else {
                belowAverageCreatures.add(currentCreature);
            }
        }
        if(aboveAverageCreatures.size() > 1) {
            Collections.sort(aboveAverageCreatures, new Comparator<MyCreature>() {

                @Override
                public int compare(MyCreature o1, MyCreature o2) {
                    return -((Double) o1.getFitness()).compareTo(((Double) o2.getFitness()));
                }

            });
        }
        if(belowAverageCreatures.size() > 1) {
            Collections.sort(belowAverageCreatures, new Comparator<MyCreature>() {

                @Override
                public int compare(MyCreature o1, MyCreature o2) {
                    return -((Double) o1.getFitness()).compareTo(((Double) o2.getFitness()));
                }

            });
        }
        sortedAverages.addAll(aboveAverageCreatures);
        sortedAverages.addAll(belowAverageCreatures);        
        if(belowAverageCreatures.size() > 1) {
            Collections.sort(sortedAverages, new Comparator<MyCreature>() {

                @Override
                public int compare(MyCreature o1, MyCreature o2) {
                    return -((Double) o1.getFitness()).compareTo(((Double) o2.getFitness()));
                }

            });
        }
        
        for(MyCreature c : aboveAverageCreatures) {
           System.out.print(c.getFitness());
            System.out.print(" " + c.getEnergy());
            System.out.println(" " + c.timeOfDeath());
        }
        for(MyCreature c : belowAverageCreatures) {
            System.out.print(c.getFitness());
            System.out.print(" " + c.getEnergy());
            System.out.println(" " + c.timeOfDeath());
        }
        
        
        if(fittestCreature == null) {
            System.err.println("couldn't find elite creature");
            return null;
        }
        
        int newGenIdx = 0;
        
        // add all creatures that above average fitness to new population
        for(MyCreature c : aboveAverageCreatures) {
            newGeneration[newGenIdx++] = c;
        }

        int currentCreatureIdx = 0;
        while (newGenIdx < numCreatures) {
            
            MyCreature currentParent = sortedAverages.get(currentCreatureIdx++);
            System.out.println("fitness " + currentParent.getFitness());
            
            // make ordered fit creatures breed with rest of group.
            for(int i = currentCreatureIdx + 1; newGenIdx < numCreatures && i < aboveAverageCreatures.size(); i++) {

                MyCreature mate = aboveAverageCreatures.get(i);

                float[] newGenes = generalCrossOver(currentParent.getChromosome(), 
                                                    mate.getChromosome());
                newGeneration[newGenIdx++] = new MyCreature(newGenes); 
                
                if(newGenIdx < numCreatures) {
                    newGenes = inverseGeneralCrossOver(currentParent.getChromosome(), 
                                                        mate.getChromosome());
                    newGeneration[newGenIdx++] = new MyCreature(newGenes); 
                }
            }
            
            currentCreatureIdx++;
        }

        /*
        for(int i = 0; newGenIdx < numCreatures; i++) {
            if(i != fittestCreatureIdx) {
                
                MyCreature partner = oldPopulation[i];
                
                if(!aboveAverageCreatures.contains(partner)) {
                    float[] newGenes = generalCrossOver(fittestChromosome, 
                                                 partner.getChromosome());
                    newGeneration[newGenIdx++] = new MyCreature(newGenes); 
                } else {
                    float[] newGenes = generalCrossOver(fittestChromosome, 
                                                 partner.getChromosome());
                    newGeneration[newGenIdx++] = new MyCreature(newGenes);                     
                }    
            } 
        }*/

        averageFitnessPerGen[avgFitIdx++] = avgFitness/numCreatures; 
        previousFitnessAvg = avgFitness/numCreatures;
        
        return newGeneration;
    }
    
    private float[] eliteCrossOver(float[] dominChrom, float[] altChrom) {
        
        Random ran = new Random();
        
        float[] offspring = new float[dominChrom.length];
        
        int xoverPoint = ran.nextInt(dominChrom.length);
        int i = 0;
        /*
        while(i < dominChrom.length) {
            if(i < xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        } */
        
        xoverPoint = ran.nextInt(9);
        while(i < 9) {
            if(i < xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }

        xoverPoint = ran.nextInt(18) + 9;
        while(i < 18) {
            if(i < xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
        
        xoverPoint = ran.nextInt(2) + 9 * 2;
        while(i < 20) {
            if(i < xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
        
        xoverPoint = ran.nextInt(2) + 9 * 2 + 2;
        while(i < dominChrom.length) {
            if(i < xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
        
        // mutate a gene.
        //mutate(offspring);
        
        return offspring;
    }
    
    private float[] simpleCrossOver(float[] dominChrom, float[] altChrom) {
        
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
   
        
        return offspring;
    }
    
    private float[] inverseSimpleCrossOver(float[] dominChrom, float[] altChrom) {
        
        Random ran = new Random();
        
        float[] offspring = new float[dominChrom.length];
        
        int xoverPoint = ran.nextInt(dominChrom.length);
        int i = 0;
        
        while(i < dominChrom.length) {
            if(i > xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
   
        
        return offspring;
    }    
           
    private float[] generalCrossOver(float[] dominChrom, float[] altChrom) {
        
        Random ran = new Random();
        
        float[] offspring = new float[dominChrom.length];
        
        int xoverPoint = ran.nextInt(dominChrom.length);
        int i = 0;
        
        xoverPoint = ran.nextInt(9);
        while(i < 9) {
            if(i < xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
        
        xoverPoint = ran.nextInt(18) + 9;
        while(i < 18) {
            if(i < xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
        
        xoverPoint = ran.nextInt(4) + 9 * 2;
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
    
    private float[] inverseGeneralCrossOver(float[] dominChrom, float[] altChrom) {
        
        Random ran = new Random();
        
        float[] offspring = new float[dominChrom.length];
        
        int xoverPoint = ran.nextInt(dominChrom.length);
        int i = 0;
        
        xoverPoint = ran.nextInt(9);
        while(i < 9) {
            if(i > xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
        
        xoverPoint = ran.nextInt(18) + 9;
        while(i < 18) {
            if(i > xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
        
        xoverPoint = ran.nextInt(4) + 9 * 2;
        while(i < dominChrom.length) {
            if(i > xoverPoint) {
                offspring[i] = dominChrom[i];
            } else {
                offspring[i] = altChrom[i];
            }
            
            i++;
        }
        
        // mutate a gene.
        //mutate(offspring);
        
        return offspring;
    }
        
    private void mutate(float[] offspring) {
        
        Random rand = new Random();
        
        // high mutation rate 
        int mutationRate = 10000;
        
        // have 11 / 1000 chance of mutation.
        int mutateGene = rand.nextInt(mutationRate);    
        
        if(mutateGene < offspring.length) {
          offspring[mutateGene] = rand.nextFloat();

          System.out.println("mutation has occurred");
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
       System.out.println("  Fitness      : " + averageFitnessPerGen[avgFitIdx - 1]);
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
        MyCreature[] new_population = breed(old_population_btc, numCreatures); // Create a new array for the new population
       
        showStatus(old_population, numCreatures);
        
        if(--genCounter == 0) {
           
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
        }
        
        return new_population;
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