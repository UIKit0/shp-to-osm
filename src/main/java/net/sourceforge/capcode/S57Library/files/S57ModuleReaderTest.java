package net.sourceforge.capcode.S57Library.files;

import static org.junit.Assert.*;

import net.sourceforge.capcode.S57Library.objects.S57ConnectedNode;
import net.sourceforge.capcode.S57Library.objects.S57Edge;
import net.sourceforge.capcode.S57Library.objects.S57IsolatedNode;
import net.sourceforge.capcode.S57Library.objects.S57Spatial;

import org.junit.Test;

public class S57ModuleReaderTest {
	static S57ModuleReader mod;

	@Test
	public final void testLoad() throws Exception {
		mod = new S57ModuleReader();
		mod.load("test/data/US3EC09M.000");
		int nr = 0;
		int nv = 0;
		int nbFaces = 0;
		int nbEdges = 0;
		int nbIsols = 0;
		int nbConnecteds = 0;
		for (Object o : mod.getSpatials()){
			S57Spatial v = (S57Spatial) o;
			nv ++;
			if (v instanceof S57Edge){
				nbEdges++;
			}else if (v instanceof S57IsolatedNode){
				nbIsols++;
			}else if (v instanceof S57ConnectedNode){
				nbConnecteds++;
			}
			if (v.name == -1){
				System.err.println(v);
				System.exit(-1);
			}
		}
		float percent = (float) nr / (float) nv; 
		System.out.println("cartos:" + mod.getNumberOfCartographicRecords());
		System.out.println("faces : " + mod.getNumberOfFaceRecords());
		System.out.println("collections:" + mod.getNumberOfCollectionrecords());
		System.out.println("connected nodes:" + mod.getNumberOfConnectedNodes());
		System.out.println("edges:" + mod.getNumberOfEdgeRecords());
		System.out.println("geo records:" + mod.getNumberOfGeoRecords());
		System.out.println("isol nodes records:" + mod.getNumberOfIsolatedNodes());
		System.out.println("meta records:" + mod.getNumberOfMetaRecords());

		assertEquals("face records", mod.getNumberOfFaceRecords(), nbFaces);
		assertEquals("edges records", mod.getNumberOfEdgeRecords(), nbEdges);
		assertEquals("isolated node records", mod.getNumberOfIsolatedNodes(), nbIsols);
		assertEquals("connected node records", mod.getNumberOfConnectedNodes(), nbConnecteds);
		
	}
}
