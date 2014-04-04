package net.ion.repository.mongo.vfs;

import net.ion.framework.util.ObjectUtil;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;

public class NodeFileName extends AbstractFileName {

	private final String rootFile;
	private String wname;

	private NodeFileName(final String scheme, final String rootFile, final String path, final FileType type) {
		super(scheme, path, type);
		this.rootFile = rootFile;
	}

	public String getRootFile() {
		return rootFile;
	}

	public FileName createName(final String path, FileType type) {
		return create(getScheme(), rootFile, path, type);
	}

	static FileName create(String scheme, String rootFile, String path, FileType type) {
		return new NodeFileName(scheme, rootFile, path, ObjectUtil.coalesce(type, FileType.FILE_OR_FOLDER));
	}

	
	public String getNodePath(){
		return isAsFile() ? super.getParent().getPath() : super.getPath() ;
	}

	boolean isAsFile() {
		return "node".equals(super.getExtension());
	}
	
	public FileType getType(){
		return isAsFile() ? FileType.FILE : FileType.FOLDER ;
	}

	public boolean isRoot() {
		return getDepth() == 0;
	}

	@Override
	protected void appendRootUri(StringBuilder buffer, boolean flag) {
		buffer.append(getScheme());
		buffer.append("://");
		// buffer.append(getWorkspaceName());
		buffer.append(rootFile);
	}

	
}
