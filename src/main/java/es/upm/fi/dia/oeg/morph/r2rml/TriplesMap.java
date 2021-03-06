package es.upm.fi.dia.oeg.morph.r2rml;

import java.util.ArrayList;
import java.util.Collection;


import com.hp.hpl.jena.rdf.model.Resource;


public class TriplesMap
{

	private String uri;
	private String tableName;
	private String tableUniqueIndex;
	private String sqlQuery;
	private SubjectMap subjectMap;
	private Collection<PredicateObjectMap> predicateObjectMap;
	private Collection<RefObjectMap> refObjectMap;
	//private String rowGraph;
	//private String tableGraphIRI; //TODO this should be a set of graph IRIs
	
	public TriplesMap(String uri)
	{
		this.setUri(uri);
		predicateObjectMap = new ArrayList<PredicateObjectMap>();
		refObjectMap = new ArrayList<RefObjectMap>();
	}
	public void addPropertyObjectMap(PredicateObjectMap propertyObjectMap)
	{
		this.predicateObjectMap.add(propertyObjectMap);
	}
	
	public Collection<PredicateObjectMap> getPropertyObjectMaps()
	{
		return this.predicateObjectMap;
	}

	public void addRefPropertyObjectMap(RefObjectMap refPropertyObjectMap)
	{
		this.refObjectMap.add(refPropertyObjectMap);
	}
	
	public Collection<RefObjectMap> getRefPropertyObjectMaps()
	{
		return this.refObjectMap;
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
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	public String getTableName()
	{
		return tableName;
	}
	public void setTableUniqueIndex(String tableUniqueIndex) {
		this.tableUniqueIndex = tableUniqueIndex;
	}
	public String getTableUniqueIndex() {
		return tableUniqueIndex;
	}
}
