package com.betera.traybar_utils;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SimplePropertySupport {

	String file;

	Properties prop;

	public SimplePropertySupport(String aFile) {
		this.file = aFile;
		prop = new SortedProperties();
		try {
			prop.load(new FileReader(new File(aFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			// prop.storeToXML(new FileOutputStream(file), null, "UTF-8");
			prop.store(new FileWriter(new File(file)), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setProperty(ScriptEntry aScriptEntry) {
		setProperty(Props.SCRIPT_PREFIX + "_" + aScriptEntry.getName() + "_UseStdout", aScriptEntry.isUseStdout());
		setProperty(Props.SCRIPT_PREFIX + "_" + aScriptEntry.getName() + "_AppendToStdout",
				aScriptEntry.isAppendToStdout());
		setProperty(Props.SCRIPT_PREFIX + "_" + aScriptEntry.getName() + "_Stdout", aScriptEntry.getStdOut());
		setProperty(Props.SCRIPT_PREFIX + "_" + aScriptEntry.getName() + "_Batch", aScriptEntry.getBatch());
		setProperty(Props.SCRIPT_PREFIX + "_" + aScriptEntry.getName() + "_LastExecution",
				aScriptEntry.getLastExecution());
	}

	public void setProperty(Map<String, ScriptEntry> aMap) {
		for (ScriptEntry entry : aMap.values()) {
			setProperty(entry);
		}
	}

	public Map<String, ScriptEntry> getAllScriptEntries() {
		Map<String, ScriptEntry> map = new HashMap<String, ScriptEntry>();

		for (Object tempObj : prop.keySet()) {
			String tempKey = (String) tempObj;

			if (tempKey.startsWith(Props.SCRIPT_PREFIX + "_")) {
				String entryName = tempKey.substring(tempKey.indexOf("_") + 1, tempKey.lastIndexOf("_"));
				map.put(entryName, getScriptEntryProperty(entryName));
			}
		}

		return map;
	}

	public ScriptEntry getScriptEntryProperty(String aName) {
		String tempBatch = getStringProperty(Props.SCRIPT_PREFIX + "_" + aName + "_Batch");
		String tempStdOut = getStringProperty(Props.SCRIPT_PREFIX + "_" + aName + "_StdOut");
		String tempLast = getStringProperty(Props.SCRIPT_PREFIX + "_" + aName + "_LastExecution");
		boolean tempUseStdout = getBoolProperty(Props.SCRIPT_PREFIX + "_" + aName + "_UseStdout", false);
		boolean tempAppendToStdout = getBoolProperty(Props.SCRIPT_PREFIX + "_" + aName + "_AppendToStdout", true);

		ScriptEntry entry = new ScriptEntry();
		entry.setName(aName);
		entry.setBatch(tempBatch);
		entry.setStdOut(tempStdOut);
		entry.setAppendToStdout(tempAppendToStdout);
		entry.setUseStdout(tempUseStdout);
		entry.setLastExecution(tempLast);

		return entry;
	}

	public void setProperty(String aKey, Dimension aDim) {
		prop.setProperty(aKey, aDim.width + "," + aDim.height);
	}

	public Dimension getDimensionProperty(String aKey) {
		String temp = getStringProperty(aKey);

		Dimension dim = new Dimension(Integer.parseInt(temp.split(",")[0].trim()), Integer.parseInt(temp.split(",")[1]
				.trim()));

		return dim;
	}

	public Dimension getDimensionProperty(String aKey, Dimension aDefault) {
		if (!prop.containsKey(aKey)) {
			return aDefault;
		}

		return getDimensionProperty(aKey);
	}

	public void setProperty(String aKey, String aValue) {
		prop.setProperty(aKey, aValue);
	}

	public void setProperty(String aKey, boolean aValue) {
		prop.setProperty(aKey, aValue ? "true" : "false");
	}

	public void setProperty(String aKey, int aValue) {
		prop.setProperty(aKey, aValue + "");
	}

	public void setProperty(String aKey, Rectangle aRect) {
		prop.setProperty(aKey, aRect.x + "," + aRect.y + "," + aRect.width + "," + aRect.height);
	}

	public String getStringProperty(String aKey) {
		return (String) prop.getProperty(aKey);
	}

	public String getStringProperty(String aKey, String anAlternative) {
		if (!prop.containsKey(aKey)) {
			return anAlternative;
		}
		return getStringProperty(aKey);
	}

	public boolean getBoolProperty(String aKey) {
		return "true".equals(getStringProperty(aKey));
	}

	public boolean getBoolProperty(String aKey, boolean anAlternative) {
		if (!prop.containsKey(aKey)) {
			return anAlternative;
		}

		return getBoolProperty(aKey);
	}

	public int getIntProperty(String aKey) {
		return Integer.parseInt(prop.getProperty(aKey));
	}

	public int getIntProperty(String aKey, int anAlternative) {
		if (!prop.containsKey(aKey)) {
			return anAlternative;
		}

		return getIntProperty(aKey);
	}

	public Rectangle getRectangleProperty(String aKey) {
		String[] temp = prop.getProperty(aKey).split(",");
		int[] tempI = new int[4];
		for (int i = 0; i < 4; i++) {
			tempI[i] = Integer.parseInt(temp[i].trim());
		}
		return new Rectangle(tempI[0], tempI[1], tempI[2], tempI[3]);
	}

	public Rectangle getRectangleProperty(String aKey, Rectangle anAlternative) {
		if (!prop.containsKey(aKey)) {
			return anAlternative;
		}

		return getRectangleProperty(aKey);
	}

}
