package net.ion.repository.mongo.node;


import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.TreeNode;

import net.ion.framework.util.SetUtil;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.ISession;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.Workspace;
import net.ion.repository.mongo.WriteSession;
import net.ion.repository.mongo.PropertyId.PType;
import net.ion.repository.mongo.util.ReadChildrenEachs;
import net.ion.repository.mongo.util.Transformers;
import net.ion.repository.mongo.util.WriteChildrenEachs;

import com.google.common.base.Function;
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

	
	private WriteNode property(PropertyId pid, Object value) {
		found().put(pid.idString(), value) ;
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
		wsession().notifyTouch(this, target, touch) ;
	}

	private WriteSession wsession() {
		return wsession;
	}


	public WriteNode clear(){
		touch(Touch.MODIFY) ;
		for(String key : found().keySet()){
			if (! key.startsWith("_")) found().removeField(key) ;
		}
		return this ;
	}


	public boolean removeSelf(){
		return parent().removeChild(fqn().name()) ;
	}
	
	public int dataSize(){
		return normalKeys().size() ;
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
	
	public Map<PropertyId, PropertyValue> toMap() {
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

	public WriteNode ref(String refName) {
		PropertyId referId = PropertyId.refer(refName);
		if (hasProperty(referId)) {
			String refPath = propertyId(referId).asString() ;
			if (refPath == null) new IllegalArgumentException("not found ref :" + refName) ;
			return wsession.pathBy(refPath) ;
		} else {
			throw new IllegalArgumentException("not found ref :" + refName) ;
		}
	}

	@Override
	public IteratorList<WriteNode> refs(String refName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public Set<String> childrenNames() {
		return children().eachNode(WriteChildrenEachs.CHILDREN_NAME);
	}

	public WriteChildren children(){
		return workspace().children(wsession, this.fqn()) ;
	}




}
