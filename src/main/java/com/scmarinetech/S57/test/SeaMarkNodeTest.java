package com.scmarinetech.S57.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;

import com.scmarinetech.S57.SeaMarkNode;
import com.scmarinetech.osm.OSMNodesParser;
import com.scmarinetech.osm.OsmNode;

import junit.framework.TestCase;

public class SeaMarkNodeTest extends TestCase {

	public void testOmToSeaMarkNode() throws IOException 
	{
		LinkedList<OsmNode> nodes = readNodes(getClass().getResource("seanode_org.xml"));
		assertEquals(1, nodes.size() );
		SeaMarkNode node1 = new SeaMarkNode( nodes.get(0) ) ;
		assertTrue(SeaMarkNode.isSeaMarkNode(node1) );
		assertTrue(SeaMarkNode.hasSeaMarksAttribures(node1) );

		nodes = readNodes(getClass().getResource("notseanode.xml"));
		assertEquals(1, nodes.size() );
		OsmNode notSeaNode = nodes.get(0) ;
		assertFalse(SeaMarkNode.isSeaMarkNode(notSeaNode) );
		assertFalse(SeaMarkNode.hasSeaMarksAttribures(notSeaNode) );

		nodes = readNodes(getClass().getResource("foreignseanode.xml"));
		assertEquals(1, nodes.size() );
		OsmNode foreignSeaNode = nodes.get(0) ;
		assertFalse(SeaMarkNode.isSeaMarkNode(foreignSeaNode) );
		assertTrue(SeaMarkNode.hasSeaMarksAttribures(foreignSeaNode) );

		nodes = readNodes(getClass().getResource("seanode_org.xml"));
		assertEquals(1, nodes.size() );
		SeaMarkNode sameAsNode1 = new SeaMarkNode( nodes.get(0) ) ;
		assertTrue( node1.hasSameGlobalId(sameAsNode1) );
		assertTrue( node1.isIdentical(sameAsNode1) );
	}

	public void testSeaMarkNodeConflation() throws IOException 
	{
		LinkedList<OsmNode> nodes = readNodes(getClass().getResource("seanode_org.xml"));
		assertEquals(1, nodes.size() );
		SeaMarkNode org_node = new SeaMarkNode( nodes.get(0) ) ;
		
		nodes = readNodes(getClass().getResource("seanode_moved_by_noaa.xml"));
		assertEquals(1, nodes.size() );
		SeaMarkNode seanode_moved_by_noaa = new SeaMarkNode( nodes.get(0) ) ;
		assertTrue( org_node.hasSameGlobalId(seanode_moved_by_noaa) );
		assertFalse( org_node.isIdentical(seanode_moved_by_noaa) );
		
		SeaMarkNode  conflated = seanode_moved_by_noaa.conflateWith(org_node);
		assertNotNull(conflated);
		
	}

	private LinkedList<OsmNode> readNodes(URL xmlfile) throws IOException {
		InputStream is = xmlfile.openStream();
		LinkedList<OsmNode> nodes = new LinkedList<OsmNode>();
		OSMNodesParser parser = new OSMNodesParser(nodes);
		parser.parseNodes(is);
		is.close();
		return nodes;
	}
}
