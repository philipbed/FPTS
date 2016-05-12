package fpts.data;

import com.sun.org.apache.xml.internal.utils.StringBufferPool;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * This is somewhat of a modified Equity class that will represent
 * an entry in the FPTS portfolio watchlist, which itself will be
 * stored inside the portfolio class
 *
 * @author Daniil Vasin
 */
public class WatchedEquity extends Equity{

    private double[] triggers;
    private TriggerStatus triggerStatus;
    //^ triggers has the format[low trigger,0 for no trigger 1 for yes trigger,high trigger]


    /**
     *
     * @param equityInfo - the Equity info being watched
     * @param trig - The Equity's watchlist triggers: triggers has the format[low trigger,0 for no trigger 1 for yes trigger,high trigger]

     */
    public WatchedEquity(EquityInfo equityInfo, double[] trig){
    	super(equityInfo, 0, "", 0, false);
        triggers = trig;
        triggerStatus = new TriggerStatus(info,trig[0],trig[2]);
        addListeners();
    }


    /********************GETTERS********************/
    /**
     * Gets Equity info
     * @return watched equity info
     */
    public EquityInfo getInfo(){ return info; }

    /**
     * Gets low trigger bound
     * @return Low trigger
     */
    public double getLowTrigger(){ return triggers[0];}

    /**
     * Gets high trigger bound
     * @return high trigger
     */
    public double getHighTrigger(){ return triggers[2];}

    /**
     * Gets whether triggers are activated
     * @return true if active, false if disabled
     */
    public boolean getTrigger() {
        if (triggers[1]==1.0)
            return true;
        return false;
    }
    
    /**
	 * This function creates an equity value property
	 * @return Returns the equity value property
     */
	public DoubleProperty equityValueProperty(){
		DoubleProperty equityValue = new SimpleDoubleProperty(this.info.getValue());
		return equityValue;
	}

    /**
     * A getter for this's trigger status status message.
     * @return
     */
    public StringProperty getStatusMessage(){
    	return triggerStatus.getStatusMessage();
    }

    /**
     * Sets the low trigger
     * @param trigger - The low trigger for the watched equity
     */
    public void setLowTrigger(double trigger) {
        triggers[0]=trigger;
    }

    /**
     * Sets the high trigger
     * @param trigger - The high trigger for the watched equity
     */
    public void setHighTrigger(double trigger) {
        triggers[2] = trigger;
    }

    /**
     * Toggles the trigger
     * @param toggle - whether triggers are on or off
     */
    public void setTrigger(double toggle){
        triggers[1] = toggle;
    }

    /**
     * Updates the reference to the equity metadata.
     */
    public void updateEquityInfo(){
        String tkr = info.getTS();
        info = EquityInfo.getMatchingInfo("",tkr,"").get(0);
    }

    /**
     * A method to see if an object is equal to this watched equity.
     * @param obj The object to check equality with.
     * @return Returns true if obj is a watched equity and their equity info is equivalent.
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof WatchedEquity && ((WatchedEquity) obj).info.equals(info)){
            return true;
        }
        return false;
    }

    /**
     * A method to add listeners to a property of the trigger status.
     */
    private void addListeners(){
        triggerStatus.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateStatusMesage();
            }
        });
    }

    /**
     * Tells the trigger status to update its status message.
     */
    public void updateStatusMesage(){
        triggerStatus.changeStatusMessage(); 
    }
}
