/**
 * 
 */
package com.ricardosantos.scriptagent;

/**
 * Script Agent exception class.
 * @author Ricardo
 */
public class ScriptAgentException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ScriptAgentException()
	{
		
	}

	/**
	 * @param message
	 */
	public ScriptAgentException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public ScriptAgentException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ScriptAgentException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ScriptAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
