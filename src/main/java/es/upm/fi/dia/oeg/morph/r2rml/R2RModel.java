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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static es.upm.fi.dia.oeg.morph.r2rml.R2RML.*;
import static es.upm.fi.dia.oeg.morph.Morph.*;
import org.apache.log4j.Logger;
import org.openjena.riot.RiotException;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Triple;
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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.op.OpBGP;
import com.hp.hpl.jena.sparql.algebra.op.OpFilter;
import com.hp.hpl.jena.sparql.algebra.op.OpProject;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.resultset.ResultSetException;
import com.hp.hpl.jena.vocabulary.RDF;

import es.upm.fi.dia.oeg.morph.Morph;

public class R2RModel 
{
	private static String TURTLE_FORMAT = "TURTLE";
	public static String DEFAULT_GRAPH = "DEFAULT";
	
	private static Logger logger = Logger.getLogger(R2RModel.class.getName());

	public static final String R2RML_URI = "http://www.w3.org/ns/r2rml";
	//private static String r2rmlNS = "http://www.w3.org/ns/r2rml#";
	//private static String morphNS = "http://es.upm.fi.dia.oeg/morph#";	
	private static String r2rml = "rr";
	private static String morph = "morph";
	private Model model;
	private Resource morphColumnOperation = null;
	
	
	private Map<String,TriplesMap> triplesMap;
	//private Property rrTermType;
	//private Property rrGraph;
	private String endpoint;
	
	public R2RModel()
	{
		
		model = ModelFactory.createDefaultModel();
    	morphColumnOperation = model.createResource(Morph.getUri()+"columnOperation");
	}
	
	public R2RModel(String mappingEnpoint)
	{
		this();
		endpoint = mappingEnpoint;
		loadLinks();
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
		} catch (RiotException e){
			throw new InvalidR2RDocumentException("Error reading  r2r document: "+e.getMessage(), e);
		}
		readTriplesMap();
	}
	
	public void write(URI uri) throws IOException
	{
		FileWriter fw = new FileWriter(new File(uri));
		model.write(fw, TURTLE_FORMAT);
		model.close();
		fw.close();		
	}

	public Collection<TriplesMap> getTriplesMap()
	{
		if (triplesMap==null)
			return null;
        return this.triplesMap.values();
	}
	
	public TriplesMap getTriplesMap(String uri)
	{
		return triplesMap.get(uri);
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
		String queryString = "PREFIX "+r2rml+": <"+R2RML.getUri()+"> \n"+
				"PREFIX "+morph+": <"+Morph.getUri()+"> \n"+
				"SELECT ?tMap ?query ?table ?subjCol ?subjColOp ?subjType ?subject ?subjClass " +
				"?subjInverse ?subjTemplate ?unique WHERE { \n" +
				tMapVar+" a <"+TriplesMap.getURI()+"> ; \n" +
				r2rml+":"+logicalTable.getLocalName()+" ?lt; \n"+
				r2rml+":"+subjectMap.getLocalName()+ " ?subjMap . \n" +
				"OPTIONAL { ?lt "+r2rml+":"+sqlQuery.getLocalName()+ " ?query . } \n"+
				"OPTIONAL { ?lt "+r2rml+":"+tableName.getLocalName() + " ?table . } \n"+				
				"OPTIONAL { ?lt "+morph+":"+uniqueIndex.getLocalName() + " ?unique . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+column.getLocalName() + " ?subjCol . } \n"+				
				"OPTIONAL { ?subjMap "+morph+":"+morphColumnOperation.getLocalName() + " ?subjColOp . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+template.getLocalName() + " ?subjTemplate . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+termType.getLocalName() + " ?subjType . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+subject.getLocalName() + " ?subject . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+classProperty.getLocalName() + " ?subjClass . } \n"+				
				//"OPTIONAL { ?subjMap "+r2rml+":"+rrGraph.getLocalName() + " ?subjGraph . } \n"+				
				//"OPTIONAL { ?subjMap "+r2rml+":"+rrGraphColumn.getLocalName() + " ?subjGraphCol . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+inverseExpression.getLocalName() + " ?subjInverse . } "+				
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
		      Literal sqlQuery = soln.getLiteral("query");
		      //Resource subjGraph = soln.getResource("subjGraph");
		      //Literal subjGraphCol = soln.getLiteral("subjGraphCol");
		      Literal inverse = soln.getLiteral("subjInverse");
		      Literal column = soln.getLiteral("subjCol");
		      Literal columnOperation = soln.getLiteral("subjColOp");
		      Literal termType = soln.getLiteral("subjType");
		      RDFNode subject = soln.getResource("subject");
		      Literal table = soln.getLiteral("table");
		      Literal subjTemplate = soln.getLiteral("subjTemplate");
		      Literal unique = soln.getLiteral("unique");
		      
		      TriplesMap tMap = new TriplesMap(uri);
		      SubjectMap subjectMap = new SubjectMap();
		      subjectMap.setRdfsClass(subjClass);
		      if (sqlQuery != null)
		    	  tMap.setSqlQuery(sqlQuery.getString());
		      if (table!=null)
		    	  tMap.setTableName(table.getString());
		      if (unique !=null)
		    	  tMap.setTableUniqueIndex(unique.getString());
		      //if (subjGraph!=null)
		      //	  subjectMap.addGraph(subjGraph.getURI());
		      //else if (subjGraphCol!= null)
		      //	  subjectMap.addGraphColumn(subjGraphCol.getString());
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
		      
		      readIndexes(tMap);
		      readGraphs(subjectMap);
		      readGraphColumns(subjectMap);
		      readPropertyObjectMap(tMap);
		      readRefPropertyObjectMap(tMap);
		      
		      triplesMap.put(uri, tMap);
		    }
		  } finally { qexec.close() ; }

		//this.triplesMap=maps;
		
	}
	
	private void readIndexes(TriplesMap tMap) 
	{
		
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
		String queryString = "PREFIX "+r2rml+": <"+R2RML.getUri()+"> \n" +
					"SELECT ?predicate ?predicateColumn ?object ?column ?datatype " +
					"?graphColumn ?graph WHERE { \n" +
					"<"+tMap.getUri()+ "> a <"+TriplesMap.getURI()+"> ; \n" +
					r2rml+":"+predicateObjectMap.getLocalName()+ " ?poMap . \n" +
					"?poMap "+r2rml+":"+predicateMap.getLocalName()+ " ?pMap . \n" +
					"?poMap "+r2rml+":"+objectMap.getLocalName()+ " ?oMap . \n" +
					"OPTIONAL { ?pMap "+r2rml+":"+predicate.getLocalName() + " ?predicate . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+column.getLocalName() + " ?predicateColumn . } \n"+												
					"OPTIONAL { ?poMap "+r2rml+":"+graphColumn.getLocalName() + " ?graphColumn . } \n"+				
					"OPTIONAL { ?poMap "+r2rml+":"+graph.getLocalName() + " ?graph . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+column.getLocalName() + " ?column . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+datatype.getLocalName() + " ?datatype . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+object.getLocalName() + " ?object . } \n"+				
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
		String queryString = "PREFIX "+r2rml+": <"+R2RML.getUri()+"> \n" +
					"SELECT ?predicate ?parentTm ?join " +
					"?graphColumn ?graph WHERE { \n" +
					"<"+tMap.getUri()+ "> a <"+TriplesMap.getURI()+"> ; \n" +
					r2rml+":"+refPredicateObjectMap.getLocalName()+ " ?poMap . \n" +
					"?poMap "+r2rml+":"+refPredicateMap.getLocalName()+ " ?pMap . \n" +
					"?poMap "+r2rml+":"+refObjectMap.getLocalName()+ " ?oMap . \n" +
					"OPTIONAL { ?pMap "+r2rml+":"+predicate.getLocalName() + " ?predicate . } \n"+				
					"OPTIONAL { ?poMap "+r2rml+":"+graphColumn.getLocalName() + " ?graphColumn . } \n"+				
					"OPTIONAL { ?poMap "+r2rml+":"+graph.getLocalName() + " ?graph . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+joinCondition.getLocalName() + " ?join . } \n"+				
					"OPTIONAL { ?oMap "+r2rml+":"+parentTriplesMap.getLocalName() + " ?parentTm . } \n"+				
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

	private void readGraphs(SubjectMap sMap)
	{
		//HashSet<String> graphs = new HashSet<String>();
		String queryString = "PREFIX "+r2rml+": <"+R2RML.getUri()+"> \n" +
					"SELECT ?graph WHERE { \n" +
						"<"+sMap.getTriplesMap().getUri()+ "> a <"+TriplesMap.getURI()+"> ; \n" +
						r2rml+":"+subjectMap.getLocalName()+ " ?sMap . \n" +
						"OPTIONAL {?sMap "+r2rml+":"+graph.getLocalName()+ " ?graph . }\n" +
						"}";
		if (logger.isTraceEnabled())
			logger.trace("Query graph "+queryString);
		
		Query query = QueryFactory.create(queryString ) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		try {
			ResultSet results = qexec.execSelect() ;
			while (results.hasNext())
			{
				QuerySolution qs  = results.next();
				Resource graph = qs.getResource("graph");
				if (graph!=null)
					sMap.addGraph(graph.getURI());
			}
		} finally { qexec.close() ; }
			
	}

	private void readGraphColumns(SubjectMap sMap)
	{
		//HashSet<String> graphs = new HashSet<String>();
		String queryString = "PREFIX "+r2rml+": <"+R2RML.getUri()+"> \n" +
					"SELECT ?graphCol WHERE { \n" +
		"<"+sMap.getTriplesMap().getUri()+ "> a <"+TriplesMap.getURI()+"> ; \n" +
		r2rml+":"+subjectMap.getLocalName()+ " ?sMap . \n" +
		"OPTIONAL {?sMap "+r2rml+":"+graphColumn.getLocalName()+ " ?graphCol . }\n" +
		"}";
		if (logger.isTraceEnabled())
			logger.trace("Query graphColumn "+queryString);
		
		Query query = QueryFactory.create(queryString ) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		try {
			ResultSet results = qexec.execSelect() ;
			while (results.hasNext())
			{
				QuerySolution qs  = results.next();
				Literal graphColumn = qs.getLiteral("graphCol");
				if (graphColumn!=null)
					sMap.addGraphColumn(graphColumn.getString());
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
		if (endpoint!=null)
			return getTMapForUri(uri);
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

	private Collection<TriplesMap> getTMapForUri(String uri)
	{
			Collection<TriplesMap> col = new ArrayList<TriplesMap>();

			String queryString = "PREFIX "+r2rml+": <"+R2RML.getUri()+"> \n" +
					"PREFIX "+morph+": <"+Morph.getUri()+"> \n"+
					"SELECT DISTINCT ?object ?column ?datatype " +
					"?tMap ?query ?table ?subjCol ?subjColOp ?subjType ?subject ?subjClass "+
					"?graphColumn ?graph WHERE { \n" +
					//"?tMap a <"+TriplesMap.getURI()+"> ; \n" +
					"?tMap "+r2rml+":"+predicateObjectMap.getLocalName()+ " ?poMap . \n" +
					//"?poMap "+r2rml+":"+predicateMap.getLocalName()+ " ?pMap . \n" +
					//"?poMap "+r2rml+":"+objectMap.getLocalName()+ " ?oMap . \n" +
					//"?pMap "+r2rml+":"+predicate.getLocalName() + " <"+uri+"> . \n"+				
					//"OPTIONAL { ?pMap "+r2rml+":"+column.getLocalName() + " ?predicateColumn . } \n"+												
					//"OPTIONAL { ?poMap "+r2rml+":"+graphColumn.getLocalName() + " ?graphColumn . } \n"+				
					//"OPTIONAL { ?poMap "+r2rml+":"+graph.getLocalName() + " ?graph . } \n"+				
					//"OPTIONAL { ?oMap "+r2rml+":"+column.getLocalName() + " ?column . } \n"+				
					//"OPTIONAL { ?oMap "+r2rml+":"+datatype.getLocalName() + " ?datatype . } \n"+				
					//"OPTIONAL { ?oMap "+r2rml+":"+object.getLocalName() + " ?object . } \n"+
					//"?tMap "+r2rml+":"+sqlQuery.getLocalName()+ " ?query ; \n"+
					"?tMap "+r2rml+":"+subjectMap.getLocalName()+ " ?subjMap . \n" +
					"?tMap "+r2rml+":"+tableName.getLocalName() + " ?table .  \n"+				
					"OPTIONAL { ?subjMap "+r2rml+":"+column.getLocalName() + " ?subjCol . } \n"+				
					//"OPTIONAL { ?subjMap "+morph+":"+morphColumnOperation.getLocalName() + " ?subjColOp . } \n"+				
					"OPTIONAL { ?subjMap "+r2rml+":"+template.getLocalName() + " ?subjTemplate . } \n"+				
					//"OPTIONAL { ?subjMap "+r2rml+":"+termType.getLocalName() + " ?subjType . } \n"+				
					"OPTIONAL { ?subjMap "+r2rml+":"+subject.getLocalName() + " ?subject . } \n"+				
					"?subjMap "+r2rml+":"+classProperty.getLocalName() + " <"+uri+"> . \n"+				
					//"OPTIONAL { ?subjMap "+r2rml+":"+rrGraph.getLocalName() + " ?subjGraph . } \n"+				
					//"OPTIONAL { ?subjMap "+r2rml+":"+rrGraphColumn.getLocalName() + " ?subjGraphCol . } \n"+				
					//"OPTIONAL { ?subjMap "+r2rml+":"+inverseExpression.getLocalName() + " ?subjInverse . } "+				

					"}";
			
			logger.debug("Query propertyObjectMap: "+queryString);
			Query query = QueryFactory.create(queryString );
			QueryExecution exe = QueryExecutionFactory.sparqlService(endpoint, query);
			ResultSet res = null;
			try
			{
				res = exe.execSelect();
				
			}
			catch (ResultSetException e)
			{
				logger.debug("No results");
				return col;
			}
			while (res.hasNext())
			{
				QuerySolution soln = res.nextSolution();
			      //Resource predicate = ResourceFactory.createResource(uri);//  soln.getResource("predicate");
			      Resource typeProperty = soln.getResource("typeProperty");
			      Literal column = soln.getLiteral("column"); 
			      Resource datatype = soln.getResource("datatype");		      
			      RDFNode object = soln.get("object");		     		  
			      Literal propColumn = soln.getLiteral("predicateColumn");
			      Literal graphColumn = soln.getLiteral("graphColumn");
			      Resource graph = soln.getResource("graph");
			      /*
			      PredicateObjectMap poMap = new PredicateObjectMap();
			      PredicateMap pMap = new PredicateMap();
			      ObjectMap oMap = new ObjectMap();
			      //if (predicate!=null)
			      pMap.setPredicate(model.createProperty(uri));
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
			      */
			      //logger.debug(oMap.getColumn()+"-"+pMap.getPredicate()+"-"+oMap.getDatatype());
			      //poMap.setPredicateMap(pMap);
			      //poMap.setObjectMap(oMap);
			      
			      Resource tMapResource = soln.getResource("tMap");
			      Resource subjClass = soln.getResource("subjClass"); 
			      Literal sqlQuery = soln.getLiteral("query");
			      //Resource subjGraph = soln.getResource("subjGraph");
			      //Literal subjGraphCol = soln.getLiteral("subjGraphCol");
			      Literal inverse = soln.getLiteral("subjInverse");
			      Literal subjColumn = soln.getLiteral("subjCol");
			      Literal subjColumnOperation = soln.getLiteral("subjColOp");
			      Literal termType = soln.getLiteral("subjType");
			      RDFNode subject = soln.getResource("subject");
			      Literal table = soln.getLiteral("table");
			      Literal subjTemplate = soln.getLiteral("subjTemplate");
			      
			      TriplesMap tMap = new TriplesMap(tMapResource.getURI());
			      SubjectMap subjectMap = new SubjectMap();
			      subjectMap.setRdfsClass(subjClass);
			      if (sqlQuery != null)
			    	  tMap.setSqlQuery(sqlQuery.getString());
			      if (table!=null)
			    	  tMap.setTableName(table.getString());
			      //if (subjGraph!=null)
			      //	  subjectMap.addGraph(subjGraph.getURI());
			      //else if (subjGraphCol!= null)
			      //	  subjectMap.addGraphColumn(subjGraphCol.getString());
			      if (subjColumnOperation != null)
			    	  subjectMap.setColumnOperation(subjColumnOperation.getString());
			      if (subjTemplate != null)
			    	  subjectMap.setTemplate(subjTemplate.getString());
			      if (subjColumn!=null)
			    	  subjectMap.setColumn(subjColumn.getString());
			      if (inverse != null)
			    	  subjectMap.setInverseExpression(inverse.getString());
			      if (subject != null)
			    	  subjectMap.setSubject(subject);
			      subjectMap.setTriplesMap(tMap);
			      tMap.setSubjectMap(subjectMap );

			      
			      
			      //poMap.setTriplesMap(tMap);
			      
			      //tMap.addPropertyObjectMap(poMap);
			      col.add(tMap);
			}

			return col;
		

	}
	
	public Collection<PredicateObjectMap> getPredicateObjectMapForUri(String uri)
	{
		Collection<PredicateObjectMap> col = new ArrayList<PredicateObjectMap>();
		
		if (endpoint!=null)
		{
			col = getPOMapForUri(uri);
			col.addAll(getRefPOMapForUri(uri));
			return col;
		}
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
	
	private Collection<PredicateObjectMap> getPOMapForUri(String uri)
	{
		Collection<PredicateObjectMap> col = new ArrayList<PredicateObjectMap>();

		String queryString = "PREFIX "+r2rml+": <"+R2RML.getUri()+"> \n" +
				"PREFIX "+morph+": <"+Morph.getUri()+"> \n"+
				"SELECT DISTINCT ?object ?column ?datatype " +
				"?tMap ?query ?table ?subjCol ?subjColOp ?subjType ?subject ?subjClass "+
				"?graphColumn ?graph WHERE { \n" +
				//"?tMap a <"+TriplesMap.getURI()+"> ; \n" +
				"?tMap "+r2rml+":"+predicateObjectMap.getLocalName()+ " ?poMap . \n" +
				"?poMap "+r2rml+":"+predicateMap.getLocalName()+ " ?pMap . \n" +
				"?poMap "+r2rml+":"+objectMap.getLocalName()+ " ?oMap . \n" +
				"?pMap "+r2rml+":"+predicate.getLocalName() + " <"+uri+"> . \n"+				
				//"OPTIONAL { ?pMap "+r2rml+":"+column.getLocalName() + " ?predicateColumn . } \n"+												
				//"OPTIONAL { ?poMap "+r2rml+":"+graphColumn.getLocalName() + " ?graphColumn . } \n"+				
				"OPTIONAL { ?poMap "+r2rml+":"+graph.getLocalName() + " ?graph . } \n"+				
				"OPTIONAL { ?oMap "+r2rml+":"+column.getLocalName() + " ?column . } \n"+				
				"OPTIONAL { ?oMap "+r2rml+":"+datatype.getLocalName() + " ?datatype . } \n"+				
				"OPTIONAL { ?oMap "+r2rml+":"+object.getLocalName() + " ?object . } \n"+
				//"?tMap "+r2rml+":"+sqlQuery.getLocalName()+ " ?query ; \n"+
				"?tMap "+r2rml+":"+subjectMap.getLocalName()+ " ?subjMap . \n" +
				"?tMap "+r2rml+":"+tableName.getLocalName() + " ?table .  \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+column.getLocalName() + " ?subjCol . } \n"+				
				//"OPTIONAL { ?subjMap "+morph+":"+morphColumnOperation.getLocalName() + " ?subjColOp . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+template.getLocalName() + " ?subjTemplate . } \n"+				
				//"OPTIONAL { ?subjMap "+r2rml+":"+termType.getLocalName() + " ?subjType . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+subject.getLocalName() + " ?subject . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+classProperty.getLocalName() + " ?subjClass . } \n"+				
				//"OPTIONAL { ?subjMap "+r2rml+":"+rrGraph.getLocalName() + " ?subjGraph . } \n"+				
				//"OPTIONAL { ?subjMap "+r2rml+":"+rrGraphColumn.getLocalName() + " ?subjGraphCol . } \n"+				
				//"OPTIONAL { ?subjMap "+r2rml+":"+inverseExpression.getLocalName() + " ?subjInverse . } "+				

				"}";
		
		logger.debug("Query propertyObjectMap: "+queryString);
		Query query = QueryFactory.create(queryString );
		QueryExecution exe = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet res = null;
		try{
			res = exe.execSelect();			
		}
		catch (ResultSetException e)
		{
			logger.debug("No results found.");
			return col;
		}

		while (res.hasNext())
		{
			QuerySolution soln = res.nextSolution();
		      //Resource predicate = ResourceFactory.createResource(uri);//  soln.getResource("predicate");
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
		      //if (predicate!=null)
		      pMap.setPredicate(model.createProperty(uri));
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
		      
		      //logger.debug(oMap.getColumn()+"-"+pMap.getPredicate()+"-"+oMap.getDatatype());
		      poMap.setPredicateMap(pMap);
		      poMap.setObjectMap(oMap);
		      
		      Resource tMapResource = soln.getResource("tMap");
		      Resource subjClass = soln.getResource("subjClass"); 
		      Literal sqlQuery = soln.getLiteral("query");
		      //Resource subjGraph = soln.getResource("subjGraph");
		      //Literal subjGraphCol = soln.getLiteral("subjGraphCol");
		      Literal inverse = soln.getLiteral("subjInverse");
		      Literal subjColumn = soln.getLiteral("subjCol");
		      Literal subjColumnOperation = soln.getLiteral("subjColOp");
		      Literal termType = soln.getLiteral("subjType");
		      RDFNode subject = soln.getResource("subject");
		      Literal table = soln.getLiteral("table");
		      Literal subjTemplate = soln.getLiteral("subjTemplate");
		      
		      TriplesMap tMap = new TriplesMap(tMapResource.getURI());
		      SubjectMap subjectMap = new SubjectMap();
		      subjectMap.setRdfsClass(subjClass);
		      if (sqlQuery != null)
		    	  tMap.setSqlQuery(sqlQuery.getString());
		      if (table!=null)
		    	  tMap.setTableName(table.getString());
		      //if (subjGraph!=null)
		      //	  subjectMap.addGraph(subjGraph.getURI());
		      //else if (subjGraphCol!= null)
		      //	  subjectMap.addGraphColumn(subjGraphCol.getString());
		      if (subjColumnOperation != null)
		    	  subjectMap.setColumnOperation(subjColumnOperation.getString());
		      if (subjTemplate != null)
		    	  subjectMap.setTemplate(subjTemplate.getString());
		      if (subjColumn!=null)
		    	  subjectMap.setColumn(subjColumn.getString());
		      if (inverse != null)
		    	  subjectMap.setInverseExpression(inverse.getString());
		      if (subject != null)
		    	  subjectMap.setSubject(subject);
		      subjectMap.setTriplesMap(tMap);
		      tMap.setSubjectMap(subjectMap );

		      
		      
		      poMap.setTriplesMap(tMap);
		      col.add(poMap);
		      //tMap.addPropertyObjectMap(poMap);

		}

		return col;
	}


	private Collection<RefPredicateObjectMap> getRefPOMapForUri(String uri)
	{
		Collection<RefPredicateObjectMap> col = new ArrayList<RefPredicateObjectMap>();

		String queryString = "PREFIX "+r2rml+": <"+R2RML.getUri()+"> \n" +
				"PREFIX "+morph+": <"+Morph.getUri()+"> \n"+
				"SELECT DISTINCT ?object ?column ?datatype " +
				"?tMap ?query ?table ?subjCol ?subjColOp ?subjType ?subject ?subjClass "+
				"?graphColumn ?graph "+
				"?parentTMap ?parentTable ?parentSubjCol ?parentSubjTemplate ?parentSubject ?parentSubjClass "+
				"WHERE { \n" +
				"?tMap "+r2rml+":"+refPredicateObjectMap.getLocalName()+ " ?poMap . \n" +
				"?tMap "+r2rml+":"+subjectMap.getLocalName()+ " ?subjMap . \n" +
				"OPTIONAL { ?subjMap "+r2rml+":"+column.getLocalName() + " ?subjCol . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+template.getLocalName() + " ?subjTemplate . } \n"+				
				"OPTIONAL { ?subjMap "+r2rml+":"+subject.getLocalName() + " ?subject . } \n"+				
				"?subjMap "+r2rml+":"+classProperty.getLocalName() + " ?subjClass .  \n"+				
				
				"?tMap "+r2rml+":"+tableName.getLocalName() + " ?table .  \n"+				
				"?poMap "+r2rml+":"+refPredicateMap.getLocalName()+ " ?pMap . \n" +
				"?poMap "+r2rml+":"+refObjectMap.getLocalName()+ " ?oMap . \n" +
				"?pMap "+r2rml+":"+predicate.getLocalName() + " <"+uri+"> . \n"+				
				"?oMap "+r2rml+":"+parentTriplesMap.getLocalName() + " ?parentTMap .  \n"+
				"?parentTMap "+r2rml+":"+subjectMap.getLocalName()+ " ?parentSubjMap . \n" +
				"?parentTMap "+r2rml+":"+tableName.getLocalName() + " ?parentTable .  \n"+				
				//"OPTIONAL { ?pMap "+r2rml+":"+column.getLocalName() + " ?predicateColumn . } \n"+												
				//"OPTIONAL { ?poMap "+r2rml+":"+graphColumn.getLocalName() + " ?graphColumn . } \n"+				
				"OPTIONAL { ?parentSubjMap "+r2rml+":"+column.getLocalName() + " ?parentSubjCol . } \n"+				
				"OPTIONAL { ?parentSubjMap "+r2rml+":"+template.getLocalName() + " ?parentSubjTemplate . } \n"+				
				"OPTIONAL { ?parentSubjMap "+r2rml+":"+subject.getLocalName() + " ?parentSubject . } \n"+				
				"?parentSubjMap "+r2rml+":"+classProperty.getLocalName() + " ?parentSubjClass .  \n"+
				//"OPTIONAL { ?poMap "+r2rml+":"+graph.getLocalName() + " ?graph . } \n"+				
				//"OPTIONAL { ?oMap "+r2rml+":"+column.getLocalName() + " ?column . } \n"+				
				//"OPTIONAL { ?oMap "+r2rml+":"+datatype.getLocalName() + " ?datatype . } \n"+				
				//"?tMap "+r2rml+":"+sqlQuery.getLocalName()+ " ?query ; \n"+
				//"OPTIONAL { ?subjMap "+r2rml+":"+rrGraph.getLocalName() + " ?subjGraph . } \n"+				
				//"OPTIONAL { ?subjMap "+r2rml+":"+rrGraphColumn.getLocalName() + " ?subjGraphCol . } \n"+				
				//"OPTIONAL { ?subjMap "+r2rml+":"+inverseExpression.getLocalName() + " ?subjInverse . } "+				
								

				"}";
		
		logger.debug("Query propertyObjectMap: "+queryString);
		Query query = QueryFactory.create(queryString );
		QueryExecution exe = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet res = null;
		try{
			res = exe.execSelect();			
		}
		catch (ResultSetException e)
		{
			logger.debug("No results found.");
			return col;
		}

		while (res.hasNext())
		{
			QuerySolution soln = res.nextSolution();
		      //Resource predicate = ResourceFactory.createResource(uri);//  soln.getResource("predicate");
		      Resource typeProperty = soln.getResource("typeProperty");
		      Literal column = soln.getLiteral("column"); 
		      Resource datatype = soln.getResource("datatype");		      
		      RDFNode object = soln.get("object");		     		  
		      Literal propColumn = soln.getLiteral("predicateColumn");
		      Literal graphColumn = soln.getLiteral("graphColumn");
		      Resource graph = soln.getResource("graph");
		      Resource parentTMap = soln.getResource("parentTMap");
		      
		      RefPredicateObjectMap poMap = new RefPredicateObjectMap();
		      RefPredicateMap pMap = new RefPredicateMap();
		      RefObjectMap oMap = new RefObjectMap();
		      //if (predicate!=null)
		      pMap.setPredicate(model.createProperty(uri));
		      //if (propColumn!=null)
		    //	  pMap.setColumn(propColumn.getString());
		      /*
		      if (column!=null)
		    	  oMap.setColumn(column.getString());
		      if (datatype!=null)		    		    
		    	  oMap.setDatatype(getDatatype(datatype.getURI()));
		      if (object != null)
		    	  oMap.setObject(object);*/
		      if (graphColumn!=null)
		    	  poMap.setGraphColumn(graphColumn.getString());
		      if (graph!=null)
		    	  poMap.setGraph(graph.getURI());
		      TriplesMap parentTriplesMap = new TriplesMap(parentTMap.getURI());

		      
		      oMap.setParentTriplesMap(parentTriplesMap);
		      //logger.debug(oMap.getColumn()+"-"+pMap.getPredicate()+"-"+oMap.getDatatype());
		      poMap.setRefPredicateMap(pMap);
		      poMap.setRefObjectMap(oMap);
		      
		      Resource tMapResource = soln.getResource("tMap");
		      Resource subjClass = soln.getResource("subjClass"); 
		      Literal sqlQuery = soln.getLiteral("query");
		      //Resource subjGraph = soln.getResource("subjGraph");
		      //Literal subjGraphCol = soln.getLiteral("subjGraphCol");
		      Literal inverse = soln.getLiteral("subjInverse");
		      Literal subjColumn = soln.getLiteral("subjCol");
		      Literal subjColumnOperation = soln.getLiteral("subjColOp");
		      Literal termType = soln.getLiteral("subjType");
		      RDFNode subject = soln.getResource("subject");
		      Literal table = soln.getLiteral("table");
		      Literal subjTemplate = soln.getLiteral("subjTemplate");

		      Literal parentTable = soln.getLiteral("table");
		      Resource parentSubjClass = soln.getResource("subjClass"); 
		      Literal parentSubjColumn = soln.getLiteral("subjCol");
		      RDFNode parentSubject = soln.getResource("subject");
		      Literal parentSubjTemplate = soln.getLiteral("subjTemplate");

		      
		      parentTriplesMap.setTableName(parentTable.getString());
		      SubjectMap parentSubjectMap = new SubjectMap();
			parentTriplesMap.setSubjectMap(parentSubjectMap );
		      if (parentSubjColumn != null)
		    	  parentSubjectMap.setColumn(parentSubjColumn.getString());
		      if (parentSubjTemplate != null)
		    	  parentSubjectMap.setTemplate(parentSubjTemplate.getString());
		      if (parentSubject != null)
		    	  parentSubjectMap.setSubject(parentSubject);
		      parentSubjectMap.setRdfsClass(parentSubjClass);
		      
		      TriplesMap tMap = new TriplesMap(tMapResource.getURI());
		      SubjectMap subjectMap = new SubjectMap();
		      subjectMap.setRdfsClass(subjClass);
		      if (sqlQuery != null)
		    	  tMap.setSqlQuery(sqlQuery.getString());
		      if (table!=null)
		    	  tMap.setTableName(table.getString());
		      //if (subjGraph!=null)
		      //	  subjectMap.addGraph(subjGraph.getURI());
		      //else if (subjGraphCol!= null)
		      //	  subjectMap.addGraphColumn(subjGraphCol.getString());
		      if (subjColumnOperation != null)
		    	  subjectMap.setColumnOperation(subjColumnOperation.getString());
		      if (subjTemplate != null)
		    	  subjectMap.setTemplate(subjTemplate.getString());
		      if (subjColumn!=null)
		    	  subjectMap.setColumn(subjColumn.getString());
		      if (inverse != null)
		    	  subjectMap.setInverseExpression(inverse.getString());
		      if (subject != null)
		    	  subjectMap.setSubject(subject);
		      subjectMap.setTriplesMap(tMap);
		      tMap.setSubjectMap(subjectMap );

		      
		      
		      poMap.setTriplesMap(tMap);
		      col.add(poMap);
		      //tMap.addPropertyObjectMap(poMap);

		}

		return col;
	}

	
	public Collection<RefPredicateObjectMap> getRefPredicateObjectMapForUri(String uri)
	{
		Collection<RefPredicateObjectMap> col = new ArrayList<RefPredicateObjectMap>();
		if (endpoint!=null)
			return getRefPOMapForUri(uri);
		
		
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

	
	private Map<String,String> uriSpaces;
	
	private Map<String,String> links;
	
	private void loadLinks()
	{
		String queryString ="PREFIX void: <http://rdfs.org/ns/void#> " +
		" SELECT ?target ?dump ?sparql ?uriSpace ?pred " +
		" WHERE { \n" +
		//" ?links  void:subjectsTarget <"+mappinguri+">; \n"+
		" ?links  void:objectsTarget ?target; \n"+
		"         void:linkPredicate ?pred. \n"+
		" ?target void:dataDump ?dump; \n"+
		"		  void:uriSpace	?uriSpace; \n "+
		"         void:sparqlEndpoint ?sparql. \n"+
		"} ";

		logger.debug("Executing query: "+queryString);
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString) ;
		InputStream is = R2RModel.class.getClassLoader().getResourceAsStream("mappings/links.ttl");
		Model m = ModelFactory.createDefaultModel().read(is,null,"TTL");
		QueryExecution qexec = QueryExecutionFactory.create(query,m );
		links = new HashMap<String,String>();
		uriSpaces = new HashMap<String,String>();
		String dumpUri = null;
		Resource sparqlEndpoint = null;
		Literal uriSpace = null;
		ResultSet rs = qexec.execSelect();
		while (rs.hasNext())
		{
			QuerySolution qs = rs.nextSolution();
			qs.getResource("target");
			dumpUri = qs.getResource("dump").getURI();
			sparqlEndpoint = qs.getResource("sparql");
			uriSpace = qs.getLiteral("uriSpace");
			Resource pred = qs.getResource("pred");
			links.put(pred.getURI(), sparqlEndpoint.getURI());
			uriSpaces.put(uriSpace.getString(), sparqlEndpoint.getURI());
		}			
	}
	
	
	
	private class QueryAbs
	{
		
		public Set<String> params;
		public Set<String> extParams;
		public List<Triple> triples;
		public List<Triple> extTriples;
		
	}
	
	
	private QueryAbs collect(Op op)
	{
		if (op instanceof OpBGP)
		{
			QueryAbs abs = new QueryAbs();
			abs.extParams = new HashSet<String>();
			abs.extTriples = new ArrayList<Triple>();
			Set<String> vars = new HashSet<String>();
			List<Triple> triples = new ArrayList<Triple>();
			OpBGP bgp = (OpBGP)op;
			Set<String> refs = new HashSet<String>();
			Set<String> objs = new HashSet<String>();
			Set<String> subjs = new HashSet<String>();
			for (Triple t:bgp.getPattern().getList())
			{
				if (links.containsKey(t.getPredicate().getURI()))
					continue;
				if (t.getObject().isVariable())
				{
					objs.add(t.getObject().getName());
					if (subjs.contains(t.getObject().getName()))
						refs.add(t.getObject().getName());
				}
				if (objs.contains(t.getSubject().getName()))
					refs.add(t.getSubject().getName());
			}
			for (Triple t:bgp.getPattern().getList())
			{
				String subjName = t.getSubject().getName();
				if (t.getPredicate().getURI().equals(RDF.type.getURI()))
				{
					boolean cont = false;
					for (String ur :uriSpaces.keySet())
					{					
						if (t.getObject().getURI().contains(ur))
						{
							cont=true;
							abs.extParams.add(subjName);
							abs.extTriples.add(t);
						}
					}
					if (cont)
						continue;
						
						
					Var sMap = Var.alloc(t.getSubject().getName()+"Smap");
					Var tMap = Var.alloc(t.getSubject().getName()+"Tmap");
					vars.add(tMap.getName());
					Triple tMapTrip = new Triple(sMap,R2RML.classProperty.asNode(),t.getObject());
					Triple sMapTrip = new Triple(tMap,R2RML.subjectMap.asNode(),sMap);
					triples.add(tMapTrip);
					triples.add(sMapTrip);										
				}
				else
				{
					if (links.containsKey(t.getPredicate().getURI()))
					{
						abs.extParams.add(subjName);
						if (t.getObject().isVariable())
							abs.extParams.add(t.getObject().getName());
						abs.extTriples.add(t);
						continue;
					}
						
					Var tMap = Var.alloc(t.getSubject().getName()+"Tmap");
					Var poMap = Var.alloc(t.getSubject().getName()+t.getPredicate().getLocalName()+"POmap");
					Var oMap = Var.alloc(t.getSubject().getName()+t.getPredicate().getLocalName()+"Omap");
					vars.add(tMap.getName());
					
					if (refs.contains(t.getObject().getName()))
					{
						Var pMap = Var.alloc(t.getSubject().getName()+t.getPredicate().getLocalName()+"PmapRef");
						triples.add(new Triple(tMap,R2RML.refPredicateObjectMap.asNode(),poMap));
						triples.add(new Triple(poMap,R2RML.refPredicateMap.asNode(),pMap));
						triples.add(new Triple(poMap,R2RML.refObjectMap.asNode(),oMap));
						triples.add(new Triple(pMap,R2RML.predicate.asNode(),t.getPredicate()));
						Var obj = Var.alloc(t.getObject().getName()+"Tmap");
						vars.add(obj.getName());
						triples.add(new Triple(oMap,R2RML.parentTriplesMap.asNode(),obj));
					}
					else
					{
						Var pMap = Var.alloc(t.getSubject().getName()+t.getPredicate().getLocalName()+"Pmap");
						triples.add(new Triple(tMap,R2RML.predicateObjectMap.asNode(),poMap));
					triples.add(new Triple(poMap,R2RML.predicateMap.asNode(),pMap));
					triples.add(new Triple(poMap,R2RML.objectMap.asNode(),oMap));
					triples.add(new Triple(pMap,R2RML.predicate.asNode(),t.getPredicate()));
					Var obj = Var.alloc(t.getObject().getName());
					vars.add(obj.getName());
					//triples.add(new Triple(oMap,R2RML.object.asNode(),obj));
					triples.add(new Triple(oMap,Var.alloc(subjName+"Pred"),obj));
					}
					
									
				}
				
			}
			abs.params = vars;
			abs.triples = triples;
			return abs;
		}
		else if (op instanceof OpProject)
		{
			OpProject proj = (OpProject)op;
			return collect(proj.getSubOp());
		}
		else if (op instanceof OpFilter)
		{
			OpFilter filter = (OpFilter)op;
			return collect(filter.getSubOp());
		}
		return null;
		
	}
	
	public void getTriplesMapForQuery(String query)
	{
		Query q = QueryFactory.create(query);
		
		Op op = Algebra.compile(q);
		
		QueryAbs ts =collect(op);
		
		String extSparql = "SELECT ";
		for (String var:ts.extParams)
		{
			extSparql+="?"+var + " ";
		}
		extSparql+="\n WHERE { ";
		for (Triple tr:ts.extTriples)
		{
			String obj = tr.getObject().isURI()?					
					"<"+tr.getObject().toString()+">":tr.getObject().toString();					
					extSparql+=tr.getSubject()+ " <"+tr.getPredicate()+ "> "+obj+ " . \n"; 
		}
		extSparql+="}";
		logger.debug(extSparql);
		
		String sparql ="SELECT DISTINCT ";
		for (String var:ts.params)
		{
			sparql+="?"+var + " ";
		}
		for (String var:ts.extParams)
		{
			sparql+="?"+var + " ";
		}
		sparql+="\n WHERE { ";
		for (Triple tr:ts.triples)
		{
			if (tr.getPredicate().isURI())
				if (tr.getPredicate().getURI().equals(R2RML.refPredicateMap.getURI()) ||
					tr.getPredicate().getURI().equals(R2RML.predicateMap.getURI()) ||
					tr.getPredicate().getURI().equals(R2RML.predicate.getURI()) ||
					tr.getSubject().getName().endsWith("PmapRef"))
				continue;
			String obj = tr.getObject().isURI()?					
					"<"+tr.getObject().toString()+">":tr.getObject().toString();
			String pred = tr.getPredicate().isURI()?
					" <"+tr.getPredicate().toString()+"> ":tr.getPredicate().toString();
			sparql+=tr.getSubject()+ pred+obj+ " . \n"; 
		}
		for (Triple tr:ts.extTriples)
		{
			String obj = tr.getObject().isURI()?					
					"<"+tr.getObject().toString()+">":tr.getObject().toString();					
			sparql+=tr.getSubject()+ " <"+tr.getPredicate()+ "> "+obj+ " . \n"; 
		}
		sparql+="}";
		logger.debug(sparql);

		Query qext = QueryFactory.create(sparql);
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, qext);
		/*
		 ResultSet rs = qe.execSelect();
		 while (rs.hasNext())
		 {
			 QuerySolution sol = rs.nextSolution();
			 
		 }*/
		

	}
	
	
	
	
	
}
