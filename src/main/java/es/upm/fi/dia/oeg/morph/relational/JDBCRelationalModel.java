package es.upm.fi.dia.oeg.morph.relational;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class JDBCRelationalModel implements RelationalModel
{
	private static final String JDBC_SOURCE_URL = "jdbc.source.url";
	private static final String JDBC_SOURCE_USER = "jdbc.source.user";
	private static final String JDBC_SOURCE_PASSWORD = "jdbc.source.password";
	private static final String JDBC_DRIVER = "jdbc.driver";
	
	private String sourceUrl;
	private String user;
	private String password;
	private String driver;
	
	@Override
	public void configure(Properties props) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		this.driver = props.getProperty(JDBC_DRIVER);
		Class.forName(driver).newInstance();
	    this.sourceUrl = props.getProperty(JDBC_SOURCE_URL); 
	    this.user = props.getProperty(JDBC_SOURCE_USER);
	    this.password = props.getProperty(JDBC_SOURCE_PASSWORD);
	      	      
	}
	
	private Connection getConnection() throws SQLException
	{
		Connection conn = DriverManager.getConnection(sourceUrl, user, password);
		return conn;
	}

	@Override
	public ResultSet query(String query) throws SQLException
	{
		Connection conn = getConnection();
		Statement st = conn.createStatement();
	    ResultSet rs = st.executeQuery(query);	   
		return rs;
	}
	
	
}
