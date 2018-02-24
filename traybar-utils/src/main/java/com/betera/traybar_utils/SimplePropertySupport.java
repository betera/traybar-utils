package com.betera.traybar_utils;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class SimplePropertySupport {

	String file;

	Properties prop;

	public SimplePropertySupport(String aFile) {
		this.file = aFile;
		prop = new Properties();
		try {
			prop.loadFromXML(new FileInputStream(new File(aFile)));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if(!prop.containsKey(aKey)) {
			return anAlternative;
		}
		return getStringProperty(aKey);
	}
	
	public int getIntProperty(String aKey) {
		return Integer.parseInt(prop.getProperty(aKey));
	}
	
	public int getIntProperty(String aKey, int anAlternative) {
		if(!prop.containsKey(aKey)) {
			return anAlternative;
		}
		
		return getIntProperty(aKey);
	}
	
	public Rectangle getRectangleProperty(String aKey) {
		String[] temp = prop.getProperty(aKey).split(",");
		int[] tempI = new int[4];
		for(int i=0;i<4;i++) {
			tempI[i] = Integer.parseInt(temp[i].trim());
		}
		return new Rectangle(tempI[0],tempI[1],tempI[2],tempI[3]);
	}
	
	public Rectangle getRectangleProperty(String aKey, Rectangle anAlternative) {
		if(!prop.containsKey(aKey)) {
			return anAlternative;
		}
		
		return getRectangleProperty(aKey);
	}
	
}
