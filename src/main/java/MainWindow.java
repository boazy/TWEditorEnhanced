package TWEditor;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MainWindow extends JFrame
  implements ActionListener
{
  private boolean windowMinimized = false;

  private boolean titleModified = false;
  private JTabbedPane tabbedPane;
  private StatsPanel statsPanel;
  private AttributesPanel attributesPanel;
  private SignsPanel signsPanel;
  private StylesPanel stylesPanel;
  private EquipPanel equipPanel;
  private InventoryPanel inventoryPanel;
  private QuestsPanel questsPanel;

  public MainWindow()
  {
    super("The Witcher Save Editor");
    setDefaultCloseOperation(2);

    String propValue = Main.properties.getProperty("window.main.position");
    if (propValue != null) {
      int sep = propValue.indexOf(',');
      int frameX = Integer.parseInt(propValue.substring(0, sep));
      int frameY = Integer.parseInt(propValue.substring(sep + 1));
      setLocation(frameX, frameY);
    }

    int frameWidth = 800;
    int frameHeight = 600;
    propValue = Main.properties.getProperty("window.main.size");
    if (propValue != null) {
      int sep = propValue.indexOf(',');
      frameWidth = Math.max(Integer.parseInt(propValue.substring(0, sep)), frameWidth);
      frameHeight = Math.max(Integer.parseInt(propValue.substring(sep + 1)), frameHeight);
    }

    setPreferredSize(new Dimension(frameWidth, frameHeight));

    JMenuBar menuBar = new JMenuBar();
    menuBar.setOpaque(true);

    JMenu menu = new JMenu("File");
    menu.setMnemonic(70);

    JMenuItem menuItem = new JMenuItem("Open");
    menuItem.setActionCommand("open");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menuItem = new JMenuItem("Save");
    menuItem.setActionCommand("save");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menuItem = new JMenuItem("Close");
    menuItem.setActionCommand("close");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menu.addSeparator();

    menuItem = new JMenuItem("Exit");
    menuItem.setActionCommand("exit");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menuBar.add(menu);

    menu = new JMenu("Actions");
    menu.setMnemonic(65);

    menuItem = new JMenuItem("Unpack Save");
    menuItem.setActionCommand("unpack save");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menuItem = new JMenuItem("Repack Save");
    menuItem.setActionCommand("repack save");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menuBar.add(menu);

    menu = new JMenu("Help");
    menu.setMnemonic(72);

    menuItem = new JMenuItem("About");
    menuItem.setActionCommand("about");
    menuItem.addActionListener(this);
    menu.add(menuItem);

    menuBar.add(menu);

    setJMenuBar(menuBar);

    this.tabbedPane = new JTabbedPane();
    this.tabbedPane.setVisible(false);
    setContentPane(this.tabbedPane);

    JPanel panel = new JPanel();
    this.statsPanel = new StatsPanel();
    panel.add(this.statsPanel);
    this.tabbedPane.addTab("Stats", panel);

    panel = new JPanel();
    this.attributesPanel = new AttributesPanel();
    panel.add(this.attributesPanel);
    this.tabbedPane.addTab("Attributes", panel);

    panel = new JPanel();
    this.signsPanel = new SignsPanel();
    panel.add(this.signsPanel);
    this.tabbedPane.addTab("Signs", panel);

    panel = new JPanel();
    this.stylesPanel = new StylesPanel();
    panel.add(this.stylesPanel);
    this.tabbedPane.addTab("Styles", panel);

    panel = new JPanel();
    this.equipPanel = new EquipPanel();
    panel.add(this.equipPanel);
    this.tabbedPane.addTab("Equipment", panel);

    panel = new JPanel();
    this.inventoryPanel = new InventoryPanel();
    panel.add(this.inventoryPanel);
    this.tabbedPane.addTab("Inventory", panel);

    panel = new JPanel();
    this.questsPanel = new QuestsPanel();
    panel.add(this.questsPanel);
    this.tabbedPane.addTab("Quests", panel);

    addWindowListener(new ApplicationWindowListener(this));
  }

  public void setTitle(String title)
  {
    if (title != null) {
      super.setTitle(title);
      this.titleModified = false;
    } else if (Main.saveDatabase == null) {
      super.setTitle("The Witcher Save Editor");
      this.titleModified = false;
    } else if ((Main.dataModified) && (!this.titleModified)) {
      super.setTitle("The Witcher Save Editor - " + Main.saveDatabase.getName() + "*");
      this.titleModified = true;
    } else if ((!Main.dataModified) && (this.titleModified)) {
      super.setTitle("The Witcher Save Editor - " + Main.saveDatabase.getName());
      this.titleModified = false;
    }
  }

  public void actionPerformed(ActionEvent ae)
  {
    try
    {
      String action = ae.getActionCommand();
      if (action.equals("open")) {
        openFile();
        if (Main.saveDatabase != null)
          setTitle("The Witcher Save Editor - " + Main.saveDatabase.getName());
        else
          setTitle(null);
      } else if (action.equals("about")) {
        aboutProgram();
      } else if (action.equals("exit")) {
        exitProgram();
      } else if (Main.saveDatabase == null) {
        JOptionPane.showMessageDialog(this, "No save file is open", "No Save", 0);
      } else if (action.equals("save")) {
        saveFile();
        setTitle(null);
      } else if (action.equals("close")) {
        closeFile();
        setTitle(null);
      } else if (action.equals("unpack save")) {
        unpackSave();
      } else if (action.equals("repack save")) {
        packSave();
        setTitle(null);
      }
    } catch (Throwable exc) {
      Main.logException("Exception while processing action event", exc);
    }
  }

  private void openFile()
  {
    if (!closeFile()) {
      return;
    }

    String currentDirectory = Main.properties.getProperty("current.directory");
    JFileChooser chooser;
    if (currentDirectory != null) {
      File dirFile = new File(currentDirectory);
      if ((dirFile.exists()) && (dirFile.isDirectory()))
        chooser = new JFileChooser(dirFile);
      else
        chooser = new JFileChooser(Main.gamePath + Main.fileSeparator + "saves");
    } else {
      chooser = new JFileChooser(Main.gamePath + Main.fileSeparator + "saves");
    }

    chooser.putClientProperty("FileChooser.useShellFolder", Boolean.valueOf(Main.useShellFolder));
    chooser.setDialogTitle("Select Save File");
    if (chooser.showOpenDialog(this) != 0) {
      return;
    }
    File file = chooser.getSelectedFile();
    Main.properties.setProperty("current.directory", file.getParent());

    loadSave(file);
  }

  private void loadSave(File file)
  {
    String saveName = file.getName();
    int sep = saveName.lastIndexOf('.');
    if (sep > 0) {
      saveName = saveName.substring(0, sep);
    }

    ProgressDialog dialog = new ProgressDialog(this, "Loading " + saveName);
    LoadFile task = new LoadFile(dialog, file);
    task.start();
    boolean success = dialog.showDialog();

    if (success)
      try {
        Main.dataChanging = true;

        DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
        list = (DBList)list.getElement("Mod_PlayerList").getValue();
        list = (DBList)list.getElement(0).getValue();
        this.statsPanel.setFields(list);
        this.attributesPanel.setFields(list);
        this.signsPanel.setFields(list);
        this.stylesPanel.setFields(list);
        this.equipPanel.setFields(list);
        this.inventoryPanel.setFields(list);
        this.questsPanel.setFields(list);

        this.tabbedPane.setSelectedIndex(0);
        this.tabbedPane.setVisible(true);

        Main.dataChanging = false;
        Main.dataModified = false;
      } catch (DBException exc) {
        Main.logException("Database format is not valid", exc);
      } catch (IOException exc) {
        Main.logException("I/O error while building tabbed panes", exc);
      }
  }

  private boolean saveFile()
  {
    if (Main.saveDatabase == null) {
      return false;
    }
    boolean saved = false;
    try
    {
      DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
      list = (DBList)list.getElement("Mod_PlayerList").getValue();
      list = (DBList)list.getElement(0).getValue();
      this.statsPanel.getFields(list);
      this.attributesPanel.getFields(list);
      this.signsPanel.getFields(list);
      this.stylesPanel.getFields(list);
      this.equipPanel.getFields(list);
      this.inventoryPanel.getFields(list);
      this.questsPanel.getFields(list);

      ProgressDialog dialog = new ProgressDialog(this, "Saving " + Main.saveDatabase.getName());
      SaveFile task = new SaveFile(dialog);
      task.start();
      saved = dialog.showDialog();
      if (saved)
        Main.dataModified = false;
    } catch (DBException exc) {
      Main.logException("Database format is not valid", exc);
    }

    return saved;
  }

  private boolean closeFile()
  {
    if (Main.saveDatabase == null) {
      return true;
    }

    if (Main.dataModified) {
      int option = JOptionPane.showConfirmDialog(this, "The current save has been modified.  Do you want to save the changes?", "Save Modified", 1);

      if (option == 2) {
        return false;
      }
      if ((option == 0) && 
        (!saveFile())) {
        return false;
      }

    }

    Main.database = null;
    Main.modDatabase = null;
    Main.saveDatabase = null;
    Main.dataModified = false;
    this.tabbedPane.setVisible(false);
    return true;
  }

  private void unpackSave()
  {
    String extractDirectory = Main.properties.getProperty("extract.directory");
    JFileChooser chooser;
    if (extractDirectory != null) {
      File dirFile = new File(extractDirectory);
      if ((dirFile.exists()) && (dirFile.isDirectory()))
        chooser = new JFileChooser(dirFile);
      else
        chooser = new JFileChooser();
    } else {
      chooser = new JFileChooser();
    }

    chooser.putClientProperty("FileChooser.useShellFolder", Boolean.valueOf(Main.useShellFolder));
    chooser.setDialogTitle("Select Destination Directory");
    chooser.setApproveButtonText("Select");
    chooser.setFileSelectionMode(1);
    if (chooser.showOpenDialog(this) != 0) {
      return;
    }
    File dirFile = chooser.getSelectedFile();
    Main.properties.setProperty("extract.directory", dirFile.getPath());
    if (!dirFile.exists()) {
      dirFile.mkdirs();
    }

    ProgressDialog dialog = new ProgressDialog(this, "Unpacking " + Main.saveDatabase.getName());
    UnpackSave task = new UnpackSave(dialog, dirFile);
    task.start();
    if (dialog.showDialog())
      JOptionPane.showMessageDialog(this, "Save game unpacked to " + dirFile.getPath(), "Save Unpacked", 1);
  }

  private void packSave()
  {
    if (Main.dataModified) {
      int option = JOptionPane.showConfirmDialog(this, "The current save has been modified and these changes will be lost.  Do you want to continue?", "Save Modified", 0);

      if (option != 0) {
        return;
      }

    }

    String extractDirectory = Main.properties.getProperty("extract.directory");
    JFileChooser chooser;
    if (extractDirectory != null) {
      File dirFile = new File(extractDirectory);
      if ((dirFile.exists()) && (dirFile.isDirectory()))
        chooser = new JFileChooser(dirFile);
      else
        chooser = new JFileChooser();
    } else {
      chooser = new JFileChooser();
    }

    chooser.putClientProperty("FileChooser.useShellFolder", Boolean.valueOf(Main.useShellFolder));
    chooser.setDialogTitle("Select Source Directory");
    chooser.setApproveButtonText("Select");
    chooser.setFileSelectionMode(1);
    if (chooser.showOpenDialog(this) != 0) {
      return;
    }
    File dirFile = chooser.getSelectedFile();
    Main.properties.setProperty("extract.directory", dirFile.getPath());
    if (!dirFile.exists()) {
      JOptionPane.showMessageDialog(this, "Source directory does not exist", "Directory not found", 0);

      return;
    }

    Main.dataModified = false;
    ProgressDialog dialog = new ProgressDialog(this, "Packing " + Main.saveDatabase.getName());
    PackFile task = new PackFile(dialog, dirFile);
    task.start();
    boolean saved = dialog.showDialog();

    File file = Main.saveDatabase.getFile();
    closeFile();
    if (saved)
      loadSave(file);
  }

  private void exitProgram()
  {
    closeFile();

    if (Main.modFile.exists()) {
      Main.modFile.delete();
    }
    if (Main.databaseFile.exists()) {
      Main.databaseFile.delete();
    }

    if (!this.windowMinimized) {
      Point p = Main.mainWindow.getLocation();
      Dimension d = Main.mainWindow.getSize();
      Main.properties.setProperty("window.main.position", p.x + "," + p.y);
      Main.properties.setProperty("window.main.size", d.width + "," + d.height);
    }

    Main.saveProperties();

    System.exit(0);
  }

  private void aboutProgram()
  {
    StringBuilder info = new StringBuilder(256);
    info.append("<html>The Witcher Save Editor Version 2.1<br>");

    info.append("<br>User name: ");
    info.append(System.getProperty("user.name"));

    info.append("<br>Home directory: ");
    info.append(System.getProperty("user.home"));

    info.append("<br><br>OS: ");
    info.append(System.getProperty("os.name"));

    info.append("<br>OS version: ");
    info.append(System.getProperty("os.version"));

    info.append("<br>OS patch level: ");
    info.append(System.getProperty("sun.os.patch.level"));

    info.append("<br><br>Java vendor: ");
    info.append(System.getProperty("java.vendor"));

    info.append("<br>Java version: ");
    info.append(System.getProperty("java.version"));

    info.append("<br>Java home directory: ");
    info.append(System.getProperty("java.home"));

    info.append("<br>Java class path: ");
    info.append(System.getProperty("java.class.path"));

    info.append("<br><br>TW install path: ");
    info.append(Main.installPath);

    info.append("<br>TW data path: ");
    info.append(Main.gamePath);

    info.append("<br>Temporary data path: ");
    info.append(Main.tmpDir);

    info.append("<br>Language identifier: ");
    info.append(Main.languageID);

    info.append("</html>");
    JOptionPane.showMessageDialog(this, info.toString(), "About The Witcher Save Editor", 1);
  }

  private class ApplicationWindowListener extends WindowAdapter
  {
    private JFrame window;

    public ApplicationWindowListener(JFrame window)
    {
      this.window = window;
    }

    public void windowIconified(WindowEvent we)
    {
      MainWindow.this.windowMinimized = true;
    }

    public void windowDeiconified(WindowEvent we)
    {
      MainWindow.this.windowMinimized = false;
    }

    public void windowClosing(WindowEvent we)
    {
      try
      {
        MainWindow.this.exitProgram();
      } catch (Exception exc) {
        Main.logException("Exception while closing application window", exc);
      }
    }
  }
}

