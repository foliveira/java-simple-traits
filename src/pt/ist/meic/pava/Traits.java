@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Trait { }

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithTraits 
{
	@Trait values();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithAliasedTraits 
{
	@AliasedTrait values();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface AliasedTrait 
{
	@Trait trait();
	String[] aliases(); default new String[0];
}