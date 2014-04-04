package net.ion.bleujin.mongo;

import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.LocalSubDirFileProvider;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;

import org.apache.commons.vfs2.provider.ram.RamFileProvider;
import org.apache.commons.vfs2.provider.temp.TemporaryFileProvider;

public class TestVfs extends TestCase{

	private FileSystemEntry entry;

	public void setUp() throws Exception {
		super.setUp();
		entry = VFS.createEmpty();
		if (!entry.hasProvider("afield")) {
			LocalSubDirFileProvider aprovider = new LocalSubDirFileProvider();
			aprovider.setPrefixDir("c:/working/");
			entry.addProvider("afield", aprovider);
		}
		if (!entry.hasProvider("temp")) {
			entry.addProvider("temp", new TemporaryFileProvider());
		}
		if (!entry.hasProvider("ram")) {
			entry.addProvider("ram", new RamFileProvider());
		}
	}

	
	@Override
	protected void tearDown() throws Exception {
		entry = null ;
		super.tearDown();
	}
	
	public void testList() throws Exception {
		String fileName = "afield://imsi";

		VFile newFile = entry.resolveFile(fileName);

		List<VFile> children = newFile.getChildren() ;
		for (VFile child : children) {
			Debug.debug(child.getFileObject().getName().getPath(), child.getName().getPath()) ;
		}
	}
	
	

}
