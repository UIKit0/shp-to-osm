package com.scmarinetech.osm;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

public class OsmApi {
	
	//private static final String OSM_API_URI = "http://api06.dev.openstreetmap.org";
	//private static final String OSM_API_URI = "http://api.openstreetmap.org";
	private static final String OSM_XAPI_URI = "http://www.overpass-api.de";

	public List<OsmNode> getNodes(double left, double bottom, double right, double top) {
		LinkedList<OsmNode> nodes = new LinkedList<OsmNode>();
		
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		String bbox  = new StringBuilder("map?bbox=")
		.append(left).append(',')
		.append(bottom).append(',')
		.append(right).append(',')
		.append(top)
		.toString();

		URI retrieveBboxUri;
		try {
			retrieveBboxUri = new URIBuilder( OSM_XAPI_URI )
			.setPath("/api/xapi")
			.setQuery(bbox)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return nodes;
		}

		HttpGet httpGet = new HttpGet( retrieveBboxUri );
		try {
			System.out.println( "Requesting " + httpGet.toString() );
        	HttpResponse response = httpclient.execute(httpGet);
    	    HttpEntity entity = response.getEntity();
		    System.out.println(response.getStatusLine());

    	    if ( response.getStatusLine().getStatusCode() == 200 )
    	    {
    	    	OSMNodesParser parser = new OSMNodesParser(nodes);
    	    	parser.parseNodes( entity.getContent() );
    			System.out.println( "Received " + nodes.size() + " nodes" );
    	    }
		
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
    	    httpGet.releaseConnection();
    	}

		return nodes;
	}

}
