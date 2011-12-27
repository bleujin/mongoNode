package net.ion.radon.repository.speed;

import java.util.List;

import org.apache.commons.collections.Closure;
import org.bson.types.ObjectId;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeConstants;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeObject;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.TestBaseRepository;
import net.ion.radon.repository.TestOnlyWorkspace;

public class TestDataUpdate extends TestBaseRepository{

	
	public void xtestRemoveIndex() throws Exception {
		List<NodeObject> indexes = session.getCurrentWorkspace().getIndexInfo() ;
		DBCollection dbc = new TestOnlyWorkspace(session.getCurrentWorkspace()).getCollection() ;

		// drop index ...
		for (NodeObject index : indexes) {
			if ("_id_".equals(index.get("name"))) continue ; 
			DBObject dbo = (DBObject) index.get("key") ;
			if ((dbo.get("__aradon.uid") != null || dbo.get("__path") != null) && (index.get("unique") == null || index.get("unique").equals(Boolean.FALSE)) ) {
				Debug.debug(index, index.get("key")) ;
				dbc.dropIndex((String)index.get("name")) ;
			}
		}
		Debug.line(session.getCurrentWorkspace().getIndexInfo()) ;
	}
	
	public void testUpdateRegacy() throws Exception {
		session.newNode().put("emp", "bleujin") ;
		session.commit() ;


		NodeCursor ncWithNoAradonId = session.createQuery().eq("__aradon.group", "__empty").find() ;
		while(ncWithNoAradonId.hasNext()){
			Node nextNode = ncWithNoAradonId.next() ;
			nextNode.setAradonId("__empty", nextNode.getIdentifier()) ;
			int modcount = session.commit();
		}

		NodeCursor ncNoPath = session.createQuery().isNotExist(NodeConstants.PATH).find() ;
		while(ncNoPath.hasNext()){
			Node nextNode = ncNoPath.next() ;
			nextNode.setAradonId("__empty", nextNode.getIdentifier()) ;
			int modcount = session.commit();
		}

//		session.createQuery().find().each(PageBean.ALL, new Closure() {
//			public void execute(Object arg0) {
//				Debug.line(((Node)arg0).toMap()) ;
//			}
//		}) ;
		
//		session.createQuery().eq("__aradon.group", "__empty").updateChain().put("__aradon.uid", new ObjectId().toString()).update() ;
//		session.createQuery().isNotExist("__path").updateChain().put("__path", new ObjectId().toString()).update() ;
	}
	
	
	public void xtestRun() throws Exception {
		Debug.line(session.getWorkspaceNames()) ;
		
		for (String wname : session.getWorkspaceNames()) {
			session.changeWorkspace(wname) ;
			UpdateVersion.create(session).execute() ;
		}
	}
}
