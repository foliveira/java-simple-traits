package pt.ist.meic.pava;

import javassist.ClassPool;
import javassist.Loader;

public class RunWithTraits {
	public static void main(String[] args) {
		try {
			String[] pargs = new String[] {};
			
			Loader loader = new Loader();
			loader.addTranslator(ClassPool.getDefault(), new TraitsTranslator());
			
			if(args.length > 0)
			{
				pargs = new String[args.length-1];
				System.arraycopy(args, 1, pargs, 0, pargs.length);
			}
			
			loader.run(args[0], pargs); 
			
		} catch (Throwable e) {
			e.printStackTrace();
			//TODO: Inform the user about the application usage
		}
	}
}
