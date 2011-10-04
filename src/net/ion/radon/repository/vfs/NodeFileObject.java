package net.ion.radon.repository.vfs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;
import net.sf.json.JSONObject;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileContentInfoFactory;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystem;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.NameScope;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.provider.AbstractFileObject;
import org.apache.commons.vfs.util.RandomAccessMode;

public final class NodeFileObject extends AbstractFileObject {

	static String NODE_EXTENSION = "node";

	private NodeFileSystem nfs;

	private transient Node currentNode;
	
	protected NodeFileObject(FileName name, NodeFileSystem fs) throws FileSystemException {
		super(name, fs);
		this.nfs = fs;
		this.currentNode = getSession().createQuery().findByPath(getName().getNodePath());
		
	}

	private Node getCurrentNode() {
//		if (currentNode == null) return getSession().findByPath(getName().getNodePath());
		return currentNode;
	}

	protected FileType doGetType() throws Exception {
		return getName().getType();
	}

	protected FileObject[] doListChildrenResolved() throws Exception {
		List<Node> children = getCurrentNode().getChild().toList(PageBean.ALL);//getDataNode(MustExist).getChild().toList(PageBean.ALL);

		List<FileObject> result = ListUtil.newList() ;
		for (Node child : children) {
			NodeFileObject fo = new NodeFileObject(resolveChildName(child.getName()), nfs);
			fo.injectType(FileType.FOLDER) ;
			result.add(fo) ;
		}

		if (getName().getDepth() > 0){
			NodeFileObject fo = new NodeFileObject(resolveChildName(getName().getBaseName() + "." + NODE_EXTENSION), nfs);
			fo.injectType(FileType.FILE) ;
			result.add(fo) ;
		}
		
		return result.toArray(new FileObject[0]);
	}

	private FileName resolveChildName(String childName) throws FileSystemException {
		return getFileSystem().getFileSystemManager().resolveName(getName(), childName, NameScope.CHILD);
	}

	protected String[] doListChildren() throws Exception {
		// not use..
		throw new UnsupportedOperationException() ;
	}

	private Session getSession() {
		return nfs.getSession();
	}

	protected long doGetContentSize() throws UnsupportedEncodingException {
		
		return getDataBytes().length;
	}

	protected InputStream doGetInputStream() throws FileSystemException, UnsupportedEncodingException {
		if (getType() != FileType.FILE)
			throw new FileSystemException("is not file");
		// getData().setLastModified(node.getLastModified()) ;
		return new ByteArrayInputStream(getDataBytes());
	}
	
	
	byte[] getDataBytes() throws UnsupportedEncodingException {
		String jsonString = JSONObject.fromObject(currentNode.toPropertyMap()).toString();
		return jsonString.getBytes("UTF-8");
	}
	

	protected OutputStream doGetOutputStream(boolean bAppend) throws Exception {
		NodeFileData ndata = NodeFileData.load(getName(), (!bAppend ? new byte[0] : getDataBytes())) ; 
		checkParent() ;
		
		return new NodeFileOutputStream(getSession(), this, ndata);
	}

	public NodeFileName getName() {
		return (NodeFileName) super.getName();
	}

	@Override
	protected void doDelete() throws FileSystemException {
		
		getSession().createQuery().startPathInclude(getName().getNodePath()).remove() ;
		// getSession().remove(currentNode);
	}

	public int delete(final FileSelector selector) throws FileSystemException {
		if (selector instanceof AllFileSelector) {
			synchronized (getFileSystem()) {
				try {
					doDelete();
					super.handleDelete();
				} catch (final RuntimeException re) {
					throw re;
				} catch (final Exception exc) {
					throw new FileSystemException("vfs.provider/delete.error", new Object[] { getName() }, exc);
				}
				return 1;
			}
		} else {
			return super.delete(selector);
		}
	}

	protected long doGetLastModifiedTime() throws Exception {
		return currentNode.getLastModified();
	}

	protected boolean doSetLastModTime(long modtime) throws Exception {
		return true;
	}

	protected void doCreateFolder() throws Exception {
		this.injectType(FileType.FOLDER);

		Node parentNode = findParentNode();
		parentNode.createChild(getName().getBaseName());
		getSession().commit();

	}

	private void checkParent() throws FileSystemException {
		if (getName() == null) {
			throw new FileSystemException(new IllegalStateException("The data has no name. " + getName()));
		}

		// Add to the parent
		if (getName().getDepth() > 0 && (!getParent().exists())) {
			throw new FileSystemException("not exist parent.");
		}
	}

	private Node findParentNode() {
		return getSession().createQuery().findByPath(getName().getParent().getPath());
	}

	protected void doRename(FileObject newfile) throws Exception {
		if (!exists()) {
			throw new FileSystemException("File does not exist: " + getName());
		}
		// Copy data
		NodeFileObject to = (NodeFileObject) newfile;
		to.save(NodeFileData.load(getName(), getDataBytes())) ;
		
		this.doDelete();
	}


	void save(NodeFileData ndata) throws FileSystemException {
		ndata.save(getSession()) ;
	}

	protected RandomAccessContent doGetRandomAccessContent(RandomAccessMode mode) throws Exception {
		return new NodeFileRandomAccessContent(NodeFileData.load(getName(), getDataBytes()), mode);
	}


	protected void injectType(FileType filetype) {
		super.injectType(filetype);
	}

	public void close() throws FileSystemException {
		super.close() ;
		Debug.line() ;
	}
	
	public void refresh() throws FileSystemException{
		super.refresh() ;
		this.currentNode = getSession().createQuery().findByPath(getName().getNodePath());
	}
	

	@Override
	public FileSystem getFileSystem() {
		return nfs;
	}

	@Override
	protected FileContentInfoFactory getFileContentInfoFactory() {
		return NodeFileContentInfoFactory.getInstance();
	}

	@Override
	public boolean exists() throws FileSystemException {
		if (getName().getDepth() == 0)
			return true;
		return ((getType() == FileType.FILE) && getSession().createQuery().findByPath(getName().getParent().getPath()) != null) || ((getType() == FileType.FOLDER) && getSession().createQuery().findByPath(getName().getPath()) != null);
	}

	protected FileContent doCreateFileContent() throws FileSystemException {
		return super.doCreateFileContent();
		// return new DefaultFileContent(this, getFileContentInfoFactory());
	}

	public FileObject resolveFile(String path) throws FileSystemException {
		return super.resolveFile(path);
	}

	public void createFolder() throws FileSystemException {
		synchronized (nfs) {
			if (!isWriteable()) {
				throw new FileSystemException("vfs.provider/create-folder-read-only.error", getName());
			}

			if (exists())
				return;

			// Traverse up the heirarchy and make sure everything is a folder
			final FileObject parent = getParent();
			if (parent != null) {
				parent.createFolder();
			}

			try {
				// Create the folder
				doCreateFolder();

				// Update cached info
				handleCreate(FileType.FOLDER);
			} catch (final RuntimeException re) {
				throw re;
			} catch (final Exception exc) {
				throw new FileSystemException("vfs.provider/create-folder.error", getName(), exc);
			}
		}
	}
	
}
