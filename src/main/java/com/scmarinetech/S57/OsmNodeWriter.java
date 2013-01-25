package com.scmarinetech.S57;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.scmarinetech.osm.OsmNode;

public class OsmNodeWriter {

	final private String osmdir;
	final private String prefix;
	private int maxNodes = 5000 ;

	private File nodesFile;
	private int osmFileIdx = 0;
	private int nodeCount = 0;
	private FileWriter nw;

	public OsmNodeWriter(String osmdir, String prefix) {
		this.osmdir = osmdir;
		this.prefix = prefix;
	}

	public void open() {
		try {
			nodesFile =  new File(osmdir ,  String.format("%s_%03d.osm", this.prefix, osmFileIdx++) );  
			System.out.println("Creating  " +  nodesFile.getAbsolutePath() );
			nw = new FileWriter( nodesFile );
			nw.write("<?xml version='1.0' encoding='UTF-8'?>\n");
			nw.write("<osm version='0.6' generator='s57-to-osm'>\n");
			nodeCount = 0;
		} catch (IOException e) {
		}
		
	}

	public void close() {
		try {
			nw.write("</osm>");
			nw.close();
			System.out.println("Created  " +  nodesFile.getAbsolutePath() );
		} catch (IOException e) {
		}
	}

	public void write(OsmNode node) {
		if ( nodeCount >= maxNodes  )
		{
			close();
			open();
			nodeCount = 0;
		}
		
		nodeCount ++ ;

		try {
			nw.write( node.toString() );
		} catch (IOException e) {
		}
		
	}

	public void setMaxNodes(int maxNodes) {
		this.maxNodes= maxNodes;
	}

}
