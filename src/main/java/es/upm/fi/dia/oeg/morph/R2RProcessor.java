package es.upm.fi.dia.oeg.morph;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Observable;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.RDFDatatype;

import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import es.upm.fi.dia.oeg.morph.r2rml.InvalidPropertyMapException;
import es.upm.fi.dia.oeg.morph.r2rml.InvalidR2RDocumentException;
import es.upm.fi.dia.oeg.morph.r2rml.InvalidR2RLocationException;
import es.upm.fi.dia.oeg.morph.r2rml.R2RModel;
import es.upm.fi.dia.oeg.morph.r2rml.PredicateObjectMap;
import es.upm.fi.dia.oeg.morph.r2rml.TriplesMap;
import es.upm.fi.dia.oeg.morph.relational.RelationalModel;
import es.upm.fi.dia.oeg.morph.relational.RelationalModelException;


public class R2RProcessor extends Observable
{
	public static final String R2R_MAPPING_URL = "r2r.mapping.url";
	public static final String R2R_RELATIONAL_MODEL_CLASS = "r2r.relationalmodel.class";
	private static Logger logger = Logger.getLogger(R2RProcessor.class.getName());

	private boolean configured = false;
	public R2RModel model;
	public RelationalModel relational;
	
	public R2RProcessor()
	{
	}
	
	private boolean isConfigured()
	{
		return configured;
	}

	private void configureRelational(Properties props) throws R2RProcessorConfigurationException
	{
		Class<?> relationalClass;
		try
		{
			//relationalClass =  Class.forName(props.getProperty(R2R_RELATIONAL_MODEL_CLASS));
			relationalClass = Class.forName("es.upm.fi.dia.oeg.morph.relational.JDBCRelationalModel");
		} catch (ClassNotFoundException e2)
		{
			e2.printStackTrace();// TODO remove this
			String msg = "Unable to instantiate Relational Model: "+e2.getMessage();
			logger.error(msg);
			throw new R2RProcessorConfigurationException(msg);
		}
		try
		{
			relational = (RelationalModel)relationalClass.newInstance();
		} catch (InstantiationException e2)
		{		
			String msg = "Unable to instantiate Relational Model: "+e2.getMessage();
			logger.error(msg);
			throw new R2RProcessorConfigurationException(msg);
		
		} catch (IllegalAccessException e2)
		{
			String msg = "Unable to instantiate Relational Model: "+e2.getMessage();
			logger.error(msg);
			throw new R2RProcessorConfigurationException(msg);		
		}
		
		try
		{
			relational.configure(props);
		} catch (InstantiationException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
	}
	
	public void configure(Properties props) throws R2RProcessorConfigurationException, InvalidR2RDocumentException, InvalidR2RLocationException 
	{
		configureRelational(props);
		
		URI mapping;
		try
		{
			mapping = new URI(props.getProperty(R2R_MAPPING_URL));
		} catch (URISyntaxException e1)
		{
			throw new IllegalArgumentException("Invalid Mapping URI: "+e1.getMessage(),e1);
		}
		logger.debug("Mapping file: "+mapping);
		if (model!=null)
			model.close();
		model = new R2RModel();

		model.read(mapping);
		configured = true;
	}
	
	public Dataset transform() throws InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException
	{
		if (!isConfigured())
			throw new R2RProcessorConfigurationException("Processor not configured.");
		Model m = ModelFactory.createDefaultModel();
		DataSource d = DatasetFactory.create(m); 
		
		
		for (TriplesMap tMap : model.getTriplesMap())
		{
			DataSource newD = execute(d,tMap);
			merge(d,newD);				 
		}
		
		return d;
		
	}

	private static void merge(DataSource targetDs, Dataset addedDs)
	{
		targetDs.getDefaultModel().add(addedDs.getDefaultModel());
		Iterator<String> names = addedDs.listNames();
		while (names.hasNext())
		{
			String uri = names.next();
			if (targetDs.containsNamedModel(uri))
			{
				targetDs.getNamedModel(uri).add(addedDs.getNamedModel(uri));
			}
			else 
			{
				targetDs.addNamedModel(uri, addedDs.getNamedModel(uri));
			}
		}
	}
	
	private static void serialize(Dataset d)//TODO move this outside
	{		
		Model m = d.getDefaultModel();
		logger.info("Write default graph");
		m.write(System.out, "TURTLE");
		Iterator<String> names = d.listNames();
		while (names.hasNext())
		{
			String uri = names.next();
			logger.info("Write graph: "+uri );
			Model nm = d.getNamedModel(uri);
			nm.write(System.out,"TURTLE");
		}	
	}
	
	public void generate() throws InvalidPropertyMapException, R2RProcessorConfigurationException, RelationalModelException
	{
		serialize(transform());
	}
	public DataSource execute(DataSource d,TriplesMap tMap) throws InvalidPropertyMapException, RelationalModelException
	{
		try
		{
			String query = tMap.getSqlQuery();
			logger.debug("Query to execute: "+query);
			ResultSet rs = relational.query(query);
			while (rs.next())
			{
				TriplesMapInstance tIns = new TriplesMapInstance(tMap);
				tIns.setResultSet(rs);// TODO this is awful
				tIns.setDataSource(d);
		
				Resource subj = tIns.getGeneratedSubject();
				Model tMapModel = getModel(d, tIns.getGeneratedGraphUri());
				
				if (tIns.getGeneratedRdfType() !=null)
					tMapModel.add(subj,RDF.type,tIns.getGeneratedRdfType());
								
				
				for (PredicateObjectMap prop:tMap.getPropertyObjectMaps())
				{
					PredicateObjectMapInstance propIns = new PredicateObjectMapInstance(prop,tIns);
					propIns.setResultSet(rs); //TODO this is awful
					
					Model pModel = getModel(d,propIns.getGeneratedGraphUri());
							
					String column = prop.getObjectMap().getColumn();
					RDFDatatype datatype = prop.getObjectMap().getDatatype();
					logger.info("bigbig"+propIns.getGeneratedProperty());
					if (column!=null)
					{
						pModel.add(subj,propIns.getGeneratedProperty(),rs.getString(column),datatype);
					
					}
					else				
						pModel.add(subj,propIns.getGeneratedProperty(),prop.getObjectMap().getObject());
				}
			}
			rs.close();
			
		} catch (SQLException e)
		{
			String msg = "Exception accessing relational model: "+e.getMessage();
			logger.error(msg);
			throw new RelationalModelException(msg);			
		}
		
		return d;
	}
	
	private static Model getModel(DataSource d,String uri) 
	{				
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
	
	
	
}
