package fpts.data;
/**
 * A type of Equity that represents a Bond
 * @author - Philip Bedward
 */
public class Bond extends Equity{
	/**
	 * A constructor for a bond.
	 * @param ei The equity metatdata for this object.
	 * @param ap The aquisition price for this bond.
	 * @param da The date this bond is created.
	 * @param ns The number of shares in this bond.
	 * @param fca Whether this bond was aquired with cash from a cash account.
	 */
    public Bond(EquityInfo ei, double ap, String da, double ns, boolean fca){
        super(ei,ap,da,ns,fca);
    }
}
