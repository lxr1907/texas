package com.notnoop.apns.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.notnoop.apns.ApnsNotification;
import com.notnoop.exceptions.NetworkIOException;

public class ApnsPooledConnection implements ApnsConnection {
	// private static final Logger logger =
	// LoggerFactory.getLogger(ApnsPooledConnection.class);

	private final ApnsConnection prototype;
	private final int max;

	private final ExecutorService executors;
	private final ConcurrentLinkedQueue<ApnsConnection> prototypes;

	public ApnsPooledConnection(ApnsConnection prototype, int max) {
		this(prototype, max, Executors.newFixedThreadPool(max));
	}

	public ApnsPooledConnection(ApnsConnection prototype, int max, ExecutorService executors) {
		this.prototype = prototype;
		this.max = max;

		this.executors = executors;
		this.prototypes = new ConcurrentLinkedQueue<>();
	}

	private final ThreadLocal<ApnsConnection> uniquePrototype = new ThreadLocal<ApnsConnection>() {
		protected ApnsConnection initialValue() {
			ApnsConnection newCopy = prototype.copy();
			prototypes.add(newCopy);
			return newCopy;
		}
	};

	public void sendMessage(final ApnsNotification m) throws NetworkIOException {
		Future<Void> future = executors.submit(new Callable<Void>() {
			public Void call() throws Exception {
				uniquePrototype.get().sendMessage(m);
				return null;
			}
		});
		try {
			future.get();
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException ee) {
			if (ee.getCause() instanceof NetworkIOException) {
				throw (NetworkIOException) ee.getCause();
			}
		}
	}

	public ApnsConnection copy() {
		return new ApnsPooledConnection(prototype, max);
	}

	public void close() {
		executors.shutdown();
		try {
			executors.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// logger.warn("pool termination interrupted", e);
		}
		for (ApnsConnection conn : prototypes) {
			Utilities.close(conn);
		}
		Utilities.close(prototype);
	}

	public void testConnection() {
		prototype.testConnection();
	}

	public synchronized void setCacheLength(int cacheLength) {
		for (ApnsConnection conn : prototypes) {
			conn.setCacheLength(cacheLength);
		}
	}

	public int getCacheLength() {
		return prototypes.peek().getCacheLength();
	}
}
