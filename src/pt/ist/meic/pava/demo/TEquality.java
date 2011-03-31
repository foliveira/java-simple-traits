package pt.ist.meic.pava.demo;

import pt.ist.meic.pava.Trait;
import pt.ist.meic.pava.WithTraits;

@Trait
@WithTraits({TEquatable.class})
public abstract class TEquality implements IEquality {
	public abstract boolean isEql(Object obj);
	public boolean isNotEql(Object obj) { return !isEql(obj); }
}
