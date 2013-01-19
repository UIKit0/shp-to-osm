package com.scmarinetech.osm;

import java.util.HashMap;

public class OsmNode {
	
	final public long id;
	final public double lat;
	final public double lon;
	final public HashMap<String,String> tags;
	
	
	public OsmNode( long id, double lat, double lon) 
	{
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		tags = new HashMap<String, String>();
	}
	
	public void addTag(String key, String value) {
		tags.put(key, value);
	}
		
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( String.format("<node id='%d' version='1' visible='true'  lat='%f' lon='%f'>\n"
				,id
				,lat
				,lon) );
		
		for( String k : tags.keySet() )
		{
			sb.append("<tag k='").append(k).append("' ");
			sb.append("v='").append(tags.get(k)).append("' />\n");
		}
		
		sb.append( "</node>\n");
		return sb.toString();
	}

	
}
