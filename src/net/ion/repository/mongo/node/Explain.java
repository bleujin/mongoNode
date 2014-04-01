package net.ion.repository.mongo.node;

import java.io.Serializable;

import net.ion.framework.util.StringUtil;

import com.mongodb.DBObject;

public class Explain implements Serializable{

	private static final long serialVersionUID = -1250252558101449426L;

	private DBObject dbo ;
	private Explain(DBObject dbo) {
		this.dbo = dbo ;
	}

	public static Explain create(DBObject dbo) {
		return new Explain(dbo);
	}
	
	public boolean useIndex(){
		return ! "BasicCursor".equals(dbo.get("cursor")) ;
	}
	
	public String toString(){
		return dbo.toString() ;
	}

	public String useIndexName() {
		return StringUtil.substringAfter(dbo.get("cursor").toString(), " ");
	}

}
