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
	
	@Test
	public void testR2RMLTC0005a() throws Exception {
		String directoryName = "D005-1table3columns3rows2duplicates";
		String testName = "R2RMLTC0005a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0006a() throws Exception {
		String directoryName = "D006-1table1primarykey1column1row";
		String testName = "R2RMLTC0006a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007a() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007b() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007c() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007d() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007d";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007e() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007e";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007f() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007f";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007g() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007g";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007h() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007h";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0007i() throws Exception {
		String directoryName = "D007-1table1primarykey2columns1row";
		String testName = "R2RMLTC0007i";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0008a() throws Exception {
		String directoryName = "D008-1table1compositeprimarykey3columns1row";
		String testName = "R2RMLTC0008a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0008b() throws Exception {
		String directoryName = "D008-1table1compositeprimarykey3columns1row";
		String testName = "R2RMLTC0008b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0008c() throws Exception {
		String directoryName = "D008-1table1compositeprimarykey3columns1row";
		String testName = "R2RMLTC0008c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0009a() throws Exception {
		String directoryName = "D009-2tables1primarykey1foreignkey";
		String testName = "R2RMLTC0009a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0009b() throws Exception {
		String directoryName = "D009-2tables1primarykey1foreignkey";
		String testName = "R2RMLTC0009b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0009c() throws Exception {
		String directoryName = "D009-2tables1primarykey1foreignkey";
		String testName = "R2RMLTC0009c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0009d() throws Exception {
		String directoryName = "D009-2tables1primarykey1foreignkey";
		String testName = "R2RMLTC0009d";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

	@Test
	public void testR2RMLTC0010a() throws Exception {
		String directoryName = "D010-1table1primarykey3colums3rows";
		String testName = "R2RMLTC0010a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0010b() throws Exception {
		String directoryName = "D010-1table1primarykey3colums3rows";
		String testName = "R2RMLTC0010b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0010c() throws Exception {
		String directoryName = "D010-1table1primarykey3colums3rows";
		String testName = "R2RMLTC0010c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0011a() throws Exception {
		String directoryName = "D011-M2MRelations";
		String testName = "R2RMLTC0011a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0011b() throws Exception {
		String directoryName = "D011-M2MRelations";
		String testName = "R2RMLTC0011b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0012a() throws Exception {
		String directoryName = "D012-2tables2duplicates0nulls";
		String testName = "R2RMLTC0012a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0012b() throws Exception {
		String directoryName = "D012-2tables2duplicates0nulls";
		String testName = "R2RMLTC0012b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0012c() throws Exception {
		String directoryName = "D012-2tables2duplicates0nulls";
		String testName = "R2RMLTC0012c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0012d() throws Exception {
		String directoryName = "D012-2tables2duplicates0nulls";
		String testName = "R2RMLTC0012d";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0013a() throws Exception {
		String directoryName = "D013-1table1primarykey3columns2rows1nullvalue";
		String testName = "R2RMLTC0013a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

	@Test
	public void testR2RMLTC0014a() throws Exception {
		String directoryName = "D014-3tables1primarykey1foreignkey";
		String testName = "R2RMLTC0014a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0014b() throws Exception {
		String directoryName = "D014-3tables1primarykey1foreignkey";
		String testName = "R2RMLTC0014b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0014c() throws Exception {
		String directoryName = "D014-3tables1primarykey1foreignkey";
		String testName = "R2RMLTC0014c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0014d() throws Exception {
		String directoryName = "D014-3tables1primarykey1foreignkey";
		String testName = "R2RMLTC0014d";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

	@Test
	public void testR2RMLTC0015a() throws Exception {
		String directoryName = "D015-1table3columns1composityeprimarykey3rows2languages";
		String testName = "R2RMLTC0015a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0015b() throws Exception {
		String directoryName = "D015-1table3columns1composityeprimarykey3rows2languages";
		String testName = "R2RMLTC0015b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

	@Test
	public void testR2RMLTC0016a() throws Exception {
		String directoryName = "D016-1table1primarykey10columns3rowsSQLdatatypes";
		String testName = "R2RMLTC0016a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

	@Test
	public void testR2RMLTC0016b() throws Exception {
		String directoryName = "D016-1table1primarykey10columns3rowsSQLdatatypes";
		String testName = "R2RMLTC0016b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

	@Test
	public void testR2RMLTC0016c() throws Exception {
		String directoryName = "D016-1table1primarykey10columns3rowsSQLdatatypes";
		String testName = "R2RMLTC0016c";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0016d() throws Exception {
		String directoryName = "D016-1table1primarykey10columns3rowsSQLdatatypes";
		String testName = "R2RMLTC0016d";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

	@Test
	public void testR2RMLTC0016e() throws Exception {
		String directoryName = "D016-1table1primarykey10columns3rowsSQLdatatypes";
		String testName = "R2RMLTC0016e";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

	@Test
	public void testR2RMLTC0018a() throws Exception {
		String directoryName = "D018-1table1primarykey2columns3rows";
		String testName = "R2RMLTC0018a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0019a() throws Exception {
		String directoryName = "D019-1table1primarykey3columns3rows";
		String testName = "R2RMLTC0019a";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}
	
	@Test
	public void testR2RMLTC0019b() throws Exception {
		String directoryName = "D019-1table1primarykey3columns3rows";
		String testName = "R2RMLTC0019b";
		String configurationFile = testName + ".morph.properties";
		R2RMLTS.runMorph(directoryName, configurationFile);
	}

}
