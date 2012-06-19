package net.ion.radon.repository.vfs;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileContentInfo;
import org.apache.commons.vfs2.FileContentInfoFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.DefaultFileContentInfo;

public class NodeFileContentInfoFactory implements FileContentInfoFactory{

	private static NodeFileContentInfoFactory SELF = new NodeFileContentInfoFactory() ;
	
	public FileContentInfo create(FileContent content) throws FileSystemException {
		return new DefaultFileContentInfo("application/json", "utf-8");
	}

	public static FileContentInfoFactory getInstance() {
		return SELF;
	}

}
