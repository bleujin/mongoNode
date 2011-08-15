package net.ion.radon.repository;

public interface NodeConstants {

	public static final String RESERVED_PREFIX = "__" ;
	
	public static final String ID = "_id";
	public static final String ARADON = RESERVED_PREFIX + "aradon";
	public static final String PATH = RESERVED_PREFIX + "path" ;
	public static final String NAME = RESERVED_PREFIX + "name" ;
	public static final String CREATED = RESERVED_PREFIX + "created" ;
	public static final String LASTMODIFIED = RESERVED_PREFIX + "lastmodified" ;
	public static final String TIMEZONE = RESERVED_PREFIX + "timezone" ;
	public static final String OWNER = RESERVED_PREFIX + "owner" ;
	public static final String NODE_MODEL_LABEL = RESERVED_PREFIX + "nodemodel" ;
	// public static final String WORKSPACE = "_workspace";
	
	
	
	final static String GROUP = "group";
	final static String UID = "uid";
	final static String GHASH = "ghash";
	
	
	final static String ARADON_GROUP =  ARADON +  ".group";
	final static String ARADON_UID = ARADON +  ".uid";
	final static String ARADON_GHASH = ARADON +  ".ghash";

}
