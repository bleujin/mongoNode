package net.ion.repository.mongo.vfs;

import java.io.UnsupportedEncodingException;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;

import org.apache.commons.vfs2.FileSystemException;

public class NodeFileUtil {

//	static ReadNode toNode(ReadNode node, FileName fileName, byte[] buffers) throws FileSystemException {
//		try {
//			String utf8String = new String(buffers, "UTF-8");
//			if (StringUtil.isEmpty(utf8String)) return node ;
//			
//			JsonObject jso = JsonParser.fromString(utf8String).getAsJsonObject();
//
//			node.putAll(jso.toMap()) ;
//			return node;
//		} catch (UnsupportedEncodingException e) {
//			throw new FileSystemException(e);
//		}
//	}
	
	static JsonObject toJSONObject(byte[] buffers) throws FileSystemException{
		try {
			String utf8String = new String(buffers, "UTF-8");
			return JsonParser.fromString(utf8String).getAsJsonObject();
		} catch (UnsupportedEncodingException e) {
			throw new FileSystemException(e);
		}
	}
	

}
