package fpts.simulation;

import fpts.data.Equity;

import java.util.ArrayList;

/**
 * This class is repsonsible for implementing the bull market
 * strategy for this system. This strategy directly implements
 * all the functionality from the Market Simulation Strategy.
 */
public class BullMarketStrategy implements MarketSimulationStrategy {

    /**
     * Will simulate a bear market over "step" amount of time
     *
     * @param equities - the equities held in the provided memento
     * @param percentage - percentage each equity will be increase by
     * @param days - integer representing the total days
     * @return - The updated equities
     */
    public ArrayList<Equity> simulateMarket(ArrayList<Equity> equities, double percentage, int days){

        //Initialize values
        ArrayList<Equity> retEquities = new ArrayList<>(); //new array list of Equities to be returned
        double value; //current share value of the equity

        //Iterate over the list of equities and modify values to create new equities
        for(Equity e : equities){
            value = e.getShareValue();
            value = (value) * Math.pow(1 + (percentage/100),(days/365));
            retEquities.add(
                    new Equity(e.getEquityInfo(),
                            value, e.getDate(), e.getNumShares(), e.getFromCashAccount()));
        }
        return retEquities;
    }
}
