package fpts.simulation;

import fpts.data.Equity;

import java.util.ArrayList;

/**
 * Interface for all Market Simulation Strategies
 *
 * @author William Estey
 */
public interface MarketSimulationStrategy {

    public ArrayList<Equity> simulateMarket(ArrayList<Equity> equities, double percentage, int days);

}
