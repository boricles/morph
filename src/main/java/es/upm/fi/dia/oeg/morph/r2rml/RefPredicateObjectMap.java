package es.upm.fi.dia.oeg.morph.r2rml;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class RefPredicateObjectMap extends PredicateObjectMap
{
	private RefPredicateMap refPredicateMap;
	private RefObjectMap refObjectMap;
	
	/*
	private String graphColumn;
	private String graph; //TODO should we accept multi-values?
	private String graphTemplate;
	
	public String getGraphColumn()
	{
		return graphColumn;
	}
	public void setGraphColumn(String graphColumn)
	{
		this.graphColumn = graphColumn;
	}
	public String getGraph()
	{
		return graph;
	}
	public void setGraph(String graph)
	{
		this.graph = graph;
	}
	public String getGraphTemplate()
	{
		return graphTemplate;
	}
	public void setGraphTemplate(String graphTemplate)
	{
		this.graphTemplate = graphTemplate;
	}*/
	public void setRefPredicateMap(RefPredicateMap refPredicateMap)
	{
		this.refPredicateMap = refPredicateMap;
	}
	public RefPredicateMap getRefPredicateMap()
	{
		return refPredicateMap;
	}
	public void setRefObjectMap(RefObjectMap refObjectMap)
	{
		this.refObjectMap = refObjectMap;
	}
	public RefObjectMap getRefObjectMap()
	{
		return refObjectMap;
	}
	
	@Override
	public RDFNode getConstant()
	{
		return null;
	}
	@Override
	public String getColumn()
	{
		return null;
	}
	@Override
	public String getTemplate()
	{
		return null;
	}
	
}
