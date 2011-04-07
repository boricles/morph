package es.upm.fi.dia.oeg.morph.r2rml;

import com.hp.hpl.jena.rdf.model.RDFNode;

public abstract class NodeMap
{
	
	public abstract String getColumn();
	public abstract String getColumnOperation();
	public abstract String getTemplate();
	
	public abstract RDFNode getConstant();
	public void setTriplesMap(TriplesMap triplesMap)
	{
		this.triplesMap = triplesMap;
	}


	public TriplesMap getTriplesMap()
	{
		return triplesMap;
	}
	private TriplesMap triplesMap;


}
