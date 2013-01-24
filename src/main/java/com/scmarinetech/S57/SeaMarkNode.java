package com.scmarinetech.S57;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import com.scmarinetech.osm.OsmNode;

public class SeaMarkNode extends OsmNode {
	
	// Unique node identification 
	long   lnam;
	long   geoHash;
	String tagsHash;
	
	static MessageDigest md = null;
	
	public SeaMarkNode( long id, double lat, double lon) 
	{
		super(id, lat, lon );
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
		tags.put("noaa:lnam" , Long.toHexString(this.lnam) );
		
		this.geoHash = computeGeoHash(lat, lon);
		tags.put("noaa:geohash" , Long.toHexString( this.geoHash ) );
		
		this.tagsHash = computeTagHash();
		tags.put("noaa:taghash" , this.tagsHash );
	}

	private String computeTagHash() {
		
		if ( md == null )
		{
			 try {
				md = MessageDigest.getInstance("SHA1") ;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} 
		}
		
		md.reset();
		
		List<String> tagsKeys = new LinkedList<String>( tags.keySet() );
		java.util.Collections.sort( tagsKeys );
		for( String key : tagsKeys )
		{
			if ( !key.contentEquals("noaa:taghash") && !key.contentEquals("noaa:geohash") )
			{
				md.update(key.getBytes());
				md.update(tags.get(key).getBytes());
			}
		}
		
		byte[] digest  = md.digest();
		StringBuilder sb = new StringBuilder();
		for ( byte b : digest)
			sb.append(String.format("%02x", b & 0xff));

		//System.out.println( sb.toString());
		return sb.toString();
	}

	private long computeGeoHash(double lat, double lon) {
		    return Math.round((lat + 100) * 1e6 ) 
			      + Math.round((lon + 200) * 1e6 ) * 1000000;
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
		return computeGeoHash(this.lat, this.lon) == computeGeoHash(node.lat, node.lon) 
				&& this.computeTagHash().contentEquals( node.computeTagHash() ); 
	}
	
	public boolean wasMovedByNoaa(SeaMarkNode node) {
		return this.geoHash != node.geoHash;
	}
	public boolean tagsChangedByNoaa(SeaMarkNode node) {
		return ! this.tagsHash.contentEquals( node.tagsHash );
	}

	public boolean wasMovedByHuman() {
		return computeGeoHash(this.lat, this.lon) != this.geoHash ;
	}

	public boolean tagsChangedByHuman() {
		return ! this.computeTagHash().contentEquals( this.tagsHash );
	}

	public SeaMarkNode conflateWith(SeaMarkNode original) {
        // |                          | Moved by noaa   - true   | Moved by noaa   - false  |
        // | Moved by human  - true   |         noaa             |          human           |
        // | Moved by human  - false  |         noaa             |          no change        |

		//                            | Tagged by noaa - true | Tagged by noaa - false
        // | Tagged by human - true   |        noaa           |         human
        // | Tagged by human - false  |        noaa           |         no change

		double newLat = this.lat;
		double newLon = this.lon;
		if ( original.wasMovedByHuman( ) && ! this.wasMovedByNoaa( original ))
		{
			newLat = original.lat;
			newLon = original.lon;
		}
		
		SeaMarkNode node = new SeaMarkNode(this.id, newLat, newLon);

		if ( original.tagsChangedByHuman( ) && ! this.tagsChangedByNoaa( original ))
		{
			node.tags.putAll(original.tags);
		}
		else
		{
			node.tags.putAll(this.tags);
		}
		
		node.tags.remove("noaa:lnam");
		node.tags.remove("noaa:geohash");
		node.tags.remove("noaa:taghash");
		node.tags.remove("seamark:type");
		
		node.closeNode(this.tags.get("seamark:type"), this.lnam);
		
		return node;
	}

}
