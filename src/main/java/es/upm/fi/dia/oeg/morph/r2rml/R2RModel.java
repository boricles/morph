package es.upm.fi.dia.oeg.morph.r2rml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.n3.turtle.TurtleParseException;
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
import com.hp.hpl.jena.reasoner.rulesys.impl.RDFSCMPPreprocessHook;

public class R2RModel 
{
	private static String TURTLE_FORMAT = "TURTLE";
	public static String DEFAULT_GRAPH = "DEFAULT";
	
	private static Logger logger = Logger.getLogger(R2RModel.class.getName());

	public static final String R2RML_URI = "http://www.w3.org/ns/r2rml";
	private static String r2rmlNS = "http://www.w3.org/ns/r2rml#";
	private static String morphNS = "http://es.upm.fi.dia.oeg/morph#";	
	private static String r2rml = "rr";
	private static String morph = "morph";
	private Model model;
	
	private Property rrSqlQuery = null;
    private Property rrClass = null;
    private Property rrColumn = null;
    private Property rrSubject = null;
    private Property rrSubjectMap = null;
    private Property rrPredicateObjectMap = null;
    private Property rrPredicateMap = null;
    private Property rrObjectMap = null;
    private Property rrRefPredicateObjectMap = null;
    private Property rrRefPredicateMap = null;
    private Property rrRefObjectMap = null;
    private Property rrPredicate = null;
    private Property rrPropertyColumn = null;
    private Property rrRDFTypeProperty = null;
    private Property rrDatatype = null;
    private Property rrTableName = null;
    private Property rrObject = null;
    private Property rrTableGraphIRI = null;
    private Property rrRowGraph = null;
    private Property rrInverseExpression = null;
    private Property rrGraphColumn = null;
    private Property rrColumnGraphIRI = null;    
	private Resource rrTriplesMap = null;
	private Resource rrTemplate = null;
	private Property rrJoinCondition = null;
	private Property rrParentTriplesMap = null;
	
	private Resource morphColumnOperation = null;
	
	
	private Map<String,TriplesMap> triplesMap;
	private Property rrTermType;
	private Property rrGraph;
	
	public R2RModel()
	{
		
		model = ModelFactory.createDefaultModel();
		rrSqlQuery = model.createProperty(r2rmlNS+"SQLQuery");
        rrClass = model.createProperty(r2rmlNS+"class");
        rrColumn = model.createProperty(r2rmlNS+"column");
        rrSubject = model.createProperty(r2rmlNS+"subject");
        rrSubjectMap = model.createProperty(r2rmlNS+"subjectMap");
        rrPredicateObjectMap = model.createProperty(r2rmlNS+"predicateObjectMap");
        rrPredicateMap = model.createProperty(r2rmlNS+"predicateMap");
        rrObjectMap = model.createProperty(r2rmlNS+"objectMap");
        rrRefPredicateObjectMap = model.createProperty(r2rmlNS+"refPredicateObjectMap");
        rrRefPredicateMap = model.createProperty(r2rmlNS+"refPredicateMap");
        rrRefObjectMap = model.createProperty(r2rmlNS+"refObjectMap");
        
        rrPredicate = model.createProperty(r2rmlNS+"predicate");
        rrPropertyColumn = model.createProperty(r2rmlNS+"propertyColumn");
        rrRDFTypeProperty = model.createProperty(r2rmlNS+"RDFTypeProperty");
        rrDatatype = model.createProperty(r2rmlNS+"datatype");
        rrTableName = model.createProperty(r2rmlNS+"tableName");

        rrTemplate = model.createProperty(r2rmlNS+"template");
        rrObject = model.createProperty(r2rmlNS+"object");
        rrTableGraphIRI = model.createProperty(r2rmlNS+"tableGraphIRI");
        rrRowGraph = model.createProperty(r2rmlNS+"rowGraph");
        rrInverseExpression = model.createProperty(r2rmlNS+"inverseExpression"); 
        rrColumnGraphIRI = model.createProperty(r2rmlNS,"columnGraphIRI");

        rrTermType = model.createProperty(r2rmlNS,"termtype");
        rrGraph = model.createProperty(r2rmlNS,"graph");
        rrGraphColumn = model.createProperty(r2rmlNS,"graphColumn");
    	rrTriplesMap = model.createResource(r2rmlNS+"TriplesMapClass");

    	rrJoinCondition = model.createProperty(r2rmlNS+"joinCondition");
    	rrParentTriplesMap = model.createProperty(r2rmlNS+"parentTriplesMap");
    	
    	morphColumnOperation = model.createResource(morphNS+"columnOperation");
	}
	
	public void close()
	{
		model.close();
	}
	
	public void read(URI mappingUrl) throws InvalidR2RDocumentException, InvalidR2RLocationException
	{
		if (mappingUrl.isAbsolute())
		{
			try
			{
				InputStream in = new FileInputStream(new File(mappingUrl));
				this.read(in);
				in.close();
			}
			catch (FileNotFoundException e)
			{
				throw new InvalidR2RLocationException(e.getMessage(), e);
			} catch (IOException e)
			{
				throw new InvalidR2RLocationException(e.getMessage(), e);			}
		}
		else
		{
			URL url = R2RModel.class.getClassLoader().getResource(mappingUrl.toString());
			try
			{
				this.read(url.toURI());
			} catch (URISyntaxException e)
			{
				String msg = "Error reading the mapping location: "+e.getMessage();
				logger.error(msg);
				throw new InvalidR2RLocationException(msg,e);
			}
		}
	}
	
	public void read(InputStream in) throws InvalidR2RDocumentException
	{
		RDFReader arp = model.getReader(TURTLE_FORMAT);
		try
		{
			arp.read(model,in,"");
		}
		catch (TurtleParseException e)
		{
			String msg = "Error parsing the r2r document: "+e.getMessage();
			logger.error(msg);
			throw new InvalidR2RDocumentException(msg,e);
		}
		readTriplesMap();
		
	}
	
	public void write(URI uri) throws IOException
	{
		FileWriter fw = new FileWriter(new File(uri));
		model.write(fw, TURTLE_FORMAT);
		model.close();
		
	}

	public Collection<TriplesMap> getTriplesMap()
	{
        return this.triplesMap.values();
	}
	
	private void readTriplesMap()
	{
		triplesMap = new HashMap<String,TriplesMap>();
		readTriplesMap(null);
		
	}
	
	private void readTriplesMap(String triplesMapUri)
	{
		String tMapVar = "?tMap";
		if (triplesMapUri!=null)
			tMapVar = "<"+triplesMapUri+">";
		//Collection<TriplesMap> maps = new ArrayList<TriplesMap>();
		String queryString = "PREFIX "+r2rml+": <"+r2rmlNS+"> \n"+
				"PREFIX "+morph+": <"+morphNS+"> \n"+
				"SELECT ?tMap ?query ?table ?subjCol ?subjColOp ?subjType ?subject ?subjClass " +
				"?subjGraph ?subjGraphCol ?subjInverse ?subjTemplate WHERE { \n" +
				tMapVar+" a <"+rrTriplesMap.getURI()+"> ; \n" +
				r2rml+":"+rrSqlQuery.getLocalName()+ " ?query ; \n"+
				r2rml+":"+rrSubjectMap.getLocalName()+ " ?subjMap . \n" +
				"OPTIONAL { "+tMapVar+" "+r2rml+":"+rrTableName.getLocalName() + " ?table . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrColumn.getLocalName() + " ?subjCol . } \n"+				
				"OPTIONAL { ?subjMap "+morph+":"+morphColumnOperation.getLocalName() + " ?subjColOp . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrTemplate.getLocalName() + " ?subjTemplate . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrTermType.getLocalName() + " ?subjType . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+rrSubject.getLocalName() + " ?subject . } \n"+				
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
		      
		      String uri = triplesMapUri;
		      if (triplesMapUri==null)
		      {
		    	if (soln.get("tMap").isAnon())
			    		uri = soln.get("tMap").asNode().getBlankNodeLabel();
		    	else if (soln.get("tMap").isResource())
		    		uri = soln.get("tMap").asResource().getURI();
		      }
		      //triplesMapUri==null?soln.get("tMap").asResource().getURI():triplesMapUri;
		      if (triplesMap.containsKey(uri))
		    	  continue;
		      logger.debug("Triples map found: "+uri);
		      
		      Resource subjClass = soln.getResource("subjClass"); 
		      Literal sqlQuery = soln.get("query").asLiteral();
		      Resource subjGraph = soln.getResource("subjGraph");
		      Literal subjGraphCol = soln.getLiteral("subjGraphCol");
		      Literal inverse = soln.getLiteral("subjInverse");
		      Literal column = soln.getLiteral("subjCol");
		      Literal columnOperation = soln.getLiteral("subjColOp");
		      Literal termType = soln.getLiteral("subjType");
		      RDFNode subject = soln.getResource("subject");
		      Literal table = soln.getLiteral("table");
		      Literal subjTemplate = soln.getLiteral("subjTemplate");
		      
		      TriplesMap tMap = new TriplesMap(uri);
		      SubjectMap subjectMap = new SubjectMap();
		      subjectMap.setRdfsClass(subjClass);
		      tMap.setSqlQuery(sqlQuery.getString());
		      if (table!=null)
		    	  tMap.setTableName(table.getString());
		      if (subjGraph!=null)
		    	  subjectMap.setGraph(subjGraph.getURI());
		      else if (subjGraphCol!= null)
		    	  subjectMap.setGraphColumn(subjGraphCol.getString());
		      if (columnOperation != null)
		    	  subjectMap.setColumnOperation(columnOperation.getString());
		      if (subjTemplate != null)
		    	  subjectMap.setTemplate(subjTemplate.getString());
		      if (column!=null)
		    	  subjectMap.setColumn(column.getString());
		      if (inverse != null)
		    	  subjectMap.setInverseExpression(inverse.getString());
		      if (subject != null)
		    	  subjectMap.setSubject(subject);
		      subjectMap.setTriplesMap(tMap);
		      tMap.setSubjectMap(subjectMap );
		      
		      readPropertyObjectMap(tMap);
		      readRefPropertyObjectMap(tMap);
		      
		      triplesMap.put(uri, tMap);
		    }
		  } finally { qexec.close() ; }

		//this.triplesMap=maps;
		
	}
	
	/*
	private void fillRefTriplesMap(TriplesMap tMap)
	{
		for (TriplesMap t:getTriplesMap())
		{
			for (RefPredicateObjectMap refPOM:t.getRefPropertyObjectMaps())
			{
				if (refPOM.getObjectMap().get)
			}
		}
	}
	*/
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
		      poMap.setTriplesMap(tMap);
		      tMap.addPropertyObjectMap(poMap);
		    }
		  } finally { qexec.close() ; }
		
	}

	private void readRefPropertyObjectMap(TriplesMap tMap)
	{
		String queryString = "PREFIX "+r2rml+": <"+r2rmlNS+"> \n" +
					"SELECT ?predicate ?parentTm ?join " +
					"?graphColumn ?graph WHERE { \n" +
					"<"+tMap.getUri()+ "> a <"+rrTriplesMap.getURI()+"> ; \n" +
					r2rml+":"+rrRefPredicateObjectMap.getLocalName()+ " ?poMap . \n" +
					"?poMap "+r2rml+":"+rrRefPredicateMap.getLocalName()+ " ?pMap . \n" +
					"?poMap "+r2rml+":"+rrRefObjectMap.getLocalName()+ " ?oMap . \n" +
					"OPTIONAL { ?pMap "+r2rml+":"+rrPredicate.getLocalName() + " ?predicate . } \n"+				
					"OPTIONAL { ?poMap "+r2rml+":"+rrGraphColumn.getLocalName() + " ?graphColumn . } \n"+				
					"OPTIONAL { ?poMap "+r2rml+":"+rrGraph.getLocalName() + " ?graph . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+rrJoinCondition.getLocalName() + " ?join . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+rrParentTriplesMap.getLocalName() + " ?parentTm . } \n"+				
					"}";
		logger.debug("Query refPropertyObjectMap: "+queryString);
		
		Query query = QueryFactory.create(queryString ) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      Resource predicate = soln.getResource("predicate");
		      Literal join = soln.getLiteral("join"); 
		      RDFNode parent = soln.get("parentTm");		     		  
		      Literal graphColumn = soln.getLiteral("graphColumn");
		      Resource graph = soln.getResource("graph");
		      
		      RefPredicateObjectMap poMap = new RefPredicateObjectMap();
		      RefPredicateMap pMap = new RefPredicateMap();
		      RefObjectMap oMap = new RefObjectMap();
		      if (predicate!=null)
		    	  pMap.setPredicate(model.createProperty(predicate.getURI()));
		      
		      if (join!=null)
		    	  oMap.setJoinCondition(join.getString());
		      if (parent != null)
		      {
		    	  String parentUri = parent.isAnon()?parent.asNode().getBlankNodeLabel():parent.asResource().getURI(); 
		    	  logger.debug(parentUri);
		    	  if (triplesMap.containsKey(parentUri))
		    		  oMap.setParentTriplesMap(triplesMap.get(parentUri));
		    	  else
		    	  {
		    		  logger.debug("Find ref: "+parentUri);
		    		  readTriplesMap(parentUri);
		    		  oMap.setParentTriplesMap(triplesMap.get(parentUri));
		    	  }
		      }
		      if (graphColumn!=null)
		    	  poMap.setGraphColumn(graphColumn.getString());
		      if (graph!=null)
		    	  poMap.setGraph(graph.getURI());
		      
		      
		      logger.debug(oMap.getJoinCondition()+"-"+pMap.getPredicate());
		      if (oMap.getParentTriplesMap()!=null)
		    	  logger.debug("Triples: -"+oMap.getParentTriplesMap().getUri());
		      poMap.setRefPredicateMap(pMap);
		      poMap.setRefObjectMap(oMap);
		      poMap.setTriplesMap(tMap);
		      tMap.addRefPropertyObjectMap(poMap);
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

	public Collection<TriplesMap> getTriplesMapForUri(String uri)
	{
		Collection<TriplesMap> col = new ArrayList<TriplesMap>();
		for (TriplesMap t : this.getTriplesMap())
		{
			Resource rdfclass = t.getSubjectMap().getRdfsClass();
			logger.info(uri+"--"+rdfclass.getURI());
			if (rdfclass!=null && rdfclass.getURI().equals(uri))
				col.add(t);
		}
		return col;
	}

	public Collection<PredicateObjectMap> getPredicateObjectMapForUri(String uri)
	{
		Collection<PredicateObjectMap> col = new ArrayList<PredicateObjectMap>();
		
		for (TriplesMap t :this.getTriplesMap())
		{
			for (PredicateObjectMap moMap:t.getPropertyObjectMaps())
			{
				if (moMap.getPredicateMap().getPredicate().getURI().equals(uri))
					col.add(moMap);
			}
		}
		col.addAll(getRefPredicateObjectMapForUri(uri));
		return col;
	}
	
	public Collection<RefPredicateObjectMap> getRefPredicateObjectMapForUri(String uri)
	{
		Collection<RefPredicateObjectMap> col = new ArrayList<RefPredicateObjectMap>();
		
		for (TriplesMap t :this.getTriplesMap())
		{
			for (RefPredicateObjectMap moMap:t.getRefPropertyObjectMaps())
			{
				if (moMap.getRefPredicateMap().getPredicate().getURI().equals(uri))
					col.add(moMap);
			}
		}
		return col;
	}
	
	
	
	
	
}
