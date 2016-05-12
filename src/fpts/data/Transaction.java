package fpts.data;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * This class is responsible for creating and manipulating the data for
 * transactions. This class implements the functionality from Serializable.
 */
public class Transaction implements Serializable{
	
	/**
	 * This variable can prevent problems with loading old, incompatible versions of Transaction.
	 * If we ever make large scale variable changes, increment this number by 1.
	 */
	private static final long serialVersionUID = 1L;
	
	private Holding gain;//receiver
	private Holding loss;//giver
	private double netValue;
	private String date; //Currently holding date as a string. We may want to consider making an object for it for ease of comparison.


	/**
	 * A constructor for a transaction.
	 * @param g The holding gained.
	 * @param l The holding lost.
	 * @param d The date of the transaction.
	 */
	public Transaction(Holding g,Holding l, String d){
		gain = g;
		loss = l;
		netValue = g.getTotalValue();
		date = d;
	}
	
	/**
	 * The getter for this Transactions gain.
	 * @return Returns the holding gained (bought) in this transaction.
	 */
	public Holding getGain(){
		return gain;
	}
	
	/**
	 * The getter for this Transactions loss.
	 * @return Returns the holding lost (sold) in this transaction.
	 */
	public Holding getLoss(){
		return loss;
	}

	/**
	 * The getter for this Transactions date.
	 * @return Returns the date the transaction was performed.
	 */
	public String getDate(){
		return date;
	}
	
	/**
	 * The toString for this object.
	 * @return Returns this Transaction in the form of
	 * 
	 * Transaction:
	 * Gain: $gain
	 * Loss: $loss
	 * Date: $date
	 */
	@Override
	public String toString(){
		return "Transaction:\nGain: " + gain + "\nLoss: " + loss + "\nDate: " + date;
	}

	/**
	 * A getter for an observable property, in this case the toString();
	 * @return Returns a string property watching this transaciton.
	 */
	public StringProperty transactionProperty(){
		StringProperty transaction = new SimpleStringProperty(this.toString());
		return transaction;
	}
}
