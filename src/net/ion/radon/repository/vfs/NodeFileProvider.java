package net.ion.radon.repository.vfs;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.RepositoryCentral;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractOriginatingFileProvider;
import org.apache.commons.vfs2.provider.FileProvider;

import com.mongodb.MongoException;

public class NodeFileProvider extends AbstractOriginatingFileProvider implements FileProvider {

	public static final Collection<Capability> capabilities = Collections.unmodifiableCollection(Arrays.asList(new Capability[] { Capability.CREATE, Capability.DELETE, Capability.RENAME, Capability.GET_TYPE, Capability.GET_LAST_MODIFIED, Capability.SET_LAST_MODIFIED_FILE, Capability.SET_LAST_MODIFIED_FOLDER,
			Capability.LIST_CHILDREN, Capability.READ_CONTENT, Capability.URI, Capability.WRITE_CONTENT, Capability.APPEND_CONTENT, Capability.RANDOM_ACCESS_READ, Capability.RANDOM_ACCESS_WRITE }));

	private String databaseName;
	private int port;
	private String serverHost;
	private String workspaceName;

	public NodeFileProvider() {
		super();
		setFileNameParser(NodeFileNameParser.getInstance());
	}

	protected FileSystem doCreateFileSystem(FileName fname, FileSystemOptions opts) throws FileSystemException {
		try {
			NodeFileSystemConfigBuilder cbuilder = NodeFileSystemConfigBuilder.getInstance() ;
			cbuilder.setServer(serverHost, port, databaseName, workspaceName);
			RepositoryCentral repositoryCentral = cbuilder.getRepository();
			String dbName = ObjectUtil.coalesce(databaseName, cbuilder.getDBName()) ;
			String wname = ObjectUtil.coalesce(workspaceName, cbuilder.getWorkspaceName()) ;
			super.getConfigBuilder() ;
			
			return new NodeFileSystem(repositoryCentral, dbName, wname, fname, opts);
		} catch (UnknownHostException e) {
			throw new FileSystemException(e.getCause()) ;
		} catch (MongoException e) {
			throw new FileSystemException(e.getCause()) ;
		}
		
	}

	public Collection<Capability> getCapabilities() {
		return capabilities;
	}
	
	public FileSystemConfigBuilder getConfigBuilder(){
		return NodeFileSystemConfigBuilder.getInstance() ;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost ;
	}

	public void setPort(String port) {
		this.port = NumberUtil.toInt(port) ;
	}

	public void setDBName(String dbName) {
		this.databaseName = dbName ; 
	}

	public void setWorkspaceName(String workspaceName) {
		this.workspaceName = workspaceName ;
	}
	
	
	public FileObject findFile(FileObject fo, String uri, FileSystemOptions foptions) throws FileSystemException{
		NodeFileObject result = (NodeFileObject) super.findFile(fo, uri, foptions);
		FileType ftype = NodeFileObject.NODE_EXTENSION.equals(result.getName().getExtension()) ? FileType.FILE : FileType.FOLDER;
		result.injectType(ftype) ;
		return result ;
	}
	
//	public FileObject findFile(FileObject baseFile, String uri, FileSystemOptions fileSystemOptions) throws FileSystemException {
//		FileObject result = super.findFile(baseFile, uri, fileSystemOptions);
//		((NodeFileObject)result).injectType(uri.endsWith("/") ? FileType.FOLDER : FileType.FILE) ;
//		return result ;
//	}
}
