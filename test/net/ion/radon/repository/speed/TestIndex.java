package net.ion.radon.repository.speed;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.repository.NodeObject;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.TestOnlyWorkspace;

import org.bson.types.ObjectId;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class TestIndex extends TestBaseRepository{

	public void testNotExistUniqueIndex() throws Exception {
		session.newNode().put("ename", "bleujin").put("index", 1) ;
		session.commit() ;
		for (int i : ListUtil.rangeNum(5)) {
			session.createQuery().eq("index", i).updateChain().put("index", i).put("ename", i).merge() ;
		}
		assertEquals(5, session.createQuery().find().count()) ;
	}
	
	public void testWhenExistUinqueIndex() throws Exception {
		// session.getCurrentWorkspace().makeIndex(PropertyFamily.create("ename", -1), "ename_idx", true) ;

		for (int i : ListUtil.rangeNum(5)) {
			session.createQuery().eq("index", i).updateChain().put("index", i).put("myname", i).merge() ;
		}
		assertEquals(5, session.createQuery().find().count()) ;
	}
	
	public void testMerge() throws Exception {
		session.createQuery().eq("emp", 1).updateChain().put("address", "busan").put("index", 1).put("d1", "d1").merge() ;
		session.createQuery().eq("emp", 2).updateChain().put("address", "busan").put("index", 1).put("d1", "d1").merge() ;
		assertEquals(2, session.createQuery().find().count()) ;
	}

	public void testMerge2() throws Exception {
		session.createQuery().aradonGroupId("emp", 1).updateChain().put("address", "busan").put("index", 1).put("d1", "d1").merge() ;
		session.createQuery().aradonGroupId("emp", 2).updateChain().put("address", "seoul").put("index", 2).put("d2", "d2").merge() ;
		assertEquals(2, session.createQuery().find().count()) ;
	}
	
	public void testMerge3() throws Exception {
		session.createQuery().aradonGroup("emp").eq("emp", 1).updateChain().put("address", "busan").put("index", 1).put("d1", "d1").merge() ;
		session.createQuery().aradonGroup("emp").eq("emp", 2).updateChain().put("address", "seoul").put("index", 2).put("d2", "d2").merge() ;
		assertEquals(2, session.createQuery().find().count()) ;
	}

}
