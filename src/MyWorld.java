import cosc343.assig2.World;
import cosc343.assig2.Creature;

import java.util.*;

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
    private final int numGenerations = 600;
    
    private void breed() {
        
    }
    
    private void crossOver() {
        
    }
    
    /**
     * Prints out population status of generation.
     */
    private void showStatus(MyCreature[] old_population, int numCreatures) {
       // Here is how you can get information about old creatures and how
       // well they did in the simulation
       float avgLifeTime = 0f;
       int nSurvivors = 0;

       for(MyCreature creature : old_population) {
           
          boolean dead   = creature.isDead();
          int     energy = creature.getEnergy();

          if(dead) {
             avgLifeTime += (float) creature.timeOfDeath();
             
          } else {
              
             nSurvivors++;
             avgLifeTime += (float) numTurns;
          }
       }

       // Right now the information is used to print some stats...but you should
       // use this information to access creatures fitness.  It's up to you how
       // you define your fitness function.  You should add a print out or
       // some visual display of average fitness over generations.
       avgLifeTime /= (float) numCreatures;
       System.out.println("Simulation stats:");
       System.out.println("  Survivors    : " + nSurvivors + " out of " + numCreatures);
       System.out.println("  Avg life time: " + avgLifeTime + " turns");
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

       showStatus(old_population, numCreatures);
       
       // Create a new array for the new population
       MyCreature[] new_population = new MyCreature[numCreatures];
       
       // Having some way of measuring the fitness, you should implement a proper
       // parent selection method here and create a set of new creatures.  You need
       // to create numCreatures of the new creatures.  If you'd like to have
       // some elitism, you can use old creatures in the next generation.  This
       // example code uses all the creatures from the old generation in the
       // new generation.
       for(int i = 0; i < numCreatures; i++) {
          new_population[i] = old_population[i];
       }

       // Return new population of cratures.
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
       new MyWorld(gridSize, windowWidth, windowHeight, repeatableMode, 
                                                        perceptFormat);
    }
  
}