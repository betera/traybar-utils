package com.betera.traybar_utils.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Date;

import com.betera.traybar_utils.ScriptEntry;
import com.betera.traybar_utils.TraybarUtilsApp;

public class PlayAction extends AbstractUIAction {

	protected ScriptEntry script;

	public PlayAction(TraybarUtilsApp app, ScriptEntry script) {
		super(app);
		this.script = script;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String stdout = "";

			if (script.isUseStdout()) {
				stdout = (script.isAppendToStdout() ? " >>" : " >") + script.getStdOut();
			}

			Runtime.getRuntime().exec(script.getBatch() + stdout);

			app.updateScriptLastExecution(script, new Date());

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public String getImageName() {
		return "play";
	}

}
