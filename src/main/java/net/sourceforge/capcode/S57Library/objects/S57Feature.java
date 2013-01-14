/**
 * 
 */
package net.sourceforge.capcode.S57Library.objects;

import java.awt.Color;
import java.util.Vector;

import net.sourceforge.capcode.S57Library.basics.E_S57RecordType;
import net.sourceforge.capcode.S57Library.basics.Link;
import net.sourceforge.capcode.S57Library.basics.S52Color;
import net.sourceforge.capcode.S57Library.basics.S57Pos2D;
import net.sourceforge.capcode.S57Library.catalogs.E_S57Object;
import net.sourceforge.capcode.S57Library.catalogs.S57Attribute;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldFFPT;
import net.sourceforge.capcode.S57Library.fiedsRecords.S57FieldFRID;

/**
 * An object which contains the non-locational information about real world entities.
 * Feature objects are defined in Appendix A, IHO Object Catalogue.
 * in capcode, features are weighted by a priority code to specify the Z-order of the feature. 
 * The lower is the code, the earlier the object will be displayable and maybe overlapped by others.   
 */
public class S57Feature extends S57Object {
	/** stores the object information (code, className, etc) 
	 * @see E_S57Object*/
	public E_S57Object object;
	/** stores the object primitive type (area, line, point) 
	 * @see E_S57ObjectPrimitiveType*/
	public E_S57ObjectPrimitiveType objectPrimitiveType;
	/**a feature must have a world wide unique identifier (see 7.6.2 FOID)*/
	public long worldWideIdentifier;

	/* == attributes common to most features == */	
	/** the possible colors of an object */
	private Color[] colors = {Color.black};
	/** the unique color of a object*/
	protected Color color = Color.black;
	/**  the minimum scale at which the object should be visible */
	private int scaleMinimum = 0;
	/** information about the object */
	protected String information = "";
	/**national object name*/
	public String nationalName = "";
	/**object name*/
	public String intName = "";
	/** link to the associated features */
	public S57ObjectsVector linkedFeatures;
	/** link to the associated connected nodes (eg area and lines)*/
	public Vector<Link> linkToSpatials;
	
	/**
	 * creates a Feature with a priority of 0
	 */
	public S57Feature(){
		super();
	}

	/**
	 * set the world wide identifier of the feature.
	 * this is call only by the S-57 parser when FOID field is decoded.
	 * see IHO S-57 specification 7.6.2 (FOID)
	 * @param wwi
	 * @see net.sourceforge.capcode.S57Libray.fieldsRecords.S57FieldFOID
	 */
	public void setWorldWideIdentifier(long wwi){
		worldWideIdentifier = wwi;
	}
	
	public String toString(){
		return super.toString() 
		+ (object != null ? ";object:" + object : "")
		+ (objectPrimitiveType != null ? ";primitive:" + objectPrimitiveType : "")
		+ (linkedFeatures != null? linkedFeatures : "");
	}

	public void addFeatures(S57FieldFFPT fpt, S57ObjectsVector objects) {
		for (Object o : fpt.names){
			Long i = (Long)o;
			S57Object so = objects.searchByLongIdentifier(i);
			if (so != null){
				linkedFeatures().add(so);
			}
		}
	}

	private S57ObjectsVector linkedFeatures() {
		if( linkedFeatures == null){
			linkedFeatures = new S57ObjectsVector(1);
		}
		return linkedFeatures;
	}
	
	/**
	 * @param colors the colors to set
	 */
	public void setColor(Color[] colors) {
		this.colors = colors;
		if (colors.length>0){
			color = colors[0];
		}
	}

	/**
	 * @return the colors
	 */
	public Color[] getColors() {
		return colors;
	}

	/**
	 * @return the colors
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param scaleMinimum the scaleMinimum to set
	 */
	public void setScaleMinimum(int scaleMinimum) {
		this.scaleMinimum = scaleMinimum;
	}

	/**
	 * @return the scaleMinimum
	 */
	public int getScaleMinimum() {
		return scaleMinimum;
	}
			
	public void addVectors(Vector links) {
		//keep the link for later (draw the shape of the feature for example)
		if (linkToSpatials == null){
			linkToSpatials = new Vector<Link>();
		}
		linkToSpatials.addAll(links);
	}
	
	public static S57Feature buildFromFRID(S57FieldFRID frid) throws Exception {
		S57Feature res = null;
		if (frid.recordType != null && frid.recordType == E_S57RecordType.feature) {
				res = new S57Feature();
				res.object = frid.object;
				res.objectPrimitiveType = frid.objectPrimitiveType;
		}else{
			throw new S57WrongTypeException(frid.recordType + " not treated");				
		}
		if (res != null){
			res.name = (int) frid.name;
		}
		return res;
	}

}
