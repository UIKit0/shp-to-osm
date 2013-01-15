package net.sourceforge.capcode.S57Library.catalogs;

/**
 * This enumeration provide a means to get the name, accronym, type and class for a given code.
 * These values are read from the file data/s57attributes.csv
 */
public class S57CatalogAttributeElement {
	enum Type {Enumerated, List, Float,Integer, Coded_String,  };
	
	/** the attribute code */
	public int code;
	/** the attribute name */
	public String name;
	/** the acronym of the attribute */
	public String accronym;
	/** the type of attribute 
	 * Enumerated ("E") - the expected input is a number selected from a list of predefined attribute values; exactly one value must be chosen.
	 * List ("L") - the expected input is a list of one or more numbers selected from a list of pre-defined attribute values.
	 * Float ("F") - the expected input is a floating point numeric value with defined range, resolution, units and format. 
	 * Integer ("I") - the expected input is an integer numeric value with defined range, units and format. 
	 * Coded String ("A") - the expected input is a string of ASCII characters in a predefined format; the information is encoded according to defined coding systems. 
	 * Free Text ("S") - the expected input is a free-format alphanumeric string; it may be a file name which points to a text or graphic file. 
	 * 
   */
	public char type; 
	
	public S57CatalogAttributeElement(int code, String name, String accronym, char type){
		this.code = code;
		this.name = name;
		this.accronym = accronym;
		this.type = type;
	}
	
	public String toString(){
		return name;
	}
}
