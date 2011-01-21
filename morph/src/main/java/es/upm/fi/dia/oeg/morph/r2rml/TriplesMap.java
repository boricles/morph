package es.upm.fi.dia.oeg.morph.r2rml;

import java.util.ArrayList;
import java.util.Collection;


import com.hp.hpl.jena.rdf.model.Resource;


public class TriplesMap
{

	private String uri;
	private String tableOwner;
	private String tableName;
	private String sqlQuery;
	private SubjectMap subjectMap;
	private Collection<PredicateObjectMap> predicateObjectMap;
	private Collection<RefPredicateObjectMap> refPredicateObjectMap;
	//private String rowGraph;
	//private String tableGraphIRI; //TODO this should be a set of graph IRIs
	
	public TriplesMap(String uri)
	{
		this.setUri(uri);
		predicateObjectMap = new ArrayList<PredicateObjectMap>();
	}
	public void addPropertyObjectMap(PredicateObjectMap propertyObjectMap)
	{
		this.predicateObjectMap.add(propertyObjectMap);
	}
	
	public Collection<PredicateObjectMap> getPropertyObjectMaps()
	{
		return this.predicateObjectMap;
	}

	public void setSqlQuery(String sqlQuery)
	{
		this.sqlQuery = sqlQuery;
	}

	public String getSqlQuery()
	{
		return sqlQuery;
	}

	public void setSubjectMap(SubjectMap subjectMap)
	{
		this.subjectMap = subjectMap;
	}

	public SubjectMap getSubjectMap()
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
	public void setTableOwner(String tableOwner)
	{
		this.tableOwner = tableOwner;
	}
	public String getTableOwner()
	{
		return tableOwner;
	}
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	public String getTableName()
	{
		return tableName;
	}
}
