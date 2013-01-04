#!/bin/sh

JAR_FILE=target/shp-to-osm-0.8.8-SNAPSHOT-jar-with-dependencies.jar
SHAPE_FILE=~/tmp/noaa.zip
OUT_DIR=~/tmp/noaa-gis-osm

maxlat=38.4116493
maxlon=-121.523409
minlat=36.503462
minlon=-123.6258341

# Cleanup 
rm -rf ${OUT_DIR}
mkdir -p ${OUT_DIR}

# Download shape file from NOAA
java -cp ${JAR_FILE} com.scmarinetech.noaa.gis.Main \
--shapefile ${SHAPE_FILE} \
--band APPROACH_HARBOR \
--scale  APPROACH \
--bbox	"${minlon} ${minlat} ${maxlon} ${maxlat}" \
--objlist s-57-import-list.txt


# Convert shape file to OSM 
java -cp ${JAR_FILE} com.yellowbkpk.geo.shp.Main \
--shapefile ${SHAPE_FILE} \
--keepOnlyTagged \
--osmfile noaa_gis_ \
--outdir ${OUT_DIR} \
--outputFormat osm \
--shapeSuffix _point \
--rulesfile noaa_gis-to-open_sea_map-rules.txt 



