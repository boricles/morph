package es.upm.fi.dia.oeg.morph.r2rml;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.Property;

public class PredicateMap
{
	private String column;
	private Property predicate;
	private String template;
	private String inverseExpression;
	
	public void setColumn(String column)
	{
		this.column = column;
	}
	public String getColumn()
	{
		return column;
	}
	public void setPredicate(Property predicate)
	{
		this.predicate = predicate;
	}
	public Property getPredicate()
	{
		return predicate;
	}
	public void setTemplate(String template)
	{
		this.template = template;
	}
	public String getTemplate()
	{
		return template;
	}
	public void setInverseExpression(String inverseExpression)
	{
		this.inverseExpression = inverseExpression;
	}
	public String getInverseExpression()
	{
		return inverseExpression;
	}
}
