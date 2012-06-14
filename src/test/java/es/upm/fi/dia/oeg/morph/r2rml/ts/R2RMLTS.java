package es.upm.fi.dia.oeg.morph.r2rml.ts;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

import es.upm.fi.dia.oeg.morph.Morph;
import es.upm.fi.dia.oeg.morph.R2RProcessor;

public class R2RMLTS {
	private static Logger logger = Logger.getLogger(R2RMLTS.class.getName());
	
	private static void runMorph(String testDirectory, String propertiesFileName) {
		String currentDirectory = System.getProperty("user.dir");
		testDirectory = "src/test/resources/mappings/r2rmlts/" + testDirectory;
		String propertiesFilePath = currentDirectory + "/" + testDirectory + "/" + propertiesFileName;

		Morph morph = new Morph();
		try {
				Properties props = morph.buildConfiguration(propertiesFilePath);
				R2RProcessor r2rprocessor = new R2RProcessor();
				r2rprocessor.configure(props);
				Dataset dataset = r2rprocessor.transform();
			
				//temporary code
				Model m = dataset.getDefaultModel();
				logger.info("Write default graph");
				m.write(System.out, "TURTLE");
				Iterator<String> names = dataset.listNames();
				while (names.hasNext())
				{
					String uri = names.next();
					logger.info("Write graph: "+uri );
					Model nm = dataset.getNamedModel(uri);
					nm.write(System.out,"TURTLE");
				}	
		}
		catch(Exception ex) {
			logger.error("Error during the configuration ", ex);
		}		
	}
	
	@Test
	public void testR2RMLTC0000() {
		String testDirectory = "D000-1table1column0rows";
		String propertiesFileName = "R2RMLTC0000.morph.properties";
		R2RMLTS.runMorph(testDirectory, propertiesFileName);
	}

	@Test
	public void testR2RMLTC0001a() throws Exception {
		String testDirectory = "D001-1table1column1row";
		String propertiesFileName = "R2RMLTC0001a.morph.properties";
		R2RMLTS.runMorph(testDirectory, propertiesFileName);
	}
	
	@Test
	public void testR2RMLTC0001b() throws Exception {
		String directoryName = "D001-1table1column1row";
		String testName = "R2RMLTC0001b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002a() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002b() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002c() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002d() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002d";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002e() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002e";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002f() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002f";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002g() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002g";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002h() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002h";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002i() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002i";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0002j() throws Exception {
		String directoryName = "D002-1table2columns1row";
		String testName = "R2RMLTC0002j";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0003a() throws Exception {
		String directoryName = "D003-1table3columns1row";
		String testName = "R2RMLTC0003a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0003b() throws Exception {
		String directoryName = "D003-1table3columns1row";
		String testName = "R2RMLTC0003b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0003c() throws Exception {
		String directoryName = "D003-1table3columns1row";
		String testName = "R2RMLTC0003c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0004a() throws Exception {
		String directoryName = "D004-1table2columns1row";
		String testName = "R2RMLTC0004a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0004b() throws Exception {
		String directoryName = "D004-1table2columns1row";
		String testName = "R2RMLTC0004b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
}
