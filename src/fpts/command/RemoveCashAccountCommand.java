package fpts.command;

import fpts.data.CashAccount;
import fpts.data.Portfolio;

/**
 * A class that represents a concrete Command.
 * This class is responsible for removing
 * the cash account from the Portfolio in use.
 *
 * @author Philip Bedward
 */
public class RemoveCashAccountCommand implements Command {

    private Portfolio currentPortfolio;
    private CashAccount cashAcctRemove;
    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * Constructor for the AddCashAccountCommand
     * @param currentP - The Portfolio currently in use.
     * @param cashAcct - The cashAcct to be removed.
     */
    public RemoveCashAccountCommand(Portfolio currentP, CashAccount cashAcct){
        currentPortfolio = currentP;

        cashAcctRemove = cashAcct;
    }

    /**
     * Removes the cash account from the portfolio
     */
    @Override
    public void execute(){
        currentPortfolio.removeHolding(cashAcctRemove);
        careTaker.pushToUndo(this);
    }

    /**
     * Adds the cash account to the portfolio
     */
    @Override
    public void unExecute(){
        currentPortfolio.addHolding(cashAcctRemove);
        careTaker.pushToRedo(this);
    }
}
