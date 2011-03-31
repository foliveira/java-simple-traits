package pt.ist.meic.pava.examples;

public class Test2 {

	public static void main (String[] args) {
		SimpleWithAliasedTraits c = new SimpleWithAliasedTraits();

		c.asTrait1().b();
		c.asTrait3().b();
		c.c();
	}
}
