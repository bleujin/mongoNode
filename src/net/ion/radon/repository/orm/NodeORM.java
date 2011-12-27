package net.ion.radon.repository.orm;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.repository.AradonId;
import net.ion.radon.repository.InListNode;
import net.ion.radon.repository.InNode;
import net.ion.radon.repository.MergeQuery;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.TempNode;

import org.apache.commons.beanutils.ConstructorUtils;

public abstract class NodeORM implements ORMObject {

	private static final long serialVersionUID = 6966530917552174294L;
	private AradonId aid ;
	private TempNode node ;
	private Session session ;
	private IDMethod idm ;
	
	protected NodeORM(Object keyValue) {
		IDMethod idm = getClass().getAnnotation(IDMethod.class);
		if (idm == null)
			throw new IllegalArgumentException("bean must has IDMethod annotation");
//		this.put(idm.keyPropId(), keyValue);
	};
	
	void mergeNode(Session session, IDMethod idm, Object uid){
		this.aid = AradonId.create(idm.groupId(), uid) ;
		this.node = session.tempNode() ;
		this.session = session ;
		this.idm = idm ; 
	}

	public void put(String key, Object value) {
		node.put(key, value);
	}

	public Object get(String key) {
		return node.get(key);
	}

	public Object getUid() {
		return aid.getUid();
	}

	protected int getAsInt(String key) {
		return NumberUtil.toInt(node.getString(key), 0);
	}

	protected void push(String key, InnerNodeORM orm) {
		node.inlist(key).push(orm.getNode().toMap()) ;
	}

	protected <T extends InnerNodeORM> List<T> inlist(String inname, Class<T> clz) {
		List<T> result = ListUtil.newList();
		try {
			InListNode listNode = node.inlist(inname);
			for (InNode inode : listNode.createQuery().find()) {
				InnerNodeORM orm = clz.cast(ConstructorUtils.invokeConstructor(clz, new Object[0]));
				result.add((T) orm.load(inode));
			}
			return result;
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected void inner(String inname, InnerNodeORM orm) {
		put(inname, orm.getNode());
	}

	protected <T extends InnerNodeORM> T inner(String inname, Class<T> clz) {
		try {
			InNode in = node.inner(inname);
			InnerNodeORM result = clz.cast(ConstructorUtils.invokeConstructor(clz, new Object[0]));
			return (T) result.load(in);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getString(String key) {
		return ObjectUtil.toString(node.get(key));
	}

	public NodeORM load(Node loadNode) {
		if (loadNode == null)
			return null;

		this.node = loadNode.toTemp();
		this.session = loadNode.getSession() ;
		this.aid = loadNode.getAradonId() ;
		this.idm = getClass().getAnnotation(IDMethod.class);
		return this;
	}
	
	public NodeResult save(){
		NodeResult result = session.getWorkspace(idm.workspaceName()).merge(session, MergeQuery.createByAradon(aid.getGroup(), aid.getUid()), node);
		return result ;
	}

}