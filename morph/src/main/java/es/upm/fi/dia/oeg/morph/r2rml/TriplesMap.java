package es.upm.fi.dia.oeg.morph.r2rml;

import java.util.ArrayList;
import java.util.Collection;


import com.hp.hpl.jena.rdf.model.Resource;


public class TriplesMap
{

	private String uri;
	private Resource rdfsClass;
	private String logicalTable;
	private IRIorBlankNodeMap subjectMap;
	private Collection<RDFTermMap> propertyObjectMap;
	private String rowGraph;
	private String tableGraphIRI; //TODO this should be a set of graph IRIs
	private ForeignKey foreignKeyMap;
	
	public TriplesMap(String uri)
	{
		this.setUri(uri);
		propertyObjectMap = new ArrayList<RDFTermMap>();
	}
	public void addPropertyObjectMap(RDFTermMap propertyObjectMap)
	{
		this.propertyObjectMap.add(propertyObjectMap);
	}
	
	public Collection<RDFTermMap> getPropertyObjectMaps()
	{
		return this.propertyObjectMap;
	}
	
	public void setRdfsClass(Resource rdfsClass)
	{
		this.rdfsClass = rdfsClass;
	}
	public Resource getRdfsClass()
	{
		return this.rdfsClass;
	}

	public void setLogicalTable(String logicalTable)
	{
		this.logicalTable = logicalTable;
	}

	public String getLogicalTable()
	{
		return logicalTable;
	}

	public void setSubjectMap(IRIorBlankNodeMap subjectMap)
	{
		this.subjectMap = subjectMap;
	}

	public IRIorBlankNodeMap getSubjectMap()
	{
		return subjectMap;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public String getUri()
	{
		return uri;
	}

	public void setForeignKeyMap(ForeignKey foreignKeyMap)
	{
		this.foreignKeyMap = foreignKeyMap;
	}

	public ForeignKey getForeignKeyMap()
	{
		return foreignKeyMap;
	}

	public void setRowGraph(String rowGraph)
	{
		this.rowGraph = rowGraph;
	}

	public String getRowGraph()
	{
		return rowGraph;
	}
	public void setTableGraphIRI(String tableGraphIRI)
	{
		this.tableGraphIRI = tableGraphIRI;
	}
	public String getTableGraphIRI()
	{
		return tableGraphIRI;
	}

}
