package pt.ist.meic.pava.examples;

import pt.ist.meic.pava.Trait;

@Trait
public abstract class TTrait2 implements Trait2 {

	public abstract int c(Object obj);

	public boolean a() {
		System.out.println("Trait2.a()");
		if (c(this) > 0) {
			return true;
		} else {
			System.out.println("\tc(this) wasn't positive");
			return false;
		}
	}
}
