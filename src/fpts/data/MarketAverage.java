package fpts.data;

/**
 * This is a class that extends equity and represents a MarketAverage stock.
 */
public class MarketAverage extends Equity{

    private String marketName;
    private double numShares;
    private double shareValue;


	/**
	 * This is a constructor for market
	 * @param name
	 * @param sharval
	 * @param dateacq
	 * @param numshar
     * @param fromca
     */
    public MarketAverage(String name, double sharval, String dateacq, double numshar, boolean fromca) {
        marketName = name;
        dateAcquired = dateacq;
        numShares = numshar;
        shareValue = sharval;
        fromCashAccount = fromca;
    }

	/**
	 * This is a boolean function that adds shares (holding type)
	 * @param h The Equity whose shares should be added to this one.
	 * @return
     */
    @Override
	public boolean add(Holding h) {
		if(h instanceof MarketAverage && h.equals(this)){
			numShares  += ((MarketAverage) h).numShares;
			return true;
		}
		return false;
	}

	/**
	 * This is a boolean function that subtracts shares (holding type)
	 * @param h The Equity with the shares to remove.
	 * @return false if cannot subtract, otherwise true ~ (sell)
     */
    @Override
	public boolean subtract(Holding h){
		if(h instanceof MarketAverage && h.equals(this) && ((MarketAverage) h).numShares < numShares){
			sell(((MarketAverage) h).numShares);
			return true;
		}
		return false;
	}

	/**
	 * A getter for the name
	 * @return Returns the name of the market
     */
    @Override
	public String getName() {
		return marketName;
	}

	/**
	 * This method converts equity to a String.
	 * @return Returns this Equity in the form [Equity: (Name=$name) (AqSharePrice=$price) (Date=$date) (NumShares=$numShares) (FromCA=$fromcashaccount)]
	 */
	@Override
	public String toString(){
		return "[Market: (Name=" + marketName + ") (AqSharePrice=" + shareValue + ") (Date=" + dateAcquired + ") (NumShares=" + numShares + ") (FromCA=" + fromCashAccount + ")]";
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
	 * A method to determine equality with an object.
	 * @param obj The object to check equality with.
	 * @return Returns true if obj is a market average and their market name matches.
	 */
    @Override
    public boolean equals(Object obj){
		if(obj instanceof MarketAverage && ((MarketAverage) obj).getName().equals(marketName)){
			return true;
		}
		return false;
	}

	/**
	 * A getter for the value based on number of shares
	 * @return value of the share
     */
	public double getTotalValue() { return shareValue * numShares; }

	/**
	 * A getter for the equity info
	 * @return
     */
	public EquityInfo getEquityInfo(){
        return null;
    }

	/**
	 * A getter for the date
	 * @return Returns the date acquired
     */
	public String getDate(){
		return dateAcquired;
	}

	/**
	 * A getter for the number of shares
	 * @return Returns the number of shares
     */
	public double getNumShares(){
		return numShares;
	}

	/**
	 * This is a boolean function that checks if the ticker symbol is a match for the given string
	 * @param tickSym The ticker symbol (or fragment of) this equity to see if matches. If you don't have one, make it an empty string or null.
	 * @param name The name (or fragment of) this equity info to see if matches. If you don't have one, make it an empty string or null.
	 * @param markDex The MarketAverage index (or fragment of) this equity info to see if matches. If you don't have one, make it an empty or null.
     * @return False if not a match
     */
	public boolean isMatch(String tickSym, String name, String markDex){
		return false;
	}



}
