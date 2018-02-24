package com.betera.traybar_utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SortedProperties extends Properties {

	public Enumeration<Object> keys() {
		Enumeration<Object> keysEnum = super.keys();
		Vector<Object> keyList = new Vector<Object>();

		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}

		Collections.sort(keyList, new Comparator<Object>() {

			public int compare(Object o1, Object o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		return keyList.elements();
	}

}