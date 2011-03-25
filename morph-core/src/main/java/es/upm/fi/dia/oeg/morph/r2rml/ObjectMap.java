package es.upm.fi.dia.oeg.morph.r2rml;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class ObjectMap
{
	private RDFNode object;
	private String column;
	private String termType;
	private RDFDatatype datatype;
	private String inverseExpression;
	private String template;
	public RDFNode getObject()
	{
		return object;
	}
	public void setObject(RDFNode object)
	{
		this.object = object;
	}
	public String getColumn()
	{
		return column;
	}
	public void setColumn(String column)
	{
		this.column = column;
	}
	public String getTermType()
	{
		return termType;
	}
	public void setTermType(String termType)
	{
		this.termType = termType;
	}
	public RDFDatatype getDatatype()
	{
		return datatype;
	}
	public void setDatatype(RDFDatatype datatype)
	{
		this.datatype = datatype;
	}
	public String getInverseExpression()
	{
		return inverseExpression;
	}
	public void setInverseExpression(String inverseExpression)
	{
		this.inverseExpression = inverseExpression;
	}
	public String getTemplate()
	{
		return template;
	}
	public void setTemplate(String template)
	{
		this.template = template;
	}
	
	
}
