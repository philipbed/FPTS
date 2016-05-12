package fpts.data;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;
import java.text.DecimalFormat;

import fpts.Visitor;

/**
 * This class holds the responsibility of initializing and manipulating
 * a cash account which is used within the user's portfolio. This cash account
 * class implements the functionality from Holding, Serializable,
 * and Comparable.
 */
public class CashAccount implements Holding, Serializable, Comparable<CashAccount>{
	
	private double amount;
	private String name;
	private String date;
	
	/**
	 * Constructor for a CashAccount.
	 * @param amt The initial amount in the cash account.
	 * @param nm The name of this account.
	 * @param dt The date this account was created.
	 */
	public CashAccount(double amt, String nm, String dt){
		amount = amt;
		name = nm;
		date = dt;

		//nameProperty = new SimpleStringProperty(name);
		//dateProperty = new SimpleStringProperty(date);
		//balanceProperty = new SimpleDoubleProperty(amount);
	}
	
	/**
	 * A getter for account name.
	 * @return Returns the name of this account.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * A getter for the value of the account.
	 * @return Returns the value of this account.
	 */
	public double getTotalValue(){
		return amount;
	}

	/**
	 * A getter for the date of the account.
	 * @return Returns the date of the account.
     */
	public String getDate() {return date;}

	/**
	 * A method for adding the value of two cashaccounts of the same name together.
	 * @param h The cashaccount value to add to this one.
	 * @return Returns true if succesfully added, false if not.
	 */
	@Override
	public boolean add(Holding h) {
		if(h instanceof CashAccount && ((CashAccount) h).name.equals(name)){
			amount  += ((CashAccount) h).amount;
			return true;
		}
		return false;
	}
	
	/**
	 * A method for removing the value of a cash account from this cash account.
	 * @param h The CashAccount with the amount to remove.
	 * @return Returns true if the subtraction was able to occur, false if not. Returns false if the subtraction would result in a negative value in this account.
	 */
	@Override
	public boolean subtract(Holding h){
		if(h instanceof CashAccount && h.equals(this) && ((CashAccount) h).amount < amount){
			withdraw(((CashAccount) h).amount);
			return true;
		}
		return false;
	}

	/**
	 * Method to add a deposit to a cash account
	 * @param amountDeposited the value to deposit into this account
     */
	public void deposit(double amountDeposited){
		this.amount += amountDeposited;
	}

	/**
	 * Method to withdraw money from a cash account
	 * @param amountWithdrawn - amount to be withdrawn from the cash account
	 * @return - a boolean whether the withdrawal could be performed or not
     */
	public boolean withdraw(double amountWithdrawn){
		if(this.amount >= amountWithdrawn){
			this.amount -= amountWithdrawn;
			return true;
		}

		return false;
	}

	/**
	 * A method to get a String representation of this object.
	 * @return Returns this object in the form of "[CashAccount: (Name=$name) (Amount=$amount) (Date=$date)]"
	 */
	public String toString(){
		return "[CashAccount: (Name=" + name + ") (Amount=" + amount + ") (Date=" + date + ")]";
	}
	
	/**
	 * A method to see if this CashAccount is equal to an object. If name and type matches, return true.
	 * @param obj The object to test against
	 * @return Returns true if name and object type match.
	 */
	@Override
	public boolean equals(Object obj){
		if(obj instanceof CashAccount && ((CashAccount) obj).name.equals(name)){
			return true;
		}
		return false;
	}

	/**
	 * A method for comparing two CashAccounts.
	 * @param ca The cash account to compare to this account.
	 * @return Returns greater than 0 if ca has more cash than this account, 0 if even, and less 0 if less cash.
	 */
	@Override
	public int compareTo(CashAccount ca) {
		return (int) (ca.amount - amount);
	}

	/**
	 * This function creates a new name property
	 * @return Returns the said name property
	 */
	public StringProperty nameProperty(){
		StringProperty nameProperty = new SimpleStringProperty(name);
		return nameProperty;
	}

	/**
	 * This function creates a new date property
	 * @return Returns the said date property
     */
	public StringProperty dateProperty(){
		StringProperty dateProperty = new SimpleStringProperty(date);
		return dateProperty;
	}

	/**
	 * This function creates a new balance property
	 * @return Returns the said balance property
     */
	public DoubleProperty balanceProperty(){

		String amtString = String.format("%.2f",amount);
		Double amt = Double.parseDouble(amtString);
		DoubleProperty balanceProperty = new SimpleDoubleProperty(amt);
		return balanceProperty;
	}

	/**
	 * A method to execute code with a visitor.
	 */
	@Override
	public double accept(Visitor v) {
		return v.visit(this);
	}


}
