package com.scmarinetech.noaa.gis;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class NoaaDownloader {

	/**
	 * Downloads the shape file from NOAA GIS server http://ocs-gis.ncd.noaa.gov/ENC_Direct/encdirect_download.html 
	 * @param outFilename  Destination file  
	 * @param band  e.g. APPROACH_HARBOR
	 * @param scale  e.g. APPROACH
	 * @param minx
	 * @param miny
	 * @param maxx
	 * @param maxy
	 * @param objNames list of S-57 objects to be included to the shape file
	 */
	static void downloadShapeFile(String outFilename, String band,
			String scale, double minx, double miny, double maxx, double maxy,
			List<String> objNames) {
		StringBuilder themes = new StringBuilder();
		for( String obj : objNames )
		{
			themes.append(band)
	          .append('.')
	          .append(obj)
	          .append(':')
	          .append(scale)
	          .append(' ');
		}
		
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("userSelectedThemes", themes.toString() ));
		
		nvps.add(new BasicNameValuePair("lowerLeftX", Double.toString(minx)));
		nvps.add(new BasicNameValuePair("lowerLeftY", Double.toString(miny)));
		nvps.add(new BasicNameValuePair("upperRightX", Double.toString(maxx)));
		nvps.add(new BasicNameValuePair("upperRightY", Double.toString(maxy)));
	
		nvps.add(new BasicNameValuePair("queryMethod", "dbUnits"));
		nvps.add(new BasicNameValuePair("format",      "2shp.fme"));
		nvps.add(new BasicNameValuePair("coordsys",    "LL84"));
		nvps.add(new BasicNameValuePair("SSFunction",  "remoteFetch"));
	
		HttpPost httpPost = new HttpPost("http://ocs-spatial.ncd.noaa.gov:80/SpatialDirect/translationServlet");
	
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String zipFileUri = null;  
	
		try {
		    System.out.println("Posting request to " + httpPost.getURI() );
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse response = httpclient.execute(httpPost);
		    System.out.println(response.getStatusLine());
		    HttpEntity entity = response.getEntity();
		    
		    String responseString = EntityUtils.toString(entity);
		    // Parse response 
		    zipFileUri = NoaaDownloader.parseSyncResponse( responseString );
		    // and ensure it is fully consumed
		    EntityUtils.consume(entity);
		    
		    if ( zipFileUri == null )
		    {
		    	System.out.print( "Invalid response" );
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    httpPost.releaseConnection();
		}
		
	    if ( zipFileUri != null )
	    {
	    	System.out.println( "Downloading  " + zipFileUri );
	    	HttpGet httpGet = new HttpGet( zipFileUri );
	
	    	try {
				HttpResponse response = httpclient.execute(httpGet);
	    	    HttpEntity entity = response.getEntity();
			    System.out.println(response.getStatusLine());
	    	    if ( response.getStatusLine().getStatusCode() == 200 )
	    	    {
					FileOutputStream fos = new FileOutputStream( outFilename );
	    	    	entity.writeTo( fos );
	    	    	fos.close();
	    	    	System.out.println( "Downloaded to " + outFilename);
	    	    }
	    	    EntityUtils.consume(entity);
	    	} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
	    	    httpGet.releaseConnection();
	    	}
	    	
	    }
	}

	/**
	 * Parses the response of ArcGIS FME Translation Server 
	 * See documentation at http://downloads2.esri.com/support/documentation/other_/DDE_RefGuide_Updated0904.pdf 
	 * @param httpResponse
	 * @return
	 */
	static private String parseSyncResponse(String httpResponse) {
	
	    int start = httpResponse.indexOf("<!-- RESULTSTART^TRANSLATION_RESULT_MSG^", 0);
	    if ( start >= 0 )
	    {
	    	int end = httpResponse.indexOf("^RESULTEND -->", 0);
	    	if ( end >= 0 )
	    	{
	    		String fmeResult = httpResponse.substring(start, end);
	    		String[] t = fmeResult.split("\\^");
	    		if ( t.length >=4 )
	    		{
	    			return t[3];
	    		}
	    	}
	    }
	    return null;
	}

}
