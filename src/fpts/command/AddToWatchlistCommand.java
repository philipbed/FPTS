package fpts.command;

import fpts.data.EquityInfo;
import fpts.data.Portfolio;
import fpts.data.WatchedEquity;

/**
 * A class that represents the command to add an equity to the Watchlist
 *
 * @author Daniil Vasin
 */
public class AddToWatchlistCommand implements Command{

    private Portfolio currentPortfolio;
    private WatchedEquity newEquity;
    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * A constructor for an AddToWatchlistCommand
     * @param currentP The current portfolio.
     * @param einfo The equity metadata to watch.
     * @param trigger The array of data representing the triggers.
     */
    public AddToWatchlistCommand(Portfolio currentP, EquityInfo einfo, double[] trigger){
        currentPortfolio = currentP;

        newEquity = new WatchedEquity(einfo,trigger);
    }
   
    /**
     * Adds an equity to the watch list
     */
    @Override
    public void execute() {
        currentPortfolio.addToWatchlist(newEquity);
        careTaker.pushToUndo(this);
    }

    /**
     * Removes an equity from the watch list
     */
    @Override
    public void unExecute() {
        currentPortfolio.removeFromWatchlist(newEquity);
        careTaker.pushToRedo(this);

    }
}
