package fpts.command;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Phil on 4/10/2016
 */
public class UndoRedoCaretaker {

    private Stack<Command> undoStack;

    private Stack<Command> redoStack;

    private static UndoRedoCaretaker caretakerInstance;

    private BooleanProperty undoBooleanProperty;

    private BooleanProperty redoBooleanProperty;

    /**
     * Method that enforces the Singleton pattern
     * Makes sure only one instance of the caretaker
     * is used throughout the systerm.
     *
     * @return - the instance of the caretaker
     */
    public static UndoRedoCaretaker getCaretakerInstance(){

        if(caretakerInstance == null){
            caretakerInstance = new UndoRedoCaretaker();
        }


        return caretakerInstance;
    }

    /**
     * Constructor for the UndoRedoCaretaker
     * initializes both stacks
     */
    private UndoRedoCaretaker(){
        undoStack = new Stack<>();

        redoStack = new Stack<>();
    }

    /**
     * Pushes the command to the undoStack
     * @param command - the command to be pushed
     */
    public void pushToUndo(Command command){
        undoStack.push(command);
    }

    /**
     * Pushes the command to the redoStack
     * @param command - the command to be pushed
     */
    public void pushToRedo(Command command){
        redoStack.push(command);
    }

    /**
     * Pops the top command from the stack
     * @return - a command object at the top of the undo stack
     */
    public Command popFromUndo(){
        if( !undoStack.empty() ) {
            return undoStack.pop();
        }

        return null;
    }

    /**
     * Pops the top command from the stack
     * @return - a command object at the top of the undo stack
     */
    public Command popFromRedo(){
        if( !redoStack.empty() ){
            return redoStack.pop();
        }

        return null;
    }

    /**
     * A getter to tell whether the undo stack is empty.
     * @return Returns true if the undo stack is empty.
     */
    public BooleanProperty isUndoStackEmpty(){
        undoBooleanProperty = new SimpleBooleanProperty();

        setUndoEmptyProperty();


        return undoBooleanProperty;
    }

    /**
     * A getter to tell whether the redo stack is empty.
     * @return Returns true if the redo stack is empty.
     */
    public BooleanProperty isRedoStackEmpty(){
        redoBooleanProperty = new SimpleBooleanProperty();

        setRedoEmptyProperty();

        return redoBooleanProperty;
    }

    /**
     * A method to update the undo boolean property to whether the undo stack is empty.
     */
    public void setUndoEmptyProperty(){
        undoBooleanProperty.set(undoStack.isEmpty());
    }

    /**
     * A method to update the redo boolean property to whether the redo stack is empty.
     */
    public void setRedoEmptyProperty() { redoBooleanProperty.set(redoStack.isEmpty()); }
    
    /**
     * Equals method for Caretaker class in case of comparison.
     * @param obj - the UndoRedoCaretaker
     * @return - the boolean result if they are equal or not
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof UndoRedoCaretaker){
            UndoRedoCaretaker caretaker = (UndoRedoCaretaker) obj;

            return (this.undoStack.equals(caretaker.undoStack) && this.redoStack.equals(caretaker.redoStack));
        }
        return false;
    }
}
