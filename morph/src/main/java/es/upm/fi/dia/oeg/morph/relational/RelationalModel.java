package es.upm.fi.dia.oeg.morph.relational;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public interface RelationalModel
{
	void configure(Properties props) throws InstantiationException, IllegalAccessException, ClassNotFoundException;
	ResultSet query(String string) throws SQLException;

}
