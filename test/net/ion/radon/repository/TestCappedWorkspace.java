package net.ion.radon.repository;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;

import com.mongodb.DBCollection;

public class TestCappedWorkspace extends TestBaseRepository{


	private String cappedWName = "mycap" ;
	@Override protected void setUp() throws Exception {
		super.setUp();
		session.getWorkspace(cappedWName).drop() ;
	}
	
	
	public void testIsCapped() throws Exception {
		DBCollection col = session.getCurrentWorkspace().innerCollection() ;
		assertEquals(false, col.isCapped()) ;
	}
	
	public void testCreate() throws Exception {
		session.changeWorkspace(cappedWName, WorkspaceOption.createByMax(10)) ;
		
		DBCollection col = session.getCurrentWorkspace().innerCollection() ;
		Debug.line(col.getOptions()) ;
		assertEquals(true, col.isCapped()) ;
	}
	
	public void testLimit() throws Exception {
		session.changeWorkspace(cappedWName, WorkspaceOption.createByMax(10)) ;
		
		for (int index : ListUtil.rangeNum(20)) {
			session.newNode().put("index", index) ;
		}
		session.commit() ;
		
		assertEquals(10, session.createQuery().find().count()) ;
	}
	
	public void testOrder() throws Exception {
		session.changeWorkspace(cappedWName, WorkspaceOption.createByMax(10)) ;
		
		for (int index : ListUtil.rangeNum(20)) {
			session.newNode().put("index", index) ;
			session.commit() ;
		}
		assertEquals(10, session.createQuery().gte("index", 10).find().count()) ;
	}
	
}
