package net.ion.radon.repository.vfs;

import java.net.UnknownHostException;

import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.RepositoryCentral;

import org.apache.commons.vfs.FileSystemConfigBuilder;
import org.apache.commons.vfs.FileSystemOptions;

import com.mongodb.MongoException;


public final class NodeFileSystemConfigBuilder extends FileSystemConfigBuilder {

	private static final String MAX_SIZE_KEY = "maxsize";
	private static final String SERVER_ADDRESS = "_mongo_address";
	private static final String SERVER_PORT = "_mongo_port";

	private static NodeFileSystemConfigBuilder singleton = new NodeFileSystemConfigBuilder();

	private String address ;
	private int port = 27017;
	private String dbName ;
	private String workName ;
	private NodeFileSystemConfigBuilder() {
		super();
	}

	public static NodeFileSystemConfigBuilder getInstance() {
		return singleton;
	}

	protected Class getConfigClass() {
		return NodeFileSystem.class;
	}

	public int getMaxSize(FileSystemOptions opts) {
		Integer size = (Integer) getParam(opts, MAX_SIZE_KEY);
		if (size != null) {
			return size.intValue();
		} else {
			return Integer.MAX_VALUE;
		}

	}

	public void setMaxSize(FileSystemOptions opts, int sizeInBytes) {
		setParam(opts, MAX_SIZE_KEY, new Integer(sizeInBytes));
	}
	
	public void setServer(String ipAddress, int port, String dbName, String workName){
		this.address = ipAddress ;
		this.port = port ;
		this.dbName = dbName ;
		this.workName = workName ; 
	}

	public RepositoryCentral getRepository() throws UnknownHostException, MongoException {
		if (StringUtil.isEmpty(address)) {
			throw new IllegalArgumentException("not setted server address..") ;
		}
		
		return RepositoryCentral.create(address, port) ;
		
	}

	public String getDBName() {
		return this.dbName ;
	}
	
	public String getWorkspaceName(){
		return this.workName ;
	}
}
