package es.upm.fi.dia.oeg.morph.r2rml;

import com.hp.hpl.jena.rdf.model.Property;

public class RefPredicateMap
{
	private Property predicate;

	public void setPredicate(Property predicate)
	{
		this.predicate = predicate;
	}

	public Property getPredicate()
	{
		return predicate;
	}
}
