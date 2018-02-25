package com.betera.traybar_utils;

import java.beans.PropertyChangeListener;

public interface Boundable {

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);

}
