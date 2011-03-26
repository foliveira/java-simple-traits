package pt.ist.meic.pava.demo;

import pt.ist.meic.pava.WithTraits;

@WithTraits({TEquality.class})
public class Person {
	public boolean isEql(Object obj) {
		return asEquatable().isEquatable();
	}
	
	public IEquality asEquality() {
		return (IEquality)this;
	}
	
	public IEquatable asEquatable() {
		return (IEquatable)this;
	}
}
