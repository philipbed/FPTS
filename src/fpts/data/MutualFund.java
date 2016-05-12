package fpts.data;
/**
 * A type of Equity that is able to hold multiple stocks and bonds
 *
 * @author Philip Bedward
 */
public class MutualFund extends Equity{
	/**
	 * A constructor for a mutual fund.
	 * @param ei The equity metadata for this equity.
	 * @param ap The aquisition price of this mutual fund.
	 * @param da The date this mutual fund was aquired.
	 * @param ns The number of shares in this mutual fund.
	 * @param fca Whether or not this mutual fund was purchased with cash from a cash account.
	 */
    public MutualFund(EquityInfo ei, double ap, String da, double ns, boolean fca){
        super(ei,ap,da,ns,fca);
    }
}
