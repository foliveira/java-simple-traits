package pt.ist.meic.pava;

public class RunWithTraits
{
	public static void main(String[] args) 
	{
		Class clazz = Class.forName(args[0]);
		Method mainMethod = clazz.getDeclaredMethod("main", new Class[] { String[].class });
		
		//Inject all Traits code into relevant classes
		
		String newArgs = new String[args.length - 1];
		System.arrayCopy(args, 1, newArgs, 0, newArgs.length);
		mainMethod.invoke(null, newArgs);
	}
}