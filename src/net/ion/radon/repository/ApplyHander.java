package net.ion.radon.repository;

import com.mongodb.DBCollection;

public interface ApplyHander {

	Object handle(NodeCursor nc);

}
