package fpts.controllers;

import fpts.simulation.*;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class SimulationController extends LogoutParent{

	@FXML
    TextField numSteps;
	
	@FXML
	MenuButton timeInterval;
	
	@FXML
	TextField percentChange;
	
	@FXML
	RadioButton BullStrat;
	
	@FXML
	RadioButton BearStrat;
	
	@FXML
	RadioButton NoGrowthStrat;

	@FXML
	MenuItem dayItem, monthItem, yearItem;

	@FXML
	LineChart SimulationChart;

	@FXML
	Text CurrSimulation, CurrStep, stratFlag, intervalFlag, stepFlag, percentFlag;


	/**
	 * Sets the menu button text when the 'day' menu item is clicked
	 */
	public void setDayItemHandler(){
		timeInterval.setText(dayItem.getText());
	}

	/**
	 * Sets the menu button text when the 'month' menu item is clicked
	 */
	public void setMonthItemHandler(){
		timeInterval.setText(monthItem.getText());

	}

	/**
	 * Sets the menu button text when the 'year' menu item is clicked
	 */
	public void setYearItemHandler() {
		timeInterval.setText(yearItem.getText());

	}

	/**
	 * Clears the simulation tab after the corresponding actions have been performed.
	 */
	private void clearSimulationTab(){
		BullStrat.setSelected(false);
		BearStrat.setSelected(false);
		NoGrowthStrat.setSelected(false);
		numSteps.setText("");
		if(percentChange.isDisable()){
			percentChange.setText("0.0");
		}
		else {
			percentChange.setText("");
		}
	}

	/**
	 * Updates the Simulation display.
	 */
	private void updateSimulation(){
		SimulationChart.getData().setAll(SimulationCT.exportSimulations());
		CurrSimulation.setText(Integer.toString(SimulationCT.getCurrSim()));
		CurrStep.setText(Integer.toString(SimulationCT.getCurrentStep()) + " / " + Integer.toString(SimulationCT.getTotalSteps()));
	}

	/**
	 * Removes the last Simulation that was created.
	 */
	@FXML
	public void removeLastSimulation(){
		SimulationCT.endSimulation(false);
		updateSimulation();
	}

	/**
	 * Removes all Simulation in the caretaker.
	 */
	@FXML
	public void removeAllSimulations(){
		SimulationCT.endSimulation(true);
		updateSimulation();
	}

	/**
	 * Brings you to the previous Simulation.
	 */
	@FXML
	public void previousSimulation(){
		if(SimulationCT.prevStep()) {updateSimulation();}
	}

	/**
	 * Brings you to the next Simulation.
	 */
	@FXML
	public void nextSimulation(){
		if(SimulationCT.nextStep()){updateSimulation();}
	}

	/**
	 * Brings you back to the last Simulation.
	 */
	@FXML
	public void lastSimulation(){
		while(SimulationCT.nextStep());
		updateSimulation();
	}

	/**
	 * Brings you back to the first Simulation.
	 */
	@FXML
	public void firstSimulation(){
		while(SimulationCT.prevStep());
		updateSimulation();
	}

	/**
	 * Runs full simulation on the equity list.
	 */
	@FXML
	public void simulateAll() {
		if(checkSimulationInput()) {
			runSimulation(true);
		}
	}

	/**
	 * Runs Step by step simulation.
	 */
	@FXML
	public void simulateStepByStep() {
		if(checkSimulationInput()) {
			runSimulation(false);
		}
	}

	/**
	 * Runs a new Simulation.
	 * @param runAll If true, runs all steps at once. If false, runs step by step.
	 */
	private void runSimulation(boolean runAll){
		//find strategy
		MarketSimulationStrategy strategy;
		if(BullStrat.isSelected()){
			strategy = new BullMarketStrategy();
		}else if(BearStrat.isSelected()){
			strategy = new BearMarketStrategy();
		}else{
			strategy = new NoGrowthMarketStrategy();
		}

		//run simulation and update
		SimulationCT.run(strategy, timeInterval.getText().toLowerCase(), Integer.parseInt(numSteps.getText()), Double.parseDouble(percentChange.getText()), runAll);
		updateSimulation();

		//clear tab
		clearSimulationTab();
	}

	/**
	 * A method to verify that simulation inputs match expected formats.
	 * @return Returns true if input is acceptable.
	 */
	private boolean checkSimulationInput(){
		boolean ret = true;

		//clear flags
		stratFlag.setVisible(false);
		intervalFlag.setVisible(false);
		stepFlag.setVisible(false);
		percentFlag.setVisible(false);

		//check if a strategy has been selected
		if(!BullStrat.isSelected() && !BearStrat.isSelected() && !NoGrowthStrat.isSelected()){
			ret = false;
			stratFlag.setVisible(true);
		}

		//check if an interval has been selected
		if(timeInterval.getText().equals("(Select Interval)")){
			ret = false;
			intervalFlag.setVisible(true);
		}

		//check if the number of steps is between 1 and 100
		if(!numSteps.getText().matches("\\d+")){
			ret = false;
			stepFlag.setVisible(true);
			numSteps.setText("");
		}
		else if(Integer.valueOf(numSteps.getText()) < 1 || Integer.valueOf(numSteps.getText()) > 100){
			ret = false;
			stepFlag.setVisible(true);
			numSteps.setText("");
		}

		//check if the percentage is between 0.0 and 1000.0
		if(!percentChange.getText().matches("\\d+(\\.\\d+)?")){
			ret = false;
			percentFlag.setVisible(true);
			percentChange.setText("");
		}
		else if(Double.valueOf(percentChange.getText()) < 0.0 || Double.valueOf(percentChange.getText()) > 1000.0){
			ret = false;
			percentFlag.setVisible(true);
			percentChange.setText("");
		}

		return ret;
	}

	/**
	 * Sets the percentChange field to be enabled.
	 */
	@FXML
	public void setPercentOn(){
		percentChange.setDisable(false);
	}

	/**
	 * Sets the percentChange field to be disabled at "0.0"
	 */
	@FXML
	public void setPercentOff(){
		percentChange.setDisable(true);
		percentChange.setText("0.0");
	}
	
}
