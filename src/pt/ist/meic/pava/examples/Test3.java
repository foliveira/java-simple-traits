package pt.ist.meic.pava.examples;

public class Test3 {

	public static void main (String[] args) {
		MixedWithAliasedTraits c = new MixedWithAliasedTraits();

		c.asTrait1().b();
		c.asTrait3().b();
		c.asTrait3().c();
	}
}
