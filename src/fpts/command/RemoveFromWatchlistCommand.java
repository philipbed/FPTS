package fpts.command;

import fpts.data.Portfolio;
import fpts.data.WatchedEquity;

/**
 * A class that represents the command to remove and equity from the Watchlist
 * @author Daniil Vasin
 */
public class RemoveFromWatchlistCommand implements Command{

    private Portfolio currentPortfolio;
    private WatchedEquity equity;
    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * A constructor for a RemoveFromWatchlistCommand object.
     * @param currentP The current portfolio.
     * @param eq The watched equity to remove.
     */
    public RemoveFromWatchlistCommand(Portfolio currentP, WatchedEquity eq){
        currentPortfolio = currentP;

        equity = eq;
    }

    /**
     * Removes the equity from the watchlist
     */
    @Override
    public void execute() {
        currentPortfolio.removeFromWatchlist(equity);
        careTaker.pushToUndo(this);
    }

    /**
     * Adds the equity to the watchlist
     */
    @Override
    public void unExecute() {
        currentPortfolio.addToWatchlist(equity);
        careTaker.pushToRedo(this);
    }

}
