package es.upm.fi.dia.oeg.morph.r2rml;

public class RefObjectMap
{
	private TriplesMap parentTriplesMap;
	private String joinCondition;

	public void setParentTriplesMap(TriplesMap parentTriplesMap)
	{
		this.parentTriplesMap = parentTriplesMap;
	}

	public TriplesMap getParentTriplesMap()
	{
		return parentTriplesMap;
	}

	public void setJoinCondition(String joinCondition)
	{
		this.joinCondition = joinCondition;
	}

	public String getJoinCondition()
	{
		return joinCondition;
	}
}
