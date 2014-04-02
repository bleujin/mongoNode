package net.ion.repository.mongo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.repository.mongo.index.IndexHandler;
import net.ion.repository.mongo.index.SessionJob;
import net.ion.repository.mongo.node.WriteNode;
import net.ion.repository.mongo.node.WriteNode.Touch;

import org.apache.commons.collections.set.ListOrderedSet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class WriteSession implements ISession<WriteNode> {

	private ReadSession rsession;
	private Workspace workspace;
	private String colName;
	private Set<LogRow> logRows = ListOrderedSet.decorate(ListUtil.newList());;
	private Cache<Fqn, WriteNode> cache = CacheBuilder.newBuilder().build();
	private Map<String, Object> attrs = MapUtil.newMap();
	private List<SessionJob> sjobs = ListUtil.newList();

	private WriteSession(ReadSession rsession) {
		this.rsession = rsession;
		this.workspace = rsession.workspace();
		this.colName = rsession.colName();
	}

	public static WriteSession create(ReadSession rsession) {
		return new WriteSession(rsession);
	}

	public void beginTran() {

	}

	public void endTran() throws IOException {
		rsession.workspace().writeLog(this, rsession, this.sjobs, logRows);
	}

	public WriteNode pathBy(String path) {
		return pathBy(Fqn.fromString(path));
	}

	public WriteNode pathBy(final Fqn fqn) {
		try {
			return cache.get(fqn, new Callable<WriteNode>() {
				@Override
				public WriteNode call() throws Exception {
					Fqn parentFqn = fqn.getParent();
					while (parentFqn != Fqn.ROOT) {
						final Fqn target = parentFqn;
						// create parent
						WriteNode parent = cache.get(parentFqn, new Callable<WriteNode>() {
							@Override
							public WriteNode call() throws Exception {
								return workspace.pathBy(WriteSession.this, target);
							}
						});
						parentFqn = parentFqn.getParent();
					}
					return workspace.pathBy(WriteSession.this, fqn);
				}
			});
		} catch (Exception e) {
			throw new IllegalStateException(e) ;
		}

	}

	String colName() {
		return colName;
	}

	public void notifyTouch(WriteNode source, Fqn targetFqn, Touch touch) {
		if ((touch == Touch.TOUCH) || (targetFqn.isRoot() && touch == Touch.TOUCH))
			return;
		logRows.add(LogRow.create(source, touch, targetFqn));
	}

	public void completed(boolean isSuccess) {

	}

	public WriteNode root() {
		return pathBy("/");
	}

	public boolean exists(String fqn) {
		return workspace.exists(rsession, fqn);
	}

	public boolean exists(Fqn fqn) {
		return workspace.exists(rsession, fqn);
	}

	public Credential credential() {
		return rsession.credential();
	}

	static class LogRow {

		private WriteNode source;
		private Touch touch;
		private Fqn target;

		LogRow(WriteNode source, Touch touch, Fqn target) {
			this.source = source;
			this.touch = touch;
			this.target = target;
		}

		final static LogRow create(WriteNode source, Touch touch, Fqn target) {
			return new LogRow(source, touch, target);
		}

		public Touch touch() {
			return touch;
		}

		public Fqn target() {
			return target;
		}

		public WriteNode source() {
			return source;
		}

		// @Override
		// public boolean equals(Object obj) {
		// if (!LogRow.class.isInstance(obj))
		// return false;
		//
		// LogRow that = (LogRow) obj;
		// return this.touch == that.touch && this.target.equals(that.target);
		// }
		//
		// @Override
		// public int hashCode() {
		// return target.hashCode() + touch.ordinal();
		// }

		public String toString() {
			return target + ", " + touch;
		}
	}

	@Override
	public Workspace workspace() {
		return workspace;
	}

	public ReadSession readSession() {
		return rsession;
	}

	public <T> T attribute(String name, Class<T> clz) {
		return clz.cast(attrs.get(name));
	}

	public WriteSession attribute(String name, Object value) {
		attrs.put(name, value);
		return this;
	}

	public IndexHandler ensureIndex(String indexName) {
		return IndexHandler.create(this, indexName);
	}

	public WriteSession addSessionJob(SessionJob sessionJob) {
		sjobs.add(sessionJob);
		return this;
	}

}
