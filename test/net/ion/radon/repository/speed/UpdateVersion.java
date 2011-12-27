package net.ion.radon.repository.speed;

import static net.ion.radon.repository.NodeConstants.ARADON_GROUP;
import static net.ion.radon.repository.NodeConstants.ARADON_UID;

import java.util.List;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeConstants;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeObject;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.TestOnlyWorkspace;

public class UpdateVersion {

	private Session session ;
	private UpdateVersion(Session session) {
		this.session = session ;
	}

	public static UpdateVersion create(Session session) {
		return new UpdateVersion(session);
	}

	public void execute() {
		deleteOldIndex() ;
		updateRegacy() ;
		recreateIndex() ;
	}

	private static PropertyFamily ARADON_INDEX = PropertyFamily.create(ARADON_GROUP, 1).put(ARADON_UID, -1);
	private static PropertyFamily PATH_INDEX = PropertyFamily.create(NodeConstants.PATH, 1);
	private void recreateIndex() {
		session.getCurrentWorkspace().makeIndex(ARADON_INDEX, "_aradon_id", Boolean.TRUE);
		session.getCurrentWorkspace().makeIndex(PATH_INDEX, "_path_id", Boolean.TRUE);
	}

	private void updateRegacy() {
		NodeCursor ncWithNoAradonId = session.createQuery().eq("__aradon.group", "__empty").find() ;
		while(ncWithNoAradonId.hasNext()){
			Node nextNode = ncWithNoAradonId.next() ;
			nextNode.setAradonId("__empty", nextNode.getIdentifier()) ;
			int modcount = session.commit();
		}
	}

	private void deleteOldIndex() {
		List<NodeObject> indexes = session.getCurrentWorkspace().getIndexInfo() ;
		DBCollection dbc = new TestOnlyWorkspace(session.getCurrentWorkspace()).getCollection() ;

		// drop index ...
		for (NodeObject index : indexes) {
			if ("_id_".equals(index.get("name"))) continue ; 
			DBObject dbo = (DBObject) index.get("key") ;
			if ((dbo.get("__aradon.uid") != null || dbo.get("__path") != null) && (index.get("unique") == null || index.get("unique").equals(Boolean.FALSE)) ) {
				Debug.debug(index, index.get("key")) ;
				dbc.dropIndex((String)index.get("name")) ;
				Debug.line((String)index.get("name") + " index droped") ;
			}
		}
	}

}
