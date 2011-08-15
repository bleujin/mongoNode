package net.ion.radon.repository.vfs;

import java.io.IOException;
import java.io.OutputStream;

import net.ion.radon.repository.Session;

import org.apache.commons.vfs.FileSystemException;

/**
 * OutputStream to a RamFile.
 * 
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS
 *         team</a>
 */
public class NodeFileOutputStream extends OutputStream {

	private Session session; 
	private NodeFileData data;

	protected byte[] buffer1 = new byte[1];

	protected boolean closed = false;

	private IOException exc;
	private NodeFileObject file ;

	NodeFileOutputStream(Session session, NodeFileObject file, NodeFileData data) {
		super();
		this.session = session;
		this.file = file ;
		this.data = data;
	}

	public void write(byte[] b, int off, int len) throws IOException {
		data.write(b, off, len) ;
		
	}

	public void write(int b) throws IOException {
		buffer1[0] = (byte) b;
		this.write(buffer1);
	}

	public void flush() throws IOException {
	}

	public void close() throws IOException {
		if (closed) {
			return;
		}
		// Notify on close that there was an IOException while writing
		if (exc != null) {
			throw exc;
		}
		try {
			this.closed = true;
			
			this.file.save(this.data) ;
			
			this.file.refresh() ;
			this.file.close() ;
			
			
			// Close the
//			this.file.close();
		} catch (Exception e) {
			throw new FileSystemException(e);
		}
	}

}
