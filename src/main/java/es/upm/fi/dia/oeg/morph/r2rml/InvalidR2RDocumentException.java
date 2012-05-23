package es.upm.fi.dia.oeg.morph.r2rml;

public class InvalidR2RDocumentException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2638822989623799293L;

	public InvalidR2RDocumentException(String msg)
	{
		super(msg);
	}
	public InvalidR2RDocumentException(String msg,Throwable e)
	{
		super(msg,e);
	}
}
