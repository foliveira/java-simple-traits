package pt.ist.meic.pava.examples;

import pt.ist.meic.pava.WithTraits;

@WithTraits({TTrait1.class, TTrait2.class})
public class SimpleWithTraits {
	public int i = 0;


	public int c(Object obj) {
		System.out.println("SimpleWithTraits.c()=" + this.i);
		return this.i++;
	}

	public Trait1 asTrait1() { return (Trait1) this; }
	public Trait2 asTrait2() { return (Trait2) this; }
}
