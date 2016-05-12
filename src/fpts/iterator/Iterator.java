package fpts.iterator;

/**
 * This is a generic iterator interface. So far, it is only implemented in an internal class of EquityInfoList.
 * @author Ryan
 * @param <E> Generic paramater. In the only implementation, it is EquityInfo.
 */
public interface Iterator<E> {
	public abstract boolean hasNext();
	public abstract E next();
}
