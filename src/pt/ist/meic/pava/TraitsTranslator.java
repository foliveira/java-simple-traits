package pt.ist.meic.pava;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.Translator;

public class TraitsTranslator implements Translator 
{
	@Override
	public void onLoad(ClassPool cpool, String classname) 
		throws NotFoundException, CannotCompileException 
	{
		CtClass clazz = cpool.get(classname);
		AnnotationsHandler ah = new AnnotationsHandler(cpool, clazz);
		
		try 
		{			
			for(Object annotation : clazz.getAnnotations())
				ah.handle(annotation);
			
			clazz.writeFile();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	@Override
	public void start(ClassPool cpool) 
		throws NotFoundException, CannotCompileException 
	{
		/* Empty by default */
	}

}
