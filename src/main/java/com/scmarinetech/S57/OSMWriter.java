package com.scmarinetech.S57;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.sourceforge.capcode.S57Library.catalogs.S57Attribute;
import net.sourceforge.capcode.S57Library.objects.S57Feature;

public class OSMWriter {

	S57ToOsmMapper map;
	
	public OSMWriter()
	{
		map = new S57ToOsmMapper();
	}
	
	public void write(List<FeaturedSpatial> featuredSpatials, String osmfile) throws IOException {
		System.out.println("Creating  " +  osmfile + " ...");

		FileWriter fw = new FileWriter(osmfile);
		fw.write("<?xml version='1.0' encoding='UTF-8'?>\n");
		fw.write("<osm version='0.6' generator='s57-to-osm'>\n");
		
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
		
		fw.write(String.format("<node id='%d' version='1' visible='true'  lat='%f' lon='%f'>\n"
				,id
				,fs.spatial.coordinate.latitude
				,fs.spatial.coordinate.longitude));
		
		String masterName = null;
		String name = "unknown";
		for ( S57Feature f : fs.getFeatures() )
		{
			name = map.getObjectName(f.object.code);
			if ( name != null )
			{
				writeAttributes(fw, f);
				if ( f.linkedFeatures != null )
					masterName = name;
			}
		}
		fw.write("<tag k='seamark:type' v='");
		fw.write( masterName != null ? masterName : name);
		fw.write("' />");
		
		fw.write("</node>\n");
	}

	private void writeAttributes(FileWriter fw, S57Feature feature) throws IOException {
		for ( S57Attribute attr : feature.attributes )
		{
			if (     map.getAttrName(attr.name.code) != null 
					&& attr.value != null 
					&& map.getAttrValue(attr.name.code, attr.name.type, attr.value ) != null)
			{
				fw.write("<tag k='seamark:");
				fw.write( map.getObjectName(feature.object.code) );
				fw.write(":");
				fw.write( map.getAttrName(attr.name.code) );
				if ( "colour".contentEquals(map.getAttrName(attr.name.code)) )
				{
					fw.write("");
				}
				fw.write("' v='");
				fw.write( map.getAttrValue(attr.name.code, attr.name.type, attr.value ) );
				fw.write("' />\n");
			}
		}
		
	}

}
