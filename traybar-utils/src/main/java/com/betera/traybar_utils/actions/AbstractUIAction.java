package com.betera.traybar_utils.actions;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.betera.traybar_utils.TraybarUtilsApp;

public abstract class AbstractUIAction extends AbstractAction {

	protected ImageIcon icon;

	protected TraybarUtilsApp app;

	public static final String ICON_KEY = "icon";

	public abstract String getImageName();

	public AbstractUIAction(TraybarUtilsApp app) {
		this.app = app;
		icon = app.getImage(getImageName(), app.getButtonSize());

		putValue(ICON_KEY, icon);
	}

}
