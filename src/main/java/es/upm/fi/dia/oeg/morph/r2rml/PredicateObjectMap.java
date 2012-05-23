package es.upm.fi.dia.oeg.morph.r2rml;



import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class PredicateObjectMap extends NodeMap
{
	private PredicateMap predicateMap;
	private ObjectMap objectMap;

	
	protected String graphColumn;
	protected String graph; //TODO should we accept multi-values?
	protected String graphTemplate;
	

	
	public PredicateObjectMap()
	{
		//this.parent = parent;
	}
	
	
	
	public String getGraphColumn()
	{
		return graphColumn;
	}

	public void setGraphColumn(String graphColumn)
	{
		this.graphColumn = graphColumn;
	}


	
	public void setGraph(String graph)
	{
		this.graph = graph;
	}

	public String getGraph()
	{
		return graph;
	}



	public void setGraphTemplate(String graphTemplate)
	{
		this.graphTemplate = graphTemplate;
	}



	public String getGraphTemplate()
	{
		return graphTemplate;
	}



	public void setPredicateMap(PredicateMap predicateMap)
	{
		this.predicateMap = predicateMap;
	}



	public PredicateMap getPredicateMap()
	{
		return predicateMap;
	}



	public void setObjectMap(ObjectMap objectMap)
	{
		this.objectMap = objectMap;
	}



	public ObjectMap getObjectMap()
	{
		return objectMap;
	}



	@Override
	public String getColumn()
	{
		return getObjectMap().getColumn();
	}



	@Override
	public RDFNode getConstant()
	{
		return getObjectMap().getObject();
	}



	@Override
	public String getColumnOperation()
	{
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String getTemplate()
	{
		return getObjectMap().getTemplate();
	}
	
}
