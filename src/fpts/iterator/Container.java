package fpts.iterator;

/**
 * The generic collection interface. Only implemented by EquityInfoList.
 * @author Ryan
 * @param <E> Generic variable to be held by the implementing class. The iterator over that container will be of this type.
 */
public interface Container<E> {
	public Iterator<E> getIterator();
}
