package com.scmarinetech.S57;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.scmarinetech.utils.BoundingBox;

import net.sourceforge.capcode.S57Library.basics.Link;
import net.sourceforge.capcode.S57Library.files.S57ModuleReader;
import net.sourceforge.capcode.S57Library.objects.E_S57ObjectPrimitiveType;
import net.sourceforge.capcode.S57Library.objects.S57Feature;
import net.sourceforge.capcode.S57Library.objects.S57Object;
import net.sourceforge.capcode.S57Library.objects.S57ObjectsVector;
import net.sourceforge.capcode.S57Library.objects.S57Spatial;

public class S57Reader {

	private final List<FeaturedSpatial> fearuredSpatials;
	private final BoundingBox bbox;
	public S57Reader(BoundingBox bbox)
	{
		this.bbox = bbox;
		fearuredSpatials = new LinkedList<FeaturedSpatial>();
	}

	public List<FeaturedSpatial> readEncFile(InputStream is, String encName, int size) {
		try {
			S57ModuleReader reader = new S57ModuleReader();
			reader.load( is , encName, size );
			processFeatures(reader);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fearuredSpatials;
	}
	
	public  List<FeaturedSpatial>  readEncFile(String encfile) {
		try {
			System.out.println("Loading  " +  encfile + " ...");
			S57ModuleReader reader = new S57ModuleReader();
			reader.load( encfile );
			processFeatures(reader);
					
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fearuredSpatials;
	}

	private void processFeatures(S57ModuleReader reader) {
		S57ObjectsVector features = reader.getFeatures();
		S57ObjectsVector spatials = reader.getSpatials();
		System.out.println("Found " + features.size() + " features for " +  spatials.size() + " spatials");
		
		for ( S57Object obj : features )
		{
			S57Feature feature = (S57Feature)obj;

			if ( E_S57ObjectPrimitiveType.byCode( feature.objectPrimitiveType.code ) ==  E_S57ObjectPrimitiveType.point )
			{
				
				if (  feature.linkToSpatials != null )
				{
					for ( Link l : feature.linkToSpatials )
					{
						S57Spatial s = (S57Spatial) spatials.searchByCode( l.name );
						if ( s != null && bbox.contains(s.coordinate.latitude, s.coordinate.longitude) )
						{
							addToFeaturedSpatialList(s, feature);
						}
					}
				}
			}
		}
		
		System.out.println("Created " +  fearuredSpatials.size() + " featured spatials");
	}

	private void addToFeaturedSpatialList(S57Spatial s, S57Feature f) {
		
		// Check if we already have this spatial in our list
		FeaturedSpatial featuredSpatial = null;
		for( FeaturedSpatial fs : fearuredSpatials )
		{
			if ( fs.spatial.name == s.name )
			{
				featuredSpatial = fs;
				break;
			}
		}
		if ( featuredSpatial == null )
		{
			featuredSpatial = new FeaturedSpatial(s);
			fearuredSpatials.add(featuredSpatial);
		}
		featuredSpatial.addFeature(f);
	}

}
