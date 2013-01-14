package net.sourceforge.capcode.S57Library.objects;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Vector;

import net.sourceforge.capcode.S57Library.basics.Link;
import net.sourceforge.capcode.S57Library.basics.S57Pos2D;

/**
 * A one-dimensional spatial object, located by two or more coordinate pairs (or
 * two connected nodes) and optional interpolation parameters. If the parameters
 * are missing, the interpolation is defaulted to straight line segments between the
 * coordinate pairs. In the chain-node, planar graph and full topology data
 * structures, an edge must reference a connected node at both ends and must
 * not reference any other nodes
 */
public class S57Edge extends S57Spatial {
	public S57ConnectedNode begin;
	public S57ConnectedNode end;

	public Shape getPath(){
		if (path == null){
			path = new Path2D.Float();
				path.moveTo(
						begin.coordinate.longitude, 
						begin.coordinate.latitude);				

			if (positions != null){
				for (int i = 0; i < positions.size(); i++){
					S57Pos2D p = (S57Pos2D)positions.elementAt(i);
					path.lineTo(p.longitude, p.latitude);
				}	
			}
			
			path.lineTo(
					end.coordinate.longitude, 
					end.coordinate.latitude);
		}
		return path;
	}
	
	public Shape getInversedPath(){
		if (inversedPath == null){
			inversedPath = new Path2D.Float();
				inversedPath.moveTo(
						end.coordinate.longitude, 
						end.coordinate.latitude);				
				
			if (positions != null){
				for (int i = positions.size()-1; i>= 0; i--){
					S57Pos2D p = (S57Pos2D)positions.elementAt(i);
					inversedPath.lineTo(
							p.longitude, 
							p.latitude);
				}				
			}
			
			inversedPath.lineTo(
					begin.coordinate.longitude, 
					begin.coordinate.latitude);
		}
		return inversedPath;
	}
	
	public void addVectors(Vector links, S57ObjectsVector vectors) {
		Link l1 = (Link) links.elementAt(0);
		Link l2 = (Link) links.elementAt(1);
		begin = (S57ConnectedNode) (l1.isBeginingNode() ? vectors.searchByCode(l1.name) : null);
		end = (S57ConnectedNode) (l2.isEndNode() ? vectors.searchByCode(l2.name) : null);
	}
	
}
