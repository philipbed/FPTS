package fpts.command;


import fpts.data.Equity;
import fpts.data.EquityInfo;
import fpts.data.Portfolio;

/**
 * A class that represents a concrete Command.
 * This class is responsible for adding
 * an equity to the Portfolio in use.
 *
 * @author Philip Bedward
 */
public class AddEquityCommand implements Command {

    private Portfolio currentPortfolio;
    private Equity newEquity;
    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * A constructor for an AddEquityCommand.
     * @param currentP The current portfolio.
     * @param equityInfo The metadata for the equity to add.
     * @param price The price we're buying at.
     * @param date The date of the transaction.
     * @param numShares The number of shares being bought.
     * @param fromCashAcct Whether or not this purchase is with cash from a cash account.
     */
    public AddEquityCommand(Portfolio currentP, EquityInfo equityInfo, double price, String date, double numShares, boolean fromCashAcct ){
        currentPortfolio = currentP;

        newEquity = new Equity(equityInfo,price,date,numShares,fromCashAcct);
    }

    /**
     * Adds the equity to the portfolio
     */
    @Override
    public void execute() {
        currentPortfolio.addHolding(newEquity);
        careTaker.pushToUndo(this);
    }

    /**
     * Removes the equity from the portfolio
     */
    @Override
    public void unExecute() {
        currentPortfolio.removeHolding(newEquity);
        careTaker.pushToRedo(this);
    }

}
