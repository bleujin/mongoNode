package net.ion.radon.repository.vfs ;

import java.io.Serializable;

import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;
import net.sf.json.JSONObject;

import org.apache.commons.vfs.FileSystemException;

/**
 * RAM File Object Data.
 * 
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS
 *         team</a>
 */
class NodeFileData implements Serializable {

	private NodeFileName name ;
	private byte[] buffer;
	private NodeFileData(NodeFileName name, byte[] buffer) {
		super();
		this.buffer = buffer ;
		if (name == null) {
			throw new IllegalArgumentException("name can not be null");
		}
		this.name = name;
	}
	
	public static NodeFileData create(NodeFileName name) {
		return new NodeFileData(name, new byte[0]);
	}

	public static NodeFileData load(NodeFileName name, byte[] datas) {
		return new NodeFileData(name, datas);
	}


	byte[] getBuffer() {
		return buffer;
	}

	void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public String toString() {
		return this.name.toString();
	}

	private int size() {
		return buffer.length;
	}

	void resize(int newSize) {
		int size = this.size();
		byte[] newBuf = new byte[newSize];
		System.arraycopy(this.buffer, 0, newBuf, 0, size);
		this.buffer = newBuf;
	}
	
	void save(Session session) throws FileSystemException {

		JSONObject jsonObject = NodeFileUtil.toJSONObject(getBuffer());
		Node found = session.createQuery().path(name.getNodePath()).findOne();
		found.putAll(jsonObject) ;  
		session.commit();
		this.buffer = new byte[0] ;
	}


	void write(byte[] b, int off, int len) {
		int size = this.size();
		int newSize = this.size() + len;
		// Store the Exception in order to notify the client again on close()
		this.resize(newSize);
		System.arraycopy(b, off, this.getBuffer(), size, len);
	}
	
	

}
