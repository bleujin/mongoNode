package net.ion.radon.repository.vfs;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.local.LocalFileNameParser;

public class NodeFileNameParser extends LocalFileNameParser {
	private static final NodeFileNameParser INSTANCE = new NodeFileNameParser();

	public static NodeFileNameParser getInstance() {
		return INSTANCE;
	}

	protected String extractRootPrefix(final String uri, final StringBuffer name) throws FileSystemException {
		if (name.length() == 0 || name.charAt(0) != '/') {
			throw new FileSystemException("vfs.provider.local/not-absolute-file-name.error", uri);
		}

		// do not strip the separator, BUT also return it ...
		return "/";
	}

	protected FileName createFileName(String scheme, final String rootFile, final String path, final FileType type) {
		return NodeFileName.create(scheme, "", path, type);
	}

}
