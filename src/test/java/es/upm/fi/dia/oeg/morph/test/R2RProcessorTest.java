package es.upm.fi.dia.oeg.morph.test;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import es.upm.fi.dia.oeg.morph.ParameterUtils;
import es.upm.fi.dia.oeg.morph.R2RProcessor;
import es.upm.fi.dia.oeg.morph.R2RProcessorConfigurationException;
import es.upm.fi.dia.oeg.morph.r2rml.InvalidPropertyMapException;
import es.upm.fi.dia.oeg.morph.r2rml.InvalidR2RDocumentException;
import es.upm.fi.dia.oeg.morph.r2rml.InvalidR2RLocationException;
import es.upm.fi.dia.oeg.morph.relational.RelationalModelException;
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
		
		Connection c = DriverManager.getConnection(
		          "jdbc:hsqldb:mem:test", "SA", "");
		PreparedStatement ps;
		
		String create = 
			"CREATE SCHEMA test";
					
		ps = c.prepareStatement(create);
		ps.execute();
		
		create = 
			"CREATE TABLE test.dept ( "+
			" deptno int NOT NULL PRIMARY KEY, "+
			" dname varchar(30) DEFAULT NULL,"+
			" loc varchar(100) DEFAULT NULL"+
			" )";
					
		ps = c.prepareStatement(create);
		ps.execute();
		
		
		create = "CREATE TABLE test.emp ("+
				 " empno int NOT NULL,"+
				 " ename varchar(100) DEFAULT NULL,"+
				 " job varchar(30) DEFAULT NULL,"+
				 " deptno int DEFAULT NULL,"+
				 " etype varchar(30) DEFAULT NULL,"+
				 " PRIMARY KEY (empno) )";
		ps = c.prepareStatement(create);
		ps.execute();
		
		create = "CREATE TABLE test.likes ( "+
				  " id  varchar(4000) DEFAULT NULL, "+
				  " likeType varchar(30) DEFAULT NULL, "+
				  " likedObj varchar(100) DEFAULT NULL )";
		ps = c.prepareStatement(create);
		ps.execute();
		
		String insert = 
			"INSERT INTO test.dept VALUES "+
			"(10,'APPSERVER','NEW YORK'),"+
			"(20,'APPCONF','CHICAGO')";
		ps = c.prepareStatement(insert);
		ps.execute();
		
		insert = 
			"INSERT INTO test.emp VALUES "+
			"(7369,'SMITH','CLERK',10,'PARTTIME')";
		ps = c.prepareStatement(insert);
		ps.execute();
		
		insert = 
			"INSERT INTO test.likes VALUES "+
			"(7369,'Playing','Soccer'),"+
			"(7369,'Watching','Basketball'),";
		
		String query =
			"SELECT CONCAT(deptno,'asdasd','asdasd') AS dcon FROM test.dept";
		
		ps = c.prepareStatement(query);
		ResultSet rs = ps.executeQuery();
		while (rs.next())
		{
			System.out.println(rs.getObject("dcon"));
		}
		
		c.close();
		
	}

	
	@Test
	public void testConfigure() throws R2RProcessorConfigurationException, InvalidR2RDocumentException, InvalidR2RLocationException
	{
		R2RProcessor r2r = new R2RProcessor();
		r2r.configure(props);	
	}

	@Test 
	public void testGenerateEx1() throws InvalidPropertyMapException, R2RProcessorConfigurationException, 
	RelationalModelException, InvalidR2RDocumentException, InvalidR2RLocationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example1.r2r");
		r2r.configure(props);
		r2r.generate();
	}
	
	@Test
	public void testGenerateEx2() throws InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException,
	InvalidR2RDocumentException, InvalidR2RLocationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example2.r2r");
		r2r.configure(props);
		r2r.generate();
	}

	@Test
	public void testGenerateEx3() throws InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException, 
	InvalidR2RDocumentException, InvalidR2RLocationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example3.r2r");
		r2r.configure(props);
		r2r.generate();
	}

	@Test
	public void testGenerateEx4() throws InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException, 
	InvalidR2RDocumentException, InvalidR2RLocationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example4.r2r");
		r2r.configure(props);
		r2r.generate();
	}
	
	@Test@Ignore
	public void testGenerateEx5() throws  InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException, 
	InvalidR2RDocumentException, InvalidR2RLocationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example5.r2r");
		r2r.configure(props);
		r2r.generate();
	}
	
	@Test@Ignore
	public void testGenerateEx6() throws  InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException, 
	InvalidR2RDocumentException, InvalidR2RLocationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example6.r2r");
		r2r.configure(props);
		r2r.generate();
	}
	
	@Test
	public void testGenerateEx7() throws  InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException, 
	InvalidR2RDocumentException, InvalidR2RLocationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example7.r2r");
		r2r.configure(props);
		//r2r.generate();
	}
	
	@Test
	public void testGenerateEx8() throws  InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException, 
	InvalidR2RDocumentException, InvalidR2RLocationException
	{
		props.setProperty(R2RProcessor.R2R_MAPPING_URL, "mappings/example8.r2r");
		r2r.configure(props);
		
		//r2r.generate();
	}

	
	@Test
	public void testMappingSQL() throws  JSQLParserException
	{
		//ZqlParser p = new ZqlParser();
		String query = "Select CONCAT('example.com/emp/', empno) AS empURI "+
            ", empno"+
            ", ename"+
            ", CONCAT('example.com/emp/job/', job) AS jobTypeURI "+
            ", job "+
            ", deptno"+
            ", CONCAT('example.com/emp/etype/', etype) AS empTypeURI"+
            ", etype "+
            ", CONCAT('example.com/graph/', job ,'/' , etype) AS graphURI "+
       "from test.EMP ;";
		query = "SELECT un.p FROM (SELECT p FROM nop) un;";
		query = "SELECT CONCAT('papa',coca) AS cosa FROM temp;";
		CCJSqlParserManager p = new CCJSqlParserManager();
		Statement s = p.parse(new StringReader(query));
		
		
		
		
		//InputStream is = IOUtils.toInputStream(query);
		//p.initParser(is);
		//ZQuery zq = (ZQuery) p.readStatement();
		
		
	}
}
