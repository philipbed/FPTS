package fpts.data;

import fpts.Visitor;

public interface Holding {
	public abstract boolean add(Holding h);
	public abstract boolean subtract(Holding h);
	public abstract String getName();
	public abstract String getDate();
	public abstract double getTotalValue();
	public abstract double accept(Visitor v);
}
