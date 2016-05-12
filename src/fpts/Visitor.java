package fpts;

import fpts.data.Holding;

/**
 * Interface for all Concrete Visitors
 *
 * @author Philip Bedward
 */
public interface Visitor {

    double visit(Holding holding);
}
