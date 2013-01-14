/**
 * 
 */
package net.sourceforge.capcode.S57Library.catalogs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

/**
 * @author cyrille
 *
 */
public class S57CatalogExpectedValues extends Vector<S57ExpectedValue>{
	/** 
	 * the static list of all possible S57 expectd values. 
	 */
	public static S57CatalogExpectedValues list;
	static{
		try {
			list = new S57CatalogExpectedValues("data/s57expectedInput.csv");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	public S57CatalogExpectedValues(String fileName) throws FileNotFoundException{
		super(200);
		Scanner scanner = new Scanner(new File(fileName));
		//skip header
		scanner.nextLine();
		while(scanner.hasNextLine()){
			String[] fields = scanner.nextLine().split(",");
			if (fields.length >= 3){
				S57ExpectedValue v = new S57ExpectedValue(Integer.parseInt(fields[0]), Integer.parseInt(fields[1]), fields[2]);				
				super.add(v);
			}
		}
		scanner.close();
	}
	/**
	 * @param attribute : the code of the S57 attribute(eg: 113)
	 * @param code : the code of the expected input id for this attribute
	 * @return a String giving the meaning of the code
	 */
	public static String getByCode(int attribute, int code){
		for (S57ExpectedValue element : list){
			if (element.attribute == attribute && element.code == code){
				return element.meaning;
			}
		}
		return null;
	}

}
