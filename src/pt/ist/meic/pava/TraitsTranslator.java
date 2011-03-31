package pt.ist.meic.pava;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;

public class TraitsTranslator implements Translator 
{
	private final CodeConverter _codeConverter = new CodeConverter();
	private final LinkedList<CtClass> _classes = new LinkedList<CtClass>();
	
	@Override
	public void onLoad(ClassPool cpool, String classname) 
		throws NotFoundException, CannotCompileException 
	{
		HashMap<String, CtMethod> requiredMethods = new HashMap<String, CtMethod>();
		
		try 
		{					
			CtClass clazz = cpool.get(classname);
			AnnotationsHandler ah = new AnnotationsHandler(cpool, clazz, requiredMethods, _codeConverter);
		
			for(Object annotation : clazz.getAnnotations())
				ah.handle(annotation);
			
			for(CtMethod mdesc : requiredMethods.values())
			{
				try 
				{
					clazz.getDeclaredMethod(mdesc.getName(), mdesc.getParameterTypes());
					requiredMethods.remove(mdesc);
				} 
				catch(NotFoundException e) { Logger.Log("Requirement method " + mdesc.getLongName() + " not defined."); }
			}
				
			_classes.add(clazz);
			
			instrumentKnownClasses();
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
	
	private void instrumentKnownClasses() 
		throws CannotCompileException, NotFoundException, IOException
	{		
		for(CtClass c : _classes)
		{			
			c.defrost();
			Logger.Log("Applying converter to: " + c.getName());
			c.instrument(_codeConverter);
		}
	}

}
