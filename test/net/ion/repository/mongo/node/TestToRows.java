package net.ion.repository.mongo.node;

import java.util.Date;

import net.ion.framework.db.Page;
import net.ion.framework.db.Rows;
import net.ion.framework.util.Debug;
import net.ion.repository.mongo.TestBaseReset;
import net.ion.repository.mongo.WriteJob;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.expression.ExpressionParser;
import net.ion.repository.mongo.expression.SelectProjection;
import net.ion.repository.mongo.expression.TerminalParser;
import net.ion.repository.mongo.util.WriteJobs;
import net.ion.rosetta.Parser;

public class TestToRows extends TestBaseReset{

	
	public void testHello() throws Exception {
		session.tranSync(WriteJobs.HELLO) ;
		
		session.root().children().toRows("name, greeting").debugPrint();
		session.pathBy("/bleujin").toRows("name, greeting").debugPrint();

	}
	
	public void testDateType() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("int", 1).property("str", "string").property("lng", 1L).property("dte", new Date()) ;
				return null;
			}
		}) ;
		
		Rows rows = session.root().children().eq("int", 1).toRows("int, str, lng, dte") ;
		rows.first() ;
		
//		assertEquals("1", rows.getString("int"));
//		assertEquals("string", rows.getString("str"));
//		assertEquals("1", rows.getString("lng"));

		assertEquals(1, rows.getInt("int"));
		assertEquals("string", rows.getString("str"));
		assertEquals(1L, rows.getLong("lng"));
		assertEquals(new Date().getDate(), rows.getDate("dte").getDate());
	}
	

	public void testDebugPrint() throws Exception {
		session.tranSync(new WriteJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 20) ;
				return null;
			}
		}) ;
		
		Rows rows = session.root().children().toRows("this.name b, this.age");
		rows.debugPrint() ;
	}

	
	
	
	public void testCaseWhenParser() throws Exception {
		Parser<SelectProjection> parser = ExpressionParser.selectProjection();
		
		SelectProjection sp = TerminalParser.parse(parser, "case when /*+ comment */ (this.age = 20) then 'self' else 'other' end as name");
//		SelectProjection sp = TerminalParser.parse(parser, "case when /*+ comment */ (this.age > 20) then 'self' else 'other' end as name");
		
		Debug.line(sp) ;
		
		
	}
	
	public void testFunction() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/dept/dev").property("name", "dev").refTo("manager", "/emps/bleujin") ;
				return null;
			}
		}) ;
		Rows rows = session.pathBy("/emps").children().toRows("substring(this.name, 2) s");
		rows.debugPrint() ;
	}
	
	public void testRelation() throws Exception {
		session.tranSync(new WriteJob<Void>(){
			@Override
			public Void handle(WriteSession wsession) throws Exception {
				wsession.pathBy("/emps/bleujin").property("name", "bleujin").refTo("dept", "/dept/dev") ;
				wsession.pathBy("/dept/dev").property("name", "dev").refTo("manager", "/emps/bleujin") ;
				return null;
			}
		}) ;
		
		Rows rows = session.pathBy("/emps").children().toRows("this.dept.manager.name managerName");
		rows.debugPrint() ;
		
		session.pathBy("/emps/bleujin").toRows("this.dept.manager.name managerName") ;
	}
	

	public void testCaseWhen() throws Exception {
		session.tranSync(WriteJobs.dummy("/bleujin", 10)) ;
		Rows rows = session.pathBy("/bleujin").children().toRows(Page.TEN, "(case when this.name='dummy' then true else false end) as isbleujin");
//		rows.debugPrint() ;
		assertEquals(true, rows.firstRow().getBoolean("isbleujin")) ;
	}
	

	
	public void testOldInterface() throws Exception {
		session.tranSync(WriteJobs.dummy("/bleujin", 120)) ;
		
		Rows rows = session.pathBy("/bleujin").children().ascending("dummy").toRows(Page.create(10, 11, 10), "this.name b, dummy, this.age");
		assertEquals(20, rows.firstRow().getInt("cnt")) ;
		assertEquals(100, rows.firstRow().getInt("dummy")) ;

		rows = session.pathBy("/bleujin").children().ascending("dummy").toRows(Page.create(10, 5, 10), "this.name b, dummy, this.age");
		assertEquals(101, rows.firstRow().getInt("cnt")) ;
		assertEquals(40, rows.firstRow().getInt("dummy")) ;

	}
	
	public void testPageToAdRows() throws Exception {
		session.tranSync(WriteJobs.dummy("/bleujin", 120)) ;
		
		Rows rows = session.pathBy("/bleujin").children().ascending("dummy").toRows(Page.create(10, 11, 10), "this.name b, dummy, this.age");


		assertEquals(20, rows.firstRow().getInt("cnt")) ;
		assertEquals(100, rows.firstRow().getInt("dummy")) ;

		rows = session.pathBy("/bleujin").children().ascending("dummy").toRows(Page.create(10, 5, 10), "this.name b, dummy, this.age");
		assertEquals(101, rows.firstRow().getInt("cnt")) ;
		assertEquals(40, rows.firstRow().getInt("dummy")) ;

		rows = session.pathBy("/bleujin").children().ascending("dummy").toRows(Page.create(10, 13, 10), "this.name b, dummy, this.age");
		assertEquals(0, rows.getRowCount()) ;

		rows = session.pathBy("/bleujin").children().ascending("dummy").skip(10).toRows(Page.create(10, 5, 10), "this.name b, dummy, this.age");
		assertEquals(101, rows.firstRow().getInt("cnt")) ;
		assertEquals(50, rows.firstRow().getInt("dummy")) ;
	}
	
}
