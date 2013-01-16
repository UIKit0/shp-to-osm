package com.scmarinetech.S57;

import java.io.FileWriter;
import java.io.IOException;

public class OSMWriter implements SeaMarkNodeSink {

	S57ToOsmMapper map;
	FileWriter fw;
	String osmfilePrefix; 
	private int maxNodes = 5000 ;
	private int osmFileIdx = 0;
	private int nodeCount = 0;
	private String osmFileName;
	
	public OSMWriter(String osmfile) throws IOException
	{
		this.map = new S57ToOsmMapper();
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

	public void onNodeDecoded(SeaMarkNode seaMarkNode) {
		try {
			if ( nodeCount >= maxNodes  )
			{
				onDataSetEnd();
				onDataSetStart();
				nodeCount = 0;
			}
			fw.write( seaMarkNode.toString() );
			nodeCount ++ ;
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

}
