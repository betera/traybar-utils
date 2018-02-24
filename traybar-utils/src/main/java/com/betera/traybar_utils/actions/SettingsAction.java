package com.betera.traybar_utils.actions;

import java.awt.event.ActionEvent;

import com.betera.traybar_utils.TraybarUtilsApp;

public class SettingsAction extends AbstractUIAction {

	public SettingsAction(TraybarUtilsApp app) {
		super(app);
	}

	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public String getImageName() {
		return "settings";
	}

}
