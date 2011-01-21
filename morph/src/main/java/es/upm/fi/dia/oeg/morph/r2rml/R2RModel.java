package es.upm.fi.dia.oeg.morph.r2rml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;

public class R2RModel 
{
	private static String TURTLE_FORMAT = "TURTLE";
	public static String DEFAULT_GRAPH = "DEFAULT";
	
	private static Logger logger = Logger.getLogger(R2RModel.class.getName());

	private static String r2rmlNS = "http://www.w3.org/ns/r2rml#";
	private static String r2rml = "rr";
	private Model model;
	
	private Property rrSqlQuery = null;
    private Property rrClass = null;
    private Property rrColumn = null;
    private Property rrSubjectMap = null;
    private Property rrPredicateObjectMap = null;
    private Property rrPredicateMap = null;
    private Property rrObjectMap = null;
    private Property rrPredicate = null;
    private Property rrPropertyColumn = null;
    private Property rrRDFTypeProperty = null;
    private Property rrDatatype = null;
    private Property rrObject = null;
    private Property rrTableGraphIRI = null;
    private Property rrRowGraph = null;
    private Property rrInverseExpression = null;
    private Property rrGraphColumn = null;
    private Property rrColumnGraphIRI = null;
    
	private Resource rrTriplesMap = null;

	private Collection<TriplesMap> triplesMap;
	private Property rrTermType;
	private Property rrGraph;
	
	public R2RModel()
	{
		
		model = ModelFactory.createDefaultModel();
		rrSqlQuery = model.createProperty(r2rmlNS+"SQLQuery");
        rrClass = model.createProperty(r2rmlNS+"class");
        rrColumn = model.createProperty(r2rmlNS+"column");
        rrSubjectMap = model.createProperty(r2rmlNS+"subjectMap");
        rrPredicateObjectMap = model.createProperty(r2rmlNS+"predicateObjectMap");
        rrPredicateMap = model.createProperty(r2rmlNS+"predicateMap");
        rrObjectMap = model.createProperty(r2rmlNS+"objectMap");
        rrPredicate = model.createProperty(r2rmlNS+"predicate");
        rrPropertyColumn = model.createProperty(r2rmlNS+"propertyColumn");
        rrRDFTypeProperty = model.createProperty(r2rmlNS+"RDFTypeProperty");
        rrDatatype = model.createProperty(r2rmlNS+"datatype");
        rrObject = model.createProperty(r2rmlNS+"object");
        rrTableGraphIRI = model.createProperty(r2rmlNS+"tableGraphIRI");
        rrRowGraph = model.createProperty(r2rmlNS+"rowGraph");
        rrInverseExpression = model.createProperty(r2rmlNS+"inverseExpression"); 
        rrColumnGraphIRI = model.createProperty(r2rmlNS,"columnGraphIRI");

        rrTermType = model.createProperty(r2rmlNS,"termtype");
        rrGraph = model.createProperty(r2rmlNS,"graph");
        rrGraphColumn = model.createProperty(r2rmlNS,"graphColumn");
    	rrTriplesMap = model.createResource(r2rmlNS+"TriplesMapClass");

		
	}
	
	public void close()
	{
		model.close();
	}
	
	public void read(URI mappingUrl) throws IOException
	{
		InputStream in = new FileInputStream(new File(mappingUrl));
		this.read(in);
		in.close();
	}
	
	public void read(InputStream in)
	{
		RDFReader arp = model.getReader(TURTLE_FORMAT);
		arp.read(model,in,"");
		readTriplesMap();
	}

	public Collection<TriplesMap> getTriplesMap()
	{
        return this.triplesMap;
	}
	
	private void readTriplesMap()
	{
		Collection<TriplesMap> maps = new ArrayList<TriplesMap>();
		String queryString = "PREFIX "+r2rml+": <"+r2rmlNS+"> \n"+
				"SELECT ?tMap ?query ?subjCol ?subjType ?subjClass ?subjGraph ?subjGraphCol ?subjInverse WHERE { \n" +
				"?tMap a <"+rrTriplesMap.getURI()+"> ; \n" +
				r2rml+":"+rrSqlQuery.getLocalName()+ " ?query ; \n"+
				r2rml+":"+rrSubjectMap.getLocalName()+ " ?subjMap . \n" +
				"?subjMap "+r2rml+":"+rrColumn.getLocalName() + " ?subjCol . \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrTermType.getLocalName() + " ?subjType . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrClass.getLocalName() + " ?subjClass . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrGraph.getLocalName() + " ?subjGraph . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrGraphColumn.getLocalName() + " ?subjGraphCol . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrInverseExpression.getLocalName() + " ?subjInverse . } "+				
				"}";
		
		logger.debug("Query tripleMap: "+queryString);
		Query query = QueryFactory.create(queryString ) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      String uri = soln.get("tMap").asResource().getURI();
		      
		      logger.debug("Triples map found: "+uri);
		      
		      Resource subjClass = soln.getResource("subjClass"); 
		      Literal sqlQuery = soln.get("query").asLiteral();
		      Resource subjGraph = soln.getResource("subjGraph");
		      Literal subjGraphCol = soln.getLiteral("subjGraphCol");
		      Literal inverse = soln.getLiteral("subjInverse");
		      Literal column = soln.getLiteral("subjCol");
		      Literal termType = soln.getLiteral("subjType");
		      
		      TriplesMap tMap = new TriplesMap(uri);
		      SubjectMap subjectMap = new SubjectMap();
		      subjectMap.setRdfsClass(subjClass);
		      tMap.setSqlQuery(sqlQuery.getString());
		      if (subjGraph!=null)
		    	  subjectMap.setGraph(subjGraph.getURI());
		      else if (subjGraphCol!= null)
		    	  subjectMap.setGraphColumn(subjGraphCol.getString());
		      subjectMap.setColumn(column.getString());
		      if (inverse != null)
		    	  subjectMap.setInverseExpression(inverse.getString());
		      tMap.setSubjectMap(subjectMap );
		      
		      readPropertyObjectMap(tMap);
		      
		      maps.add(tMap);
		    }
		  } finally { qexec.close() ; }

		this.triplesMap=maps;
		
	}
	

	private void readPropertyObjectMap(TriplesMap tMap)
	{
		String queryString = "PREFIX "+r2rml+": <"+r2rmlNS+"> \n" +
					"SELECT ?predicate ?predicateColumn ?object ?column ?datatype " +
					"?graphColumn ?graph WHERE { \n" +
					"<"+tMap.getUri()+ "> a <"+rrTriplesMap.getURI()+"> ; \n" +
					r2rml+":"+rrPredicateObjectMap.getLocalName()+ " ?poMap . \n" +
					"?poMap "+r2rml+":"+rrPredicateMap.getLocalName()+ " ?pMap . \n" +
					"?poMap "+r2rml+":"+rrObjectMap.getLocalName()+ " ?oMap . \n" +
					"OPTIONAL { ?pMap "+r2rml+":"+rrPredicate.getLocalName() + " ?predicate . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+rrColumn.getLocalName() + " ?predicateColumn . } \n"+												
					"OPTIONAL { ?poMap "+r2rml+":"+rrGraphColumn.getLocalName() + " ?graphColumn . } \n"+				
					"OPTIONAL { ?poMap "+r2rml+":"+rrGraph.getLocalName() + " ?graph . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+rrColumn.getLocalName() + " ?column . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+rrDatatype.getLocalName() + " ?datatype . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+rrObject.getLocalName() + " ?object . } \n"+				
					"}";
		logger.debug("Query propertyObjectMap: "+queryString);
		Query query = QueryFactory.create(queryString ) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      Resource predicate = soln.getResource("predicate");
		      Resource typeProperty = soln.getResource("typeProperty");
		      Literal column = soln.getLiteral("column"); 
		      Resource datatype = soln.getResource("datatype");		      
		      RDFNode object = soln.get("object");		     		  
		      Literal propColumn = soln.getLiteral("predicateColumn");
		      Literal graphColumn = soln.getLiteral("graphColumn");
		      Resource graph = soln.getResource("graph");
		      
		      PredicateObjectMap poMap = new PredicateObjectMap();
		      PredicateMap pMap = new PredicateMap();
		      ObjectMap oMap = new ObjectMap();
		      if (predicate!=null)
		    	  pMap.setPredicate(model.createProperty(predicate.getURI()));
		      if (propColumn!=null)
		    	  pMap.setColumn(propColumn.getString());
		      
		      if (column!=null)
		    	  oMap.setColumn(column.getString());
		      if (datatype!=null)		    		    
		    	  oMap.setDatatype(getDatatype(datatype.getURI()));
		      if (object != null)
		    	  oMap.setObject(object);
		      if (graphColumn!=null)
		    	  poMap.setGraphColumn(graphColumn.getString());
		      if (graph!=null)
		    	  poMap.setGraph(graph.getURI());
		      
		      logger.debug(oMap.getColumn()+"-"+pMap.getPredicate()+"-"+oMap.getDatatype());
		      poMap.setPredicateMap(pMap);
		      poMap.setObjectMap(oMap);
		      tMap.addPropertyObjectMap(poMap);
		    }
		  } finally { qexec.close() ; }
		
	}

	private RDFDatatype getDatatype(String uri)
	{
		//TODO there must be a be better way to do this
		if (uri.equals(XSDDatatype.XSDstring.getURI()))
			return XSDDatatype.XSDstring;
		else if (uri.equals(XSDDatatype.XSDpositiveInteger.getURI()))
			return XSDDatatype.XSDpositiveInteger;
		else if (uri.equals(XSDDatatype.XSDint.getURI()))
			return XSDDatatype.XSDint;
		else if (uri.equals(XSDDatatype.XSDdecimal.getURI()))
			return XSDDatatype.XSDdecimal;
		else if (uri.equals(XSDDatatype.XSDdouble.getURI()))
			return XSDDatatype.XSDdouble;
		else if (uri.equals(XSDDatatype.XSDdateTime.getURI()))
			return XSDDatatype.XSDdateTime;
		
		return XSDDatatype.XSDanyURI;
	}
	
	
	
	
	
	
}
