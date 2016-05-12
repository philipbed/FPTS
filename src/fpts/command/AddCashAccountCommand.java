package fpts.command;

import fpts.data.CashAccount;
import fpts.data.Portfolio;

/**
 * A class that represents a concrete Command.
 * This class is responsible for adding
 * the cash account to the Portfolio in use.
 *
 * @author Philip Bedward
 */
public class AddCashAccountCommand implements Command{

    private Portfolio currentPortfolio;
    private CashAccount newCashAcct;
    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * Constructor for the AddCashAccountCommand
     * @param currentP - The Portfolio currently in use.
     * @param amt - The initial amount in the Cash Account
     * @param name -  The name of the Cash Account
     * @param date - The date the Account was opened
     */
    public AddCashAccountCommand(Portfolio currentP, double amt, String name, String date){
        currentPortfolio = currentP;
        newCashAcct = new CashAccount(amt,name,date);
    }

    /**
     * Adds the cash account to the portfolio
     */
    @Override
    public void execute() {
        currentPortfolio.addHolding(newCashAcct);
        careTaker.pushToUndo(this);
    }

    /**
     * Removes the cash account from the portfolio
     */
    @Override
    public void unExecute() {
        currentPortfolio.removeHolding(newCashAcct);
        careTaker.pushToRedo(this);
    }
}
