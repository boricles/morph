package es.upm.fi.dia.oeg.morph;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class Morph 
{
	protected static final String uri="http://es.upm.fi.dia.oeg/morph#";
	
	public static final Property uniqueIndex = property("uniqueIndex");

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
	
	public static void main(String []args) {
		System.out.println("Running");
	}

}
