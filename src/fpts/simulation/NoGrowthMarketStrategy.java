package fpts.simulation;

import fpts.data.Equity;

import java.util.ArrayList;

/**
 * This class is responsible for representing the
 * no growth market strategy from the market simulation.
 * This class implements the MarketSimulationStrategy and its
 * functionality
 */
public class NoGrowthMarketStrategy implements MarketSimulationStrategy {

    /**
     * Will simulate a bear market over "step" amount of time
     *
     * @param equities - the equities held in the provided memento
     * @param percentage - percentage input is ignored
     * @param days - integer representing the total days
     * @return - The updated equities
     */
    public ArrayList<Equity> simulateMarket(ArrayList<Equity> equities, double percentage, int days){

        //Initialize values
        ArrayList<Equity> retEquities = new ArrayList<>(); //new array list of Equities to be returned

        //Iterate over the list of equities and modify values to create new equities
        for(Equity e : equities){
            retEquities.add(
                    new Equity(e.getEquityInfo(),
                            e.getShareValue(), e.getDate(), e.getNumShares(), e.getFromCashAccount()));
        }
        return retEquities;    }
}
