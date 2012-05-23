package es.upm.fi.dia.oeg.morph.r2rml;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class SubjectMap extends NodeMap
{
	private String column;
	private String template;
	private String termType;
	private Resource rdfsClass;
	private Set<String> graphSet;
	private Set<String> graphColumnSet;
	private Set<String> graphTemplateSet;
	private String inverseExpression;
	private RDFNode subject;
	
	//TODO: this is solely for morph
	private String columnOperation;

	public SubjectMap()
	{
		graphSet = new HashSet<String>();
		graphColumnSet = new HashSet<String>();
		graphTemplateSet = new HashSet<String>();
		
	}
	
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
	public Set<String> getGraphSet()
	{
		return graphSet;
	}
	public Set<String> getGraphColumnSet()
	{
		return graphColumnSet;
	}
	public Set<String> getGraphTemplateSet()
	{
		return graphTemplateSet;
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

	public void setColumnOperation(String columnOperation)
	{
		this.columnOperation = columnOperation;
	}

	public String getColumnOperation()
	{
		return columnOperation;
	}

	public void addGraph(String graph)
	{
		graphSet.add(graph);
	}
	public void addGraphColumn(String graphColumn)
	{
		graphColumnSet.add(graphColumn);
	}
	public void addGraphTemplate(String graphTemplate)
	{
		graphTemplateSet.add(graphTemplate);
	}
	
}
