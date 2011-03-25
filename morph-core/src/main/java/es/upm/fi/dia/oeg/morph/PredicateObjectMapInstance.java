package es.upm.fi.dia.oeg.morph;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

import es.upm.fi.dia.oeg.morph.r2rml.R2RModel;
import es.upm.fi.dia.oeg.morph.r2rml.PredicateObjectMap;

public class PredicateObjectMapInstance
{
	private PredicateObjectMap termMap;
	
	private Property generatedProperty;
	private String generatedGraphUri;
	
	private TriplesMapInstance parent;
	
	private ResultSet rs;

	private static Logger logger = Logger.getLogger(PredicateObjectMapInstance.class.getName());

	public PredicateObjectMapInstance(PredicateObjectMap propMap, TriplesMapInstance tIns)
	{
		this.termMap = propMap;
		this.parent = tIns;
	}
	
	public String getGeneratedGraphUri()
	{
		if (this.generatedGraphUri==null)
			generateGraphUri();			
		return this.generatedGraphUri;
	}
	
	private void generateGraphUri() 
	{
		String uri = null;
		if (termMap.getGraph()!=null)
			uri = termMap.getGraph();
		else if (termMap.getGraphColumn()!=null)
		{
			try
			{
				uri = rs.getString(termMap.getGraphColumn());
			} catch (SQLException e)
			{			
				e.printStackTrace();//TODO exception handling
				logger.error("Error generating Uri: "+e.getMessage());
			}
		}
		else if (parent.getGeneratedGraphUri()!=null)
			uri = parent.getGeneratedGraphUri();
		else
			uri = R2RModel.DEFAULT_GRAPH;
		this.generatedGraphUri = uri;
	}

	public Property getGeneratedProperty()
	{
		if (this.generatedProperty==null)
			generateProperty();
		return this.generatedProperty;
	}
	
	private void generateProperty()
	{
		Property property = null;
		if (termMap.getPredicateMap().getPredicate()!=null)
		{
			property = termMap.getPredicateMap().getPredicate();
		}
		else if (termMap.getPredicateMap().getColumn()!=null)
		{
			String propUri;
			try
			{
				propUri = rs.getString(termMap.getPredicateMap().getColumn());
				property= getModel().createProperty(propUri);
			} catch (SQLException e)
			{
				logger.error("Can't create property: "+e.getMessage());
				e.printStackTrace();
			}
		}
		
		this.generatedProperty = property;
	}
	
	
	private Model getModel()
	{
		String uri = getGeneratedGraphUri();
		DataSource d = parent.getDataSource();
	if (!uri.equals(R2RModel.DEFAULT_GRAPH))
	{
		if (d.containsNamedModel(uri))
			return d.getNamedModel(uri);
		else
		{	
			d.addNamedModel(uri, ModelFactory.createDefaultModel());
			return d.getNamedModel(uri);
		}
	}
	else
		return d.getDefaultModel();
	}
	public void setResultSet(ResultSet rs2)
	{
		this.rs = rs2;
	}

}
