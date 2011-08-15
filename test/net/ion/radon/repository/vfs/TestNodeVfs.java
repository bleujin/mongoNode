package net.ion.radon.repository.vfs;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.LocalSubDirFileProvider;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;
import net.ion.framework.vfs.VFileContent;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;
import net.sf.json.JSONObject;

import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.ram.RamFileProvider;
import org.apache.commons.vfs.provider.temp.TemporaryFileProvider;

public class TestNodeVfs extends TestBaseRepository {

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
		if (!entry.hasProvider("node")) {
// 			NodeFileSystemConfigBuilder.getInstance().setServer("61.250.201.78", 27017, "test", "wname") ;
			NodeFileProvider nodeFileProvider = new NodeFileProvider();
			nodeFileProvider.setServerHost("61.250.201.78") ;
			nodeFileProvider.setPort("27017") ;
			nodeFileProvider.setDBName("test") ;
			nodeFileProvider.setWorkspaceName("wname") ;
			entry.addProvider("node", nodeFileProvider) ;
		}
		
		if (!entry.hasProvider("ram")) {
			entry.addProvider("ram", new RamFileProvider());
		}
		session.changeWorkspace("wname") ;
		session.dropWorkspace() ;
		
	}

	
	@Override
	protected void tearDown() throws Exception {
		entry = null ;

		super.tearDown();
	}

	
	private void initNode() {
		Node node = session.newNode("dept").setAradonId("meta", "dept").put("name", "dept root").put("year", 20);
		Node dev = node.createChild("dev").put("name", "dev team").put("age", 20);
		Node secondboy = node.createChild("sales").put("name", "sales marketing").put("age", 20);
		
		dev.createChild("solution").put("name", "dev solution") ;
		
		session.commit(); 
	}




	public void testLocalFileName() throws Exception {
		initNode();
		String fileName = "afield://imsi";

		VFile newFile = entry.resolveFile(fileName);

		List<VFile> children = newFile.getChildren() ;
		for (VFile child : children) {
			Debug.debug(child.getFileObject().getName().getPath(), child.getName().getPath()) ;
		}
	}

	public void testRoot() throws Exception {
		VFile rootFile = entry.resolveFile("node://"); 

		assertEquals("", rootFile.getName().getBaseName()) ;
		assertEquals("/", rootFile.getName().getPath()) ;
		
		List<VFile> children = rootFile.getChildren() ;
		assertEquals(0, children.size()) ;
	}
	
	
	public void testFileName() throws Exception {
		initNode();
		String fileName = "node://dept/dev";

		VFile newFile = entry.resolveFile(fileName);
		
		assertEquals("dev", newFile.getName().getBaseName()) ;
		assertEquals("/dept/dev", newFile.getName().getPath()) ;
		assertEquals("node:///dept/dev", newFile.getName().toString()) ;
	}
	
	
	public void testRead() throws Exception {
		initNode();
		
		VFile dept = entry.resolveFile("node://dept");
		assertEquals(FileType.FOLDER, dept.getType()) ;
		assertEquals(true, dept.exists()) ;
		
		
		String fileName = "node://dept/dept.node";
		VFile deptNode = entry.resolveFile(fileName);
		
		
		Debug.line(deptNode.getType()) ;
		
		assertEquals(true, deptNode.exists()) ;
		Reader reader = new InputStreamReader(deptNode.getInputStream(), "UTF-8") ;
		String result = IOUtil.toString(reader);
		JSONObject jso = JSONObject.fromObject(result) ;
		assertEquals(20, jso.get("year")) ;
		
		VFile bigboy = entry.resolveFile("node://dept/dev");
		assertEquals(FileType.FOLDER, bigboy.getType()) ;
		//Debug.debug(IOUtil.toString(new InputStreamReader(bigboy.getInputStream(), "UTF-8"))) ;
	}
	
	public void testReadNotExist() throws Exception {
		String fileName = "node://not_exist";
		VFile notExistFile = entry.resolveFile(fileName);

		try {
			Reader reader = new InputStreamReader(notExistFile.getInputStream(), "UTF-8") ;
			fail() ;
		} catch (FileSystemException ignore) {
		}
	}
	

	
	
	public void testChildren() throws Exception {
		initNode();
		
		String fileName = "node://dept";
		VFile dept = entry.resolveFile(fileName);
		assertEquals(FileType.FOLDER, dept.getType()) ;

		List<VFile> children = dept.getChildren() ;
		assertEquals(3, children.size()) ;
		
		List<String> result = ListUtil.newList() ;
		for (VFile child : children) {
			result.add(child.getName().getPath()) ;
		}
		
		assertTrue(result.contains("/dept/dev"));
		assertTrue(result.contains("/dept/sales"));
		assertTrue(result.contains("/dept/dept.node"));
	}
	
	
	public void testGrandChild() throws Exception {
		initNode();

		VFile dept = entry.resolveFile("node://dept");
		
		VFile dev = dept.getChild("dev") ;
		
		assertEquals(true, dev.getFileObject() != null) ;
		assertEquals(FileType.FOLDER, dev.getType()) ;
		
		assertEquals("/dept/dev", dev.getName().getPath()) ;
		
		List<VFile> gchild = dev.getChildren();
		assertEquals(2, gchild.size()) ;
	}
	
	
	public void testCreate() throws Exception {
		initNode() ;
		String fileName = "node://dept/dev2";
		
		VFile dev2 = entry.resolveFile(fileName);
		assertEquals(FileType.FOLDER, dev2.getType()) ;
		assertEquals(FileType.FOLDER, dev2.getName().getType()) ;
		
		dev2.createFolder() ;
		assertEquals(true, dev2.exists()) ;
		
		
		VFile def2File = entry.resolveFile("node://dept/dev2/dev2.node") ;
		assertEquals(true, def2File.exists()) ;
		
		IOUtil.copyNClose(new StringReader("{name:'dev2 team', desc:'µ¥ºêÆÀ', date:'" + new Date().toString() + "'}"), def2File.getOutputStream(), "UTF-8") ;

		
		Node found = session.createQuery().eq("name", "dev2 team").findOne() ;
		assertEquals("dev2 team", found.getString("name")) ;
		
		Node foundByPath = session.createQuery().findByPath("/dept/dev2") ;
		
		Debug.debug(foundByPath) ;
		assertEquals("dev2 team", foundByPath.getString("name")) ;
		assertEquals("µ¥ºêÆÀ", foundByPath.getString("desc")) ;
	
	}

	
	public void testModify() throws Exception {
		initNode();
		
		VFile dept = entry.resolveFile("node://dept/dept.node");
		IOUtil.copyNClose(new StringReader("{greeting:'ÇÑ±Û', age:20}"), dept.getOutputStream(), "UTF-8") ;
		
		Node node = session.createQuery().findByPath("/dept") ;
		
		assertEquals("ÇÑ±Û", node.getString("greeting")) ;
		
	}
	
	
	public void testContentTypeAndSize() throws Exception {
		initNode();
		VFile dept = entry.resolveFile("node://dept/dept.node");

		VFileContent vc = dept.getContent() ;

		assertEquals("application/json", vc.getContentType()) ;
		assertEquals("utf-8", vc.getEncoding()) ;
		
		assertEquals(true, vc.getSize() > 0) ;
		assertEquals(IOUtil.toByteArray(vc.getInputStream()).length, vc.getSize()) ;
	}
	
	
	
	public void testDelete() throws Exception {
		initNode() ;
		session.newNode() ;
		session.commit() ;
		
		VFile dept = entry.resolveFile("node://dept/dept.node");
		
		dept.deleteSub() ;
		
		assertEquals(1, session.createQuery().find().count()) ;
	}
	
	
	
	public void testRename() throws Exception {
		initNode() ;
		
		
		String fileName = "node://bleujin";
		VFile bleujin = entry.resolveFile(fileName);
		
		
		bleujin.rename("hero") ;
		// Debug.debug(bleujin.getName()) ;
	}

	
}
