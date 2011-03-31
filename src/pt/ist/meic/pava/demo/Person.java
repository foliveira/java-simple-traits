package pt.ist.meic.pava.demo;

import pt.ist.meic.pava.WithTraits;

@WithTraits({TEquality.class})
public class Person {
	private String name = "fabio";
	
	public boolean isEql(Object obj) {
		if(obj instanceof Person)
			return ((Person)obj).name.equals(this.name);
		
		return false;
	}
	
	public IEquality asEquality() {
		return (IEquality)this;
	}
	
	public IEquatable asEquatable() {
		return (IEquatable)this;
	}
}
