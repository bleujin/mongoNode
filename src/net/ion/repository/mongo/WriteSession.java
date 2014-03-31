package net.ion.repository.mongo;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections.set.ListOrderedSet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.ion.framework.util.ListUtil;
import net.ion.repository.mongo.node.WriteNode;
import net.ion.repository.mongo.node.WriteNode.Touch;

public class WriteSession {

	private ReadSession rsession;
	private Workspace workspace;
	private String colName;
	private Set<LogRow> logRows = ListOrderedSet.decorate(ListUtil.newList());;
	private Cache<Fqn, Boolean> cache = CacheBuilder.newBuilder().maximumSize(100).build();

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
		rsession.workspace().writeLog(this, rsession, logRows);
	}

	public WriteNode pathBy(String path) {
		Fqn fqn = Fqn.fromString(path);
		Fqn parent = fqn.getParent();
		try {
			while (parent != Fqn.ROOT) {
				final Fqn target = parent ;
				boolean exists = cache.get(parent, new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return workspace.exists(rsession, target);
					}
				});
				if (! exists) workspace.pathBy(this, target) ;
				parent = parent.getParent();
			}
		} catch (ExecutionException e) {
			throw new IllegalArgumentException(e) ;
		}
		return workspace.pathBy(this, fqn);
	}

	public WriteNode pathBy(Fqn path) {
		return workspace.pathBy(this, path);
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

		@Override
		public boolean equals(Object obj) {
			if (!LogRow.class.isInstance(obj))
				return false;

			LogRow that = (LogRow) obj;
			return this.touch == that.touch && this.target.equals(that.target);
		}

		@Override
		public int hashCode() {
			return target.hashCode() + touch.ordinal();
		}

		public String toString() {
			return target + ", " + touch;
		}
	}

}
