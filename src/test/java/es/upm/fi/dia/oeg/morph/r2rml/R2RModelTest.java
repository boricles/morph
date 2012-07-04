package es.upm.fi.dia.oeg.morph.r2rml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;

public class R2RModelTest
{

	private static Logger logger = Logger.getLogger(R2RModelTest.class.getName());

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@Test
	public void testR2RModel()
	{
		R2RModel m = new  R2RModel();
		assertNotNull(m);
	}

	@Test
	public void testClose()
	{
		R2RModel m = new R2RModel();
		m.close();
		
	}

	@Test
	public void testReadURI() throws InvalidR2RDocumentException, InvalidR2RLocationException, URISyntaxException
	{
		R2RModel m = new R2RModel();
		m.read(new URI("mappings/example8.r2r"));
		assertNotNull(m.getTriplesMap());
		assertEquals(3, m.getTriplesMap().size());
		TriplesMap t = m.getTriplesMap("http://es.upm.fi.dia.oeg/R2RMapping#MilfordWaveObservation");
		assertNotNull(t);
		logger.debug("graphs "+t.getSubjectMap().getGraphSet());
		assertEquals(3, t.getSubjectMap().getGraphSet().size());
		assertEquals(2,t.getSubjectMap().getGraphColumnSet().size());		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWriteRelativeUri() throws InvalidR2RDocumentException, InvalidR2RLocationException, URISyntaxException, IOException
	{
		R2RModel m = new R2RModel();
		m.read(new URI("mappings/example8.r2r"));
		m.write(new URI("mappings/export.r2r"));
	}

	@Test
	public void testGetTriplesMap()
	{
		R2RModel m = new R2RModel();
		assertNull(m.getTriplesMap());
		//m.read(new URI("mappings/example8.r2r"));
		
	}

	@Test
	public void testGetTriplesMapForUri() throws InvalidR2RDocumentException, InvalidR2RLocationException, URISyntaxException
	{
		R2RModel m = new R2RModel();
		m.read(new URI("mappings/example8.r2r"));
		Collection<TriplesMap> tMaps = m.getTriplesMapForUri("http://semsorgrid4env.eu/ns#Observation");
		assertEquals(1, tMaps.size());
		tMaps = m.getTriplesMapForUri("http://semsorgrid4env.eu/ns#Unexistent");
		assertEquals(0, tMaps.size());
	}

	/*
	@Test
	public void testGetPredicateObjectMapForUri() throws InvalidR2RDocumentException, InvalidR2RLocationException, URISyntaxException
	{
		R2RModel m = new R2RModel();
		m.read(new URI("mappings/example8.r2r"));
		Collection<PredicateObjectMap> poMaps= m.getPredicateObjectMapForUri("http://semsorgrid4env.eu/ns#observationResult");
		assertEquals(1, poMaps.size());
	}

	@Test
	public void testGetRefPredicateObjectMapForUri() throws InvalidR2RDocumentException, InvalidR2RLocationException, URISyntaxException
	{
		R2RModel m = new R2RModel();
		m.read(new URI("mappings/example8.r2r"));
		Collection<RefPredicateObjectMap> poMaps= m.getRefPredicateObjectMapForUri("http://semsorgrid4env.eu/ns#sensorProp");
		assertEquals(1, poMaps.size());
	}
	
	@Test@Ignore
	public void testGetRemotePredicateObjectMapForUri() throws InvalidR2RDocumentException, InvalidR2RLocationException, URISyntaxException
	{
		R2RModel m = new R2RModel("http://localhost:8080/openrdf-workbench/repositories/owlimDemo/query");
		m.getPredicateObjectMapForUri("http://purl.oclc.org/NET/ssnx/ssn#observedBy");
	}
	*/
	@Test@Ignore
	public void testGetMappingsForQuery()
	{
		R2RModel m = new R2RModel("http://localhost:8080/openrdf-workbench/repositories/owlimDemo/query");
		String query ="PREFIX ssg: <http://semsorgrid4env.eu/ns#> "+							
			"PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "+
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "+
			"PREFIX cd:  <http://www.semsorgrid4env.eu/ontologies/CoastalDefences.owl#> "+
			"PREFIX dul:	<http://www.loa-cnr.it/ontologies/DUL.owl#> "+
			"PREFIX time: <http://www.w3.org/2006/time#> "+
			"PREFIX regions: <http://www.semsorgrid4env.eu/ontologies/AdditionalRegions.owl#> "+
			"PREFIX swissex: <http://swiss-experiment.ch/metadata#>  "+
			"PREFIX quantity: <http://data.nasa.gov/qudt/owl/quantity#> "+
			"PREFIX qudt: <http://data.nasa.gov/qudt/owl/qudt#> "+
			"PREFIX propPressure: <http://sweet.jpl.nasa.gov/2.1/propPressure.owl#> "+ 
			"SELECT ?temperature ?sensor ?timestamp "+
			//"FROM NAMED STREAM swissex:WannengratSensors.srdf [NOW - 90 DAY] "+ 
			"WHERE " +
			"{ "+
			"	?obs     	a ssn:Observation;"+
			"				ssn:observationResult ?result;"+
			"				ssn:observedBy ?sensor.  "+		
			"	?result		ssn:hasValue ?obsresult."+
			"	?obsresult  ssn:numericValue ?temperature."+
			"	?sensor 	ssn:observes ?prop."+
			"	?prop       a propPressure:Pressure."+	
			"	FILTER ( ?temperature > 0 )"+
			"	FILTER ( ?temperature < -10 )"+
			"}";
		m.getTriplesMapForQuery(query);
		
		

	}
}
