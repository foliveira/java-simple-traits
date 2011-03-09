package pt.ist.meic.pava;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RunWithTraits
{
	public static void main(String[] args) 
	{
		Class clazz;
		Method mainMethod = null;
		
		try {
			clazz = Class.forName(args[0]);
			mainMethod = clazz.getDeclaredMethod("main", new Class[] { String[].class });
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		//Inject all Traits code into relevant classes
		
		String[] newArgs = new String[args.length - 1];
		System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		try {
			mainMethod.invoke(null, newArgs);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}