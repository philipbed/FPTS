package fpts.command;

import fpts.data.Holding;
import fpts.data.Portfolio;
import fpts.data.Transaction;

/**
 * A class that represents a concrete Command.
 * This class is responsible for adding and removing
 * the transaction to and/or from the history
 *
 * @author Philip Bedward
 */
public class AddToHistoryCommand implements Command {

    private Portfolio currentPortfolio;
    private Transaction currentTransaction;

    /**
     * Constructor for an addToHistoryCommand.
     *
     * @param currentP - The Portfolio currently in use.
     * @param loss - The holding that value is being deducted from
     * @param gain - The holding that value is being gain in
     * @param date - The date of the transaction
     */
    public AddToHistoryCommand(Portfolio currentP, Holding loss, Holding gain, String date){
        currentPortfolio = currentP;
        currentTransaction = new Transaction(gain,loss,date);
    }

    /**
     * Carries out the Command by
     * Adding the transaction to the transaction
     * history of the Portfolio
     */
    @Override
    public void execute() {
        currentPortfolio.addTransactionToHistory(currentTransaction);
    }

    /**
     * Undos the previous command by
     * removing the transaction from the
     * transaction history in the Portfolio
     *
     */
    @Override
    public void unExecute() {
        currentPortfolio.removeTransactionFromHistory(currentTransaction);
    }

}
