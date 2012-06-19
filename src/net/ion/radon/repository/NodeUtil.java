package net.ion.radon.repository;


import static net.ion.radon.repository.NodeConstants.ID;
import static net.ion.radon.repository.NodeConstants.RESERVED_PREFIX;
public class NodeUtil {

	public final static boolean isReservedProperty(String propId){
		return propId.startsWith(RESERVED_PREFIX) || propId.equals(ID) ;
	}
	
}
