package com.scmarinetech.osm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.http.entity.ContentProducer;

import com.yellowbkpk.osm.OSMFile;
import com.yellowbkpk.osm.primitive.Tag;
import com.yellowbkpk.osm.primitive.node.Node;

public class OsmContentProducer implements ContentProducer {

	private static final NumberFormat LAT_LON_FORMAT = NumberFormat.getInstance();
    static{
        LAT_LON_FORMAT.setGroupingUsed(false);
        LAT_LON_FORMAT.setMaximumFractionDigits(7);
    }

	final private List<Integer> idsToRemove;
	final private String generator;
	final int changeSetId;

	private OSMFile osmFile;

	public OsmContentProducer(List<Integer> idsToRemove, File osmDir, int changeSetId, String generator) {
		this.idsToRemove = idsToRemove;

		this.generator = generator;
		this.changeSetId = changeSetId;
		
		
		File [] files = osmDir.listFiles( new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		try{
			osmFile = OSMFile.fromFiles(Arrays.asList(files));
					
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void writeTo(OutputStream os) throws IOException {
		
		OutputStreamWriter out = new OutputStreamWriter(os);
		
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<osmChange version=\"0.6\" generator=\""+generator+"\">\n");
        
        // Nodes to create
        out.write("  <create version=\"0.6\" generator=\""+generator+"\">\n");
        outputNewNodesToCreate(out, osmFile.getNodeIterator() );
        out.write("  </create>\n");
        
        // Nodes to delete
        
        out.write("  <delete>\n");
        outputOldNodesToDelete(out);
        out.write("  </delete>\n");
        
        out.write("</osmChange>\n");

        out.flush();
	}

	private  void outputNewNodesToCreate(Writer out, Iterator<Node> nodeIter) throws IOException {
        while (nodeIter.hasNext()) {
            Node node = nodeIter.next();
    
            out.write("    <node id=\"");
            out.write(Integer.toString(node.getID()));
            out.write("\" lat=\"");
            out.write(LAT_LON_FORMAT.format(node.getLat()));
            out.write("\" lon=\"");
            out.write(LAT_LON_FORMAT.format(node.getLon()));
            out.write("\" changeset=\"");
            out.write( Integer.toString(changeSetId));
            out.write("\" version=\"");
            out.write( Integer.toString(1));
            out.write("\"");
            
            if (node.hasTags()) {
                out.write(">\n");
    
                Iterator<Tag> tagIter = node.getTagIterator();
                outputTags(out, tagIter);
    
                out.write("    </node>\n");
            } else {
                out.write("/>\n");
            }
    
        }
    }

	private  void outputTags(Writer out, Iterator<Tag> tagIter) throws IOException {
        while (tagIter.hasNext()) {
            Tag tag = tagIter.next();
    
            out.write("      <tag k=\"");
            out.write(tag.getKey());
            out.write("\" v=\"");
            out.write(tag.getValue());
            out.write("\"/>\n");
        }
    }

	private void outputOldNodesToDelete(OutputStreamWriter out) throws IOException {
		for ( int id : idsToRemove )
        {
        	out.write("    <node id=\"");
        	out.write(Integer.toString( id ));
            out.write("\" changeset=\"");
            out.write( Integer.toString(changeSetId));
            out.write("\" version=\"");
            out.write( Integer.toString(1));
        	out.write("\"/>\n");
        }
	}


}
