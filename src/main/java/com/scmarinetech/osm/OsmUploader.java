package com.scmarinetech.osm;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class OsmUploader {

	private static final String OSM_API_URI = "http://www.overpass-api.de/api/xapi";

	public static void uploadOsmFiles(File osmdir, double minx, double miny,
			double maxx, double maxy, int uid) {

		// Download data currently in OSM
		List<Integer> ids = downloadExistingObjectIds(minx, miny, maxx, maxy, uid );
		
		// Build change file 
		buildChangeFile(ids, osmdir);

	}

	private static List<Integer>  downloadExistingObjectIds(double minx, double miny,
			double maxx, double maxy, int uid ) {

		List<Integer> ids = new ArrayList<Integer>();
		
		String bbox  = new StringBuilder()
			.append("node")
			
			.append('[')
		    .append("bbox=")
			.append(minx).append(',')
			.append(miny).append(',')
			.append(maxx).append(',')
			.append(maxy)
			.append(']')

			.append('[')
			.append("@uid=")
			.append(uid)
			.append(']')
			.toString();
    	
		DefaultHttpClient httpclient = new DefaultHttpClient();


		URI retrieveBboxUri;
		try {
			retrieveBboxUri = new URIBuilder( OSM_API_URI )
			.setQuery(bbox)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return ids;
		}

    	HttpGet httpGet = new HttpGet( retrieveBboxUri );

		try {
			System.out.println( "Requesting " + httpGet.toString() );
        	HttpResponse response = httpclient.execute(httpGet);
    	    HttpEntity entity = response.getEntity();
		    System.out.println(response.getStatusLine());
    	    if ( response.getStatusLine().getStatusCode() == 200 )
    	    {
    	    	OSMIdParser parser = new OSMIdParser();
    	    	ids = parser.readIds( entity.getContent() );
    	    	System.out.println( "Downloaded " + ids.size() + " ids");
    	    }
    	    EntityUtils.consume(entity);
    	} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
    	    httpGet.releaseConnection();
    	}

		return ids;
	}

	private static void buildChangeFile(List<Integer> ids, File osmFile) {
		
		
	}
	
}
