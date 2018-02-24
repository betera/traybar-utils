package com.betera.traybar_utils;

public class ScriptEntry {

	private String name;
	private String batch;
	private boolean useStdout;

	public String toString() {
		return "\"" + getName() + "\",\"" + getBatch() + "\"," + (isUseStdout() ? "true" : "false");
	}

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

}
