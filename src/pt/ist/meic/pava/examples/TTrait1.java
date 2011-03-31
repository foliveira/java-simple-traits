package pt.ist.meic.pava.examples;

import pt.ist.meic.pava.Trait;

@Trait
public abstract class TTrait1 implements Trait1 {

	public abstract boolean a();

	public void b() {
		System.out.println("Trait1.b()");
		if (a())
			System.out.println("\ta() was true");
	}
}
