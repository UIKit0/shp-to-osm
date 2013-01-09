package com.scmarinetech.osm;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class OsmUploader {

	private static final String OSM_API_URI = "http://api06.dev.openstreetmap.org";
	//private static final String OSM_API_URI = "http://api.openstreetmap.org";
	private static final String OSM_XAPI_URI = "http://www.overpass-api.de/api/xapi";

	final private String username;
	final private String password;
	
	public OsmUploader(String userName, String password)
	{
		this.username = userName;
		this.password = password;
	}
	
	public void uploadOsmFiles(File osmdir, double minx, double miny,
			double maxx, double maxy, int uid) {

		// Download data currently in OSM
		List<Integer> ids = downloadExistingObjectIds(minx, miny, maxx, maxy, uid );

		// Open the change set 
		
		int chnageSetId = openChnageSet();
		
		// Upload the change file 
		uploadChangeFile(ids, osmdir, chnageSetId);
		
		// Close the change set 
		closeChangeSet(chnageSetId);

	}


	private List<Integer>  downloadExistingObjectIds(double minx, double miny,
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
			retrieveBboxUri = new URIBuilder( OSM_XAPI_URI )
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

	private int openChnageSet() {
		int id = -1;
		DefaultHttpClient httpclient = new DefaultHttpClient();
		 try {
	            httpclient.getCredentialsProvider().setCredentials(
	                    new AuthScope(null, -1),
	                    new UsernamePasswordCredentials(username, password));
	            
	    		URI  createChnageSetUri = new URIBuilder( OSM_API_URI )
	    		    .setPath("/api/0.6/changeset/create")
	    			.build();

	    		String changeSet = new StringBuilder()
	    		  .append("<osm>")
	    		  .append("<changeset>")
	    		  .append("<tag k=\"created_by\" v=\"shp-to-osm-sea\"/>")
	    		  .append("<tag k=\"comment\"    v=\"NOAA import\"/>")
	    		  .append("</changeset>")
	    		  .append("</osm>")
	    		  .toString();
	    		
	    		HttpPost httpPost = new HttpPost( createChnageSetUri );
	    		httpPost.addHeader("X_HTTP_METHOD_OVERRIDE", "PUT");
	    		httpPost.setEntity( new StringEntity( changeSet ) );

    	    	System.out.println( "Sending request to create change set ... ");
	        	
    	    	HttpResponse response = httpclient.execute( httpPost );
	    		
			    System.out.println(response.getStatusLine());
	    	    if ( response.getStatusLine().getStatusCode() == 200 )
	    	    {
	        	    HttpEntity entity = response.getEntity();

	        	    String resp = convertStreamToString( entity.getContent() );
	    	    	System.out.println( "Response: " + resp);
	    	    	id = Integer.parseInt( resp  );
	        	    EntityUtils.consume(entity);
	    	    }
	        	
		 } catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        httpclient.getConnectionManager().shutdown();
	    }
		return id;
	}

	private void closeChangeSet(int id) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		 try {
	            httpclient.getCredentialsProvider().setCredentials(
	                    new AuthScope(null, -1),
	                    new UsernamePasswordCredentials(username, password));
	            
	    		URI  createChnageSetUri = new URIBuilder( OSM_API_URI )
	    		    .setPath("/api/0.6/changeset/" + id + "/close" )
	    			.build();

	    		HttpPost httpPost = new HttpPost( createChnageSetUri );
	    		httpPost.addHeader("X_HTTP_METHOD_OVERRIDE", "PUT");

   	    	System.out.println( "Sending request to close change set ... ");
	        	
   	    	HttpResponse response = httpclient.execute( httpPost );
	    		
			System.out.println(response.getStatusLine());
	        	
		 } catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        httpclient.getConnectionManager().shutdown();
	    }
		
	}

	// http://stackoverflow.com/a/5445161
	private static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private void uploadChangeFile(List<Integer> ids, File osmDir, int changeSetId ) {

		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		 try {
	            httpclient.getCredentialsProvider().setCredentials(
	                    new AuthScope(null, -1),
	                    new UsernamePasswordCredentials(username, password));
	            
	    		URI  createChnageSetUri = new URIBuilder( OSM_API_URI )
    		    .setPath("/api/0.6/changeset/" + changeSetId + "/upload" )
	    			.build();

	    		HttpPost httpPost = new HttpPost( createChnageSetUri );
	    		
	    		OsmContentProducer osmContentProducer = new OsmContentProducer(ids, osmDir, changeSetId);
	    		
				EntityTemplate entityTemplate = new EntityTemplate ( osmContentProducer );
				entityTemplate.setContentType("text/xml");
				httpPost.setEntity( entityTemplate );

   	    	    System.out.println( "Uploading change set ... ");
	        	
   	    	    HttpResponse response = httpclient.execute( httpPost );
	    		
			    System.out.println(response.getStatusLine());
			    
	    	    if ( response.getStatusLine().getStatusCode() != 200 )
	    	    {
	        	    HttpEntity entity = response.getEntity();

	        	    String resp = convertStreamToString( entity.getContent() );
	    	    	System.out.println( "Response: " + resp);
	        	    EntityUtils.consume(entity);
	    	    }
	        	
		 } catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        httpclient.getConnectionManager().shutdown();
	    }
		
	}
	
}
