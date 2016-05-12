package fpts.data;
/**
 * An Equity that represents a stock
 * @author Philip Bedward
 */
public class Stock extends Equity{
	/**
	 * A constructor for a Stock.
	 * @param ei The equity metatdata for this Stock.
	 * @param ap The aquisition price of this stock.
	 * @param da The date this stock was aquired.
	 * @param ns The number of shares of this stock bought.
	 * @param fca Whether or not this stock was bought with cash from a cash account.
	 */
    public Stock(EquityInfo ei, double ap, String da, double ns, boolean fca){
        super(ei,ap,da,ns,fca);
    }
}
