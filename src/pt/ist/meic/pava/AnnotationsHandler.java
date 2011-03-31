package pt.ist.meic.pava;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.DuplicateMemberException;

import pt.ist.meic.pava.exception.TraitDefinitionException;

public class AnnotationsHandler 
{
	private static CtClass JAVA_LANG_OBJECT;
	private static final Hashtable<String, String> EMPTY_ALIASES;
	
	private final ClassPool _classPool;
	private final CtClass _rootClass;
	private final CodeConverter _codeConverter;
	private final HashMap<String, CtMethod> _requiredMethods;
	
	static 
	{
		EMPTY_ALIASES = new Hashtable<String, String>();
		
		try { JAVA_LANG_OBJECT = ClassPool.getDefault().get("java.lang.Object"); } 
		catch (NotFoundException e) { }
	}
	
	public AnnotationsHandler(ClassPool cpool, CtClass rootClass, HashMap<String, CtMethod> requiredMethods, CodeConverter codeConverter) 
	{
		_classPool = cpool;
		_rootClass = rootClass;
		_requiredMethods = requiredMethods;
		_codeConverter = codeConverter;
	}

	public void handle(Object annotation) 
		throws NotFoundException, IOException, CannotCompileException, ClassNotFoundException 
	{
		System.err.println();
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
	private void handleAnnotation(WithAliasedTraits annotation) 
		throws NotFoundException, CannotCompileException, ClassNotFoundException, IOException 
	{
		Logger.Log("We got a @WithAliasedTraits");
		
		for(AliasedTrait trait : annotation.value())
		{
			Hashtable<String, String> aliases = convertToHashtable(trait.aliases());
			Class<?> traitClass = trait.trait();
			
			if(traitClass.getAnnotation(Trait.class) != null)
			{
				String className = traitClass.getName();
				Logger.Log("Found a Trait class: " + className);
				CtClass clazz = _classPool.get(className);

				if(!checkForTraitCorrectness(clazz))
					throw new TraitDefinitionException(String.format("Check for Trait %s malformation.", className));

				injectMethods(clazz, aliases);
				injectInterfaces(clazz, aliases);
				
				processTraitHierarchy(clazz);
			}
		}
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
		Logger.Log("We've got a @WithTraits");
		
		for(Class<?> traitClass : annotation.value()) 
		{			
			if(traitClass.getAnnotation(Trait.class) != null)
			{
				String className = traitClass.getName();
				Logger.Log("Found a Trait class: " + className);
				CtClass clazz = _classPool.get(className);
				
				if(!checkForTraitCorrectness(clazz))
					throw new TraitDefinitionException(String.format("Check for Trait %s malformation.", className));

				injectMethods(clazz);
				injectInterfaces(clazz);
				
				processTraitHierarchy(clazz);
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
		if(classDescriptor.getSuperclass() == null && classDescriptor.getInterfaces().length == 0)
			return;
		
		for(Object annotation : classDescriptor.getAnnotations())
			handle(annotation);
	}
	
	/**
	 * Helper method to inject methods without aliases rewrite.
	 * 
	 * @param traitDescriptor The trait class descriptor
	 * 
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	private void injectMethods(CtClass traitDescriptor) 
		throws CannotCompileException, NotFoundException 
	{
		injectMethods(traitDescriptor, EMPTY_ALIASES);
	}
	
	/**
	 * Helper method to inject interfaces without aliases rewrite.
	 * 
	 * @param traitDescriptor The trait class descriptor
	 * 
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	private void injectInterfaces(CtClass traitDescriptor) 
		throws NotFoundException, CannotCompileException 
	{
		injectInterfaces(traitDescriptor, EMPTY_ALIASES);		
	}
	
	/**
	 * Inject every declaring method from the trait into the root class
	 * 	 
	 * @param traitDescriptor The trait class descriptor
	 * @throws NotFoundException 
	 */
	private void injectMethods(CtClass traitDescriptor, Hashtable<String, String> aliases) 
		throws CannotCompileException, NotFoundException
	{		
		for(CtMethod method : traitDescriptor.getDeclaredMethods())
		{
			int modifiers = method.getModifiers();
			String name = method.getLongName();
			
			if(Modifier.isStatic(modifiers))
				throw new TraitDefinitionException("Traits can't have static methods.");
			
			if(!Modifier.isAbstract(modifiers) && !method.isEmpty()) 
			{
				try 
				{
					Logger.Log("Injecting method: " + name);
					CtMethod cmethod = new CtMethod(method, _rootClass, null);
					
					if(aliases.containsKey(cmethod.getName()))
					{
						Logger.Log("Renaming method: " + cmethod.getLongName());
						{
							String oldName = cmethod.getName();
							cmethod.setName(aliases.get(oldName));
							_codeConverter.redirectMethodCall(oldName, cmethod);
						}
						Logger.Log("To: " + cmethod.getLongName());
					}
					_rootClass.addMethod(cmethod);
					
					String key = method.getName() + method.getSignature();
					
					if(_requiredMethods.containsKey(key))
						_requiredMethods.remove(key);
				}
				catch (DuplicateMemberException e) { Logger.Log("Duplicate method will not be injected."); }
			}
			else 
			{
				Logger.Log("Adding requirement method " + method.getLongName());
				String methodKey = String.format("%s.%s", method.getName(), method.getSignature());
				_requiredMethods.put(methodKey, method);
			}
		}
	}

	/**
	 * Inject every declaring interface from the trait in the root class
	 * 	 
	 * @param traitDescriptor The trait class descriptor
	 * @throws CannotCompileException 
	 */
	private void injectInterfaces(CtClass traitDescriptor, Hashtable<String, String> aliases) 
		throws NotFoundException, CannotCompileException 
	{
		for(CtClass itface : traitDescriptor.getInterfaces()) 
		{
			for(CtMethod cmethod : itface.getMethods()) 
			{
				String name = cmethod.getName();
				
				if(aliases.containsKey(name))
				{
					try
					{
						itface.getDeclaredMethod(aliases.get(name), cmethod.getParameterTypes());
						Logger.Log("Duplicated method " + aliases.get(name) + ". Not injecting.");
					}
					catch(NotFoundException e)
					{
						CtMethod m;
						
						Logger.Log("Injecting new interface method.");
						{
							m = CtNewMethod.abstractMethod(cmethod.getReturnType(), aliases.get(name), cmethod.getParameterTypes(), cmethod.getExceptionTypes(), itface);
							itface.addMethod(m);
							_codeConverter.redirectMethodCall(cmethod, m);
						}
						Logger.Log("Method : " + m.getLongName() + " was injected.");
					}
				}
			}
			
			if(Arrays.asList(_rootClass.getInterfaces()).contains(itface))
			{
				Logger.Log(String.format("Interface %s is already implemented.", itface.getName()));
				continue;
			}
			
			Logger.Log("Injecting interface: " + itface.getName());
			Logger.Log("With methods:");
			for(CtMethod m : itface.getDeclaredMethods())
				Logger.Log(m.getLongName());
			
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
