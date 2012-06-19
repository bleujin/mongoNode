package net.ion.radon.repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import junit.framework.TestCase;
import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.ResultSetHandler;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.manager.OracleCacheDBManager;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;

public class TestNodeCreateSpeed extends TestCase {

	private RepositoryCentral rc;
	private IDBController dc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rc = RepositoryCentral.testCreate();
		DBManager dbm = new OracleCacheDBManager("jdbc:oracle:thin:@dev-oracle.i-on.net:1521:dev10g", "MSSQL_ICS5", "MSSQL_ICS5", 20);
		dc = new DBController(dbm);
		dc.initSelf();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dc.destroySelf();
	}

	public void testRow() throws Exception {
		Rows rs = dc.getRows("select * from article_tblc where rownum <= 1000 and isUsing = 'T'");
		ResultSetMetaData meta = rs.getMetaData();
		String[] names = new String[meta.getColumnCount()];
		for (int i = 0, last = meta.getColumnCount(); i < last; i++) {
			names[i] = meta.getColumnName(i + 1);
		}
		Debug.line(names) ;
		while (rs.next()) {
			for (String name : names) {
				rs.getString(name);
			}
		}
	}

	public void testCreate() throws Exception {
		final Session session = rc.testLogin("speed");
		session.dropWorkspace();

		IUserCommand cmd = dc.createUserCommand("select * from article_tblc where rownum <= 1000 and isUsing = 'T'");
		Object result = cmd.execHandlerQuery(new ResultSetHandler() {

			public Object handle(ResultSet rs) throws SQLException {

				ResultSetMetaData meta = rs.getMetaData();
				String[] names = new String[meta.getColumnCount()];
				for (int i = 0, last = meta.getColumnCount(); i < last; i++) {
					names[i] = meta.getColumnName(i + 1);
				}

				int index = 0;
				long length = 0L;
				while (rs.next()) {
					Node node = session.newNode();
					node.setAradonId("article", rs.getString("catId") + "_" + rs.getInt("artId"));
					for (String name : names) {
						String value = rs.getString(name);
						node.put(name, value);
						length += name.length() + (value == null ? 0 : value.length());
					}
					if ((index++) % 100 == 0) {
						System.out.print('.');
						session.commit();
					}
				}
				session.commit();
				return length;
			}
		});

		Debug.line(session.createQuery().count(), result);
	}

	
	public void testInsert() throws Exception {
		final Session session = rc.testLogin("speed");
		session.dropWorkspace();
		
		for (int i : ListUtil.rangeNum(2000)) {
			Node node = session.newNode() ;
			node.put( "a" + RandomUtil.nextRandomString(100), RandomUtil.nextRandomString(900));
			if (i % 100 == 0) {
				System.out.print('.');
				session.commit();
			}
		}
		session.commit() ;
		Debug.line(session.createQuery().count());
	}
	
}
