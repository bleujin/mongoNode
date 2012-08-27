package net.ion.radon.repository;

import net.ion.framework.util.StringUtil;

import com.mongodb.DBObject;

public class FakeExplain extends Explain{

	private Explain explain ;
	
	private Session session ;
	private PropertyQuery iquery ;
	private Columns columns ;
	protected FakeExplain(Session session, PropertyQuery query, Columns columns) {
		super(null) ;
		this.session = session ;
		this.iquery = query;
		this.columns = columns ;
	}

	public static FakeExplain load(Session session, PropertyQuery query, Columns column) {
		return new FakeExplain(session, query, column);
	}
	
	public boolean useIndex(){
		initExplain() ;
		return explain.useIndex() ;
	}
	
	private void initExplain() {
		if (explain == null){
			NodeCursor nc = session.getCurrentWorkspace().findInner(session, iquery, columns) ;
			this.explain = nc.limit(1).explain() ;
		}
	}

	public String toString(){
		initExplain() ;
		return explain.toString() ;
	}

	public String useIndexName() {
		initExplain() ;
		return explain.useIndexName();
	}

}
