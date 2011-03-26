package pt.ist.meic.pava.exception;

public class TraitDefinitionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6853890141851001543L;
	
	public TraitDefinitionException() { super(); }
	
	public TraitDefinitionException(String msg) { super(msg); }

	public TraitDefinitionException(String msg, Throwable t) { super(msg, t); }
}
