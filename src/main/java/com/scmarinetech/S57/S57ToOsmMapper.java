package com.scmarinetech.S57;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.commons.lang.StringEscapeUtils;

public class S57ToOsmMapper {

	private class Attribute 
	{
		HashMap<Integer,String> values;
		String name;
		Attribute(String name)
		{	
			this.name = name;
			this.values = new HashMap<Integer, String>();
		}
	}
	
	HashMap<Integer,String> objNamesMap;
	HashMap<Integer,Attribute> attrNamesMap;
	public S57ToOsmMapper()
	{
		objNamesMap = new HashMap<Integer, String>();
		attrNamesMap = new HashMap<Integer, Attribute>();
		Scanner scanner;
		try {
			scanner = new Scanner(new File("data/s57_to_osm_objects.csv"));
			//skip header
			scanner.nextLine();
			while(scanner.hasNextLine()){
				String[] fields = scanner.nextLine().split(",");
				objNamesMap.put(Integer.decode(fields[0]), fields[1].trim() );	
			}
			scanner.close();
			
			
			scanner = new Scanner(new File("data/s57_to_osm_attrs.csv"));
			//skip header
			scanner.nextLine();
			while(scanner.hasNextLine()){
				String[] fields = scanner.nextLine().split("\\|");
				Attribute attribute = new Attribute(fields[1].trim() );
				attrNamesMap.put(Integer.decode(fields[0]), attribute );
				
				for ( int i = 2; i < fields.length; i+=2 )
				{
					attribute.values.put(Integer.decode(fields[i]), fields[i+1].trim());
				}
				
			}
			scanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	public String getObjectName(int code) {
		return objNamesMap.get( code );
	}
	
	public String getAttrName(int code) {
		if ( attrNamesMap.containsKey( code ) )
			return attrNamesMap.get( code ).name;
		else
			return null;
	}
	
	public String getAttrValue(int code, char type, String value) {
		Attribute attr = attrNamesMap.get( code );
		String out = value;
		/* the type of attribute 
		 * Enumerated ("E") - the expected input is a number selected from a list of predefined attribute values; exactly one value must be chosen.
		 * List ("L") - the expected input is a list of one or more numbers selected from a list of pre-defined attribute values.
		 * Float ("F") - the expected input is a floating point numeric value with defined range, resolution, units and format. 
		 * Integer ("I") - the expected input is an integer numeric value with defined range, units and format. 
		 * Coded String ("A") - the expected input is a string of ASCII characters in a predefined format; the information is encoded according to defined coding systems. 
		 * Free Text ("S") - the expected input is a free-format alphanumeric string; it may be a file name which points to a text or graphic file. 
		 * 
	   */
		try {  
		
			switch( type )
			{
			case 'E':
			{
				out = attr.values.get( Integer.parseInt(value ) );
			}
			break;
			case 'L':
			{
				String[] fields = value.split(",");
				String s = attr.values.get( Integer.parseInt( fields[0] ) );
				StringBuilder sb = new StringBuilder( );
				if ( s != null )
				{
					sb .append( s );
				}
				for ( int i = 1; i < fields.length ; i++ )
				{
					String s1 = attr.values.get( Integer.parseInt( fields[i]));
					if ( s1 != null )
					{
						sb .append(";");
						sb .append( s1 );
					}
				}
				if ( sb.length() > 0 )
					out = sb.toString();
				else
					out = null;
			}
			break;
			default:
				out = StringEscapeUtils.escapeXml(value);
			break;
		}
		}catch ( NumberFormatException e )
		{
			return null;
		}
		
		return out;
	}

}
