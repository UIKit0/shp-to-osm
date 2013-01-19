package com.scmarinetech.noaa.enc;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.scmarinetech.S57.FeaturedSpatial;
import com.scmarinetech.S57.S57Reader;
import com.scmarinetech.S57.S57ToOSMConverter;
import com.scmarinetech.S57.SeaMarkNodeSink;
import com.scmarinetech.utils.BoundingBox;

public class NoaaDownloader {
	
	class NoaaXmlParser extends DefaultHandler
	{
		boolean inUrl;
		boolean inMD_Distribution;
		public NoaaXmlParser()
		{
			inUrl = false;
			inMD_Distribution = false;
		}
	    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	        if ("URL".equals(qName)) {
	        	inUrl = true;
	        }     
	        if ("MD_Distribution".equals(qName)) {
	        	inMD_Distribution = true;
	        }     
	    }
	    public void characters(char[] ch,
                int start,
                int length)
	    {
	    	if ( inUrl && inMD_Distribution)
	    	{
	    		String url = new String(ch, start, length);
	    		zipUrls.add ( url );
	    	}
	    }
	    public void endElement(String uri, String localName, String qName) throws SAXException {
	        if ("URL".equals(qName)) {
	        	inUrl = false;
	        }     
	        if ("MD_Distribution".equals(qName)) {
	        	inMD_Distribution = false;
	        }     
	    }
	    
	    public void parse(InputStream is) {

	        try {
	            SAXParserFactory factory = SAXParserFactory.newInstance();
	            SAXParser saxParser = factory.newSAXParser();
	            saxParser.parse(is, this);
	        } catch (ParserConfigurationException e) {
	            e.printStackTrace();
	        } catch (SAXException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}

	}
	
	ArrayList<String> zipUrls;
	BoundingBox bbox;
	
	public NoaaDownloader()
	{
		bbox = new BoundingBox();
	}

	public void downloadEncFiles(String xmlFileUrl, SeaMarkNodeSink seaMarkNodeSink) {
	
		zipUrls = new ArrayList<String>();
		
		HttpGet httpGetXml = new HttpGet(xmlFileUrl);
	
		DefaultHttpClient httpclient = new DefaultHttpClient();
	
		try {
		    System.out.println("Requesting " + httpGetXml.getURI() );
			HttpResponse response = httpclient.execute(httpGetXml);
		    System.out.println(response.getStatusLine());
		    HttpEntity entity = response.getEntity();
		    
		    NoaaXmlParser parser = new NoaaXmlParser();
		    parser.parse( entity.getContent() );
		    
		    EntityUtils.consume(entity);
		    
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    httpGetXml.releaseConnection();
		}
		
		
		for ( String zipUrl : zipUrls )
		{
			HttpGet httpGetZip = new HttpGet(zipUrl);
			try {
			    System.out.println("Requesting " + httpGetZip.getURI() );
				HttpResponse response = httpclient.execute( httpGetZip );
			    System.out.println(response.getStatusLine());
			    HttpEntity entity = response.getEntity();
			    
		        ZipInputStream zis = new ZipInputStream( entity.getContent() );
		        ZipEntry entry;
		        while ( (entry  = zis.getNextEntry() ) != null )
		        {
		            String name = entry.getName();
				    if( name.endsWith(".000") )
				    {
						S57Reader reader = new S57Reader(bbox);
						List<FeaturedSpatial>  featuredSpatials = reader.readEncFile( zis, name, (int) entry.getSize() );
						S57ToOSMConverter converter = new S57ToOSMConverter( seaMarkNodeSink );
					    converter.doConversion( featuredSpatials );
				    }
				    zis.closeEntry();
		        }
			    
			    EntityUtils.consume(entity);
			    
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} finally {
				httpGetZip.releaseConnection();
			}
			
		}
		
		
	}

	public void setBoundBox(BoundingBox bbox) {
		this.bbox = bbox;
	}

}
