package com.scmarinetech.S57;

import java.io.IOException;
import java.util.List;

import com.scmarinetech.osm.OsmNode;
import com.scmarinetech.osm.OsmNodeFetcher;

public class OSMChangeWriter implements SeaMarkNodeSink {

	private static final double NEIGBOUR_DIST = (1. / 60. / 1850.)  * 50; // 50 meters  
	final private OsmNodeFetcher nodeFetcher;
		
	private final OsmNodeWriter newNodesOsmWriter;
	private final OsmNodeWriter conflictsOsmWriter;
	private final OsmNodeWriter autoConflateOsmWriter;
	
	public OSMChangeWriter(String osmdir) throws IOException
	{
		this.nodeFetcher = new OsmNodeFetcher();
		
		this.newNodesOsmWriter = new OsmNodeWriter(osmdir, "new_nodes_");
		this.conflictsOsmWriter = new OsmNodeWriter(osmdir, "conflict_");
		this.autoConflateOsmWriter = new OsmNodeWriter(osmdir, "auto_resolved_");
	}
	
	public void setMaxNodes(int maxNodes)
	{
		this.newNodesOsmWriter.setMaxNodes( maxNodes );
		this.conflictsOsmWriter.setMaxNodes( maxNodes );
		this.autoConflateOsmWriter.setMaxNodes( maxNodes );
	}

	public void onDataSetStart() {
		newNodesOsmWriter.open();
		conflictsOsmWriter.open();
		autoConflateOsmWriter.open();
	}

	public void onDataSetEnd() {
		newNodesOsmWriter.close();
		conflictsOsmWriter.close();
		autoConflateOsmWriter.close();
	}

	public void onNodeDecoded(SeaMarkNode seaMarkNode) {
			createChange( seaMarkNode );
	}

	private void createChange(SeaMarkNode seaMarkNode) {  
		
		List<OsmNode> neigbours =  nodeFetcher.getNodes(seaMarkNode.lat, seaMarkNode.lon, NEIGBOUR_DIST );
		if ( ! neigbours.isEmpty() )
		{
			System.out.println("Found " + neigbours.size() + " neigbours");
			boolean changeOrConflictWriten = false;
			for ( OsmNode node:  neigbours )
			{
				if ( comapreWithNeigbour(seaMarkNode, node) ) 
				{
					changeOrConflictWriten = true;
					break;
				}
			}
			
			if ( ! changeOrConflictWriten )
			{
				// No neighbor was similar to us
				newNodesOsmWriter.write( seaMarkNode );
			}
		}
		else
		{
			// No neighbors, it's safe to create new node 
			newNodesOsmWriter.write(seaMarkNode);
		}
	}

	/**
	 * 
	 * @param seaMarkNode
	 * @param neigbour
	 * @return false - if no action is taken<br>
	 *          true - if change or conflict is written or identical node is found 
	 */
	private boolean comapreWithNeigbour(SeaMarkNode seaMarkNode, OsmNode neigbour) {
		
		if ( SeaMarkNode.isSeaMarkNode ( neigbour ) )
		{
			SeaMarkNode neigbSeaMark = new SeaMarkNode ( neigbour );
			if ( neigbSeaMark.hasSameGlobalId ( seaMarkNode ) )
			{
				if ( ! neigbSeaMark.isIdentical ( seaMarkNode ) )
				{
					SeaMarkNode newNode = seaMarkNode.conflateWith( neigbSeaMark );
					autoConflateOsmWriter.write( newNode );
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
			conflictsOsmWriter.write( seaMarkNode );
			return true; 
		}
		
		return false;
	}
	
	

}
