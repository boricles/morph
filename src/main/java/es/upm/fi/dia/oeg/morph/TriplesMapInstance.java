package es.upm.fi.dia.oeg.morph;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import es.upm.fi.dia.oeg.morph.r2rml.R2RModel;
import es.upm.fi.dia.oeg.morph.r2rml.TriplesMap;

public class TriplesMapInstance
{
	private TriplesMap tMap;

	private ResultSet rs;
	private DataSource d;
	
	private String generatedGraphUri;
	private Resource generatedRdfType;
	private Resource generatedSubject;
	
	private static Logger logger = Logger.getLogger(TriplesMapInstance.class.getName());

	public TriplesMapInstance(TriplesMap t)
	{
		this.tMap = t;
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
		if (!tMap.getSubjectMap().getGraphSet().isEmpty())
			uri = tMap.getSubjectMap().getGraphSet().iterator().next();
		else if (!tMap.getSubjectMap().getGraphColumnSet().isEmpty())
		{
			try
			{
				uri = rs.getString(tMap.getSubjectMap().getGraphColumnSet().iterator().next());
			} catch (SQLException e)
			{
				logger.error("Unable to generate Uri: "+e.getMessage());
				e.printStackTrace();
			}
		}
		else 
			uri = R2RModel.DEFAULT_GRAPH;
		this.generatedGraphUri = uri;
	}
	
	public Resource getGeneratedRdfType()
	{
		if (this.generatedRdfType==null)
			generateRdfType();
		return generatedRdfType;
	}
	private void generateRdfType()
	{
		if (tMap.getSubjectMap().getRdfsClass()!=null)
			this.generatedRdfType=tMap.getSubjectMap().getRdfsClass();
		
	}
	public Resource getGeneratedSubject()
	{
		if (this.generatedSubject==null)
			generateSubject();
		return generatedSubject;
	}
	
	private void generateSubject()
	{
		String ttype=tMap.getSubjectMap().getTermType();
		if (ttype!=null)
			this.generatedSubject=getModel().createResource(new AnonId());
		else{
		String col ="";
		if (tMap.getSubjectMap().getColumn()!=null)
			col=tMap.getSubjectMap().getColumn();
		else{
			int i=tMap.getSubjectMap().getTemplate().indexOf('{');
			int j=tMap.getSubjectMap().getTemplate().indexOf('}');
			
			col=tMap.getSubjectMap().getTemplate().substring(i+1, j);
		}
			
		String id = null;
		try
		{
			id = rs.getString(col);
		} catch (SQLException e)
		{
			logger.error("Can't generate subject: "+e.getMessage());
			e.printStackTrace();
		}
		logger.debug("Identifier for uri: "+id);
		Resource subj = getModel().createResource(id);
		this.generatedSubject = subj;}
	}
	
	private Model getModel()
	{
		String uri = getGeneratedGraphUri();
		
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
	public void setResultSet(ResultSet rs)
	{
		this.rs = rs;
		
	}
	public void setDataSource(DataSource ds)
	{
		this.d=ds;
	}
	public DataSource getDataSource()
	{
		return d;
	}

}
