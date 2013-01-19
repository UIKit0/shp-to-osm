package com.scmarinetech.osm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OsmApi {
	
	public class OSMNodesParser extends DefaultHandler {

		final List<OsmNode> nodes;
	    OsmNode currentNode;
		
		OSMNodesParser (List<OsmNode> nodes)
		{
			this.nodes = nodes;
		}
		
	    /**
	     * {@inheritDoc}
	     */
	    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	        if ("node".equals(qName)) {
	        	currentNode = parseNodeAttrs(attributes);
	        }else if ("tag".equals(qName)) {
	        	addNodeTags(currentNode, attributes);
	        }
	        
	    }

		public void endElement(String uri, String localName, String qName) throws SAXException {
	        if ("node".equals(qName)) {
	        	nodes.add( currentNode ) ;
	        }     
	    }

	    private OsmNode parseNodeAttrs(Attributes attributes) {
	    	long id = Long.parseLong(attributes.getValue("id"));
	    	double lat = Double.parseDouble( attributes.getValue("lat") );
	    	double lon = Double.parseDouble( attributes.getValue("lon") );
			return new OsmNode(id, lat, lon);
		}
		private void addNodeTags(OsmNode node, Attributes attributes) {
			int len = attributes.getLength();
			for ( int i = 0; i < len; i++ )
			{
				node.addTag( attributes.getValue("k"), attributes.getValue("v") );
			}
			
		}

	    public void parseNodes(InputStream is) {

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
	
	//private static final String OSM_API_URI = "http://api06.dev.openstreetmap.org";
	private static final String OSM_API_URI = "http://api.openstreetmap.org";

	public List<OsmNode> getNodes(double left, double bottom, double right, double top) {
		LinkedList<OsmNode> nodes = new LinkedList<OsmNode>();
		
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		String bbox  = new StringBuilder("bbox=")
		.append(left).append(',')
		.append(bottom).append(',')
		.append(right).append(',')
		.append(top)
		.toString();

		URI retrieveBboxUri;
		try {
			retrieveBboxUri = new URIBuilder( OSM_API_URI )
			.setPath("/api/0.6/map")
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
