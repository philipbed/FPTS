package fpts.command;

import fpts.data.Holding;
import fpts.data.Portfolio;

/**
 * Representation of a Concrete MacroCommand object.
 *
 * @author Philip Bedward
 */
public class TransactionCommand implements Command {

    // leaf components
    private SubtractHoldingCommand subtract;
    private Command add;
    private Command addToHistory;

    private UndoRedoCaretaker caretaker;

    /**
     * Constructor for the TransactionCommand
     *
     * @param currentP - the current Portfolio in use
     * @param loss - The holding that value is being deducted from
     * @param gain - The holding that value is being gain in
     * @param date - The date of the transaction
     */
    public TransactionCommand(Portfolio currentP, Holding gain, Holding loss, String date){
        subtract = new SubtractHoldingCommand(currentP,loss);
        add = new AddHoldingCommand(currentP,gain);
        addToHistory = new AddToHistoryCommand(currentP,loss,gain,date);

        caretaker = UndoRedoCaretaker.getCaretakerInstance();
    }

    /**
     * Executes this Macro Command by calling the
     * Command leaves. Then push to the undo stack
     */
    @Override
    public void execute(){
        subtract.execute();
        add.execute();
        addToHistory.execute();

        caretaker.pushToUndo( this );
    }

    /**
     * Undo this Macro Command by unexecuting all leaf commands
     * Then push to the redo stack.
     */
    @Override
    public void unExecute(){
        subtract.unExecute();
        add.unExecute();
        addToHistory.unExecute();

        caretaker.pushToRedo( this );
    }

    /**
     * calls the canPerform method in subtract
     * @return - a boolean
     */
    public boolean canPerform(){
        return subtract.canPerform();
    }
}
