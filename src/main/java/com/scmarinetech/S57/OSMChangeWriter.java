package com.scmarinetech.S57;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.scmarinetech.osm.OsmNode;
import com.scmarinetech.osm.OsmNodeFetcher;

public class OSMChangeWriter implements SeaMarkNodeSink {

	private static final double NEIGBOUR_DIST = (1. / 60. / 1850.)  * 50; // 50 meters  
	S57ToOsmMapper map;
	final OsmNodeFetcher nodeFetcher;
	
	FileWriter fw;
	String osmfilePrefix; 
	private int maxNodes = 5000 ;
	private int osmFileIdx = 0;
	private int nodeCount = 0;
	private String osmFileName;
	
	public OSMChangeWriter(String osmfile) throws IOException
	{
		this.map = new S57ToOsmMapper();
		this.nodeFetcher = new OsmNodeFetcher();
		this.osmfilePrefix = osmfile;
	}
	
	public void setMaxNodes(int maxNodes)
	{
		this.maxNodes = maxNodes;
	}

	public void onDataSetStart() {
		try {
			osmFileName = osmfilePrefix + String.format("_%03d.osm", osmFileIdx++);
			System.out.println("Creating  " +  osmFileName);
			fw = new FileWriter( osmFileName);
			fw.write("<?xml version='1.0' encoding='UTF-8'?>\n");
			fw.write("<osm version='0.6' generator='s57-to-osm'>\n");
			nodeCount = 0;
		} catch (IOException e) {
		}
	}

	public void onDataSetEnd() {
		try {
			fw.write("</osm>");
			fw.close();
			System.out.println("Created  " +  osmFileName);
		} catch (IOException e) {
		}
	}


	public void onNodeDecoded(SeaMarkNode seaMarkNode) {
		try {
			if ( nodeCount >= maxNodes  )
			{
				onDataSetEnd();
				onDataSetStart();
				nodeCount = 0;
			}
			
			createChange( seaMarkNode );
			
			nodeCount ++ ;
		} catch (IOException e) {
		}
	}

	private void createChange(SeaMarkNode seaMarkNode)  throws IOException {
		
		List<OsmNode> neigbours =  nodeFetcher.getNodes(seaMarkNode.lat, seaMarkNode.lon, NEIGBOUR_DIST );
		fw.write( seaMarkNode.toString() );
		if ( ! neigbours.isEmpty() )
		{
			System.out.println("Found " + neigbours.size() + " neigbours");
			boolean changeOrConflictWriten = false;
			for ( OsmNode node:  neigbours )
			{
				if ( comapreWithNeigbour(seaMarkNode, node) ) 
				{
					break;
				}
			}
			
			if ( ! changeOrConflictWriten )
			{
				// No neighbor was similar to us
				fw.write(seaMarkNode.toString() );
			}
		}
	}

	private boolean comapreWithNeigbour(SeaMarkNode seaMarkNode, OsmNode neigbour) throws IOException {
		
		if ( SeaMarkNode.isSeaMarkNode ( neigbour ) )
		{
			SeaMarkNode neigbSeaMark = new SeaMarkNode ( neigbour );
			if ( neigbSeaMark.hasSameGlobalId ( seaMarkNode ) )
			{
				if ( ! neigbSeaMark.isIdentical ( seaMarkNode ) )
				{
					SeaMarkNode newNode = seaMarkNode.conflateWith( neigbSeaMark );
					fw.write(newNode.toString() );
				}
				else
				{
					// No change is necessary
				}
				return true; 
			}
			else
			{
				// Ignore this neighbor 
				return false;
			}
			
		}else if ( SeaMarkNode.hasSeaMarksAttribures( neigbour ) ){
			fw.write(seaMarkNode.toString() ); // FIXME write to conflicts writer 
			return true; 
		}
		
		
		return false;
	}
	
	

}
