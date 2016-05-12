package fpts.simulation;

import fpts.data.Equity;
import fpts.data.Portfolio;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

/**
 *
 *
 * Created by William on 3/11/16.
 */
public class SimulationCT {

    //Initialize private variables
    private static ArrayList<Simulation> caretaker = new ArrayList<>(); //ArrayList of all simulations
    private static int currentSimulation = 0; //The current Simulation being handled

    /**
     * Public getter for the currentSimulation variable.
     * @return The currentSimulation int.
     */
    public static int getCurrSim(){return currentSimulation;}


    /**
     * Private getter that returns the current Simulation being viewed.
     * @return the current Simulation being viewed or null if there is none.
     */
    private static Simulation getCurrentSimulation(){

        //if the caretaker is empty or currentSimulation is set to 0, return null
        if(caretaker.isEmpty() || currentSimulation == 0){return null;}

        //try to return the currentSimulation in the caretaker
        try{
            return caretaker.get(currentSimulation-1);
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        //if exception is thrown return the last Simulation
        return caretaker.get(caretaker.size()-1);
    }


    /**
     * Private getter that returns the last Simulation being viewed.
     * @return the last Simulation in the caretaker or null if there is none.
     */
    private static Simulation getLastSimulation(){

        //if the caretaker is empty, return null
        if(caretaker.isEmpty()){return null;}

        //return the last Simulation in the caretaker
        return caretaker.get(caretaker.size()-1);
    }


    /**
     * This method attempts to decrement the currentSimulation.
     * @return true if a previous Simulation was found or false if already at first.
     */
    private static boolean prevSimulation(){

        //if currentSimulation is greater than 1, decrement
        if(currentSimulation > 1){
            currentSimulation--;
            return true;
        }
        return false;
    }


    /**
     * This method attempts to increment the currentSimulation.
     * @return true if a next Simulation was found or false if already at last.
     */
    private static boolean nextSimulation(){

        //if currentSimulation is less than caretaker size, increment
        if(currentSimulation < caretaker.size()){
            currentSimulation++;
            return true;
        }
        return false;
    }


    /**
     * Returns the currentStep of the currentSimulation.
     * @return the current step of the simulation or 0 if no Simulation.
     */
    public static int getCurrentStep() {
        //if there is no currentSimulation, return 0
        if(getCurrentSimulation() == null){return 0;}
        return getCurrentSimulation().getCurrentStep();
    }


    /**
     * Returns for totalSteps of the current simulation.
     * @return the total steps of the current simulation.
     */
    public static int getTotalSteps(){
        if(getCurrentSimulation() == null){return 0;}
        return getCurrentSimulation().getTotalSteps();
    }


    /**
     * Getter that returns the current memento based on the currentSimulation.
     * If getCurrentSimulation returns null or the currentStep == 0, returns null.
     * @return the current memento of the current simulation or null if there is no currentSimulation.
     */
    private static ArrayList<Equity> getCurrentMemento(){

        //if currentStep == 0 or there is no currentSimulation, return null
        if(getCurrentSimulation() == null || getCurrentStep() == 0){return null;}

        //return the currentSimulation's currentMemento
        return getCurrentSimulation().getCurrentMemento();
    }


    /**
     * Getter that returns the last memento based on the last Simulation.
     * If getLastSimulation returns null, returns null.
     * @return the current memento of the current simulation or null if there is no last Simulation.
     */
    private static ArrayList<Equity> getLastMemento(){

        //if there is no last Simulation, return null
        if(getLastSimulation() == null){return null;}

        //return the last Simulation's last Memento
        return getLastSimulation().getLastMemento();
    }



    /**
     * This method creates a simulations on the last Memento and saves the Simulation in caretaker.
     * @param strategy a MarketSimulationStrategy to simulate on.
     * @param step the size of the step as "day", "month", "year".
     * @param numStep the number of steps to take.
     * @param percent the percent (not in decimal form).
     * @param runFull true means run all steps at once, false means return only the first step.
     */
    public static void run(MarketSimulationStrategy strategy, String step, int numStep, double percent, boolean runFull) {

        //set up data to simulate on
        ArrayList<Equity> data = getLastMemento();
        if(data == null){
            data = Portfolio.currentPortfolio.getEquities();
        }

        //Execute the MarketSimulationStrategy on the Equities numStep times
        caretaker.add(new Simulation(data, strategy, step, numStep, percent));
        currentSimulation = caretaker.size();


        //If step-by-step, jump back to first Memento of this Simulation
        if(!runFull){
            while(getCurrentSimulation().prevStep());
        }
    }



    /**
     * This method increments the current step.
     * @return true if there was a next step or false if already at last step.
     */
    public static boolean nextStep(){

        //check to see if there is a currentSimulation
        if(getCurrentSimulation() == null){return false;}

        //check to see if there is a next step
        if(getCurrentSimulation().nextStep()){return true;}

        else if(nextSimulation()){
            while(getCurrentSimulation().prevStep());
            return true;
        }

        return false;
    }



    /**
     * This method decrements the current step.
     * @return true if there was a previous step or false if already at first step.
     */
    public static boolean prevStep(){

        //check to see if there is a currentSimulation
        if(getCurrentSimulation() == null){return false;}

        //check to see if there is a next step
        if(getCurrentSimulation().prevStep()){return true;}

        else if(prevSimulation()){
            while(getCurrentSimulation().nextStep());
            return true;
        }

        return false;
    }



    /**
     * This is the method to end either the current simulation or all simulations
     * @param endAll true will end all simulations, false will end the current simulation
     */
    public static void endSimulation(boolean endAll){

        //end the entire simulation if endAll == true
        if(endAll){
            caretaker.clear();
            currentSimulation = 0;
        }

        //else delete only the lastSimulation
        else {
            //remove last Simulation and set current Simulation
            caretaker.remove(getLastSimulation());
            currentSimulation = caretaker.size();

            //set to last memento of the new simulation if caretaker is not empty
            if(!caretaker.isEmpty()){
                while (getCurrentSimulation().nextStep()) ;
            }
        }
    }


    /**
     * This function returns all simulated data up to currentSimulation as a formatted ArrayList of Series Objects.
     * @return An ArrayList of Series Objects for each Equity within the lastSimulation.
     */
    public static ArrayList<XYChart.Series<Integer, Double>> exportSimulations(){

        //initialize local variables
        ArrayList<XYChart.Series<Integer, Double>> formattedSimulations = new ArrayList<>();
        Equity currEquity;
        int stepTracker = 0;

        //if there is no current simulation to be shown, return the empty list
        if(getCurrentSimulation() == null){return formattedSimulations;}

        //for all Equities inside a memento
        for(int i=0; i < getCurrentSimulation().getCurrentMemento().size(); i++){
            XYChart.Series<Integer, Double> currSeries = new XYChart.Series<>();

            //for all simulations in caretaker up to currentSimulation
            for(Simulation s : caretaker.subList(0, currentSimulation)){

                //for all mementos inside simulation s
                for(int m=0; m < s.getMementos().size(); m++){
                    if(s.equals(getCurrentSimulation()) && m >= getCurrentStep()){continue;}
                    currEquity = s.getMementos().get(m).get(i);
                    currSeries.setName(currEquity.getName());
                    int interval = stepTracker + (stepToInt(s.getStep()) * m);
                    currSeries.getData().add(new XYChart.Data<>(interval, currEquity.getShareValue()));
                }

                stepTracker += stepToInt(s.getStep()) * s.getTotalSteps();
            }

            //add series to formattedSimulations
            formattedSimulations.add(currSeries);
            stepTracker = 0;
        }
        return formattedSimulations;
    }


    /**
     * This method takes a given step as "day", "month", or "year" and returns their equivalent days as an integer.
     * @param step The step as "day", "month", or "year".
     * @return The step equivalent days as an integer.
     */
    private static int stepToInt(String step){
        if(step.equals("day")){
            return 1;
        }
        else if(step.equals("month")){
            return 30;
        }
        else{
            return 365;
        }
    }
}