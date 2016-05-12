package fpts.command;

import java.util.ArrayList;
import fpts.data.Holding;
import fpts.data.Portfolio;

/**
 * A class that represents a concrete Command.
 * This class is responsible for adding
 * the Holding to the holdings in the current Portfolio.
 *
 * @author Philip Bedward
 */
public class SubtractHoldingCommand implements Command{

    private Portfolio currentPortfolio;
    private Holding loss;
    private ArrayList<Holding> holdings;

    /**
     * Constructor for a subtractHoldingCommand.
     *
     * @param currentP - The Portfolio currently in use.
     * @param loss - The holding that value is being deducted from
     */
    public SubtractHoldingCommand(Portfolio currentP, Holding loss){
        this.currentPortfolio = currentP;
        this.loss = loss;

        holdings = currentPortfolio.getHoldings();
    }

    /**
     * finds the existing Holding and subtracts the amount lost
     * from its value
     */
    @Override
    public void execute(){
        currentPortfolio.subHolding(this.loss);
    }

    /**
     * finds the existing Holding and adds the amount originally lost
     * back to its value.
     */
    @Override
    public void unExecute(){
        currentPortfolio.addHolding(this.loss);
    }


    /**
     * Helper method to be called to know if
     * that holding already exists so that it can be
     * subtracted from.
     * @return - a boolean: True if it can be executed
     *                      False: the command cannot be executed
     */
    public boolean canPerform(){
        return ( holdings.contains(this.loss) );
    }
}
