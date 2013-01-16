package com.scmarinetech.S57;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.sourceforge.capcode.S57Library.catalogs.S57Attribute;
import net.sourceforge.capcode.S57Library.objects.S57Feature;

public class S57ToOSMConverter {

	private final SeaMarkNodeSink sink;
	private final S57ToOsmMapper map;
	private int nodeId = -1;
	
	public S57ToOSMConverter(SeaMarkNodeSink sink)
	{
		this.sink = sink;
		this.map = new S57ToOsmMapper();
	}
	
	public void doConversion( List<FeaturedSpatial> featuredSpatials ) throws NoSuchAlgorithmException {
	
		sink.onDataSetStart();
		
		for ( FeaturedSpatial fs: featuredSpatials)
		{
			SeaMarkNode seaMarkNode  = createNode(nodeId, fs);
			if ( seaMarkNode != null )
			{
				sink.onNodeDecoded( seaMarkNode );
				nodeId --;
			}
		}
		
		sink.onDataSetEnd();
	}

	private SeaMarkNode createNode(int id, FeaturedSpatial fs) throws NoSuchAlgorithmException {
		
		SeaMarkNode seaMarkNode = new SeaMarkNode(id, fs.spatial.coordinate.latitude, fs.spatial.coordinate.longitude);
		
		String masterName = null;
		String name = null;
		long wwi = 0;
		long masterwwi = 0;
		for ( S57Feature f : fs.getFeatures() )
		{
			name = map.getObjectName(f.object.code);
			wwi = f.worldWideIdentifier; 
			if ( name != null )
			{
				addAttributes(seaMarkNode, f);
				if ( f.linkedFeatures != null )
				{
					masterName = name;
					masterwwi = f.worldWideIdentifier; 
				}
			}
		}
		
		if ( name != null )
		{
			seaMarkNode.closeNode( masterName != null ? masterName : name ,
					               masterwwi  != 0    ? masterwwi  :  wwi  );
			return seaMarkNode;
		}
		else
		{
			return null;
		}
		
	}

	private void addAttributes(SeaMarkNode seaMarkNode, S57Feature feature) {
		for ( S57Attribute attr : feature.attributes )
		{
			if (     map.getAttrName(attr.name.code) != null 
					&& attr.value != null 
					&& map.getAttrValue(attr.name.code, attr.name.type, attr.value ) != null)
			{
				seaMarkNode.addTag(
						 map.getObjectName(feature.object.code),
						 map.getAttrName(attr.name.code),  
						 map.getAttrValue(attr.name.code, attr.name.type, attr.value )
					);
			}
		}
	}

}
