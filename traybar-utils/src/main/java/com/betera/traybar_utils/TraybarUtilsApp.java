package com.betera.traybar_utils;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TraybarUtilsApp {

	protected TrayIcon trayIcon;
	protected SystemTray tray;
	protected PopupMenu popup;

	protected SimplePropertySupport props;

	protected final static String configFile = "config.ini";

	protected Map<String, ScriptEntry> scripts;

	// ///////////////////// FRAME ///////////////////////
	protected JFrame frame;

	protected JPanel eastPanel;
	protected JPanel centerPanel;

	protected JButton btnSettings;

	// ///////////////////// END FRAME ///////////////////////

	public TraybarUtilsApp() {
		initProperties();
		initScripts();
		initUI();
	}

	protected void initProperties() {
		props = new SimplePropertySupport(configFile);
		props.save();
	}

	protected void initUI() {
		initTray();
		initAppFrame();
	}

	protected void initScripts() {
		scripts = props.getAllScriptEntries();
	}

	protected void storeScriptProperties() {
		props.setProperty(scripts);
	}

	protected void storeProperties() {
		props.setProperty(Props.FRAME_BOUNDS, frame.getBounds());
		storeScriptProperties();
		props.save();
	}

	protected void initAppFrame() {
		if (frame != null) {
			frame.dispose();
		}

		frame = new JFrame();

		frame.setUndecorated(props.getBoolProperty(Props.FRAME_DECORATED, false));
		frame.setBounds(props.getRectangleProperty(Props.FRAME_BOUNDS, new Rectangle(0, 0, 400, 200)));

		btnSettings = new JButton(getImage("settings"));
		btnSettings.setSize(32, 32);
		btnSettings.setBorderPainted(false);
		btnSettings.setContentAreaFilled(false);

		eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());
		eastPanel.add(btnSettings, BorderLayout.EAST);

		centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout());

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(eastPanel, BorderLayout.EAST);

		frame.setVisible(true);
	}

	protected void setFrameDecoration(boolean aDecorated) {
		props.setProperty(Props.FRAME_DECORATED, aDecorated);
		initAppFrame();
	}

	protected void initTray() {
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		popup = new PopupMenu();
		trayIcon = new TrayIcon(getImage("trayicon").getImage());
		trayIcon.setImageAutoSize(true);
		tray = SystemTray.getSystemTray();

		// Create a popup menu components
		CheckboxMenuItem menuFrameDecorated = new CheckboxMenuItem("Set frame decorated");
		menuFrameDecorated.setState(props.getBoolProperty(Props.FRAME_DECORATED));
		MenuItem exitItem = new MenuItem("Exit");

		// Add components to popup menu
		popup.add(menuFrameDecorated);
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
			return;
		}

		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "This dialog box is run from System Tray");
			}
		});

		menuFrameDecorated.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				int cb1Id = e.getStateChange();
				if (cb1Id == ItemEvent.SELECTED) {
					setFrameDecoration(true);
				} else {
					setFrameDecoration(false);
				}
			}
		});

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon);
				storeProperties();
				System.exit(0);
			}
		});
	}

	protected ImageIcon getImage(String aName) {
		try {
			return new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream("images/" + aName + ".png")));
		} catch (IOException e) {
			return null;
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TraybarUtilsApp();
			}
		});
	}

}
