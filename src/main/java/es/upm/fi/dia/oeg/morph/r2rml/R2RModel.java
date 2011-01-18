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
	
	private Property rrLogicalTable = null;
    private Property rrClass = null;
    private Property rrColumn = null;
    private Property rrSubjectMap = null;
    private Property rrPropertyObjectMap = null;
    private Property rrProperty = null;
    private Property rrPropertyColumn = null;
    private Property rrRDFTypeProperty = null;
    private Property rrDatatype = null;
    private Property rrConstantValue = null;
    private Property rrTableGraphIRI = null;
    private Property rrRowGraph = null;
    private Property rrInverseExpression = null;
    private Property rrColumnGraph = null;
    private Property rrColumnGraphIRI = null;
    
	private Resource rrTriplesMap = null;

	private Collection<TriplesMap> triplesMap;
	
	public R2RModel()
	{
		
		model = ModelFactory.createDefaultModel();
		rrLogicalTable = model.createProperty(r2rmlNS+"logicalTable");
        rrClass = model.createProperty(r2rmlNS+"class");
        rrColumn = model.createProperty(r2rmlNS+"column");
        rrSubjectMap = model.createProperty(r2rmlNS+"subjectMap");
        rrPropertyObjectMap = model.createProperty(r2rmlNS+"propertyObjectMap");
        rrProperty = model.createProperty(r2rmlNS+"property");
        rrPropertyColumn = model.createProperty(r2rmlNS+"propertyColumn");
        rrRDFTypeProperty = model.createProperty(r2rmlNS+"RDFTypeProperty");
        rrDatatype = model.createProperty(r2rmlNS+"datatype");
        rrConstantValue = model.createProperty(r2rmlNS+"constantValue");
        rrTableGraphIRI = model.createProperty(r2rmlNS+"tableGraphIRI");
        rrRowGraph = model.createProperty(r2rmlNS+"rowGraph");
        rrInverseExpression = model.createProperty(r2rmlNS+"inverseExpression"); 
        rrColumnGraph = model.createProperty(r2rmlNS,"columnGraph");
        rrColumnGraphIRI = model.createProperty(r2rmlNS,"columnGraphIRI");
        
    	rrTriplesMap = model.createResource(r2rmlNS+"TriplesMap");

		
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
				"SELECT ?tMap ?class ?table ?subCol ?inverse ?tableGraph ?rowGraph WHERE { \n" +
				"?tMap a <"+rrTriplesMap.getURI()+"> ; \n" +
				r2rml+":"+rrLogicalTable.getLocalName()+ " ?table ; \n"+
				r2rml+":"+rrSubjectMap.getLocalName()+ " ?subMap . \n" +
				"OPTIONAL { ?tMap "+r2rml+":"+rrTableGraphIRI.getLocalName()+ " ?tableGraph . } \n" +
				"OPTIONAL { ?tMap "+r2rml+":"+rrRowGraph.getLocalName()+ " ?rowGraph . } \n" +
				"OPTIONAL { ?tMap "+r2rml+":"+rrClass.getLocalName()+ " ?class . } \n"+
				"?subMap "+r2rml+":"+rrColumn.getLocalName() + " ?subCol . \n"+				
				"OPTIONAL { ?subMap "+r2rml+":"+rrInverseExpression.getLocalName() + " ?inverse . } "+				
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
		      
		      Resource classRes = soln.getResource("class"); 
		      Literal tableLit = soln.get("table").asLiteral();
		      Resource tableGraph = soln.getResource("tableGraph");
		      Literal rowGraph = soln.getLiteral("rowGraph");
		      Literal inverse = soln.getLiteral("inverse");
		      Literal column = soln.getLiteral("subCol");
		      
		      TriplesMap tMap = new TriplesMap(uri);		      
		      tMap.setRdfsClass(classRes);
		      tMap.setLogicalTable(tableLit.getString());
		      if (tableGraph!=null)
		    	  tMap.setTableGraphIRI(tableGraph.getURI());
		      else if (rowGraph!= null)
		    	  tMap.setRowGraph(rowGraph.getString());
		      IRIorBlankNodeMap subjectMap = new IRIorBlankNodeMap();
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
					"SELECT ?property ?propertyColumn ?typeProperty ?column ?datatype " +
					"?constant ?columnGraph ?columnGraphIRI WHERE { \n" +
					"<"+tMap.getUri()+ "> a <"+rrTriplesMap.getURI()+"> ; \n" +
					r2rml+":"+rrPropertyObjectMap.getLocalName()+ " ?pMap . \n" +
					"OPTIONAL { ?pMap "+r2rml+":"+rrProperty.getLocalName() + " ?property . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+rrPropertyColumn.getLocalName() + " ?propertyColumn . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+rrRDFTypeProperty.getLocalName() + " ?typeProperty . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+rrColumnGraph.getLocalName() + " ?columnGraph . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+rrColumnGraphIRI.getLocalName() + " ?columnGraphIRI . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+rrColumn.getLocalName() + " ?column . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+rrDatatype.getLocalName() + " ?datatype . } \n"+				
					"OPTIONAL { ?pMap "+r2rml+":"+rrConstantValue.getLocalName() + " ?constant . } \n"+				
					"}";
		logger.debug("Query propertyObjectMap: "+queryString);
		Query query = QueryFactory.create(queryString ) ;
		  QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		  try {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      Resource property = soln.getResource("property");
		      Resource typeProperty = soln.getResource("typeProperty");
		      Literal column = soln.getLiteral("column"); 
		      Resource datatype = soln.getResource("datatype");		      
		      RDFNode constant = soln.get("constant");		     		  
		      Literal propColumn = soln.getLiteral("propertyColumn");
		      Literal columnGraph = soln.getLiteral("columnGraph");
		      Resource columnGraphIRI = soln.getResource("columnGraphIRI");
		      
		      RDFTermMap propertyMap = new RDFTermMap();
		      if (property!=null)
		    	  propertyMap.setProperty(model.createProperty(property.getURI()));
		      if (typeProperty!=null)
		    	  propertyMap.setRdfTypeProperty(model.createProperty(typeProperty.getURI()));
		      if (propColumn!=null)
		    	  propertyMap.setPropertyColumn(propColumn.getString());
		      if (column!=null)
		    	  propertyMap.setColumn(column.getString());
		      if (datatype!=null)		    		    
		    	  propertyMap.setDatatype(getDatatype(datatype.getURI()));
		      if (constant != null)
		    	  propertyMap.setConstantValue(constant);
		      if (columnGraph!=null)
		    	  propertyMap.setColumnGraph(columnGraph.getString());
		      if (columnGraphIRI!=null)
		    	  propertyMap.setColumnGraphIRI(columnGraphIRI.getURI());
		      
		      logger.debug(propertyMap.getColumn()+"-"+propertyMap.getProperty()+"-"+propertyMap.getDatatype());
		      tMap.addPropertyObjectMap(propertyMap);
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
