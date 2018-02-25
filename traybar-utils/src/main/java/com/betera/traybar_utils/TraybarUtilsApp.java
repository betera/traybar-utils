package com.betera.traybar_utils;

import info.clearthought.layout.TableLayout;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.betera.traybar_utils.actions.AbstractUIAction;
import com.betera.traybar_utils.actions.AddScriptAction;
import com.betera.traybar_utils.actions.ContextAction;
import com.betera.traybar_utils.actions.PlayAction;
import com.betera.traybar_utils.actions.SettingsAction;

public class TraybarUtilsApp implements ComponentListener {

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
	protected JPanel northPanel;
	protected JPanel centerPanel;

	protected JButton btnSettings;
	protected JButton btnAdd;
	// ///////////////////// END FRAME ///////////////////////

	// ///////////////////// UI SETTINGS ///////////////////////
	protected Font font;

	protected int fontSize;
	protected String fontFamily;

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
		initFrame();
	}

	protected class BoundLabel extends JLabel implements PropertyChangeListener {

		protected Boundable bound;

		protected String property;

		public BoundLabel(String aValue, Boundable aBound, String aProperty) {
			this.bound = aBound;
			this.property = aProperty;
			bound.addPropertyChangeListener(this);
			setText(aValue);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(property)) {
				setText(evt.getNewValue() + "");
			}
		}

		@Override
		protected void finalize() throws Throwable {
			if (bound != null) {
				bound.removePropertyChangeListener(this);
			}
			super.finalize();
		}

	}

	protected JPanel createScriptPanel(ScriptEntry anEntry) {
		JPanel panel = new JPanel();
		int size = props.getDimensionProperty(Props.BUTTON_SIZE, new Dimension(24, 24)).width;
		panel.setLayout(new TableLayout(new double[][] {
				{ TableLayout.FILL, 100 * (font.getSize() / 10.0), size + 4, size + 4 }, { 32.0 } }));

		panel.add(createButton(new PlayAction(this, anEntry)), "2, 0");
		panel.add(createButton(new ContextAction(this, anEntry)), "3, 0");

		panel.add(new BoundLabel(anEntry.getName(), anEntry, "name"), "0, 0");
		panel.add(new BoundLabel(anEntry.getLastExecution(), anEntry, "lastExecution"), "1, 0");

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
		c.anchor = GridBagConstraints.NORTH;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;

		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new GridBagLayout());
		for (ScriptEntry entry : scripts.values()) {
			tempPanel.add(createScriptPanel(entry), c);
		}

		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(tempPanel, BorderLayout.NORTH);
	}

	protected void initScripts() {
		scripts = props.getAllScriptEntries();
	}

	protected void storeScriptProperties() {
		props.setProperty(scripts);
	}

	protected void storeProperties() {
		props.setProperty(Props.FRAME_BOUNDS, frame.getBounds());
		props.setProperty(Props.FONT_SIZE, font.getSize());
		props.setProperty(Props.FONT_FAMILY, font.getFamily());
		storeScriptProperties();
		props.save();
	}

	protected void initFrame() {
		if (frame != null) {
			frame.dispose();
		}

		font = new Font(props.getStringProperty("fontFamily", "Helvetica"), Font.PLAIN, props.getIntProperty(
				"fontSize", 12));
		frame = new JFrame();

		frame.addComponentListener(this);

		frame.setType(Type.UTILITY);

		frame.setUndecorated(props.getBoolProperty(Props.FRAME_DECORATED, false));
		frame.setBounds(props.getRectangleProperty(Props.FRAME_BOUNDS, new Rectangle(0, 0, 400, 200)));

		northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		northPanel.add(createButton(new SettingsAction(this)));
		northPanel.add(createButton(new AddScriptAction(this)));

		centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(northPanel, BorderLayout.NORTH);
		frame.getContentPane().add(centerPanel, BorderLayout.CENTER);

		initScriptUI();
		fontChanged(frame.getRootPane());

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);

	}

	protected void fontChanged(Container aContainer) {
		if (aContainer instanceof JComponent) {
			((JComponent) aContainer).setFont(font);
		}

		for (Component tempComp : aContainer.getComponents()) {
			if (tempComp instanceof Container) {
				fontChanged((Container) tempComp);
			}
		}
	}

	protected void setFrameDecoration(boolean aDecorated) {
		props.setProperty(Props.FRAME_DECORATED, aDecorated);
		initFrame();
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
		menuFrameDecorated.setState(!props.getBoolProperty(Props.FRAME_DECORATED));
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
				exit();
			}
		});
	}

	protected void exit() {
		tray.remove(trayIcon);
		storeProperties();
		System.exit(0);
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
		entry.setLastExecution(new SimpleDateFormat("dd-MM HH:mm:ss").format(date));
	}

	public void componentResized(ComponentEvent e) {
		storeProperties();
	}

	public void componentMoved(ComponentEvent e) {
		storeProperties();
	}

	public void componentShown(ComponentEvent e) {
		storeProperties();
	}

	public void componentHidden(ComponentEvent e) {
		storeProperties();
	}

}
