package pt.ist.meic.pava.examples;

import pt.ist.meic.pava.WithAliasedTraits;
import pt.ist.meic.pava.AliasedTrait;

@WithAliasedTraits({
	@AliasedTrait(trait = TTrait1.class),
	@AliasedTrait(trait = TTrait3.class, aliases = {"b", "d"})
})
public class SimpleWithAliasedTraits {
	private boolean p = true;

	public boolean a() {
		this.p = ! this.p;
		System.out.println("SimpleWithAliasedTraits.a()=" + this.p);
		return this.p;
	}

	public void c() {
			System.out.println("SimpleWithAliasedTraits.c() invoking renamed Trait3.b() method.");
			asTrait3().b();
	}

	public Trait1 asTrait1() { return (Trait1) this; }
	public Trait3 asTrait3() { return (Trait3) this; }

}
