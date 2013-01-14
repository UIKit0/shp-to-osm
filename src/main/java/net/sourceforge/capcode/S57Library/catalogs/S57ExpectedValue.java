/**
 * 
 */
package net.sourceforge.capcode.S57Library.catalogs;

/**
 * @author cyrille
 *
 */
public class S57ExpectedValue {
	public int attribute;
	public int code;
	public String meaning;
	
	public S57ExpectedValue(int attribute , int code, String value){
		this.attribute = attribute;
		this.code = code;
		this.meaning = value;
	}
	
	public String toString(){
		return meaning;
	}

}
