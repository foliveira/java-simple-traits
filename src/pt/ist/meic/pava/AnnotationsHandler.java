package pt.ist.meic.pava;

import java.io.IOException;
import java.util.Hashtable;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import pt.ist.meic.pava.exception.TraitDefinitionException;

public class AnnotationsHandler 
{
	private static CtClass JAVA_LANG_OBJECT;
	
	private final ClassPool _classPool;
	private final CtClass _rootClass;
	
	static 
	{
		try 
		{
			JAVA_LANG_OBJECT = ClassPool.getDefault().get("java.lang.Object");
		} 
		catch (NotFoundException e) { }
	}
	
	public AnnotationsHandler(ClassPool cpool, CtClass root) 
	{
		_classPool = cpool;
		_rootClass = root;
	}

	public void handle(Object annotation) 
		throws NotFoundException, IOException, CannotCompileException, ClassNotFoundException 
	{
		if(annotation instanceof WithAliasedTraits)
			handleAnnotation((WithAliasedTraits)annotation);
		else if(annotation instanceof WithTraits)
			handleAnnotation((WithTraits)annotation);
		else 
			return;
	}
	
	/**
	 * Handles a class with a @WithAliasedTraits annotation.
	 * 
	 * @param annotation The annotation instance.
	 * 
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void handleAnnotation(WithAliasedTraits annotation) throws NotFoundException, CannotCompileException 
	{
		System.out.println("We got a @WithAliasedTraits");
	}	
	
	/**
	 * Handles a class with a @WithTraits annotation.
	 * 
	 * @param annotation The annotation instance.
	 * 
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void handleAnnotation(WithTraits annotation) 
		throws NotFoundException, CannotCompileException, IOException, ClassNotFoundException 
	{
		System.out.println("We've got a @WithTraits");
		
		for(Class<?> traitClass : annotation.value()) 
		{			
			if(traitClass.getAnnotation(Trait.class) != null)
			{
				String className = traitClass.getName();
				System.out.println("Found a Trait class: " + className);
				CtClass clazz = _classPool.get(className);
				
				if(!checkForTraitCorrectness(clazz))
					throw new TraitDefinitionException(String.format("Check for Trait %s malformation.", className));
				
				injectInterfaces(clazz);				
				injectMethods(clazz);
				
				processTraitHierarchy(_classPool.get(traitClass.getName()));
			}
		}
	}
	
	/**
	 * Processes and joins a trait hierarchy of classes/traits to the root class.
	 * 
	 * @param classDescriptor The trait class descriptor
	 * 
	 * @throws ClassNotFoundException
	 * @throws NotFoundException
	 * @throws IOException
	 * @throws CannotCompileException
	 */
	private void processTraitHierarchy(CtClass classDescriptor) 
		throws ClassNotFoundException, NotFoundException, IOException, CannotCompileException 
	{
		for(Object annotation : classDescriptor.getAnnotations())
			handle(annotation);
		
		CtClass superClass = _classPool.get(classDescriptor.getSuperclass().getName());
		
		if(superClass.equals(JAVA_LANG_OBJECT))
			return;
		
		injectInterfaces(superClass);
		injectMethods(superClass);
		
		processTraitHierarchy(superClass);
	}
	
	/**
	 * Inject every declaring method from the trait into the root class
	 * 	 
	 * @param traitDescriptor The trait class descriptor
	 */
	private void injectMethods(CtClass traitDescriptor) 
		throws CannotCompileException, NotFoundException 
	{
		for(CtMethod method : traitDescriptor.getDeclaredMethods())
		{
			int modifiers = method.getModifiers();
			
			if(Modifier.isStatic(modifiers))
				throw new TraitDefinitionException("Traits can't have static methods.");
			
			if(!Modifier.isAbstract(modifiers) && !method.isEmpty()) 
			{
				System.out.println("Injecting method: " + method.getLongName());
				_rootClass.addMethod(new CtMethod(method, _rootClass, null));
			}
			else 
			{
				System.out.println("Checking method " + method.getLongName() + " definition");
				CtMethod defMethod = _rootClass.getDeclaredMethod(method.getName());
				if(defMethod == null || Modifier.isAbstract(defMethod.getModifiers()))
					throw new TraitDefinitionException(String.format("Method %s is not defined", defMethod.getLongName()));
			}
		}
	}

	/**
	 * Inject every declaring interface from the trait in the root class
	 * 	 
	 * @param traitDescriptor The trait class descriptor
	 */
	private void injectInterfaces(CtClass traitDescriptor) 
		throws NotFoundException 
	{
		for(CtClass itface : traitDescriptor.getInterfaces()) 
		{
			System.out.println("Injecting interface: " + itface.getName());
			_rootClass.addInterface(itface);
		}
		
	}

	/**
	 * Checks if a trait is implemented according to the rules.
	 * 
	 * @param traitDescriptor The trait class descriptor
	 * @return True if the trait is correct
	 * @throws NotFoundException
	 */
	private boolean checkForTraitCorrectness(CtClass traitDescriptor) 
		throws NotFoundException 
	{
		int modifiers = traitDescriptor.getModifiers();
		CtClass superClass = traitDescriptor.getSuperclass();
		
		/*
		 * Check if the Trait declaring class is abstract or an interface.
		 */
		if(!Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers))
			return false;
		
		/*
		 * Check if the superclass is java.lang.Object or a Trait class
		 */
		if(!superClass.hasAnnotation(Trait.class) && !superClass.equals(JAVA_LANG_OBJECT))
			return false;
		
		/*
		 * Check if there's any fields and abort execution if there's any.
		 */
		if(traitDescriptor.getDeclaredFields().length > 0)
			return false;
		
		/*
		 * Check if there's more than one ctor and if the only one there is, is empty (default ctor).
		 */
		CtConstructor[] ctors = traitDescriptor.getConstructors();
		if(ctors.length > 1 || (ctors.length == 1 && !ctors[0].isEmpty()))
			return false;
		
		return true;
	}
	
	/**
	 * Convert a String array with an even number of values to a key-value pair hashtable
	 * @param array A String array with an even number of elements
	 * @return Null if the array has an odd number of elements or a key-value pair hashtable with odd values as key
	 * and even elements as values.
	 */
	@SuppressWarnings("unused")
	private Hashtable<String, String> convertToHashtable(String[] array)
	{
		if((array.length % 2) != 0)
			return null;
		
		Hashtable<String, String> h = new Hashtable<String, String>();
		
		for(int i = 0; i < array.length; i += 2)
			h.put(array[i], array[i + 1]);
		
		return h;
	}
}
