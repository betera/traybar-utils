package com.betera.traybar_utils.actions;

import java.awt.event.ActionEvent;

import com.betera.traybar_utils.ScriptEntry;
import com.betera.traybar_utils.TraybarUtilsApp;

public class ContextAction extends AbstractUIAction {

	protected ScriptEntry entry;

	public ContextAction(TraybarUtilsApp app, ScriptEntry anEntry) {
		super(app);
		this.entry = anEntry;
	}

	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public String getImageName() {
		return "context";
	}

}
