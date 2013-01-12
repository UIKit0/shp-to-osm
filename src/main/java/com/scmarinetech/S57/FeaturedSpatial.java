package com.scmarinetech.S57;

import java.util.LinkedList;
import java.util.List;

import net.sourceforge.capcode.S57Library.objects.S57Feature;
import net.sourceforge.capcode.S57Library.objects.S57Spatial;

class FeaturedSpatial{
	final S57Spatial spatial;
	final private List<S57Feature> features;
	FeaturedSpatial(S57Spatial spatial)
	{
		this.spatial = spatial;
		this.features = new LinkedList<S57Feature>();
	}
	public void addFeature(S57Feature f)
	{
		if ( !this.features.contains(f) )
		{
			this.features.add(f);
		}
	}
	public List<S57Feature>  getFeatures() {
		return features;
	}
}