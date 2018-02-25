package com.betera.traybar_utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ScriptEntry implements Boundable {

	private PropertyChangeSupport sup;

	private String name;
	private String batch;
	private boolean useStdout;
	private String stdOut;
	private boolean appendToStdout;
	private String lastExecution;

	public ScriptEntry() {
		sup = new PropertyChangeSupport(this);
	}

	public String getName() {
		return name;
	}

	public void addPropertyChangeListener(PropertyChangeListener aListener) {
		sup.addPropertyChangeListener(aListener);
	}

	public void removePropertyChangeListener(PropertyChangeListener aListener) {
		sup.removePropertyChangeListener(aListener);
	}

	public void setName(String name) {
		String oldVal = this.name;
		this.name = name;
		sup.firePropertyChange("name", oldVal, name);
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		String oldVal = this.batch;
		this.batch = batch;
		sup.firePropertyChange("batch", oldVal, batch);
	}

	public boolean isUseStdout() {
		return useStdout;
	}

	public void setUseStdout(boolean useStdout) {
		boolean oldVal = this.useStdout;
		this.useStdout = useStdout;
		sup.firePropertyChange("useStdout", oldVal, useStdout);
	}

	public String getStdOut() {
		return stdOut;
	}

	public void setStdOut(String stdOut) {
		String oldVal = this.stdOut;
		this.stdOut = stdOut;
		sup.firePropertyChange("stdOut", oldVal, stdOut);

	}

	public boolean isAppendToStdout() {
		return appendToStdout;
	}

	public void setAppendToStdout(boolean appendToStdout) {
		boolean oldVal = this.appendToStdout;
		this.appendToStdout = appendToStdout;
		sup.firePropertyChange("appendToStdout", oldVal, appendToStdout);
	}

	public String getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(String lastExecution) {
		String oldVal = this.lastExecution;
		this.lastExecution = lastExecution;
		sup.firePropertyChange("lastExecution", oldVal, lastExecution);

	}

}
