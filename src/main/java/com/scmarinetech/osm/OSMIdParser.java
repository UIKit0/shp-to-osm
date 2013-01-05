package com.scmarinetech.osm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class OSMIdParser extends DefaultHandler {

    private List<Integer> ids = new ArrayList<Integer>();

    /**
     * {@inheritDoc}
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("node".equals(qName)) {
            int id = Integer.parseInt(attributes.getValue("id"));
            ids.add( id );
        }     
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    }


    public List<Integer> readIds(InputStream is) {

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

        return ids;
	}

}
