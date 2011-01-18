package es.upm.fi.dia.oeg.morph.r2rml;



import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class RDFTermMap
{
	private String column;
	private Property property;
	private String propertyColumn;
	private String inverseExpression;
	private RDFDatatype datatype;
	private String language;
	private String columnGraph;
	private String columnGraphIRI; //TODO should we accept multi-values?
	private RDFNode constantValue;
	private Property rdfTypeProperty;

	
	public RDFTermMap()
	{
		//this.parent = parent;
	}
	
	public String getPropertyColumn()
	{
		return propertyColumn;
	}

	public void setPropertyColumn(String propertyColumn)
	{
		this.propertyColumn = propertyColumn;
	}

	public String getInverseExpression()
	{
		return inverseExpression;
	}

	public void setInverseExpression(String inverseExpression)
	{
		this.inverseExpression = inverseExpression;
	}

	public RDFDatatype getDatatype()
	{
		return datatype;
	}

	public void setDatatype(RDFDatatype datatype)
	{
		this.datatype = datatype;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public String getColumnGraph()
	{
		return columnGraph;
	}

	public void setColumnGraph(String columnGraph)
	{
		this.columnGraph = columnGraph;
	}

	public RDFNode getConstantValue()
	{
		return constantValue;
	}

	public void setConstantValue(RDFNode constantValue)
	{
		this.constantValue = constantValue;
	}

	public Property getRdfTypeProperty()
	{
		return rdfTypeProperty;
	}

	public void setRdfTypeProperty(Property rdfTypeProperty)
	{
		this.rdfTypeProperty = rdfTypeProperty;
	}

	public void setColumn(String column)
	{
		this.column = column;
	}

	public String getColumn()
	{
		return column;
	}

	public void setProperty(Property property)
	{
		this.property = property;
	}

	public Property getProperty()
	{
		return property;
	}

	public void setColumnGraphIRI(String columnGraphIRI)
	{
		this.columnGraphIRI = columnGraphIRI;
	}

	public String getColumnGraphIRI()
	{
		return columnGraphIRI;
	}
	
}
