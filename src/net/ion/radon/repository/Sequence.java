package net.ion.radon.repository;

import net.ion.framework.util.MapUtil;

public class Sequence implements ISequence{

	private final Session session  ;
	private final PropertyQuery query ;
	private final String PROP_ID = "seq";

	private Sequence(Session session, String seqId) {
		this.session = session ;
		this.query = PropertyQuery.createByPath("/" + seqId) ;
	}

	public static Sequence createOrLoad(Session session, String prefix, String id) {
		String seqId = prefix + "_" + id;
		return new Sequence(session, seqId);
	}
	
	public long currVal() {
		Node node = getWorkspace().findOne(session, getSeqQuery(), Columns.ALL) ;
		if (node == null) return 0L ;
		return ((Long) node.get(PROP_ID)).longValue() ;
	}

	public void reset() {
		getWorkspace().update(session, getSeqQuery(), MapUtil.create(PROP_ID, 0L), true) ;
	}

	public long nextVal() {
		getWorkspace().inc(session, getSeqQuery(), PROP_ID, 1L) ;
		Node node = getWorkspace().findOne(session, getSeqQuery(), Columns.ALL) ;
		return ((Long)node.get(PROP_ID)).longValue() ;
	}

	private PropertyQuery getSeqQuery() {
		return query;
	}

	private Workspace getWorkspace() {
		return session.getWorkspace("_sequence");
	}
}
