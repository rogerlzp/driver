package com.abc.driver.utility;

public class CellSiteDefException extends Exception{

	public CellSiteDefException()
	{
	}
	
	public CellSiteDefException(String sMessage)
	{
		super(sMessage);
		
	}
	
	public CellSiteDefException(String sMessage, Throwable causeException)
	{
		super(sMessage, causeException);
		
	}
}
