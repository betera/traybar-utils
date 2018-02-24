package com.betera.traybar_utils;

public class ScriptEntry {

	private String name;
	private String batch;
	private boolean useStdout;
	private String stdOut;
	private boolean appendToStdout;
	private String lastExecution;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public boolean isUseStdout() {
		return useStdout;
	}

	public void setUseStdout(boolean useStdout) {
		this.useStdout = useStdout;
	}

	public String getStdOut() {
		return stdOut;
	}

	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}

	public boolean isAppendToStdout() {
		return appendToStdout;
	}

	public void setAppendToStdout(boolean appendToStdout) {
		this.appendToStdout = appendToStdout;
	}

	public String getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(String lastExecution) {
		this.lastExecution = lastExecution;
	}

}
