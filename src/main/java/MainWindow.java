/*     */ package TWEditor;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Properties;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTabbedPane;
/*     */ 
/*     */ public class MainWindow extends JFrame
/*     */   implements ActionListener
/*     */ {
/*  18 */   private boolean windowMinimized = false;
/*     */ 
/*  21 */   private boolean titleModified = false;
/*     */   private JTabbedPane tabbedPane;
/*     */   private StatsPanel statsPanel;
/*     */   private AttributesPanel attributesPanel;
/*     */   private SignsPanel signsPanel;
/*     */   private StylesPanel stylesPanel;
/*     */   private EquipPanel equipPanel;
/*     */   private InventoryPanel inventoryPanel;
/*     */   private QuestsPanel questsPanel;
/*     */ 
/*     */   public MainWindow()
/*     */   {
/*  55 */     super("The Witcher Save Editor");
/*  56 */     setDefaultCloseOperation(2);
/*     */ 
/*  63 */     String propValue = Main.properties.getProperty("window.main.position");
/*  64 */     if (propValue != null) {
/*  65 */       int sep = propValue.indexOf(',');
/*  66 */       int frameX = Integer.parseInt(propValue.substring(0, sep));
/*  67 */       int frameY = Integer.parseInt(propValue.substring(sep + 1));
/*  68 */       setLocation(frameX, frameY);
/*     */     }
/*     */ 
/*  75 */     int frameWidth = 800;
/*  76 */     int frameHeight = 600;
/*  77 */     propValue = Main.properties.getProperty("window.main.size");
/*  78 */     if (propValue != null) {
/*  79 */       int sep = propValue.indexOf(',');
/*  80 */       frameWidth = Math.max(Integer.parseInt(propValue.substring(0, sep)), frameWidth);
/*  81 */       frameHeight = Math.max(Integer.parseInt(propValue.substring(sep + 1)), frameHeight);
/*     */     }
/*     */ 
/*  84 */     setPreferredSize(new Dimension(frameWidth, frameHeight));
/*     */ 
/*  89 */     JMenuBar menuBar = new JMenuBar();
/*  90 */     menuBar.setOpaque(true);
/*     */ 
/* 100 */     JMenu menu = new JMenu("File");
/* 101 */     menu.setMnemonic(70);
/*     */ 
/* 103 */     JMenuItem menuItem = new JMenuItem("Open");
/* 104 */     menuItem.setActionCommand("open");
/* 105 */     menuItem.addActionListener(this);
/* 106 */     menu.add(menuItem);
/*     */ 
/* 108 */     menuItem = new JMenuItem("Save");
/* 109 */     menuItem.setActionCommand("save");
/* 110 */     menuItem.addActionListener(this);
/* 111 */     menu.add(menuItem);
/*     */ 
/* 113 */     menuItem = new JMenuItem("Close");
/* 114 */     menuItem.setActionCommand("close");
/* 115 */     menuItem.addActionListener(this);
/* 116 */     menu.add(menuItem);
/*     */ 
/* 118 */     menu.addSeparator();
/*     */ 
/* 120 */     menuItem = new JMenuItem("Exit");
/* 121 */     menuItem.setActionCommand("exit");
/* 122 */     menuItem.addActionListener(this);
/* 123 */     menu.add(menuItem);
/*     */ 
/* 125 */     menuBar.add(menu);
/*     */ 
/* 132 */     menu = new JMenu("Actions");
/* 133 */     menu.setMnemonic(65);
/*     */ 
/* 135 */     menuItem = new JMenuItem("Unpack Save");
/* 136 */     menuItem.setActionCommand("unpack save");
/* 137 */     menuItem.addActionListener(this);
/* 138 */     menu.add(menuItem);
/*     */ 
/* 140 */     menuItem = new JMenuItem("Repack Save");
/* 141 */     menuItem.setActionCommand("repack save");
/* 142 */     menuItem.addActionListener(this);
/* 143 */     menu.add(menuItem);
/*     */ 
/* 145 */     menuBar.add(menu);
/*     */ 
/* 152 */     menu = new JMenu("Help");
/* 153 */     menu.setMnemonic(72);
/*     */ 
/* 155 */     menuItem = new JMenuItem("About");
/* 156 */     menuItem.setActionCommand("about");
/* 157 */     menuItem.addActionListener(this);
/* 158 */     menu.add(menuItem);
/*     */ 
/* 160 */     menuBar.add(menu);
/*     */ 
/* 165 */     setJMenuBar(menuBar);
/*     */ 
/* 170 */     this.tabbedPane = new JTabbedPane();
/* 171 */     this.tabbedPane.setVisible(false);
/* 172 */     setContentPane(this.tabbedPane);
/*     */ 
/* 174 */     JPanel panel = new JPanel();
/* 175 */     this.statsPanel = new StatsPanel();
/* 176 */     panel.add(this.statsPanel);
/* 177 */     this.tabbedPane.addTab("Stats", panel);
/*     */ 
/* 179 */     panel = new JPanel();
/* 180 */     this.attributesPanel = new AttributesPanel();
/* 181 */     panel.add(this.attributesPanel);
/* 182 */     this.tabbedPane.addTab("Attributes", panel);
/*     */ 
/* 184 */     panel = new JPanel();
/* 185 */     this.signsPanel = new SignsPanel();
/* 186 */     panel.add(this.signsPanel);
/* 187 */     this.tabbedPane.addTab("Signs", panel);
/*     */ 
/* 189 */     panel = new JPanel();
/* 190 */     this.stylesPanel = new StylesPanel();
/* 191 */     panel.add(this.stylesPanel);
/* 192 */     this.tabbedPane.addTab("Styles", panel);
/*     */ 
/* 194 */     panel = new JPanel();
/* 195 */     this.equipPanel = new EquipPanel();
/* 196 */     panel.add(this.equipPanel);
/* 197 */     this.tabbedPane.addTab("Equipment", panel);
/*     */ 
/* 199 */     panel = new JPanel();
/* 200 */     this.inventoryPanel = new InventoryPanel();
/* 201 */     panel.add(this.inventoryPanel);
/* 202 */     this.tabbedPane.addTab("Inventory", panel);
/*     */ 
/* 204 */     panel = new JPanel();
/* 205 */     this.questsPanel = new QuestsPanel();
/* 206 */     panel.add(this.questsPanel);
/* 207 */     this.tabbedPane.addTab("Quests", panel);
/*     */ 
/* 212 */     addWindowListener(new ApplicationWindowListener(this));
/*     */   }
/*     */ 
/*     */   public void setTitle(String title)
/*     */   {
/* 223 */     if (title != null) {
/* 224 */       super.setTitle(title);
/* 225 */       this.titleModified = false;
/* 226 */     } else if (Main.saveDatabase == null) {
/* 227 */       super.setTitle("The Witcher Save Editor");
/* 228 */       this.titleModified = false;
/* 229 */     } else if ((Main.dataModified) && (!this.titleModified)) {
/* 230 */       super.setTitle("The Witcher Save Editor - " + Main.saveDatabase.getName() + "*");
/* 231 */       this.titleModified = true;
/* 232 */     } else if ((!Main.dataModified) && (this.titleModified)) {
/* 233 */       super.setTitle("The Witcher Save Editor - " + Main.saveDatabase.getName());
/* 234 */       this.titleModified = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/*     */     try
/*     */     {
/* 245 */       String action = ae.getActionCommand();
/* 246 */       if (action.equals("open")) {
/* 247 */         openFile();
/* 248 */         if (Main.saveDatabase != null)
/* 249 */           setTitle("The Witcher Save Editor - " + Main.saveDatabase.getName());
/*     */         else
/* 251 */           setTitle(null);
/* 252 */       } else if (action.equals("about")) {
/* 253 */         aboutProgram();
/* 254 */       } else if (action.equals("exit")) {
/* 255 */         exitProgram();
/* 256 */       } else if (Main.saveDatabase == null) {
/* 257 */         JOptionPane.showMessageDialog(this, "No save file is open", "No Save", 0);
/* 258 */       } else if (action.equals("save")) {
/* 259 */         saveFile();
/* 260 */         setTitle(null);
/* 261 */       } else if (action.equals("close")) {
/* 262 */         closeFile();
/* 263 */         setTitle(null);
/* 264 */       } else if (action.equals("unpack save")) {
/* 265 */         unpackSave();
/* 266 */       } else if (action.equals("repack save")) {
/* 267 */         packSave();
/* 268 */         setTitle(null);
/*     */       }
/*     */     } catch (Throwable exc) {
/* 271 */       Main.logException("Exception while processing action event", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void openFile()
/*     */   {
/* 283 */     if (!closeFile()) {
/* 284 */       return;
/*     */     }
/*     */ 
/* 290 */     String currentDirectory = Main.properties.getProperty("current.directory");
/*     */     JFileChooser chooser;
/* 291 */     if (currentDirectory != null) {
/* 292 */       File dirFile = new File(currentDirectory);
/* 293 */       if ((dirFile.exists()) && (dirFile.isDirectory()))
/* 294 */         chooser = new JFileChooser(dirFile);
/*     */       else
/* 296 */         chooser = new JFileChooser(Main.gamePath + Main.fileSeparator + "saves");
/*     */     } else {
/* 298 */       chooser = new JFileChooser(Main.gamePath + Main.fileSeparator + "saves");
/*     */     }
/*     */ 
/* 301 */     chooser.putClientProperty("FileChooser.useShellFolder", Boolean.valueOf(Main.useShellFolder));
/* 302 */     chooser.setDialogTitle("Select Save File");
/* 303 */     if (chooser.showOpenDialog(this) != 0) {
/* 304 */       return;
/*     */     }
/* 306 */     File file = chooser.getSelectedFile();
/* 307 */     Main.properties.setProperty("current.directory", file.getParent());
/*     */ 
/* 312 */     loadSave(file);
/*     */   }
/*     */ 
/*     */   private void loadSave(File file)
/*     */   {
/* 321 */     String saveName = file.getName();
/* 322 */     int sep = saveName.lastIndexOf('.');
/* 323 */     if (sep > 0) {
/* 324 */       saveName = saveName.substring(0, sep);
/*     */     }
/*     */ 
/* 329 */     ProgressDialog dialog = new ProgressDialog(this, "Loading " + saveName);
/* 330 */     LoadFile task = new LoadFile(dialog, file);
/* 331 */     task.start();
/* 332 */     boolean success = dialog.showDialog();
/*     */ 
/* 337 */     if (success)
/*     */       try {
/* 339 */         Main.dataChanging = true;
/*     */ 
/* 341 */         DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 342 */         list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 343 */         list = (DBList)list.getElement(0).getValue();
/* 344 */         this.statsPanel.setFields(list);
/* 345 */         this.attributesPanel.setFields(list);
/* 346 */         this.signsPanel.setFields(list);
/* 347 */         this.stylesPanel.setFields(list);
/* 348 */         this.equipPanel.setFields(list);
/* 349 */         this.inventoryPanel.setFields(list);
/* 350 */         this.questsPanel.setFields(list);
/*     */ 
/* 352 */         this.tabbedPane.setSelectedIndex(0);
/* 353 */         this.tabbedPane.setVisible(true);
/*     */ 
/* 355 */         Main.dataChanging = false;
/* 356 */         Main.dataModified = false;
/*     */       } catch (DBException exc) {
/* 358 */         Main.logException("Database format is not valid", exc);
/*     */       } catch (IOException exc) {
/* 360 */         Main.logException("I/O error while building tabbed panes", exc);
/*     */       }
/*     */   }
/*     */ 
/*     */   private boolean saveFile()
/*     */   {
/* 375 */     if (Main.saveDatabase == null) {
/* 376 */       return false;
/*     */     }
/* 378 */     boolean saved = false;
/*     */     try
/*     */     {
/* 384 */       DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 385 */       list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 386 */       list = (DBList)list.getElement(0).getValue();
/* 387 */       this.statsPanel.getFields(list);
/* 388 */       this.attributesPanel.getFields(list);
/* 389 */       this.signsPanel.getFields(list);
/* 390 */       this.stylesPanel.getFields(list);
/* 391 */       this.equipPanel.getFields(list);
/* 392 */       this.inventoryPanel.getFields(list);
/* 393 */       this.questsPanel.getFields(list);
/*     */ 
/* 398 */       ProgressDialog dialog = new ProgressDialog(this, "Saving " + Main.saveDatabase.getName());
/* 399 */       SaveFile task = new SaveFile(dialog);
/* 400 */       task.start();
/* 401 */       saved = dialog.showDialog();
/* 402 */       if (saved)
/* 403 */         Main.dataModified = false;
/*     */     } catch (DBException exc) {
/* 405 */       Main.logException("Database format is not valid", exc);
/*     */     }
/*     */ 
/* 408 */     return saved;
/*     */   }
/*     */ 
/*     */   private boolean closeFile()
/*     */   {
/* 421 */     if (Main.saveDatabase == null) {
/* 422 */       return true;
/*     */     }
/*     */ 
/* 427 */     if (Main.dataModified) {
/* 428 */       int option = JOptionPane.showConfirmDialog(this, "The current save has been modified.  Do you want to save the changes?", "Save Modified", 1);
/*     */ 
/* 431 */       if (option == 2) {
/* 432 */         return false;
/*     */       }
/* 434 */       if ((option == 0) && 
/* 435 */         (!saveFile())) {
/* 436 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 443 */     Main.database = null;
/* 444 */     Main.modDatabase = null;
/* 445 */     Main.saveDatabase = null;
/* 446 */     Main.dataModified = false;
/* 447 */     this.tabbedPane.setVisible(false);
/* 448 */     return true;
/*     */   }
/*     */ 
/*     */   private void unpackSave()
/*     */   {
/* 461 */     String extractDirectory = Main.properties.getProperty("extract.directory");
/*     */     JFileChooser chooser;
/* 462 */     if (extractDirectory != null) {
/* 463 */       File dirFile = new File(extractDirectory);
/* 464 */       if ((dirFile.exists()) && (dirFile.isDirectory()))
/* 465 */         chooser = new JFileChooser(dirFile);
/*     */       else
/* 467 */         chooser = new JFileChooser();
/*     */     } else {
/* 469 */       chooser = new JFileChooser();
/*     */     }
/*     */ 
/* 472 */     chooser.putClientProperty("FileChooser.useShellFolder", Boolean.valueOf(Main.useShellFolder));
/* 473 */     chooser.setDialogTitle("Select Destination Directory");
/* 474 */     chooser.setApproveButtonText("Select");
/* 475 */     chooser.setFileSelectionMode(1);
/* 476 */     if (chooser.showOpenDialog(this) != 0) {
/* 477 */       return;
/*     */     }
/* 479 */     File dirFile = chooser.getSelectedFile();
/* 480 */     Main.properties.setProperty("extract.directory", dirFile.getPath());
/* 481 */     if (!dirFile.exists()) {
/* 482 */       dirFile.mkdirs();
/*     */     }
/*     */ 
/* 487 */     ProgressDialog dialog = new ProgressDialog(this, "Unpacking " + Main.saveDatabase.getName());
/* 488 */     UnpackSave task = new UnpackSave(dialog, dirFile);
/* 489 */     task.start();
/* 490 */     if (dialog.showDialog())
/* 491 */       JOptionPane.showMessageDialog(this, "Save game unpacked to " + dirFile.getPath(), "Save Unpacked", 1);
/*     */   }
/*     */ 
/*     */   private void packSave()
/*     */   {
/* 503 */     if (Main.dataModified) {
/* 504 */       int option = JOptionPane.showConfirmDialog(this, "The current save has been modified and these changes will be lost.  Do you want to continue?", "Save Modified", 0);
/*     */ 
/* 507 */       if (option != 0) {
/* 508 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 515 */     String extractDirectory = Main.properties.getProperty("extract.directory");
/*     */     JFileChooser chooser;
/* 516 */     if (extractDirectory != null) {
/* 517 */       File dirFile = new File(extractDirectory);
/* 518 */       if ((dirFile.exists()) && (dirFile.isDirectory()))
/* 519 */         chooser = new JFileChooser(dirFile);
/*     */       else
/* 521 */         chooser = new JFileChooser();
/*     */     } else {
/* 523 */       chooser = new JFileChooser();
/*     */     }
/*     */ 
/* 526 */     chooser.putClientProperty("FileChooser.useShellFolder", Boolean.valueOf(Main.useShellFolder));
/* 527 */     chooser.setDialogTitle("Select Source Directory");
/* 528 */     chooser.setApproveButtonText("Select");
/* 529 */     chooser.setFileSelectionMode(1);
/* 530 */     if (chooser.showOpenDialog(this) != 0) {
/* 531 */       return;
/*     */     }
/* 533 */     File dirFile = chooser.getSelectedFile();
/* 534 */     Main.properties.setProperty("extract.directory", dirFile.getPath());
/* 535 */     if (!dirFile.exists()) {
/* 536 */       JOptionPane.showMessageDialog(this, "Source directory does not exist", "Directory not found", 0);
/*     */ 
/* 538 */       return;
/*     */     }
/*     */ 
/* 544 */     Main.dataModified = false;
/* 545 */     ProgressDialog dialog = new ProgressDialog(this, "Packing " + Main.saveDatabase.getName());
/* 546 */     PackFile task = new PackFile(dialog, dirFile);
/* 547 */     task.start();
/* 548 */     boolean saved = dialog.showDialog();
/*     */ 
/* 553 */     File file = Main.saveDatabase.getFile();
/* 554 */     closeFile();
/* 555 */     if (saved)
/* 556 */       loadSave(file);
/*     */   }
/*     */ 
/*     */   private void exitProgram()
/*     */   {
/* 567 */     closeFile();
/*     */ 
/* 572 */     if (Main.modFile.exists()) {
/* 573 */       Main.modFile.delete();
/*     */     }
/* 575 */     if (Main.databaseFile.exists()) {
/* 576 */       Main.databaseFile.delete();
/*     */     }
/*     */ 
/* 582 */     if (!this.windowMinimized) {
/* 583 */       Point p = Main.mainWindow.getLocation();
/* 584 */       Dimension d = Main.mainWindow.getSize();
/* 585 */       Main.properties.setProperty("window.main.position", p.x + "," + p.y);
/* 586 */       Main.properties.setProperty("window.main.size", d.width + "," + d.height);
/*     */     }
/*     */ 
/* 592 */     Main.saveProperties();
/*     */ 
/* 597 */     System.exit(0);
/*     */   }
/*     */ 
/*     */   private void aboutProgram()
/*     */   {
/* 604 */     StringBuilder info = new StringBuilder(256);
/* 605 */     info.append("<html>The Witcher Save Editor Version 2.1<br>");
/*     */ 
/* 607 */     info.append("<br>User name: ");
/* 608 */     info.append(System.getProperty("user.name"));
/*     */ 
/* 610 */     info.append("<br>Home directory: ");
/* 611 */     info.append(System.getProperty("user.home"));
/*     */ 
/* 613 */     info.append("<br><br>OS: ");
/* 614 */     info.append(System.getProperty("os.name"));
/*     */ 
/* 616 */     info.append("<br>OS version: ");
/* 617 */     info.append(System.getProperty("os.version"));
/*     */ 
/* 619 */     info.append("<br>OS patch level: ");
/* 620 */     info.append(System.getProperty("sun.os.patch.level"));
/*     */ 
/* 622 */     info.append("<br><br>Java vendor: ");
/* 623 */     info.append(System.getProperty("java.vendor"));
/*     */ 
/* 625 */     info.append("<br>Java version: ");
/* 626 */     info.append(System.getProperty("java.version"));
/*     */ 
/* 628 */     info.append("<br>Java home directory: ");
/* 629 */     info.append(System.getProperty("java.home"));
/*     */ 
/* 631 */     info.append("<br>Java class path: ");
/* 632 */     info.append(System.getProperty("java.class.path"));
/*     */ 
/* 634 */     info.append("<br><br>TW install path: ");
/* 635 */     info.append(Main.installPath);
/*     */ 
/* 637 */     info.append("<br>TW data path: ");
/* 638 */     info.append(Main.gamePath);
/*     */ 
/* 640 */     info.append("<br>Temporary data path: ");
/* 641 */     info.append(Main.tmpDir);
/*     */ 
/* 643 */     info.append("<br>Language identifier: ");
/* 644 */     info.append(Main.languageID);
/*     */ 
/* 646 */     info.append("</html>");
/* 647 */     JOptionPane.showMessageDialog(this, info.toString(), "About The Witcher Save Editor", 1);
/*     */   }
/*     */ 
/*     */   private class ApplicationWindowListener extends WindowAdapter
/*     */   {
/*     */     private JFrame window;
/*     */ 
/*     */     public ApplicationWindowListener(JFrame window)
/*     */     {
/* 665 */       this.window = window;
/*     */     }
/*     */ 
/*     */     public void windowIconified(WindowEvent we)
/*     */     {
/* 674 */       MainWindow.this.windowMinimized = true;
/*     */     }
/*     */ 
/*     */     public void windowDeiconified(WindowEvent we)
/*     */     {
/* 683 */       MainWindow.this.windowMinimized = false;
/*     */     }
/*     */ 
/*     */     public void windowClosing(WindowEvent we)
/*     */     {
/*     */       try
/*     */       {
/* 693 */         MainWindow.this.exitProgram();
/*     */       } catch (Exception exc) {
/* 695 */         Main.logException("Exception while closing application window", exc);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.MainWindow
 * JD-Core Version:    0.6.2
 */