package net.ion.repository.mongo.node;


import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.repository.mongo.Fqn;
import net.ion.repository.mongo.PropertyId;
import net.ion.repository.mongo.PropertyValue;
import net.ion.repository.mongo.WriteSession;

import com.mongodb.DBObject;

public class WriteNode {

	private final WriteSession wsession;
	private final Fqn fqn;
	private DBObject found;
	
	public enum Touch {
		MODIFY, REMOVE, REMOVECHILDREN, TOUCH;
	}
	
	private WriteNode(WriteSession wsession, Fqn fqn, DBObject found) {
		this.wsession = wsession ;
		this.fqn = fqn ;
		this.found = found ;
	}

	public static WriteNode create(WriteSession wsession, Fqn fqn, DBObject found) {
		return new WriteNode(wsession, fqn, found);
	}

	public WriteNode child(String childPath) {
		return wsession.pathBy(Fqn.fromRelativeFqn(fqn, Fqn.fromString(childPath)));
	}
	
	public WriteNode property(String name, String value) {
		return property(PropertyId.fromString(name), value);
	}

	public WriteNode property(String name, long value) {
		return property(PropertyId.fromString(name), value);
	}

	
	private WriteNode property(PropertyId pid, Object value) {
		found.put(pid.idString(), value) ;
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

	public DBObject dbObject() {
		return found;
	}

	public long getLastModified() {
		return property("_lastmodified").asLong();
	}

	private PropertyValue property(String name) {
		return PropertyValue.create(found.get(name));
	}

}
