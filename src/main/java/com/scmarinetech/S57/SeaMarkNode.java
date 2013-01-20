package com.scmarinetech.S57;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.scmarinetech.osm.OsmNode;

public class SeaMarkNode extends OsmNode {
	
	// Unique node identification 
	long   lnam;
	long   geoHash;
	String tagsHash;
	
	static MessageDigest md = null;
	
	public SeaMarkNode( int id, double lat, double lon) throws NoSuchAlgorithmException
	{
		super(id, lat, lon );
		if ( md == null )
		{
			 md = MessageDigest.getInstance("SHA1") ; 
		}
	}

	public SeaMarkNode(OsmNode node) {
		super( node.id, node.lat, node.lon);
		tags.putAll(node.tags);
		lnam = Long.parseLong(tags.get("noaa:lnam"), 16);
		geoHash  = Long.parseLong(tags.get("noaa:geohash"), 16);
		tagsHash = tags.get("noaa:taghash");
	}

	public void addTag(String obj, String attr, String v)
	{
		tags.put("seamark:" + obj + ":" + attr , v);
	}

		
	public void closeNode(String type, long lnam)
	{
		tags.put("seamark:type" , type );
		this.lnam = lnam;
		this.geoHash = intcoord(lat) + intcoord(lon) * 1000000;
		
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

	public static boolean isSeaMarkNode(OsmNode node) {
		int count = 0;
		for ( String k : node.tags.keySet() )
		{
			if ( k.contentEquals("noaa:lnam") ) count ++; 
			if ( k.contentEquals("noaa:geohash") ) count ++; 
			if ( k.contentEquals("noaa:taghash") ) count ++; 
			if ( k.contentEquals("seamark:type") ) count ++; 
		}
		return count == 4;
	}

	public static boolean hasSeaMarksAttribures(OsmNode node) {
		for ( String k : node.tags.keySet() )
		{
			if ( k.contains("seamark:"))
				return true;
		}
		return false;
	}

	public boolean hasSameGlobalId(SeaMarkNode node) {
		return this.lnam == node.lnam;
	}

	public boolean isIdentical(SeaMarkNode node) {
		if  ( intcoord( this.lat)  != intcoord( node.lat) ) return false;  
		if  ( intcoord( this.lon)  != intcoord( node.lon) ) return false;
		
		if ( ! this.tags.equals(node.tags) ) return false;
		
		return true;
	}

	private long intcoord(double val) {
		return Math.round((val + 200) * 1e6 );
	}

	public SeaMarkNode conflateWith(SeaMarkNode original) {
		// TODO Auto-generated method stub
		return null;
	}

}
