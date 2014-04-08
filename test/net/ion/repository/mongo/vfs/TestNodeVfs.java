package net.ion.repository.mongo.vfs;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;
import net.ion.framework.vfs.VFileContent;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.node.ReadNode;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

public class TestNodeVfs extends TestBaseReset {

	private FileSystemEntry entry;

	public void setUp() throws Exception {
		super.setUp();
		entry = VFS.createEmpty();

		if (!entry.hasProvider("node")) {
// 			NodeFileSystemConfigBuilder.getInstance().setServer("61.250.201.78", 27017, "test", "wname") ;
			NodeFileProvider nodeFileProvider = new NodeFileProvider();
			nodeFileProvider.setServerHost("61.250.201.157") ;
			nodeFileProvider.setPort("27017") ;
			nodeFileProvider.setDBName("test") ;
			nodeFileProvider.setWorkspaceName("wsname") ;
			entry.addProvider("node", nodeFileProvider) ;
		}
	}

	@Override
	protected void tearDown() throws Exception {
		entry = null ;
		super.tearDown();
	}

	
	public void testRoot() throws Exception {
		VFile rootFile = entry.resolveFile("node://"); 

		assertEquals("", rootFile.getName().getBaseName()) ;
		assertEquals("/", rootFile.getName().getPath()) ;
		
		List<VFile> children = rootFile.getChildren() ;
		assertEquals(0, children.size()) ;
	}
	
	private void createSampleNode() throws IOException {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/dept").property("name", "dept").property("year", 20)
					.child("dev").property("name", "dev").property("age", 20).parent()
					.child("sales").property("name", "sales").property("age", 20);
				wsession.pathBy("/dept/dev/solution").property("name", "dev solution") ;
				return null;
			}
		}) ;
	}

	public void testFileName() throws Exception {
		createSampleNode();
		
//		session.pathBy("/dept/dev").debugPrint();
		
		String fileName = "node://dept/dev";

		VFile newFile = entry.resolveFile(fileName);
		
		assertEquals("dev", newFile.getName().getBaseName()) ;
		assertEquals("/dept/dev", newFile.getName().getPath()) ;
		assertEquals("node:///dept/dev", newFile.getName().toString()) ;
	}
	
	
	public void testRead() throws Exception {
		createSampleNode();
		
		VFile dept = entry.resolveFile("node://dept");
		assertEquals(FileType.FOLDER, dept.getType()) ;
		assertEquals(true, dept.exists()) ;
		
		
		String fileName = "node://dept/dept.node";
		VFile deptNode = entry.resolveFile(fileName);
		assertEquals(FileType.FILE, deptNode.getType());
		
		assertEquals(true, deptNode.exists()) ;
		Reader reader = new InputStreamReader(deptNode.getInputStream(), "UTF-8") ;
		String result = IOUtil.toString(reader);
		JsonObject jso = JsonParser.fromString(result).getAsJsonObject() ;
		assertEquals(20, jso.asInt("year")) ;
		
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
		createSampleNode();
		
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
		createSampleNode();

		VFile dept = entry.resolveFile("node://dept");
		
		VFile dev = dept.getChild("dev") ;
		
		assertEquals(true, dev.getFileObject() != null) ;
		assertEquals(FileType.FOLDER, dev.getType()) ;
		
		assertEquals("/dept/dev", dev.getName().getPath()) ;
		
		List<VFile> gchild = dev.getChildren();
		assertEquals(2, gchild.size()) ;
	}

	public void testCreate() throws Exception {
		createSampleNode() ;
		String fileName = "node://dept/dev2";
		
		VFile dev2 = entry.resolveFile(fileName);
		assertEquals(FileType.FOLDER, dev2.getType()) ;
		assertEquals(FileType.FOLDER, dev2.getName().getType()) ;
		
		dev2.createFolder() ;
		assertEquals(true, dev2.exists()) ;
		
		
		VFile def2File = entry.resolveFile("node://dept/dev2/dev2.node") ;
		assertEquals(true, def2File.exists()) ;
		
		IOUtil.copyNClose(new StringReader("{name:'dev2 team', desc:'������', date:" + new Date().getTime() + "}"), def2File.getOutputStream(), "UTF-8") ;

		ReadNode found = session.pathBy("/dept/dev2");
		assertEquals("dev2 team", found.property("name").asString()) ;
		assertEquals("������", found.property("desc").asString()) ;
	}
	
	public void testModify() throws Exception {
		createSampleNode();
		
		VFile dept = entry.resolveFile("node://dept/dept.node");
		IOUtil.copyNClose(new StringReader("{greeting:'�ѱ�', age:20}"), dept.getOutputStream(), "UTF-8") ;
		
		ReadNode node = session.pathBy("/dept");
		assertEquals("�ѱ�", node.property("greeting").asString()) ;
	}
	
	
	public void testContentTypeAndSize() throws Exception {
		createSampleNode();
		VFile dept = entry.resolveFile("node://dept/dept.node");

		VFileContent vc = dept.getContent() ;

		assertEquals("application/json", vc.getContentType()) ;
		assertEquals("utf-8", vc.getEncoding()) ;
		
		assertEquals(true, vc.getSize() > 0) ;
		assertEquals(IOUtil.toByteArray(vc.getInputStream()).length, vc.getSize()) ;
	}
	

	public void testDelete() throws Exception {
		createSampleNode() ;
		session.collection().debugPrint();
		
		VFile dept = entry.resolveFile("node://dept/dept.node");
		dept.deleteSub() ;
		
		session.collection().debugPrint();
		
		assertEquals(1, session.root().children().count()) ;
	}

	public void testRename() throws Exception {
		createSampleNode() ;
		
		String fileName = "node://bleujin";
		VFile bleujin = entry.resolveFile(fileName);
		
		bleujin.rename("hero") ;
		// Debug.debug(bleujin.getName()) ;
	}
}
