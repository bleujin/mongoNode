package net.ion.repository.mongo.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.SetUtil;
import net.ion.repository.mongo.ExtendPropertyId;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyId.PType;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.Workspace;
import net.ion.repository.mongo.util.ReadChildrenEachs;
import net.ion.repository.mongo.util.Transformers;

import com.google.common.base.Function;
import com.mongodb.DBObject;

public class ReadNode extends AbstractNode<ReadNode> implements NodeCommon<ReadNode>{

	private final ReadSession session;
	private final Fqn fqn;
	
	private ReadNode(ReadSession session, Fqn fqn, DBObject found) {
		super(found) ;
		this.session = session ;
		this.fqn = fqn ;
	}

	public static ReadNode create(ReadSession session, Fqn fqn, DBObject found) {
		return new ReadNode(session, fqn, found);
	}



	public Fqn fqn() {
		return fqn ;
	}

	public long getLastModified() {
		return property("_lastmodified").asLong();
	}

	public ReadChildren children(){
		return workspace().children(session, this.fqn()) ;
	}

	private Workspace workspace() {
		return session.workspace();
	}

	@Override
	public ReadSession session() {
		return session;
	}

	@Override
	public int dataSize() {
		return found().keySet().size();
	}

	@Override
	public ReadNode parent() {
		return workspace().pathBy(session, fqn.getParent());
	}


	@Override
	public boolean hasChild(String childPath) {
		return workspace().exists(session, fqn.relativeFqn(childPath) );
	}


	@Override
	public ReadNode root() {
		return session.root();
	}

	

	
	@Override
	public ReadNode child(String childFqn) {
		return workspace().pathBy(session, fqn.relativeFqn(childFqn));
	}

	@Override
	public Set<String> childrenNames() {
		return children().eachNode(ReadChildrenEachs.CHILDREN_NAME);
	}

	@Override
	public Set<PropertyId> normalKeys() {
		Set<PropertyId> result = SetUtil.newSet() ;
		for (String key : found().keySet()){
			if (key.startsWith("_") || key.startsWith("@")) continue ;
			
			PropertyId pid = PropertyId.fromString(key);
			if (pid.type() == PType.NORMAL) result.add(pid) ;
		} 
		return result;
	}


	@Override
	public Map<PropertyId, PropertyValue> toMap() {
		return transformer(Transformers.READ_TOMAP);
	}

	@Override
	public ReadNode ref(String refName) {
		PropertyValue findProp = propertyId(PropertyId.refer(refName)) ;
		if (findProp == PropertyValue.NotFound) return null ;
		return workspace().pathBy(session, Fqn.fromString(findProp.asString()));
	}

	@Override
	public boolean hasRef(String refName) {
		return found().containsField(PropertyId.refer(refName).idString());
	}

	@Override
	public boolean hasRef(String refName, Fqn fqn) {
		PropertyValue findProp = propertyId(PropertyId.refer(refName)) ;
		Set<String> set = findProp.asSet() ;
		
		return set.contains(fqn.toString());
	}

	@Override
	public IteratorList<ReadNode> refs(String refName) {
		PropertyValue findProp = propertyId(PropertyId.refer(refName)) ;
		Set<String> set = findProp.asSet() ;
		
		final ArrayList<String> refs = new ArrayList<String>(set) ;
		return new IteratorList<ReadNode>() {
			
			private Iterator<ReadNode> inter = iterator() ;
			@Override
			public Iterator<ReadNode> iterator() {
				return toList().iterator();
			}
			
			@Override
			public ReadNode next() {
				return inter.next();
			}
			
			@Override
			public boolean hasNext() {
				return inter.hasNext();
			}
			
			@Override
			public List<ReadNode> toList() {
				List<ReadNode> result = ListUtil.newList() ;
				for (String ref : refs) {
					result.add(workspace().pathBy(session, Fqn.fromString(ref))) ;
				}
				return result;
			}
		};
	}

	@Override
	public <R> R transformer(Function<ReadNode, R> transformer) {
		return transformer.apply(this);
	}

	
	@Override
	public boolean equals(Object _obj){
		if (ReadNode.class.isInstance(_obj)){
			ReadNode that = (ReadNode) _obj ;
			return this.fqn.equals(that.fqn) ;
		} return false ;
	}
	
	
	@Override
	public int hashCode(){
		return fqn.hashCode() ;
	}
	
	
	@Override
	public String toString(){
		return transformer(Transformers.READ_TOSTRING) ;
	}

}
