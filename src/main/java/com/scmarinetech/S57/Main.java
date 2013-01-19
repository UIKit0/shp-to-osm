package com.scmarinetech.S57;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.scmarinetech.noaa.enc.NoaaDownloader;
import com.scmarinetech.utils.BoundingBox;

public class Main {

	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
        CommandLineParser parser = new GnuParser();
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("xmlurl")
                .withDescription("URL to NOAA XML file")
                .withArgName("URL")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("osmfile")
                .withDescription("Path to the output osmfile.")
                .withArgName("OSMFILE")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("bbox")
                .withDescription("Bounding box.")
                .withArgName("OSMFILE")
                .hasArg()
                .create());

        try {
			CommandLine line = parser.parse(options, args, false);
			String xmlUrl = line.getOptionValue("xmlurl");
			String osmfile = line.getOptionValue("osmfile");
			

			NoaaDownloader downloader = new NoaaDownloader();

			if ( line.hasOption("bbox")) 
			{
				BoundingBox bbox = new BoundingBox( line.getOptionValue("bbox") ); 
				downloader.setBoundBox( bbox );
			}
			
			downloader.downloadEncFiles(xmlUrl, new OSMChangeWriter( osmfile ) );
			
		} catch (ParseException e1) {
            System.err.println("Could not parse command line: " + e1.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp shp-to-osm.jar", options, true);
            System.exit(1);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		}
	}

}
