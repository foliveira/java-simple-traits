package pt.ist.meic.pava.demo;

public class RunPersons {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Person myPerson = new Person();
		System.out.println("Am I me?!: " + myPerson.isEql(myPerson));
		System.out.println("Am I NOT me?!: " + myPerson.asEquality().isNotEql(myPerson));
	}
}
