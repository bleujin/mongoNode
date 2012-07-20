package net.ion.radon.repository;

import java.io.Serializable;
import java.util.Map;

import net.ion.framework.db.RepositoryException;
import net.ion.framework.util.ChainMap;
import net.ion.radon.repository.innode.TempInNode;

import com.mongodb.DBObject;

public class QueueElement {

	private TempNode tnode;
	private QueueElement(TempNode tnode) {
		this.tnode = tnode ;
	}

	public TempNode getTempNode() {
		return tnode;
	}

	static QueueElement create(TempNode tempNode) {
		return new QueueElement(tempNode);
	}

	

}
