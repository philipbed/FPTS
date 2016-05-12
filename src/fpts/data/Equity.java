package fpts.data;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

import fpts.Visitor;


/**
 * This class is responsible for holding all the information
 * that pertains to the equity holding type. It also initializes
 * and manipulates the equities within the user profile. This class
 * implements the functionality from Holding and Serializable
 */

public class Equity implements Holding, Serializable {

	private static final long serialVersionUID = 1L;
	protected EquityInfo info;
	private double shareValue;
	protected String dateAcquired;
	private double numShares;
	protected boolean fromCashAccount;

	/**
	 * Constructs a new empty equity.
	 */
	public Equity() {
		dateAcquired = "";
		shareValue = 0.0;
		numShares = 0.0;
		fromCashAccount = false;
	}
	
	/**
	 * Constructor for Equity.
	 * @param ei - the EquityInfo for this Equity
	 * @param sharval - The aquisition price per share.
	 * @param dateacq - The date this Equity was aquired.
	 * @param numshar - The number of shares owned.
	 * @param fromca - Whether or not this was purchased with cash from a cash account.
	 */
	public Equity(EquityInfo ei, double sharval, String dateacq, double numshar, boolean fromca){
		dateAcquired = dateacq;
		shareValue = sharval;
		numShares = numshar;
		fromCashAccount = fromca;
		info = ei;
	}
	
	/**
	 * Adds an Equities shares to this's shares if they have the same name.
	 * @param h The Equity who's shares should be added to this one.
	 * @return Returns true if the addition was successful, false if not.
	 */
	@Override
	public boolean add(Holding h) {
		if(h instanceof Equity && h.equals(this)){
			numShares  += ((Equity) h).numShares;
			return true;
		}
		return false;
	}
	
	/**
	 * A method for removing the shares of an equity from this equity..
	 * @param h The Equity with the shares to remove.
	 * @return Returns true if the subction was able to occur, false if not. Returns false if the subtraction would result in negative shares.
	 */
	@Override
	public boolean subtract(Holding h){
		if(h instanceof Equity && h.equals(this) && ((Equity) h).numShares <= numShares){
			sell(((Equity) h).numShares);
			return true;
		}
		return false;
	}

	/**
	 * Getter for Equity name
	 * @return Returns this Equity's name.
	 */
	@Override
	public String getName() {
		return info.getName();
	}
	
	/**
	 * This method converts equity to a String.
	 * @return Returns this Equity in the form [Equity: (Name=$name) (AqSharePrice=$price) (Date=$date) (NumShares=$numShares) (FromCA=$fromcashaccount)]
	 */
	@Override
	public String toString(){
		return "[Equity: (Name=" + info.getName() + ") (AqSharePrice=" + shareValue + ") (Date=" + dateAcquired + ") (NumShares=" + numShares + ") (FromCA=" + fromCashAccount + ")]";
	}

    /**
     * Returns the EquityInfo object for this Equity
     * @return EquityInfo
     */
    public EquityInfo getEquityInfo(){
        return this.info;
    }

    /**
     * Returns the dateAcquired for this Equity
     * @return String
     */
    public String getDate(){
        return this.dateAcquired;
    }

    /**
     * Returns the numShares for this Equity
     * @return double
     */
    public double getNumShares(){
        return this.numShares;
    }

    /**
     * Returns the fromCashAccount boolean for this Equity
     * @return boolean
     */
    public boolean getFromCashAccount(){
        return this.fromCashAccount;
    }

	/**
	 * The total cost of an equity is represented by the number shares bought
	 * 	or sold multiplied by the acquisition price per
	 * 	@param sharesDesired - The amount of desired shares to be bought or sold
	 * @return
     */
	public double getCost(double sharesDesired){
		return this.shareValue * sharesDesired ;
	}
	
	/**
	 * A method to add shares to this Equity.
	 * @param ns The number of shares to add.
	 */
	public void buy(double ns){
		numShares += ns;
	}
	
	/**
	 * A method to remove numShares from this Equity.
	 * @param ns The number of shares to remove.
	 */
	public void sell(double ns){
		numShares -= ns;
	}
	
	/**
	 * A method to see if this Equity is equal to an object. If name and type matches, return true.
	 * @param obj The object to test against
	 * @return Returns true if name and object type match.
	 */
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Equity && ((Equity) obj).info.equals(info)){
			return true;
		}
		return false;
	}
	
	/**
	 * A getter for the value of the account.
	 * @return Returns the value of this account.
	 */
	public double getTotalValue(){
		return numShares * shareValue;
	}
	
	/**
	 * A method to determine if this E is a possible match for the information provided. Simply passes it up to its EquityInfo.
	 * @param tickSym The ticker symbol (or fragment of) this equity to see if matches. If you don't have one, make it an empty string or null.
	 * @param name The name (or fragment of) this equity info to see if matches. If you don't have one, make it an empty string or null.
	 * @param markDex The market index (or fragment of) this equity info to see if matches. If you don't have one, make it an empty or null.
	 * @return Returns true if all non-empty and non-null paramaters are matches for the ticker symbol, name, and a market index for this equity info.
	 */
	public boolean isMatch(String tickSym, String name, String markDex){
		System.out.println(info);
		return info.isMatch(tickSym, name, markDex);
	}

	/**
	 * A getter for the share value
	 * @return Returns the share value
     */
	public double getShareValue() {
		return shareValue;
	}

	/**
	 * A setter for the share value
	 * @param sv: share value
     */
	public void setShareValue(double sv){
		shareValue =sv;
	}

	/**
	 * A getter for the value per number of shares
	 * @return Returns the value per number of shares
     */
	public double getValue() { return shareValue * numShares; }

	/**
	 * This function creates an equity name property
	 * @return Returns an equity name property
     */
	public StringProperty equityNameProperty(){
		return new SimpleStringProperty(this.getName());
	}

	/**
	 * This function creates an equity ticker
	 * @return Returns the equity ticker property
     */
	public StringProperty equityTickerProperty(){
		StringProperty equityTicker = new SimpleStringProperty(this.getEquityInfo().getTS());
		return equityTicker;
	}

	/**
	 * This function creates an equity market index
	 * @return Returns the given market index based on the equity
	 * info and market property
     */
	public StringProperty equityMarketIndex(){
		return this.getEquityInfo().marketProperty();
	}

	/**
	 * This function creates an equity value property
	 * @return Returns the equity value property
     */
	public DoubleProperty equityValueProperty(){
		DoubleProperty equityValue = new SimpleDoubleProperty(this.getValue());
		return equityValue;
	}

	/**
	 * Executes code in a visitor with this.
	 */
	public double accept(Visitor v){
		return v.visit(this);
	}
}
