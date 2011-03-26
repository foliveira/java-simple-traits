package pt.ist.meic.pava.demo;

import pt.ist.meic.pava.Trait;

@Trait
public abstract class TEquality extends TEquatable implements IEquality {
	public abstract boolean isEql(Object obj);
	public boolean isNotEql(Object obj) { return !isEql(obj); }
}
