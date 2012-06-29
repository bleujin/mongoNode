package net.ion.radon.repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import net.ion.framework.util.Debug;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class TestNodeObject extends TestBaseRepository{

	
	public void testSerial() throws Exception {
		DBObject dbo = new BasicDBObject() ;
		dbo.put("name", "bleujin") ;
		
		NodeObject no = NodeObject.load(dbo) ;
		
		assertEquals("bleujin", no.get("name")) ;
		
		NodeObject read = (NodeObject) writeNRead(no);
		assertEquals("bleujin", read.get("name")) ;
	}

	public void testIndexInfo() throws Exception {
		List<NodeObject> infos = session.getCurrentWorkspace().getIndexInfo() ;
		
		List<NodeObject> read = (List<NodeObject>) writeNRead(infos);
		Debug.line(read) ;
	}

	

	private Object writeNRead(Object no) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream() ;
		ObjectOutputStream out = new ObjectOutputStream(bout) ;
		out.writeObject(no) ;
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray())) ;
		return in.readObject() ;
	}
	
}
