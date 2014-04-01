package net.ion.repository.mongo;

import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.repository.mongo.node.NodeResult;
import net.ion.repository.mongo.node.ReadNode;

public class ReadSession implements ISession<ReadNode> {

	private final Workspace workspace;
	private final Credential credential;
	private final String colName;
	private Map<String, Object> attrs = MapUtil.newMap() ;
	
	ReadSession(Workspace workspace, Credential credential, String colName) {
		this.workspace = workspace ;
		this.credential = credential ;
		this.colName = colName ;
	}

	public static ReadSession create(Workspace ws, Credential credential, String colName) {
		return new ReadSession(ws, credential, colName);
	}

	
	public <T> T tranSync(WriteJob<T> job) {
		return workspace.tran(this, job) ;
	}

	public Workspace workspace() {
		return workspace;
	}
	
	public String colName(){
		return colName ;
	}

	public ReadNode pathBy(String fqn) {
		return workspace.pathBy(this, fqn);
	}


	@Override
	public ReadNode pathBy(Fqn fqn) {
		return workspace.pathBy(this, fqn);
	}

	@Override
	public boolean exists(String fqn) {
		return workspace.exists(this, fqn);
	}

	@Override
	public Credential credential() {
		return credential;
	}
	
	public ReadSession dropCollection() {
		workspace.dropCollection(this) ;
		return this ;
	}

	public ReadNode root() {
		return pathBy(Fqn.ROOT);
	}

	public <T> T attribute(String name, Class<T> clz) {
		return clz.cast(attrs.get(name));
	}
	
	public ReadSession attribute(String name, Object value){
		attrs.put(name, value) ;
		return this ;
	}

}
