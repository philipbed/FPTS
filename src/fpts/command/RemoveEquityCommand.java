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
public class RemoveEquityCommand implements Command {

    private Portfolio currentPortfolio;
    private Equity equityToRemove;
    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * A constructor for a RemoveEquityCommand.
     * @param currentP The current portfolio.
     * @param equity The equity to remove.
     */
    public RemoveEquityCommand(Portfolio currentP, Equity equity){
        currentPortfolio = currentP;

        equityToRemove = equity;
    }

    /**
     * Removes the equity from the portfolio
     */
    @Override
    public void execute() {
        currentPortfolio.removeHolding(equityToRemove);
        careTaker.pushToUndo(this);
    }

    /**
     * Adds the equity to the portfolio
     */
    @Override
    public void unExecute() {
        currentPortfolio.addHolding(equityToRemove);
        careTaker.pushToRedo(this);
    }

}
