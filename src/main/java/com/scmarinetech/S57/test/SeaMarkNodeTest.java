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
		assertFalse(org_node.wasMovedByHuman() );
		assertFalse(org_node.tagsChangedByHuman() );
		
		nodes = readNodes(getClass().getResource("seanode_moved_by_human.xml"));
		assertEquals(1, nodes.size() );
		SeaMarkNode seanode_moved_by_human = new SeaMarkNode( nodes.get(0) ) ;
		assertTrue( org_node.hasSameGlobalId(seanode_moved_by_human) );
		assertFalse( org_node.isIdentical(seanode_moved_by_human) );
		assertTrue( seanode_moved_by_human.wasMovedByHuman() );
		assertFalse ( seanode_moved_by_human.wasMovedByNoaa( org_node ) );
		assertFalse(org_node.tagsChangedByHuman() );

		nodes = readNodes(getClass().getResource("seanode_attrchanged_by_human.xml"));
		assertEquals(1, nodes.size() );
		SeaMarkNode seanode_attrchanged_by_human = new SeaMarkNode( nodes.get(0) ) ;
		assertTrue( org_node.hasSameGlobalId(seanode_attrchanged_by_human) );
		assertFalse( org_node.isIdentical(seanode_attrchanged_by_human) );
		assertFalse(seanode_attrchanged_by_human.wasMovedByHuman() );
		assertTrue(seanode_attrchanged_by_human.tagsChangedByHuman() );

		nodes = readNodes(getClass().getResource("seanode_attrchanged_by_noaa.xml"));
		assertEquals(1, nodes.size() );
		SeaMarkNode seanode_attrchanged_by_noaa = new SeaMarkNode( nodes.get(0) ) ;
		assertTrue( org_node.hasSameGlobalId(seanode_attrchanged_by_noaa) );
		assertFalse ( org_node.isIdentical(seanode_attrchanged_by_noaa) );
		assertFalse( seanode_attrchanged_by_noaa.wasMovedByNoaa( org_node ) );
		assertTrue ( seanode_attrchanged_by_noaa.tagsChangedByNoaa( org_node ) );
		assertFalse(seanode_attrchanged_by_noaa.wasMovedByHuman() );
		assertFalse(seanode_attrchanged_by_noaa.tagsChangedByHuman() );

		nodes = readNodes(getClass().getResource("seanode_moved_by_noaa.xml"));
		assertEquals(1, nodes.size() );
		SeaMarkNode seanode_moved_by_noaa = new SeaMarkNode( nodes.get(0) ) ;
		assertTrue( org_node.hasSameGlobalId(seanode_moved_by_noaa) );
		assertFalse( org_node.isIdentical(seanode_moved_by_noaa) );
		assertTrue ( seanode_moved_by_noaa.wasMovedByNoaa( org_node ) );
		assertFalse( seanode_moved_by_noaa.tagsChangedByNoaa( org_node ) );
		assertFalse( seanode_moved_by_noaa.wasMovedByHuman() );
		assertFalse( seanode_moved_by_noaa.tagsChangedByHuman() );

		
		
        // |                          | Moved by noaa   - true   | Moved by noaa   - false  |
        // | Moved by human  - true   |         noaa             |          human           |
        // | Moved by human  - false  |         noaa             |          no change        |

		//                            | Tagged by noaa - true | Tagged by noaa - false
        // | Tagged by human - true   |        noaa           |         human
        // | Tagged by human - false  |        noaa           |         no change

		// No changes from NOAA cases
		
		SeaMarkNode conflated = org_node.conflateWith(seanode_moved_by_human);
		assertEquals( conflated.lat, seanode_moved_by_human.lat, 0.000001);
		assertEquals( conflated.lon, seanode_moved_by_human.lon, 0.000001);
		for ( String key : seanode_moved_by_human.tags.keySet() )
		{
			if ( key.contentEquals("noaa:geohash")) continue; 
			assertEquals( "|"+key+"|", seanode_moved_by_human.tags.get(key), conflated.tags.get(key) );
		}
		
		conflated = org_node.conflateWith(seanode_attrchanged_by_human);
		assertEquals( conflated.lat, seanode_attrchanged_by_human.lat, 0.000001);
		assertEquals( conflated.lon, seanode_attrchanged_by_human.lon, 0.000001);
		for ( String key : seanode_moved_by_human.tags.keySet() )
		{
			if ( key.contentEquals("noaa:taghash")) continue; 
			if ( key.contentEquals("seamark:light:period")) 
			{
				assertEquals( "|"+key+"|", "2.4", conflated.tags.get(key) );
			}
			else
			{
				assertEquals( "|"+key+"|", seanode_moved_by_human.tags.get(key), conflated.tags.get(key) );
			}
		}
		
		// Changes made by noaa override human changes
		conflated = seanode_moved_by_noaa.conflateWith(seanode_moved_by_human);
		assertEquals( conflated.lat, seanode_moved_by_noaa.lat, 0.000001);
		assertEquals( conflated.lon, seanode_moved_by_noaa.lon, 0.000001);
		for ( String key : seanode_moved_by_noaa.tags.keySet() )
		{
			if ( key.contentEquals("noaa:geohash")) continue; 
			assertEquals( "|"+key+"|", seanode_moved_by_noaa.tags.get(key), conflated.tags.get(key) );
		}

		conflated = seanode_attrchanged_by_noaa.conflateWith(seanode_attrchanged_by_human);
		assertEquals( conflated.lat, seanode_attrchanged_by_noaa.lat, 0.000001);
		assertEquals( conflated.lon, seanode_attrchanged_by_noaa.lon, 0.000001);
		for ( String key : seanode_attrchanged_by_noaa.tags.keySet() )
		{
			if ( key.contentEquals("noaa:taghash")) continue; 
			if ( key.contentEquals("seamark:light:period")) 
			{
				assertEquals( "|"+key+"|", "3.5", conflated.tags.get(key) );
			}
			else
			{
				assertEquals( "|"+key+"|", seanode_attrchanged_by_noaa.tags.get(key), conflated.tags.get(key) );
			}
		}
		
		
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
