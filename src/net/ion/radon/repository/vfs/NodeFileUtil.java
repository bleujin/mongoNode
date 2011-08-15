package net.ion.radon.repository.vfs;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.INode;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileSystemException;

public class NodeFileUtil {

	static Node toNode(Node node, FileName fileName, byte[] buffers) throws FileSystemException {
		try {
			String utf8String = new String(buffers, "UTF-8");
			if (StringUtil.isEmpty(utf8String)) return node ;
			
			JSONObject jso = JSONObject.fromObject(utf8String);

			Iterator<String> kiter = jso.keys();
			while (kiter.hasNext()) {
				String key = kiter.next();
				recursiveSave(node, key, jso.get(key));
			}
			
			return node;
		} catch (UnsupportedEncodingException e) {
			throw new FileSystemException(e);
		} catch (JSONException e) {
			throw new FileSystemException(e);
		}
	}
	
	static JSONObject toJSONObject(byte[] buffers) throws FileSystemException{
		try {
			String utf8String = new String(buffers, "UTF-8");
			return JSONObject.fromObject(utf8String);
		} catch (UnsupportedEncodingException e) {
			throw new FileSystemException(e);
		}
	}
	

	private static void recursiveSave(INode node, String key, Object obj) {
		if (obj instanceof JSONArray) {
			JSONArray jsa = (JSONArray) obj;
			Iterator iter = jsa.iterator();
			while (iter.hasNext()) {
				recursiveSave(node, key, iter.next());
			}
		} else if (obj instanceof JSONObject) {
			InNode inner = node.inner(key);
			JSONObject jso = (JSONObject) obj;
			Iterator<String> kiter = jso.keys();
			while (kiter.hasNext()) {
				String ikey = kiter.next();
				recursiveSave(inner, ikey, jso.get(ikey));
			}
		} else {
			node.append(key, obj);
		}
	}
}
