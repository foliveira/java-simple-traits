package pt.ist.meic.pava.demoSoccer;

import pt.ist.meic.pava.WithTraits;
import pt.ist.meic.pava.Trait;

@Trait
@WithTraits({TCelebrations.class})
public abstract class TStriker extends TMoves {
	public void scoreGoal(){
		System.out.println("TSTRIKER -> SCORE GOAL");
		System.out.println("Goal scored");
	}
	
	public void repeatedMethod(){
		System.out.println("This is the repeated method on TStriker");
	}
}
