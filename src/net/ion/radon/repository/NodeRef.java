package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.ARADON;
import static net.ion.radon.repository.NodeConstants.GROUP;
import static net.ion.radon.repository.NodeConstants.UID;

import java.util.Map;

import com.mongodb.DBObject;
public class NodeRef implements IPropertyFamily {

	private static final long serialVersionUID = 4422760795799886975L;
    public final static String TARAGET_REF = "_ref" ; 
    public final static String TARGET_QUERY = "_query" ;
	
    private final NodeObject inner ;
    private NodeRef(NodeObject inner) {
    	this.inner = inner ;
	}

//	public static NodeRef create(String wsname, AradonId aid) {
//		BasicDBObject dbo = new BasicDBObject() ;
//		dbo.put(GROUP, aid.getGroup()) ; 
//		dbo.put(UID, aid.getUid()) ;
//		
//		
//		PropertyFamily pf = PropertyFamily.create().put(ARADON, dbo) ;
//		NodeObject inner = NodeObject.create().put(TARAGET_REF, wsname).put(TARGET_QUERY, pf) ;
//		return new NodeRef(inner);
//	}
//
//	public static NodeRef create(AradonId aid) {
//		return create(Session.getCurrent().getCurrentWorkspaceName(), aid) ;
//	}
//	public static NodeRef create(String path) {
//		NodeObject inner = NodeObject.create().put(TARAGET_REF, Session.getCurrent().getCurrentWorkspaceName()).put(TARGET_QUERY, PropertyFamily.create(PATH, path)) ;
//		return new NodeRef(inner);
//	}

	static NodeRef create(Node target) {
		NodeObject inner = NodeObject.create().put(TARAGET_REF, target.getWorkspaceName()).put(TARGET_QUERY, PropertyQuery.createById(target.getIdentifier())) ;
		return new NodeRef(inner);
	}
	
	static NodeRef load(InNode inref) {
		return new NodeRef(NodeObject.load(inref.getDBObject()));
	}

	
	public Map<String, ? extends Object> toMap() {
		return inner.toMap() ;
	}

	public DBObject getDBObject() {
		return inner.getDBObject();
	}

	public PropertyQuery toQuery(){
		DBObject dbo = (DBObject)inner.getDBObject().get(TARGET_QUERY);
		if (dbo.containsField(ARADON)){
			DBObject adbo = (DBObject) dbo.get(ARADON) ;
			return PropertyQuery.createByAradon(adbo.get(GROUP).toString(), adbo.get(UID)) ;
		}
		return PropertyQuery.load(NodeObject.load(dbo)) ;
	}

}
