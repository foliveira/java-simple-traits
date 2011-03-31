package pt.ist.meic.pava.examples;

import pt.ist.meic.pava.Trait;

@Trait
public abstract class TTrait3 implements Trait3 {

	public abstract void c();

	public void b() {
		System.out.println("Trait3.b()");
	}
}
