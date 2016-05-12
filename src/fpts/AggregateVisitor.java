package fpts;

import fpts.data.Holding;

/**
 * A Concrete Visitor that will perform an operation on
 * all Holdings in order to get an aggregate of their values
 *
 * @author Philip Bedward
 */
public class AggregateVisitor implements Visitor{

    /**
     * Gets the value of the holding that is currently being visited
     *
     * @param holding - The holding being visited in the collection
     * @return - a double containing the holding's value
     */
    @Override
    public double visit(Holding holding) {
        return holding.getTotalValue();
    }
}
