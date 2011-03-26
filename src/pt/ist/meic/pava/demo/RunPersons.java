package pt.ist.meic.pava.demo;


public class RunPersons {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Person myPerson = new Person();
		
		System.out.println(myPerson.asEquality().isNotEql(null));
		System.out.println(myPerson.isEql(null));
		
	}
}
