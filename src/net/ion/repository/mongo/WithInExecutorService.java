package net.ion.repository.mongo;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.ListUtil;

public final class WithInExecutorService extends AbstractExecutorService {

	public WithInExecutorService() {
		shutDown = false;
	}

	public void execute(Runnable command) {
		command.run();
	}

	public void shutdown() {
		shutDown = true;
	}

	public List shutdownNow() {
		shutDown = true;
		return ListUtil.EMPTY ;
	}

	public boolean isShutdown() {
		return shutDown;
	}

	public boolean isTerminated() {
		return shutDown;
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return shutDown;
	}

	private volatile boolean shutDown;
}