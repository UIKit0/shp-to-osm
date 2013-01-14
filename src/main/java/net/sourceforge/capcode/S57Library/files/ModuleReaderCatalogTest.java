package net.sourceforge.capcode.S57Library.files;

import static org.junit.Assert.*;

import net.sourceforge.capcode.S57Library.objects.S57ConnectedNode;
import net.sourceforge.capcode.S57Library.objects.S57Edge;
import net.sourceforge.capcode.S57Library.objects.S57IsolatedNode;
import net.sourceforge.capcode.S57Library.objects.S57Spatial;

import org.junit.Test;

public class ModuleReaderCatalogTest {
	static S57ModuleReader mod;

	@Test
	public final void testLoad() throws Exception {
		mod = new S57ModuleReader("test/data/CATALOG.031");
	}
}
