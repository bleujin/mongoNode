package net.ion.radon.repository.speed;

import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBConnector;
import com.mongodb.DBObject;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.repository.Explain;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.TestOnlyWorkspace;
import net.ion.radon.repository.TestWorkspace;
import net.ion.radon.repository.WorkspaceOption;

public class TestInsert extends TestBaseRepository {

	public void testInsert() throws Exception {

		session.getCurrentWorkspace() ; // pool 
		long start = System.currentTimeMillis();
		long start_con = start ;
		for (int loop : ListUtil.rangeNum(20)) {
			for (int index : ListUtil.rangeNum(100)) {
				session.newNode().setAradonId("emp", loop * 100 + index).put("name", "bleujin").put("size", RandomUtil.nextRandomString(200)); // setAradonId("index", loop * 1000 + index).
			}
			session.commit();
			long end = System.currentTimeMillis();
			Debug.debug(end - start);
			start = end;
		}
		// assertEquals(true, System.currentTimeMillis() - start_con < 500) ;
		// assertEquals(1000, session.createQuery().count()) ;
		// 1만건 기준 약 2.6sec (testCaseLoad시간 약 0.3초)
	}

	public void testBatchInsert() throws Exception {
		session.changeWorkspace("myjob", WorkspaceOption.createByMax(500000, 1000 * 1000 * 100));
		session.dropWorkspace();

		long start = System.currentTimeMillis();
		for (int loop : ListUtil.rangeNum(100)) {
			for (int index : ListUtil.rangeNum(100)) {
				session.newNode().put("name", "bleujin").put("size", RandomUtil.nextRandomString(200)); // setAradonId("index", loop * 1000 + index).
			}
			session.commit();
			long end = System.currentTimeMillis();
			Debug.debug(end - start);
			start = end;
		}
		// 1만건 기준 약 2.5sec
	}

	public void testBasicCapped() throws Exception {
		session.changeWorkspace("myjob", WorkspaceOption.createByMax(500000, 1000 * 1000 * 100));
		DBCollection dbc = new TestOnlyWorkspace(session.getCurrentWorkspace()).getCollection();

		long start = System.currentTimeMillis();
		for (int loop : ListUtil.rangeNum(100)) {
			List<DBObject> list = ListUtil.newList() ;
			for (int index : ListUtil.rangeNum(100)) {
				BasicDBObject row = new BasicDBObject();
				row.put("index", loop * 1000 + index);
				row.put("name", "bleujin");
				row.put("size", RandomUtil.nextRandomString(200));
				list.add(row);
			}
			dbc.insert(list) ;
			long end = System.currentTimeMillis();
			Debug.debug(end - start);
			start = end;
		}
		// 1만건 기준 약 1.2sec
	}

	public void testBasic() throws Exception {
		DBCollection dbc = new TestOnlyWorkspace(session.getCurrentWorkspace()).getCollection();

		dbc.ensureIndex(new BasicDBObject("index", 1));

		long start = System.currentTimeMillis();
		for (int loop : ListUtil.rangeNum(100)) {
			List<DBObject> list = ListUtil.newList() ;
			for (int index : ListUtil.rangeNum(100)) {
				BasicDBObject row = new BasicDBObject();
				row.put("index", loop * 1000 + index);
				row.put("name", "bleujin");
				row.put("size", RandomUtil.nextRandomString(200));
				list.add(row);
			}
			dbc.insert(list) ;
			long end = System.currentTimeMillis();
			Debug.debug(end - start);
			start = end;
		}
		// 1만건 기준 약 .9sec
	}

	public void testGetWorkspace() throws Exception {
		long start = System.currentTimeMillis();
		for (int index : ListUtil.rangeNum(1000)) {
			session.getWorkspace("abcd");
		}
		long end = System.currentTimeMillis();
		Debug.debug(end - start);
	}
	
	public void testDupIndex() throws Exception {
		session.newNode().setAradonId("emp", "bleujin") ;
		session.commit() ;
		session.newNode().setAradonId("emp", "bleujin") ;
		int result = session.commit() ;
		
		NodeResult nr = session.getAttribute(NodeResult.class.getCanonicalName(), NodeResult.class) ;
		Debug.debug(result, nr.getErrorMessage()) ;
		
	}

}
