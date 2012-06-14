package es.upm.fi.dia.oeg.morph;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


public class Morph 
{
	protected static final String uri="http://es.upm.fi.dia.oeg/morph#";
	
	public static final Property uniqueIndex = property("uniqueIndex");
	
	private static Logger logger = Logger.getLogger(Morph.class.getName());

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
	
	public static Properties buildConfiguration(String path) {
	    Properties properties = new Properties();
	    try {
	      properties.load(new FileInputStream(path));
	    } catch (IOException io) {
	    	logger.warn("Problems loading configuration file: {0}",io);
	    	System.exit(0);
	    }
	    return properties;
	   
	  }
	
	public static void main(String []args) {
		
		
		try {
			if (args.length==1) {  
				String propertiesFilePath = args[0];
				Properties props = buildConfiguration(propertiesFilePath);
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
			else
				logger.error("Incorrect number of arguments. Please specify the properties file location.");
		}
		catch(Exception ex) {
			logger.error("Error during the configuration ", ex);
		}
	}
}
