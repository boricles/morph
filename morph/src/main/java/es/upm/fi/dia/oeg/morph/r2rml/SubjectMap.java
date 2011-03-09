package es.upm.fi.dia.oeg.morph.r2rml;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class SubjectMap extends NodeMap
{
	private String column;
	private String template;
	private String termType;
	private Resource rdfsClass;
	private String graph;
	private String graphColumn;
	private String graphTemplate;
	private String inverseExpression;
	private RDFNode subject;
	
	@Override
	public RDFNode getConstant()
	{
		return getSubject();
	}
	
	public String getTemplate()
	{
		return template;
	}
	public void setTemplate(String template)
	{
		this.template = template;
	}
	public String getTermType()
	{
		return termType;
	}
	public void setTermType(String termType)
	{
		this.termType = termType;
	}
	public Resource getRdfsClass()
	{
		return rdfsClass;
	}
	public void setRdfsClass(Resource rdfsClass)
	{
		this.rdfsClass = rdfsClass;
	}
	public String getGraph()
	{
		return graph;
	}
	public void setGraph(String graph)
	{
		this.graph = graph;
	}
	public String getGraphColumn()
	{
		return graphColumn;
	}
	public void setGraphColumn(String graphColumn)
	{
		this.graphColumn = graphColumn;
	}
	public String getGraphTemplate()
	{
		return graphTemplate;
	}
	public void setGraphTemplate(String graphTemplate)
	{
		this.graphTemplate = graphTemplate;
	}
	public String getInverseExpression()
	{
		return inverseExpression;
	}
	public void setInverseExpression(String inverseExpression)
	{
		this.inverseExpression = inverseExpression;
	}
	public void setSubject(RDFNode subject)
	{
		this.subject = subject;
	}
	public RDFNode getSubject()
	{
		return subject;
	}

	public void setColumn(String column)
	{
		this.column = column;
	}

	@Override
	public String getColumn()
	{
		return column;
	}
	
}
