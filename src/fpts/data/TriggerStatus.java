package fpts.data;

import java.io.Serializable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Class to have an observable status message for watched equities
 *
 * @author Philip Bedward
 */
public class TriggerStatus implements Serializable {
    private double lowTrigger;
    private double highTrigger;
    private static final double NOTRIGGER = -1000.0;
    private static final String STATUS_GOOD = "Not Triggered";
    private static final String STATUS_HIGH = "Above High T";
    private static final String STATUS_LOW = "Below Low T";
    private static final String STATUS_GOOD_FROM_HIGH = "Was High";
    private static final String STATUS_GOOD_FROM_LOW = "Was Low";
    private EquityInfo info;
    private String statusMessage;

    /**
     * A constructor for a trigger status.
     * @param ei The equity metadata we're watching.
     * @param lowTrigger The low value alert level.
     * @param highTrigger The high value alert level.
     */
    public TriggerStatus(EquityInfo ei, double lowTrigger, double highTrigger){
        this.lowTrigger = lowTrigger;
        this.highTrigger = highTrigger;
        info = ei;
        //addListeners();
        statusMessage = STATUS_GOOD;
        changeStatusMessage();

    }

    /**
     * A method to automatically update the status message to a different status (or the same) depending on the value of info and the triggers.
     */
    public void changeStatusMessage(){
    	if(lowTrigger != NOTRIGGER && info.getValue() < lowTrigger){
    		setStatusMessage(STATUS_LOW);
    	}else if(highTrigger != NOTRIGGER && info.getValue() > highTrigger){
    		setStatusMessage(STATUS_HIGH);
    	}else if(statusMessage.equals(STATUS_HIGH)){
    		setStatusMessage(STATUS_GOOD_FROM_HIGH);
    	}else if(statusMessage.equals(STATUS_LOW)){
    		setStatusMessage(STATUS_GOOD_FROM_LOW);
    	}else{
    		setStatusMessage(STATUS_GOOD);
    	}
    }

    /**
     * A getter for the observable value property.
     * @return Returns a DoubleProperty for this trigger statuses info's value.
     */
    public DoubleProperty valueProperty(){
        return info.valueProperty();
    }

    /**
     * A method to manually set the status message.
     * @param message The new status message.
     */
    public void setStatusMessage(String message){
        statusMessage = message;
    }

    /**
     * A method to get the status of this TriggerStatus.
     * @return Returns STATUS_GOOD if not triggered, STATUS_HIGH || STATUS_LOW respectivly, and STATUS_GOOD_FROM_HIGH || STATUS_GOOD_FROM_LOW if recently triggered but good now.
     */
    public StringProperty getStatusMessage(){
        return new SimpleStringProperty(statusMessage);
    }

}
