package fpts.command;

import fpts.data.Holding;
import fpts.data.Portfolio;

/**
 * A class that represents a concrete Command.
 * This class is responsible for adding
 * the Holding to the holdings in the current Portfolio.
 *
 * @author Philip Bedward
 */
public class AddHoldingCommand implements Command{

    private Portfolio currentPortfolio;
    private Holding gain;

    /**
     * Constructor for an addHoldingCommand.
     *
     * @param currentP - The Portfolio currently in use.
     * @param gain - The holding that value is being gain in.
     */
    public AddHoldingCommand(Portfolio currentP, Holding gain){
        this.currentPortfolio = currentP;
        this.gain = gain;
    }

    /**
     *
     * Carries out the Command by either
     * adding the values of the holdings if
     * that same one already exists in the Portfolio
     * or adds to the Portfolio's internal list of holdings
     * if it is a new holding.
     */
    @Override
    public void execute(){
        currentPortfolio.addHolding(this.gain);
    }

    /**
     * Undos the previous command by
     * removing the holding from the
     * Portfolio's list of holdings or by
     * subtracting the amount gained if the
     * holding existed previously
     */
    @Override
    public void unExecute(){
        currentPortfolio.subHolding(this.gain);
    }

}
