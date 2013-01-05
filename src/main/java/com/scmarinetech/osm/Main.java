package com.scmarinetech.osm;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
        CommandLineParser parser = new GnuParser();
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("osmdir")
                .withDescription("Path to the directory containg osm files.")
                .withArgName("OSMDIR")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("bbox")
                .withDescription("minx miny maxx maxy")
                .withArgName("BBOX")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("uid")
                .withDescription("User id")
                .withArgName("UID")
                .hasArg()
                .isRequired()
                .create());
        
        try {
			CommandLine line = parser.parse(options, args, false);
			File osmdir = new File ( line.getOptionValue("osmdir") );

			String [] bbox = line.getOptionValue("bbox").split("\\s+");
			if ( bbox.length != 4 )
			{
	            System.err.println("bbox must have four elements");
	            HelpFormatter formatter = new HelpFormatter();
	            formatter.printHelp("java -cp shp-to-osm.jar", options, true);
	            System.exit(1);
			}
			
			double minx = Double.parseDouble(bbox[0]);
			double miny = Double.parseDouble(bbox[1]);
			double maxx = Double.parseDouble(bbox[2]);
			double maxy = Double.parseDouble(bbox[3]);

			int uid = Integer.parseInt( line.getOptionValue("uid") );
			
			OsmUploader.uploadOsmFiles(osmdir, minx, miny, maxx, maxy, uid);
						
		} catch (NumberFormatException e) {
            System.err.println("Could not parse number " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp shp-to-osm.jar", options, true);
            System.exit(1);
		} catch (ParseException e1) {
            System.err.println("Could not parse command line: " + e1.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp shp-to-osm.jar", options, true);
            System.exit(1);
		}

	}

}
