package net.ion.repository.mongo.vfs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.repository.mongo.ReadSession;
import net.ion.repository.mongo.RepositoryMongo;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

public class NodeFileSystem extends AbstractFileSystem implements Serializable {

	private static final long serialVersionUID = 3618976263774036655L;
	private final RepositoryMongo rc;
	private final String dbName;
	private final String wname;

	protected NodeFileSystem(RepositoryMongo rc, String dbName, String wname, FileName rootName, FileSystemOptions fileSystemOptions) {
		super(rootName, null, fileSystemOptions);
		this.rc = rc;
		this.dbName = dbName ;
		this.wname = wname;

	}

	protected void addCapabilities(Collection caps) {
		caps.addAll(NodeFileProvider.capabilities);
	}

	public void importTree(File file) throws FileSystemException {
		FileObject fileFo = getFileSystemManager().toFileObject(file);
		this.toNodeFileObject(fileFo, fileFo);
	}

	
	ReadSession getSession() throws IOException{
		return rc.login(dbName, wname);
	}
	
	private void toNodeFileObject(FileObject fo, FileObject root) throws FileSystemException {
		NodeFileObject memFo = (NodeFileObject) this.resolveFile(fo.getName().getPath().substring(root.getName().getPath().length()));
		if (fo.getType().hasChildren()) {
			// Create Folder
			memFo.createFolder();
			// Import recursively
			FileObject[] fos = fo.getChildren();
			for (int i = 0; i < fos.length; i++) {
				FileObject child = fos[i];
				this.toNodeFileObject(child, root);
			}
		} else if (fo.getType().equals(FileType.FILE)) {
			// Read bytes
			InputStream input = null;
			OutputStream output = null;
			try {
				input = fo.getContent().getInputStream();
				output = new BufferedOutputStream(memFo.getOutputStream(), 512);
				int i;
				while ((i = input.read()) != -1) {
					output.write(i);
				}
				output.flush();
				output.close();
			} catch (IOException e) {
				throw new FileSystemException(e.getClass().getName() + " " + e.getMessage());
			} finally {
				IOUtil.closeQuietly(input);
				IOUtil.closeQuietly(output);
			}
		} else {
			throw new FileSystemException("File is not a folder nor a file " + memFo);
		}
	}

	public void close() {
		super.close();
	}

	@Override
	public void setAttribute(String key, Object obj) {
		Debug.debug(key, obj);
	}

	@Override
	protected FileObject createFile(AbstractFileName name) throws Exception {
		NodeFileObject file = new NodeFileObject((NodeFileName)name, this);
		return file;
	}
	


}
