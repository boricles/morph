package es.upm.fi.dia.oeg.morph.test;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upm.fi.dia.oeg.morph.ParameterUtils;
import es.upm.fi.dia.oeg.morph.R2RProcessor;
import es.upm.fi.dia.oeg.morph.R2RProcessorConfigurationException;
import es.upm.fi.dia.oeg.morph.r2rml.InvalidPropertyMapException;

public class R2RProcessorTest
{
	private static Properties props = null;
	private static R2RProcessor r2r = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{		
		PropertyConfigurator.configure(R2RProcessorTest.class.getClassLoader().getResource("log4j.properties"));
		props = ParameterUtils.load(R2RProcessorTest.class.getClassLoader().getResourceAsStream("config.properties"));
		r2r = new R2RProcessor();
	}

	
	@Test
	public void testConfigure() throws IOException, URISyntaxException, R2RProcessorConfigurationException
	{
		R2RProcessor r2r = new R2RProcessor();
		r2r.configure(props);	
	}

	@Test //@Ignore
	public void testGenerateEx1() throws InvalidPropertyMapException, R2RProcessorConfigurationException, IOException, URISyntaxException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example1.r2r");
		r2r.configure(props);
		r2r.generate();
	}
	
	@Test
	public void testGenerateEx2() throws IOException, URISyntaxException, InvalidPropertyMapException, R2RProcessorConfigurationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example2.r2r");
		r2r.configure(props);
		r2r.generate();
	}

	@Test
	public void testGenerateEx3() throws IOException, URISyntaxException, InvalidPropertyMapException, R2RProcessorConfigurationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example3.r2r");
		r2r.configure(props);
		r2r.generate();
	}

	@Test
	public void testGenerateEx4() throws IOException, URISyntaxException, InvalidPropertyMapException, R2RProcessorConfigurationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example4.r2r");
		r2r.configure(props);
		r2r.generate();
	}
	
}
