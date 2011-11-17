package net.ion.radon.repository;

import static net.ion.radon.repository.NodeConstants.GHASH;
import static net.ion.radon.repository.NodeConstants.GROUP;
import static net.ion.radon.repository.NodeConstants.UID;

import java.nio.ByteBuffer;
import java.util.Map;

import net.ion.framework.util.HashFunction;
import net.ion.framework.util.StringUtil;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
public class AradonId implements IPropertyFamily{

	private static final long serialVersionUID = -9452739567395469L;
	
	static final AradonId EMPTY = CreateEmptyId();

	private final NodeObject inner ;
	
	private AradonId(BasicDBList group, Object uid, Object ghash) {
		NodeObject myinner = NodeObject.create() ;
		myinner.put(GROUP, group) ;
		myinner.put(UID, uid) ;
		myinner.put(GHASH, ghash) ;
		this.inner = myinner ;
	}
	
	private static AradonId CreateEmptyId() {
		BasicDBList group = new BasicDBList() ;
		group.add("__empty") ;
		return new AradonId(group, "-1", -1);
	}

	
	private AradonId(NodeObject inner){
		this.inner = inner ;
	}

	public String getGroup() {
		BasicDBList ilist = (BasicDBList)inner.get(GROUP);
		return ilist.get(ilist.size() - 1).toString() ;
	}
	
	public Object getUid(){
		return inner.get(UID) ;
	}

	static AradonId create(InNode dbo) {
		
		if (dbo == null) return EMPTY ;
		
		INode dlist = (INode) dbo.get(GROUP) ;
		Object uid = dbo.get(UID) ;
		Object ghash = dbo.get(GHASH) ;
		
		return new AradonId((BasicDBList)dlist.getDBObject(), uid, ghash);
	}

	public static AradonId load(DBObject dbo) {
		return new AradonId((BasicDBList)dbo.get(GROUP), dbo.get(UID), dbo.get(GHASH));
	}

	public DBObject getDBObject() {
		return inner.getDBObject();
	}

	public static AradonId create(String groupid, Object uid) {
		NodeObject inner = NodeObject.create();

		inner.put(GROUP, makeGroups(groupid));
		inner.put(GHASH, HashFunction.hashGeneral(groupid));
		inner.put(UID, uid);

		return new AradonId(inner);
	}

	private static String[] makeGroups(String groupid) {
		String[] groups = StringUtil.split(groupid, ".");

		String[] result = new String[groups.length];
		for (int i = 0; i < groups.length; i++) {
			result[i] = StringUtil.join(groups, ".", 0, i + 1);
		}
		return result;
	}
	
	public NodeObject toNodeObject() {
		return inner;
	}

	public Map<String, ? extends Object> toMap() {
		return inner.toMap();
	}

	public int hashCode(){
		return inner.hashCode() ;
	}
	
	@Override public boolean equals(Object o){
		if ( !(o instanceof AradonId)) return false ;
		AradonId that = (AradonId) o ;
		return inner.getDBObject().equals(that.getDBObject()) ;
	}
	
	
	public IPropertyFamily toQuery(){
		return PropertyQuery.createByAradon(getGroup(), getUid()) ;
	}
	
	
//	public ObjectId toObjectId(){
//		return new ObjectId(toByteArray()) ;
//	}
	
	
	private byte[] toByteArray() {
		byte b[] = new byte[12];
		ByteBuffer bb = ByteBuffer.wrap(b);
		bb.putLong(HashFunction.hashGeneral(inner.getDBObject().toString()));
		bb.putInt(getStandardTime());
		reverse(b);
		return b;
	}
	
	private void reverse(byte b[]) {
		for (int i = 0; i < b.length / 2; i++) {
			byte t = b[i];
			b[i] = b[b.length - (i + 1)];
			b[b.length - (i + 1)] = t;
		}
	}
	
	
	private static int STD_TIME = _flip(_curtime());
	
	private static int getStandardTime() {
		return STD_TIME ;
	}

	private static int _flip(int x) {
		int z = 0;
		z |= x << 24 & -16777216;
		z |= x << 8 & 16711680;
		z |= x >> 8 & 65280;
		z |= x >> 24 & 255;
		return z;
	}

	private static int _curtime() {
		return _flip((int) (System.currentTimeMillis() / 1000L));
	}
	
	
}
