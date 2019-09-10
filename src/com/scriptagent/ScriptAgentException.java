package com.scriptagent;

public class ScriptAgentException extends Exception
{

	private static final long serialVersionUID = 1L;

	public ScriptAgentException()
	{
		
	}

	public ScriptAgentException(String message)
	{
		super(message);
	}

	public ScriptAgentException(Throwable cause)
	{
		super(cause);
	}

	public ScriptAgentException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public ScriptAgentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
