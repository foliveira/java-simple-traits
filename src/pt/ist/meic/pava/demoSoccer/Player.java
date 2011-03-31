package pt.ist.meic.pava.demoSoccer;

import pt.ist.meic.pava.WithTraits;

@WithTraits({TStriker.class})
public class Player {
	public void alive(){
		System.out.println("\nPlayer is alive");
	}
	
	public void repeatedMethod(){
		System.out.println("This is the repeated method on trait Player");
	}
}
