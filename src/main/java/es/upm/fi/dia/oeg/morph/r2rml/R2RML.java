package es.upm.fi.dia.oeg.morph.r2rml;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class R2RML
{

	protected static final String uri="http://www.w3.org/ns/r2rml#";
	
	public static final Property sqlQuery = property("SQLQuery");
	public static final Property classProperty = property("class");
	public static final Property column = property("column");
	public static final Property subject = property("subject");
	public static final Property subjectMap = property("subjectMap");
	public static final Property predicateObjectMap = property("predicateObjectMap");
	public static final Property predicateMap = property("predicateMap");
	public static final Property objectMap = property("objectMap");
	public static final Property refPredicateObjectMap = property("refPredicateObjectMap");
	public static final Property refPredicateMap = property("refPredicateMap");
	public static final Property refObjectMap = property("refObjectMap");
    
	public static final Property predicate = property("predicate");
	public static final Property propertyColumn = property("propertyColumn");
	public static final Property rdfTypeProperty = property("RDFTypeProperty");
	public static final Property datatype = property("datatype");
	public static final Property tableName = property("tableName");

	public static final Property template = property("template");
	public static final Property object = property("object");
	public static final Property tableGraphIRI =property("tableGraphIRI");
	public static final Property rowGraph = property("rowGraph");
	public static final Property inverseExpression = property("inverseExpression"); 
	public static final Property columnGraphIRI = property("columnGraphIRI");

	public static final Property termType = property("termtype");
	public static final Property graph = property("graph");
	public static final Property graphColumn = property("graphColumn");
	public static final Resource TriplesMap = resource("TriplesMapClass");

	public static final Property joinCondition = property("joinCondition");
	public static final Property parentTriplesMap = property("parentTriplesMap");
	

	
	public static String getUri()
	{
		return uri;
	}
		
	protected static final Resource resource(String name)
	{ 
		return ResourceFactory.createResource(uri + name); 
	}
		
	protected static final Property property( String local )
	{ 
		return ResourceFactory.createProperty( uri, local ); 
	}
	
}
