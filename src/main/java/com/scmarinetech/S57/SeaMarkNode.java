package com.scmarinetech.S57;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class SeaMarkNode {
	
	final int id;
	final double lat;
	final double lon;
	final HashMap<String,String> tags;
	
	// Unique node identification 
	long   lnam;
	Long   geoHash;
	String tagsHash;
	
	static MessageDigest md = null;
	
	public SeaMarkNode( int id, double lat, double lon) throws NoSuchAlgorithmException
	{
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		tags = new HashMap<String, String>();
		if ( md == null )
		{
			 md = MessageDigest.getInstance("SHA1") ; 
		}
	}
	
	public void addTag(String obj, String attr, String v)
	{
		tags.put("seamark:" + obj + ":" + attr , v);
	}
		
	public void closeNode(String type, long lnam)
	{
		tags.put("seamark:type" , type );
		this.lnam = lnam;
		this.geoHash = Math.round((lat + 100) * 1e6 ) 
				       + Math.round((lon + 200) * 1e6 ) * 1000000;
		
		for( String k : tags.keySet() )
		{
			md.update(k.getBytes());
			md.update(tags.get(k).getBytes());
		}
		byte[] digest  = md.digest();
		StringBuilder sb = new StringBuilder();
		for ( byte b : digest)
			sb.append(String.format("%02x", b & 0xff));
		tagsHash = sb.toString();
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

		sb.append("<tag k='").append("noaa:lnam").append("' ");
		sb.append("v='").append(Long.toHexString(lnam)).append("' />\n");
		
		sb.append("<tag k='").append("noaa:geohash").append("' ");
		sb.append("v='").append(Long.toHexString(this.geoHash)).append("' />\n");

		sb.append("<tag k='").append("noaa:taghash").append("' ");
		sb.append("v='").append(this.tagsHash).append("' />\n");
		
		sb.append( "</node>\n");
		return sb.toString();
	}
	
}
