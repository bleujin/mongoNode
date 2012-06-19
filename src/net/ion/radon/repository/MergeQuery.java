package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.GHASH;
import static net.ion.radon.repository.NodeConstants.GROUP;
import static net.ion.radon.repository.NodeConstants.PATH;
import static net.ion.radon.repository.NodeConstants.UID;

import java.util.Map;

import net.ion.framework.util.HashFunction;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MergeQuery implements IPropertyFamily{

	private static final long serialVersionUID = -2440768087774324310L;
	private DBObject blankData = new BasicDBObject() ;
	private final PropertyQuery query ;
	private MergeQuery(PropertyQuery query ) {
		this.query = query ;
	}

	public DBObject getDBObject() {
		return query.getDBObject();
	}

	public Map<String, ? extends Object> toMap() {
		return query.toMap();
	}
	
	Map<String, ? extends Object> data(){
		return blankData.toMap() ;
	}

	public static MergeQuery createByAradon(String groupId, Object uid) {
		MergeQuery result = new MergeQuery(PropertyQuery.createByAradon(groupId, uid));

		result.putBlankData(makeAradonId(groupId, uid), "/" + groupId + "_" + uid, groupId + uid) ;
		return result;
	}

	public static MergeQuery createById(String oid) {
		MergeQuery result = new MergeQuery(PropertyQuery.createById(oid));
		
		result.putBlankData(makeAradonId(EMPTY_GROUP, oid), "/" + oid, oid) ;
		return result;
	}


	public static MergeQuery createByPath(String path) {
		MergeQuery result = new MergeQuery(PropertyQuery.create(PATH, path));

		result.putBlankData(makeAradonId(EMPTY_GROUP, path), path, StringUtil.defaultIfEmpty(StringUtil.substringAfter(path, "/"), path)) ;
		return result;
	}


	static String EMPTY_GROUP = "__empty";
	static MergeQuery load(PropertyQuery query) {
		MergeQuery result = new MergeQuery(query) ;
		String oid = new ObjectId().toString() ;
		result.putBlankData(makeAradonId(EMPTY_GROUP, oid), "/" + oid, oid) ;
		return result;
	}

	
	private void putBlankData(NodeObject aradonId, String path, String name ){
		blankData.put(NodeConstants.ARADON, aradonId.getDBObject()) ;
		blankData.put(NodeConstants.PATH, path) ;
		blankData.put(NodeConstants.NAME, name) ;
	}
	
	
	static NodeObject makeAradonId(String groupid, Object uid) {
		NodeObject inner = NodeObject.create();

		inner.put(GROUP, makeGroups(groupid));
		inner.put(GHASH, HashFunction.hashGeneral(groupid));
		inner.put(UID, uid);

		return inner ;
	}
	
	private static String[] makeGroups(String groupid) {
		String[] groups = StringUtil.split(groupid, ".");

		String[] result = new String[groups.length];
		for (int i = 0; i < groups.length; i++) {
			result[i] = StringUtil.join(groups, ".", 0, i + 1);
		}
		return result;
	}
	
	public PropertyQuery getQuery(){
		return query ;
	}

}
