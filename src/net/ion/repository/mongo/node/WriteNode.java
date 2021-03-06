package net.ion.repository.mongo.node;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ion.framework.util.ArrayUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.SetUtil;
import net.ion.framework.util.StringUtil;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.Workspace;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.exception.NotFoundPath;
import net.ion.repository.mongo.util.Transformers;
import net.ion.repository.mongo.util.WriteChildrenEachs;

import com.google.common.base.Function;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class WriteNode extends AbstractNode<WriteNode> implements NodeCommon<WriteNode> {

	private final WriteSession wsession;
	private final Fqn fqn;
	
	public enum Touch {
		MODIFY, REMOVE, REMOVECHILDREN, TOUCH;
	}
	
	private WriteNode(WriteSession wsession, Fqn fqn, DBObject found) {
		super(found) ;
		this.wsession = wsession ;
		this.fqn = fqn ;
	}

	public static WriteNode create(WriteSession wsession, Fqn fqn, DBObject found) {
		return new WriteNode(wsession, fqn, found);
	}

	public WriteNode child(String childPath) {
		return wsession.pathBy(fqn.relativeFqn(childPath));
	}
	
	public WriteNode property(String name, String value) {
		return property(PropertyId.fromString(name), value);
	}

	public WriteNode property(String name, long value) {
		return property(PropertyId.fromString(name), value);
	}

	public WriteNode property(String name, boolean value) {
		return property(PropertyId.fromString(name), Boolean.valueOf(value));
	}

	public WriteNode property(String name, Date date) {
		return property(name, date.getTime());
	}

	public WriteNode unset(String name) {
		found().removeField(name) ;
		touch(this.fqn, Touch.MODIFY) ;
		return this;
	}

	public WriteNode unset(String name, Object... values) {
		Object vals = found().removeField(name) ;
		if (BasicDBList.class.isInstance(vals)){
			BasicDBList valList = (BasicDBList) vals;
			valList.removeAll(SetUtil.create(values)) ;
			found().put(name, valList) ;
		} else if (ArrayUtil.contains(values, vals)) {
			found().removeField(name) ;
		}
		
		touch(this.fqn, Touch.MODIFY) ;
		return this;
	}

	public WriteNode unref(String refName) {
		return unset(PropertyId.refer(refName).fullString()) ;
	}

	public WriteNode unref(String refName, String... refNames) {
		return unset(PropertyId.refer(refName).fullString(), refNames) ;
	}

	
	
	
	
	public WriteNode append(String name, Object... values) {
		return append(PropertyId.fromString(name), values) ;
	}
	
	public WriteNode append(PropertyId pId, Object... values) {
		found().put(pId.fullString(), mergedList(pId, values)) ;
		touch(this.fqn, Touch.MODIFY) ;
		return this;
	}

	private BasicDBList mergedList(PropertyId pId, Object...values){
		Object val = found().get(pId.fullString()) ;
		BasicDBList result = null ;
		if (val == null) {
			result = new BasicDBList() ;
		} else if (BasicDBList.class.isInstance(val)){
			result = (BasicDBList)val ;
		} else {
			result = new BasicDBList() ;
			result.add(val) ;
		}
		
		for (Object v : values) {
			result.add(v) ;
		}
		
		return result ;
	}
	
	public WriteNode refTo(String name, String refPath) {
		return property(PropertyId.refer(name), refPath) ;
	}

	public WriteNode refTos(String name, String... refPaths) {
		return append(PropertyId.refer(name), refPaths) ;
	}

	
	private WriteNode property(PropertyId pid, Object value) {
		found().put(pid.fullString(), value) ;
		touch(this.fqn, Touch.MODIFY) ;
		return this;
	}
	
	public Fqn fqn() {
		return fqn;
	}

	private void touch(Touch touch) {
		touch(this.fqn(), touch) ;
	}

	private void touch(Fqn target, Touch touch){
		session().notifyTouch(this, target, touch) ;
	}


	public WriteNode clear(){
		touch(Touch.MODIFY) ;
		
		BasicDBObject newOb = new BasicDBObject() ;
		for(String key : found().keySet()){
			if (key.startsWith("_")) newOb.put(key, found().get(key)) ;
		}
		
		super.found(newOb) ;
		return this ;
	}


	public boolean removeSelf(){
		return parent().removeChild(fqn().name()) ;
	}

	public WriteNode parent(){
		return wsession.pathBy(fqn.getParent()) ;
	}
	
	public <T> T transformer(Function<WriteNode, T> function){
		return function.apply(this) ;
	}
	
	public boolean hasChild(String fqn){
		return wsession.exists(Fqn.fromString(fqn)) ;
	}
	
	public Map<PropertyId, PropertyValue> toPropMap() {
		return transformer(Transformers.WRITE_TOMAP);
	}

	@Override
	public WriteSession session() {
		return wsession;
	}
	
	public Workspace workspace(){
		return wsession.workspace() ;
	}

	@Override
	public WriteNode root() {
		return session().pathBy(Fqn.ROOT);
	}

	public boolean removeChild(String childPath){
		final Fqn target = fqn.relativeFqn(childPath);
		touch(target, Touch.REMOVE) ;
		
//		WriteNodeImpl found = (WriteNodeImpl) wsession.pathBy(target) ;
//		found.removeBlobIfExist(); 
		
		return workspace().remove(target) ;
	}
	
	public void removeChildren(){
		touch(Touch.REMOVECHILDREN) ;
		workspace().removeChildren(fqn) ;
	}

	@Override
	public WriteNode ref(String refName) {
		return refChildren(refName).firstNode() ;
	}

	@Override
	public IteratorList<WriteNode> refs(String refName) {
		return refChildren(refName).iterator() ;
	}
	
	
	@Override
	public Set<String> childrenNames() {
		return children().eachNode(WriteChildrenEachs.CHILDREN_NAME);
	}

	public WriteChildren children(){
		return children(false) ;
	}
	
	public WriteChildren children(boolean includeSub) {
		return workspace().children(wsession, includeSub, this.fqn()) ;
	}
	
	@Override
	public String toString(){
		return transformer(Transformers.WRITE_TOSTRING) ;
	}

	public void debugPrint() {
		transformer(Transformers.WRITE_DEBUGPRINT) ;
	}

	public ReadNode readNode() {
		return ReadNode.create(session().readSession(), fqn, found());
	}

	public WriteChildren refChildren(String refName) {
		PropertyValue findProp = propertyId(PropertyId.refer(refName)) ;
		String[] refs = (String[]) findProp.asSet().toArray(new String[0]) ;
		
		return workspace().children(wsession, true, fqn.ROOT).in("_id", refs) ;
	}





}
