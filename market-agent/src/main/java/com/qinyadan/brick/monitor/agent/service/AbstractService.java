package com.qinyadan.brick.monitor.agent.service;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractService implements Service {

	private final String name;

	private final State state = new State();

	protected AbstractService(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public boolean isStartedOrStarting() {
		return this.state.isStartedOrStarting();
	}

	public boolean isStoppedOrStopping() {
		return this.state.isStoppedOrStopping();
	}

	public boolean isStarted() {
		return this.state.isStarted();
	}

	public boolean isStopped() {
		return this.state.isStopped();
	}

	public final void start() throws Exception {
		if (this.state.beginStart()) {
			doStart();
			this.state.endStart();
		}
	}

	protected abstract void doStart() throws Exception;

	public final void stop() throws Exception {
		if (this.state.beginStop()) {
			doStop();
			this.state.endStop();
		}
	}

	protected abstract void doStop() throws Exception;

	private static final class State {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private AtomicReference<ServiceState> serviceState = new AtomicReference(ServiceState.STOPPED);

		private boolean beginStart() {
			return this.serviceState.compareAndSet(ServiceState.STOPPED, ServiceState.STARTING);
		}

		private void endStart() {
			this.serviceState.set(ServiceState.STARTED);
		}

		private boolean beginStop() {
			return this.serviceState.compareAndSet(ServiceState.STARTED, ServiceState.STOPPING);
		}

		private void endStop() {
			this.serviceState.set(ServiceState.STOPPED);
		}

		private boolean isStarted() {
			return this.serviceState.get() == ServiceState.STARTED;
		}

		private boolean isStartedOrStarting() {
			ServiceState state = (ServiceState) this.serviceState.get();
			return (state == ServiceState.STARTED) || (state == ServiceState.STARTING);
		}

		private boolean isStoppedOrStopping() {
			ServiceState state = (ServiceState) this.serviceState.get();
			return (state == ServiceState.STOPPED) || (state == ServiceState.STOPPING);
		}

		private boolean isStopped() {
			return this.serviceState.get() == ServiceState.STOPPED;
		}
	}

}
