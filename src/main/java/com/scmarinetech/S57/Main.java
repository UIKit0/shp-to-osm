package com.scmarinetech.S57;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	/**
	 * @param args
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
        CommandLineParser parser = new GnuParser();
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("encfile")
                .withDescription("Path to the input encfile.")
                .withArgName("ENCFILE")
                .hasArg()
                .isRequired()
                .create());
        options.addOption(OptionBuilder.withLongOpt("osmfile")
                .withDescription("Path to the output osmfile.")
                .withArgName("OSMFILE")
                .hasArg()
                .isRequired()
                .create());

        try {
			CommandLine line = parser.parse(options, args, false);
			String encfile = line.getOptionValue("encfile");
			
			S57Reader reader = new S57Reader();
			List<FeaturedSpatial>  featuredSpatials = reader.readEncFile(encfile);

			String osmfile = line.getOptionValue("osmfile");
			OSMWriter osmWriter = new OSMWriter();
			try {
				osmWriter.write( featuredSpatials, osmfile );
			} catch (IOException e) {
	            System.err.println("Failed to create osm file " + e.getMessage());
			}
			
		} catch (ParseException e1) {
            System.err.println("Could not parse command line: " + e1.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -cp shp-to-osm.jar", options, true);
            System.exit(1);
		}

	}

}
