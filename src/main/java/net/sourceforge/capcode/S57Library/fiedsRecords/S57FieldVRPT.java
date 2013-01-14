/**
 * 
 */
package net.sourceforge.capcode.S57Library.fiedsRecords;

import java.util.Vector;

import net.sourceforge.capcode.S57Library.basics.S57FieldAnnotation;
import net.sourceforge.capcode.S57Library.fiedsRecords.descriptive.S57DDRDataDescriptiveField;

/**
Field Tag: VRPT Field Name: Vector Record Pointer<br>
Name *NAME A(12) B(40) an Foreign pointer (see 2.2)<br>
Orientation ORNT A(1) b11 <br>
	"F" {1} Forward <br>
	"R" {2} Reverse <br>
	"N" {255} NULL (see 5.1.3)<br>
Usage indicator USAG A(1) b11 <br>
	{1} Exterior "I" <br>
	{2} Interior "C" <br>
	{3} Exterior boundary truncated by the data limit<br>
	"N" {255} NULL (see 5.1.3)<br>
Topology indicator TOPI A(1) b11<br> 
	"B" {1} Beginning node<br>
	"E" {2} End node<br>
	"S" {3} Left face<br>
	"D" {4} Right face<br>
	"F" {5} Containing face<br>
	"N" {255} NULL (see 5.1.3)<br>
Masking indicator MASK A(1) b11<br> 
	"M" {1} Mask<br>
	"S" {2} Show<br>
	"N" {255} NULL<br>
(see 5.1.3) <br>
*/
public class S57FieldVRPT extends S57FieldVectorsLink {

	public S57FieldVRPT(String tag, byte[] fieldData,
			S57DDRDataDescriptiveField fieldDefinition) throws Exception {
		super(tag, fieldData, fieldDefinition);
	}
}
