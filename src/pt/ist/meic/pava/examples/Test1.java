package pt.ist.meic.pava.examples;

public class Test1 {
	public static void main (String[] args) {
		SimpleWithTraits c = new SimpleWithTraits();

		c.asTrait2().a();
		c.asTrait1().b();
	}
}
