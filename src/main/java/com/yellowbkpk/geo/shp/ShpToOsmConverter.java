package com.yellowbkpk.geo.shp;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.yellowbkpk.osm.output.OSMOutputter;
import com.yellowbkpk.osm.primitive.Primitive;
import com.yellowbkpk.osm.primitive.Tag;
import com.yellowbkpk.osm.primitive.node.Node;
import com.yellowbkpk.osm.primitive.way.Way;
import com.yellowbkpk.osm.relation.Member;
import com.yellowbkpk.osm.relation.Relation;

public class ShpToOsmConverter {
    
    private static Logger log = Logger.getLogger(ShpToOsmConverter.class.getName());

    private static final int MAX_NODES_IN_WAY = 2000;
    private File inputFile;
    private RuleSet ruleset;
    private boolean onlyIncludeTaggedPrimitives;
    private OSMOutputter outputter;

    public ShpToOsmConverter(File shpFile, RuleSet rules, boolean onlyIncludeTaggedPrim, OSMOutputter out) {
        inputFile = shpFile;
        outputter = out;
        
        this.ruleset = rules;
        this.onlyIncludeTaggedPrimitives = onlyIncludeTaggedPrim;
    }

    public void convert() throws ShpToOsmException {

        CoordinateReferenceSystem targetCRS = buildTargetCRS();

        ShapefileDataStore dataStore = null;
        CoordinateReferenceSystem sourceCRS = null;
        MathTransform transform = null;
        try {
            // Connection parameters
            Map<String, Serializable> connectParameters = new HashMap<String, Serializable>();

            connectParameters.put("url", inputFile.toURI().toURL());
            connectParameters.put("create spatial index", false);
            dataStore = (ShapefileDataStore) DataStoreFinder.getDataStore(connectParameters);

            sourceCRS = dataStore.getSchema().getCoordinateReferenceSystem();
            if (sourceCRS == null) {
                throw new ShpToOsmException("Could not determine the shapefile's projection. " +
                		"More than likely, the .prj file was not included.");
            } else {
                log.log(Level.CONFIG, "Converting from " + sourceCRS + " to " + targetCRS);
            }

            transform = CRS.findMathTransform(sourceCRS, targetCRS, true);
        } catch (MalformedURLException e) {
            throw new ShpToOsmException("URL could not be created for input file.", e);
        } catch (IOException e) {
            throw new ShpToOsmException("Could not read input file.", e);
        } catch (FactoryException e) {
            throw new ShpToOsmException("Could not find a way to transform to lat/lon.", e);
        }
            

        // we are now connected
        String[] typeNames = dataStore.getTypeNames();
        for (String typeName : typeNames) {
            log.log(Level.FINER, "Converting " + typeName);

            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource;
            FeatureCollection<SimpleFeatureType, SimpleFeature> collection;
            FeatureIterator<SimpleFeature> iterator = null;

            try {
                featureSource = dataStore.getFeatureSource(typeName);
                collection = featureSource.getFeatures();
                iterator = collection.features();

                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();

                    Geometry rawGeom = (Geometry) feature.getDefaultGeometry();
                    
                    String geometryType = rawGeom.getGeometryType();

                    // Transform to spherical mercator
                    Geometry geometry = null;
                    try {
                        geometry = JTS.transform(rawGeom, transform);
                    } catch (TransformException e) {
                        throw new ShpToOsmException("Could not transform to spherical mercator.", e);
                    }

                    if ("MultiLineString".equals(geometryType)) {

                        for (int i = 0; i < geometry.getNumGeometries(); i++) {
							LineString geometryN = (LineString) geometry
									.getGeometryN(i);

							List<Way> ways = linestringToWays(geometryN);
							ruleset.applyLineRules(feature, geometryType, ways);
							for (Way way : ways) {

								if (shouldInclude(way)) {
									outputter.addWay(way);
								}
							}
						}

                    } else if ("MultiPolygon".equals(geometryType)) {

                        for (int i = 0; i < geometry.getNumGeometries(); i++) {
                            Polygon geometryN = (Polygon) geometry.getGeometryN(i);

                            // Get the outer ring of the polygon
                            LineString outerLine = geometryN.getExteriorRing();

                            List<Way> outerWays = polygonToWays(outerLine);

                            if (geometryN.getNumInteriorRing() > 0) {
                                Relation r = new Relation();
                                r.addTag(new Tag("type", "multipolygon"));
                                
                                // Tags go on the relation for multipolygons

                                ruleset.applyOuterPolygonRules(feature, geometryType, Arrays.asList(r));

                                for (Primitive outerWay : outerWays) {
                                    // Always include every outer way
                                    r.addMember(new Member(outerWay, "outer"));
                                }

                                // Then the inner ones, if any
                                for (int j = 0; j < geometryN.getNumInteriorRing(); j++) {
                                    LineString innerLine = geometryN.getInteriorRingN(j);

                                    List<Way> innerWays = polygonToWays(innerLine);

                                    ruleset.applyInnerPolygonRules(feature, geometryType, innerWays);
                                    
                                    for (Way innerWay : innerWays) {
                                        r.addMember(new Member(innerWay, "inner"));
                                    }

                                }
                                
                                if (shouldInclude(r)) {
                                    outputter.addRelation(r);
                                }

                            } else {
                                // If there's more than one way, then it
                                // needs to be a multipolygon and the
                                // tags need to be applied to the
                                // relation
                                if(outerWays.size() > 1) {
                                    Relation r = new Relation();
                                    r.addTag(new Tag("type", "multipolygon"));

                                    ruleset.applyOuterPolygonRules(feature, geometryType, Arrays.asList(r));

                                    for (Way outerWay : outerWays) {
                                        if (shouldInclude(outerWay)) {
                                            r.addMember(new Member(outerWay, "outer"));
                                        }
                                    }

                                    if (shouldInclude(r)) {
                                        outputter.addRelation(r);
                                    }
                                } else {
                                    // If there aren't any inner lines, then
                                    // just use the outer one as a way.
                                    ruleset.applyOuterPolygonRules(feature, geometryType, outerWays);

                                    for (Way outerWay : outerWays) {
                                        if (shouldInclude(outerWay)) {
                                            outputter.addWay(outerWay);
                                        }
                                    }
                                }
                            }
                        }
                    } else if ("Point".equals(geometryType)) {
                        List<Node> nodes = new ArrayList<Node>(geometry.getNumGeometries());
                        for (int i = 0; i < geometry.getNumGeometries(); i++) {
                            Point geometryN = (Point) geometry.getGeometryN(i);

                            Node n = pointToNode(geometryN);

                            nodes.add(n);
                        }

                        ruleset.applyPointRules(feature, geometryType, nodes);

                        for (Node node : nodes) {
                            if (shouldInclude(node)) {
                                outputter.addNode(node);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (iterator != null) {
                    // YOU MUST CLOSE THE ITERATOR!
                    iterator.close();
                }
            }

        }

    }

    private CoordinateReferenceSystem buildTargetCRS() throws ShpToOsmException {
        CoordinateReferenceSystem targetCRS = null;
        try {
            targetCRS = CRS
            .parseWKT("GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563,AUTHORITY[\"EPSG\",\"7030\"]],AUTHORITY[\"EPSG\",\"6326\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4326\"]]");
        } catch (FactoryException e) {
            throw new ShpToOsmException("Could not build the target CRS from WKT.", e);
        }
        return targetCRS;
    }

    private boolean shouldInclude(Primitive w) {
        if (onlyIncludeTaggedPrimitives) {
            return w.hasTags() && ruleset.includes(w);
        } else {
            return ruleset.includes(w);
        }
    }

    private Node pointToNode(Point geometryN) {
        Coordinate coord = geometryN.getCoordinate();
        return new Node(coord.y, coord.x);
    }

    private static List<Way> linestringToWays(LineString geometryN) {
        Coordinate[] coordinates = geometryN.getCoordinates();
        
        // Follow the 2000 nodes per way max rule
        int waysToCreate = coordinates.length / MAX_NODES_IN_WAY;
        waysToCreate += (coordinates.length % MAX_NODES_IN_WAY == 0) ? 0 : 1;

        List<Way> ways = new ArrayList<Way>(waysToCreate);
        
        Way way = new Way();

        int nodeCount = 0;
        for (Coordinate coord : coordinates) {
            Node node = new Node(coord.y, coord.x);
            way.addNode(node);
            
            if(++nodeCount % MAX_NODES_IN_WAY == 0) {
            	ways.add(way);
            	way = new Way();
            	way.addNode(node);
            }
        }
        
        // Add the last way to the list of ways
        if(way.nodeCount() > 0) {
        	ways.add(way);
        }

        return ways;
    }

    private static List<Way> polygonToWays(LineString geometryN) throws ShpToOsmException {
        Coordinate[] coordinates = geometryN.getCoordinates();
        if(coordinates.length < 2) {
            throw new ShpToOsmException("Way with less than 2 nodes.");
        }

        // Follow the 2000 max nodes per way rule
        int waysToCreate = coordinates.length / MAX_NODES_IN_WAY;
        waysToCreate += (coordinates.length % MAX_NODES_IN_WAY == 0) ? 0 : 1;

        List<Way> ways = new ArrayList<Way>(waysToCreate);
        
        Way way = new Way();
        
        // First node for the polygon
        Coordinate firstCoord = coordinates[0];
        Node firstNode = new Node(firstCoord.y, firstCoord.x);
        way.addNode(firstNode);

        // "middle" nodes
        for (int i = 1; i < coordinates.length-1; i++) {
            Coordinate coord = coordinates[i];

            Node node = new Node(coord.y, coord.x);
            way.addNode(node);
            
            if(i % (MAX_NODES_IN_WAY - 1) == 0) {
            	ways.add(way);
            	way = new Way();
            	way.addNode(node);
            }
        }
        
        // Last node should be the same ID as the first one
        Coordinate lastCoord = coordinates[coordinates.length-1];
        if(lastCoord.x == firstCoord.x && lastCoord.y == firstCoord.y) {
            way.addNode(firstNode);
        }
        
        // Add the last way to the list of ways
        if(way.nodeCount() > 0) {
        	ways.add(way);
        }
        
        return ways;
    }

}
