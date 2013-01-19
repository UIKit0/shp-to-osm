package com.scmarinetech.utils;

public class BoundingBox {
	
	final private double minx;
	final private double miny;
	final private double maxx;
	final private double maxy;
	
	public BoundingBox() {
		this.minx = -200;
		this.miny = -100;
		this.maxx = 200;
		this.maxy = 100;
	}

	public BoundingBox(double minx, double miny, double maxx, double maxy) {
		this.minx = minx;
		this.miny = miny;
		this.maxx = maxx;
		this.maxy = maxy;
	}

	public BoundingBox(String arg ) throws NumberFormatException 
	{
		String [] bbox = arg.split(",");
		if ( bbox.length != 4 )
		{
			throw new NumberFormatException();
		}
		
		this.minx = Double.parseDouble(bbox[0]);
		this.miny = Double.parseDouble(bbox[1]);
		this.maxx = Double.parseDouble(bbox[2]);
		this.maxy = Double.parseDouble(bbox[3]);
	}
	

	public boolean contains( double lat, double lon)
	{
		return lat >=  miny && lat <= maxy && lon >= minx && lon <= maxx;
	}
	
}
