package net.ion.radon.repository.perf;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

public class TestCreateNode extends TestCase {

	private RepositoryCentral rc ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.rc = RepositoryCentral.testCreate() ;
	}
	
	public void testInsert() throws Exception {
		final Session session = rc.testLogin("speed");
		session.dropWorkspace();
		
		long start = System.currentTimeMillis() ;
		for (int i : ListUtil.rangeNum(20000)) {
			Node node = session.newNode() ;
			node.put( "a" + RandomUtil.nextRandomString(100), RandomUtil.nextRandomString(900));
			session.commit();

//			if (i % 100 == 0) {
//				session.commit();
//			}
		}
		session.commit() ;
		Debug.line(System.currentTimeMillis()- start) ;
		
		Debug.line(session.createQuery().count());
	}
	
	public void testFind() throws Exception {
		final Session session = rc.testLogin("speed");
		long start = System.currentTimeMillis() ;
		for (int i : ListUtil.rangeNum(10000)) {
			NodeCursor nc = session.createQuery().eq("_id", "a" + RandomUtil.nextRandomString(100)).find();
			boolean found = nc.hasNext();
		}
		Debug.line(System.currentTimeMillis()- start) ;
	}
	
	
	public void testRaw() throws Exception {
		Mongo mongo = new Mongo("61.250.201.78") ;
		DB rawdb = mongo.getDB("raw") ;
		DBCollection col = rawdb.getCollection("speed");
		long start = System.currentTimeMillis() ;
		for (int i : ListUtil.rangeNum(20000)) {
			col.insert(new BasicDBObject("a" + RandomUtil.nextRandomString(100), RandomUtil.nextRandomString(900))) ;
		}
		Debug.line(System.currentTimeMillis()- start) ;
	}
	


}


interface Repeatable {
	public void repeat() ;
}