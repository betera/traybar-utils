package com.betera.traybar_utils.actions;

import java.awt.event.ActionEvent;

import com.betera.traybar_utils.TraybarUtilsApp;

public class AddScriptAction extends AbstractUIAction {

	public AddScriptAction(TraybarUtilsApp app) {
		super(app);
	}

	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public String getImageName() {
		return "add";
	}

}
