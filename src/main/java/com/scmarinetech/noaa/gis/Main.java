package com.scmarinetech.noaa.gis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        options.addOption(OptionBuilder.withLongOpt("shapefile")
                .withDescription("Path to the output shapefile.")
                .withArgName("SHPFILE")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("band")
                .withDescription("band, e.g. APPROACH_HARBOR")
                .withArgName("BAND")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("scale")
                .withDescription("scale, e.g. APPROACH")
                .withArgName("SCALE")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("bbox")
                .withDescription("minx miny maxx maxy")
                .withArgName("BBOX")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("objlist")
                .withDescription("File containig S-57 objects abbreviations to be downloaded")
                .withArgName("OBJLIST")
                .hasArg()
                .isRequired()
                .create());
        
        try {
			CommandLine line = parser.parse(options, args, false);
			String outFilename = line.getOptionValue("shapefile");
			String band = line.getOptionValue("band");
			String scale = line.getOptionValue("scale");
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

			String objlistFile = line.getOptionValue("objlist");
			try {
				List<String>  objNames  = readObjectNamesFile( objlistFile );

				NoaaDownloader.downloadShapeFile(outFilename, band, scale, minx, miny, maxx, maxy, objNames);

			} catch (IOException e) {
	            System.err.println("Failed to read obect list file " + objlistFile + e.getLocalizedMessage() );
	            HelpFormatter formatter = new HelpFormatter();
	            formatter.printHelp("java -cp shp-to-osm.jar", options, true);
	            System.exit(1);
			}
						
		} catch (NumberFormatException e) {
            System.err.println("Could not parse command line: " + e.getMessage());
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

	/**
	 * Reads the list of S-57 object names to be downloaded
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static List<String> readObjectNamesFile(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        List<String> objNames = new ArrayList<String>();

        while ((line = br.readLine()) != null) {
            
            String trimmedLine = line.trim();
            
            // Skip comments
            if(trimmedLine.startsWith("#")) {
                continue;
            }
            
            // Skip empty lines
            if("".equals(trimmedLine)) {
                continue;
            }
            
            objNames.add(trimmedLine);
        }
        
        br.close();
		return objNames;
	}

}
