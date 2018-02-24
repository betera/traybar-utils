package com.betera.traybar_utils;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.betera.traybar_utils.actions.AbstractUIAction;
import com.betera.traybar_utils.actions.PlayAction;
import com.betera.traybar_utils.actions.SettingsAction;

public class TraybarUtilsApp {

	protected TrayIcon trayIcon;
	protected SystemTray tray;
	protected PopupMenu popup;

	protected SimplePropertySupport props;

	protected final static String configFile = "config.ini";

	protected Map<String, ScriptEntry> scripts;

	protected static Map<String, ImageIcon> cache = new HashMap<String, ImageIcon>();

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
		initScriptUI();
	}

	protected JPanel createScriptPanel(ScriptEntry anEntry) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JButton btnPlay = createButton(new PlayAction(this, anEntry));
		panel.add(btnPlay, BorderLayout.EAST);
		panel.add(new JLabel(anEntry.getName()), BorderLayout.CENTER);

		return panel;
	}

	protected JButton createButton(AbstractUIAction anAction) {
		JButton btn = new JButton();

		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);

		btn.setAction(anAction);

		btn.setSize(props.getDimensionProperty(Props.BUTTON_SIZE, new Dimension(24, 24)));
		btn.setIcon(getImage(anAction.getImageName(), btn.getSize()));
		btn.setPreferredSize(btn.getSize());
		props.setProperty(Props.BUTTON_SIZE, btn.getSize());

		return btn;
	}

	protected void initScriptUI() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 2, 2);
		c.weightx = 1.0;

		for (ScriptEntry entry : scripts.values()) {
			centerPanel.add(createScriptPanel(entry), c);
		}
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

		btnSettings = createButton(new SettingsAction(this));
		btnSettings.setSize(32, 32);
		btnSettings.setBorderPainted(false);
		btnSettings.setContentAreaFilled(false);

		eastPanel = new JPanel();
		eastPanel.setLayout(new BorderLayout());
		eastPanel.add(btnSettings, BorderLayout.EAST);

		centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(eastPanel, BorderLayout.EAST);
		frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

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

	public Dimension getButtonSize() {
		return props.getDimensionProperty(Props.BUTTON_SIZE, new Dimension(24, 24));
	}

	public ImageIcon getImage(String aName, Dimension aSize) {
		String key = aName + "#" + aSize.width + "," + aSize.height;
		if (cache.containsKey(key)) {
			return cache.get(key);
		}

		try {
			Image image = ImageIO.read(this.getClass().getResourceAsStream("images/" + aName + ".png"))
					.getScaledInstance(aSize.width, aSize.height, Image.SCALE_SMOOTH);

			ImageIcon icon = new ImageIcon(image);

			cache.put(key, icon);
			return icon;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public ImageIcon getImage(String aName) {

		if (cache.containsKey(aName)) {
			return cache.get(aName);
		}

		try {
			ImageIcon icon = new ImageIcon(
					ImageIO.read(this.getClass().getResourceAsStream("images/" + aName + ".png")));
			cache.put(aName, icon);
			return icon;
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

	public void updateScriptLastExecution(ScriptEntry entry, Date date) {
		entry.setLastExecution(new SimpleDateFormat("mm-dd hh:MM:ss").format(date));
	}

}
