package com.scmarinetech.S57;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.sourceforge.capcode.S57Library.catalogs.S57Attribute;
import net.sourceforge.capcode.S57Library.objects.S57Feature;

public class OSMWriter {

	public void write(List<FeaturedSpatial> featuredSpatials, String osmfile) throws IOException {
		System.out.println("Creating  " +  osmfile + " ...");

		FileWriter fw = new FileWriter(osmfile);
		fw.write("<?xml version='1.0' encoding='UTF-8'?>");
		fw.write("<osm version='0.6' generator='me'>");
		
		int nodeId = -1;
		for ( FeaturedSpatial fs: featuredSpatials)
		{
			writeNode(fw, nodeId--, fs);
		}
		
		fw.write("</osm>");

		fw.close();
		System.out.println("Created  " +  osmfile);
	}

	private void writeNode(FileWriter fw, int id, FeaturedSpatial fs) throws IOException {
		fw.write(String.format("<node id='%d' version='1' lat='%f' lon='%f'>\n"
				,id
				,fs.spatial.coordinate.latitude
				,fs.spatial.coordinate.longitude));
		for ( S57Feature f : fs.getFeatures() )
		{
			writeAttributes(fw, f);
		}
		fw.write("</osm>\n");
	}

	private void writeAttributes(FileWriter fw, S57Feature feature) throws IOException {
		for ( S57Attribute attr : feature.attributes )
		{
			fw.write( feature.object.toString() );
			fw.write("/");
			fw.write( attr.name.accronym );
			fw.write("/");
			if ( attr.value != null )
			{
				fw.write( attr.value );
			}
			fw.write("\n");
		}
		
	}

}
