package net.ion.radon.repository.vfs;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileContentInfo;
import org.apache.commons.vfs.FileContentInfoFactory;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.impl.DefaultFileContentInfo;

public class NodeFileContentInfoFactory implements FileContentInfoFactory{

	private static NodeFileContentInfoFactory SELF = new NodeFileContentInfoFactory() ;
	
	public FileContentInfo create(FileContent content) throws FileSystemException {
		return new DefaultFileContentInfo("application/json", "utf-8");
	}

	public static FileContentInfoFactory getInstance() {
		return SELF;
	}

}
