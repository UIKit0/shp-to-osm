package com.scmarinetech.osm;

import java.util.LinkedList;
import java.util.List;

public class OsmNodeFetcher {

	final private static double CACHE_BB_SIZE = 0.05; 
	private List<OsmNode> cachedList;
	final private OsmApi osmApi;
	double cachedLat;
	double cachedLon;
	boolean cacheValid;
	
	public OsmNodeFetcher()
	{
		osmApi = new OsmApi();
		cacheValid = false;
	}
	
	/**
	 * Returns list of the nodes around given lat, lon
	 * @param lat
	 * @param lon
	 * @param bbsize
	 * @return
	 */
	public List<OsmNode> getNodes(double lat, double lon, double bbsize)
	{
		if ( ! cacheExistFor( lat, lon  ) ) 
		{
			refreshCache( lat, lon );
		}
		
		List<OsmNode> list = new LinkedList<OsmNode>();
		for ( OsmNode node : cachedList )
		{
			if( isInBox( node.lat, node.lon, lat, lon, bbsize) )
			{
				list.add(node);
			}
		}
		
		return list;
	}

	private boolean isInBox(double lat, double lon, double centerlat, double centerlon, double d) {
		return 
				   (lat > (centerlat - d) )
				&& (lat < (centerlat + d) )
				&& (lon > (centerlon - d) )
				&& (lon < (centerlon + d) )
				;
	}

	private boolean cacheExistFor(double lat, double lon ) {
		if ( cacheValid &&  isInBox( lat, lon, cachedLat, cachedLon, CACHE_BB_SIZE) ) 
			return true;
		else
			return false; 
			
	}

	private void refreshCache(double lat, double lon) {
		cachedList = osmApi.getNodes(lon - CACHE_BB_SIZE, lat - CACHE_BB_SIZE,  lon + CACHE_BB_SIZE, lat + CACHE_BB_SIZE );
		cacheValid = true;
		cachedLat = lat;
		cachedLon = lon;
	}

}
