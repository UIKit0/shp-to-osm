/**
 * 
 */
package net.sourceforge.capcode.S57Library.fiedsRecords;

import java.util.Vector;

import net.sourceforge.capcode.S57Library.basics.S57FieldAnnotation;
import net.sourceforge.capcode.S57Library.fiedsRecords.descriptive.S57DDRDataDescriptiveField;

/**
 * Field Tag: FSPT Field Name: Feature Record to Spatial Record Pointer Subfield name Label Format
 * Name *NAME A(12) B(40) an Foreign pointer (see 2.2)
 * Orientation ORNT A(1) b11 
 * 	"F" {1} Forward 
 * 	"R" {2} Reverse
 * 	"N" {255} NULL
 * Usage indicator USAG A(1) b11 
 * 	"E" {1} Exterior	
 * 	"I" {2} Interior
 * 	"C" {3} Exterior boundary truncated by the data limit
 * 	"N" {255} NULL
 * Masking indicator MASK A(1) b11 
 * 	"M" {1} Mask
 * 	"S" {2} Show
 * "N" {255} NULL */
public class S57FieldFSPT extends S57FieldVectorsLink {
	public S57FieldFSPT(String tag, byte[] fieldData,
			S57DDRDataDescriptiveField fieldDefinition) throws Exception {
		super(tag, fieldData, fieldDefinition);
	}

}
