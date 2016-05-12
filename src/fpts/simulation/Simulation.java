package fpts.simulation;

import fpts.data.Equity;

import java.util.ArrayList;


/**
 * A user can see how their portfolio is expected to perform over time by specifying a time period and
 * a simulation algorithm to use.
 *
 * A user can input the number of steps to move forward, and the interval of each time step.
 * The allowed time intervals are a day, a month, and a year.
 * The user can run the simulation step by step, or all at once (we’ll have to find end date of last step and store it).
 *
 * A user can choose a simulation in which equity prices never change. (No-Growth-Market)
 * A user can choose a simulation with an inputted annual price increase percentage. (Bull-Market)
 * A user can choose a simulation with an inputted annual price decrease percentage. (Bear-Market)
 *
 * After a simulation is complete, the portfolio value is shown.
 * The user can run another simulation, or reset the prices to either the prices before the simulation or
 * the current prices (read back from csv file).
 *
 * Simulation runs the market strategies on the portfolio's equities
 *
 * Created by William on 4/8/16.
 */
public class Simulation {

    //Private fields holding all the Simulation data.
    private String step; //The step of this Simulation ("day", "month", or "year").
    private int numStep; //The total number of steps for this Simulation.
    private int currentStep; //The current step being output by this Simulation.
    private ArrayList<ArrayList<Equity>> mementos; //An ArrayList of all mementos created for this Simulation.


    /**
     * Constructor for a Simulation object.
     *
     * @param equities The ArrayList
     * @param strategy The MarketSimulationStrategy used for this Simulation.
     * @param step     The step of this Simulation ("day", "month", or "year").
     * @param numStep  The total number of steps for this Simulation.
     * @param percent  The percent for this Simulation (not in decimal form).
     */
    public Simulation(ArrayList<Equity> equities, MarketSimulationStrategy strategy, String step, int numStep, double percent) {
        this.step = step;
        this.numStep = numStep;
        this.currentStep = 0;
        this.mementos = new ArrayList<>();

        int days = stepToInt(step);
        //Execute the MarketSimulationStrategy on the Equities numStep times
        for (int i = 1; i <= numStep; i++) {
            mementos.add(strategy.simulateMarket(equities, percent, days * i));
            currentStep++;
        }
    }


    /**
     * Public getter for step.
     *
     * @return The step of this Simulation ("day", "month", or "year").
     */
    public String getStep() {
        return this.step;
    }


    /**
     * Public getter for numStep.
     *
     * @return The total number of steps for this Simulation.
     */
    public int getTotalSteps() {
        return this.numStep;
    }


    /**
     * Public getter for currStep.
     *
     * @return The current step being output by this Simulation.
     */
    public int getCurrentStep() {
        return this.currentStep;
    }


    /**
     * Private getter for mementos.
     *
     * @return An ArrayList of all mementos created for this Simulation.
     */
    public ArrayList<ArrayList<Equity>> getMementos() {
        return this.mementos;
    }


    /**
     * Private getter that returns the current memento based on the currentStep.
     * If currentStep is out of bounds, catches IndexOutOfBoundException and returns the last memento in the simulation.
     *
     * @return The current memento of the current simulation or the last memento if index is invalid.
     */
    public ArrayList<Equity> getCurrentMemento() {

        //if currentStep == 0, return the last memento of the current simulation
        if (getCurrentStep() == 0) {
            return getMementos().get(getTotalSteps() - 1);
        }

        //try to get the currentSimulation
        try {
            return getMementos().get(getCurrentStep() - 1);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        //if index out of bounds, return the last memento by default
        return getMementos().get(getTotalSteps() - 1);
    }


    /**
     * Private getter that returns the current memento based on the currentStep.
     * If currentStep is out of bounds, catches IndexOutOfBoundException and returns the last memento in the simulation.
     *
     * @return The current memento of the current simulation or the last memento if index is invalid.
     */
    public ArrayList<Equity> getLastMemento() {

        //return the last memento
        return getMementos().get(getTotalSteps() - 1);
    }


    /**
     * This method increments the currentStep.
     *
     * @return true if there was a next step or false if already at last step.
     */
    public boolean nextStep() {

        //check to see if there is a next step
        if (currentStep >= getTotalSteps()) {
            return false;
        }

        //update current step
        currentStep++;

        return true;
    }


    /**
     * This method decrements the current step.
     *
     * @return true if there was a previous step or false if already at first step.
     */
    public boolean prevStep() {

        //check to see if there is a previous step
        if (currentStep <= 1) {
            return false;
        }

        //update current step
        currentStep--;

        return true;
    }


    /**
     * This method takes a given step as "day", "month", or "year" and returns their equivalent days as an integer.
     * @param step The step as "day", "month", or "year".
     * @return The step equivalent days as an integer.
     */
    private static int stepToInt(String step) {
        if (step.equals("day")) {
            return 1;
        } else if (step.equals("month")) {
            return 30;
        } else {
            return 365;
        }
    }
}