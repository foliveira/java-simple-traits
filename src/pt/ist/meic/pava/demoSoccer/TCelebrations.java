package pt.ist.meic.pava.demoSoccer;

import pt.ist.meic.pava.Trait;

@Trait
public abstract class TCelebrations {
	public void dance(){
		System.out.println("TCELEBRATIONS -> DANCE");
		System.out.println("Player is dancing");
	}
	
	private void sing(){
		System.out.println("TCELEBRATIONS -> SING");
		System.out.println("Player is singing");
	}
	
	public void singAndDance(){
		System.out.println("TCELEBRATIONS -> SING AND DANCE");
		dance();
		sing();
	}
}
