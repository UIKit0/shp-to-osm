package com.scmarinetech.osm;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OSMNodesParser extends DefaultHandler {

	final List<OsmNode> nodes;
    OsmNode currentNode;
	
	public OSMNodesParser (List<OsmNode> nodes)
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