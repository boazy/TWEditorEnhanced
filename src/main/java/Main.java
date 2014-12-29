/*     */ package TWEditor;
/*     */ 
/*     */ import sun.misc.OSEnvironment;

import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ 
/*     */ public class Main
/*     */ {
/*     */   public static JFrame mainWindow;
/*     */   public static String fileSeparator;
/*     */   public static String lineSeparator;
/*  34 */   public static boolean useShellFolder = true;
/*     */   public static String installPath;
/*     */   public static String installDataPath;
/*     */   public static String gamePath;
/*     */   public static String tmpDir;
/*     */   public static File propFile;
/*     */   public static Properties properties;
/*     */   public static StringsDatabase stringsDatabase;
/*     */   public static int languageID;
/*     */   public static Map<String, Object> resourceFiles;
/*     */   public static List<ItemTemplate> itemTemplates;
/*     */   public static SaveDatabase saveDatabase;
/*     */   public static File databaseFile;
/*     */   public static Database database;
/*     */   public static String savePrefix;
/*     */   public static String modName;
/*     */   public static File modFile;
/*     */   public static ResourceDatabase modDatabase;
/*     */   public static List<Quest> quests;
/*  91 */   public static boolean dataModified = false;
/*     */ 
/*  94 */   public static boolean dataChanging = false;
/*     */   private static String deferredText;
/*     */   private static Throwable deferredException;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
                String osName = System.getProperty("os.name").toLowerCase();
                boolean osMac = osName.startsWith("mac");
                boolean osWin = osName.startsWith("windows");
/* 114 */       fileSeparator = System.getProperty("file.separator");
/* 115 */       lineSeparator = System.getProperty("line.separator");
/* 116 */       tmpDir = System.getProperty("java.io.tmpdir");
/*     */ 
/* 121 */       databaseFile = new File(new StringBuilder().append(tmpDir).append("TWEditor.ifo").toString());
/* 122 */       modFile = new File(new StringBuilder().append(tmpDir).append("TWEditor.mod").toString());
/*     */ 
/* 127 */       String option = System.getProperty("UseShellFolder");
/* 128 */       if ((option != null) && (option.equals("0"))) {
/* 129 */         useShellFolder = false;
/*     */       }
/*     */ 
/* 137 */       installPath = System.getProperty("TW.install.path");
/* 138 */       String languageString = System.getProperty("TW.language");
/* 139 */       if (languageString != null)
/* 140 */         languageID = Integer.parseInt(languageString);
/*     */       else {
/* 142 */         languageID = -1;
/*     */       }
/* 144 */       if ((installPath == null) || (languageID == -1)) {
                  if (osMac) {
                      installPath = "/Applications/The Witcher.app/Contents/Resources/drive_c/Program Files/The Witcher";
                      languageID = 3;
                  } else if (osWin) {
                      String regString = "reg query \"HKLM\\Software\\CD Projekt Red\\The Witcher\"";
                      Process process = Runtime.getRuntime().exec(regString);
                      StreamReader streamReader = new StreamReader(process.getInputStream());
                      streamReader.start();
                      process.waitFor();
                      streamReader.join();

                      Pattern p = Pattern.compile("\\s*(\\S*)\\s*(\\S*)\\s*(.*)");
                      String line;
                      while ((line = streamReader.getLine()) != null) {
                          Matcher m = p.matcher(line);
                          if ((m.matches()) && (m.groupCount() == 3) && (m.group(2).equals("REG_SZ"))) {
                              String keyName = m.group(1);
                              if ((keyName.equals("InstallFolder")) && (installPath == null))
                                  installPath = m.group(3);
                              else if ((keyName.equals("Language")) && (languageID == -1)) {
                                  languageID = Integer.parseInt(m.group(3));
                              }
                          }
                      }
                  }

/*     */ 
/* 165 */         if (installPath == null) {
/* 166 */           throw new IOException("Unable to locate The Witcher installation directory");
/*     */         }
/* 168 */         if (languageID == -1) {
/* 169 */           throw new IOException("Unable to determine the installed language");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 175 */       installDataPath = new StringBuilder().append(installPath).append(fileSeparator).append("Data").toString();
/* 176 */       File dirFile = new File(installDataPath);
/* 177 */       if (!dirFile.exists()) {
/* 178 */         dirFile.mkdirs();
/*     */       }
/*     */ 
/* 185 */       gamePath = System.getProperty("TW.data.path");
/* 186 */       if (gamePath == null) {
/* 187 */         File defaultDir = FileSystemView.getFileSystemView().getDefaultDirectory();
                  String userSubPath = osMac ? "com.cdprojektred.TheWitcher/The Witcher" : "The Witcher";
/* 188 */         gamePath = new StringBuilder().append(defaultDir).append(fileSeparator).append(userSubPath).toString();
/*     */       }
/*     */ 
/* 194 */       dirFile = new File(new StringBuilder().append(gamePath).append(fileSeparator).append("saves").toString());
/* 195 */       if (!dirFile.exists()) {
/* 196 */         dirFile.mkdirs();
/*     */       }
/*     */ 
/* 201 */       File stringsFile = new File(new StringBuilder().append(installDataPath).append(fileSeparator).append("dialog_").append(languageID).append(".tlk").toString());
/* 202 */       if (!stringsFile.exists()) {
/* 203 */         throw new IOException(new StringBuilder().append("Localized strings database ").append(stringsFile.getPath()).append(" does not exist").toString());
/*     */       }
/* 205 */       stringsDatabase = new StringsDatabase(stringsFile);
/*     */ 
/* 210 */       KeyDatabase keyDatabase = new KeyDatabase(new StringBuilder().append(installDataPath).append(fileSeparator).append("main.key").toString());
/* 211 */       List keyEntries = keyDatabase.getEntries();
/* 212 */       resourceFiles = new HashMap(keyEntries.size());
/* 213 */       for (Object keyEntryObj : keyEntries) {
                  KeyEntry keyEntry = (KeyEntry)keyEntryObj;
/* 214 */         String name = keyEntry.getFileName().toLowerCase();
/* 215 */         int sep = name.lastIndexOf('.');
/* 216 */         if (sep > 0) {
/* 217 */           String ext = name.substring(sep);
/* 218 */           if ((ext.equals(".2da")) || (ext.equals(".uti"))) {
/* 219 */             resourceFiles.put(name, keyEntry);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 226 */       processOverrides(new File(installDataPath));
/*     */ 
/* 232 */       dirFile = new File(new StringBuilder().append(System.getProperty("user.home")).append(fileSeparator).append("Application Data").append(fileSeparator).append("ScripterRon").toString());
/*     */ 
/* 234 */       if (!dirFile.exists()) {
/* 235 */         dirFile.mkdirs();
/*     */       }
/* 237 */       propFile = new File(new StringBuilder().append(dirFile.getPath()).append(fileSeparator).append("TWEditor.properties").toString());
/* 238 */       properties = new Properties();
/* 239 */       if (propFile.exists()) {
/* 240 */         FileInputStream in = new FileInputStream(propFile);
/* 241 */         properties.load(in);
/* 242 */         in.close();
/*     */       }
/*     */ 
/* 248 */       properties.setProperty("java.version", System.getProperty("java.version"));
/* 249 */       properties.setProperty("java.home", System.getProperty("java.home"));
/* 250 */       properties.setProperty("os.name", System.getProperty("os.name"));
/* 251 */       properties.setProperty("sun.os.patch.level", System.getProperty("sun.os.patch.level"));
/* 252 */       properties.setProperty("user.name", System.getProperty("user.name"));
/* 253 */       properties.setProperty("user.home", System.getProperty("user.home"));
/* 254 */       properties.setProperty("install.path", installPath);
/* 255 */       properties.setProperty("game.path", gamePath);
/* 256 */       properties.setProperty("temp.path", tmpDir);
/*     */ 
/* 261 */       UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/* 262 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 264 */           Main.createAndShowGUI();
/*     */         } } );
/*     */     }
/*     */     catch (Throwable exc) {
/* 268 */       logException("Exception during program initialization", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void processOverrides(File dirFile)
/*     */   {
/* 278 */     File[] files = dirFile.listFiles();
/* 279 */     for (File file : files)
/* 280 */       if (file.isDirectory()) {
/* 281 */         processOverrides(file);
/*     */       } else {
/* 283 */         String name = file.getName().toLowerCase();
/* 284 */         int sep = name.lastIndexOf('.');
/* 285 */         if (sep > 0) {
/* 286 */           String ext = name.substring(sep);
/* 287 */           if ((ext.equals(".2da")) || (ext.equals(".uti")))
/* 288 */             resourceFiles.put(name, file);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void createAndShowGUI()
/*     */   {
/*     */     try
/*     */     {
/* 305 */       JFrame.setDefaultLookAndFeelDecorated(true);
/*     */ 
/* 310 */       mainWindow = new MainWindow();
/* 311 */       mainWindow.pack();
/* 312 */       mainWindow.setVisible(true);
/*     */ 
/* 317 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 319 */           Main.buildTemplates();
/*     */         } } );
/*     */     }
/*     */     catch (Throwable exc) {
/* 323 */       logException("Exception while initializing application window", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void buildTemplates()
/*     */   {
/* 331 */     ProgressDialog dialog = new ProgressDialog(mainWindow, "Loading item templates");
/* 332 */     LoadTemplates task = new LoadTemplates(dialog);
/* 333 */     task.start();
/* 334 */     dialog.showDialog();
/*     */   }
/*     */ 
/*     */   public static void saveProperties()
/*     */   {
/*     */     try
/*     */     {
/* 342 */       FileOutputStream out = new FileOutputStream(propFile);
/* 343 */       properties.store(out, "TWEditor Properties");
/* 344 */       out.close();
/*     */     } catch (Throwable exc) {
/* 346 */       logException("Exception while saving application properties", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String getString(int stringRef)
/*     */   {
/* 359 */     return stringsDatabase.getString(stringRef);
/*     */   }
/*     */ 
/*     */   public static String getLabel(int stringRef)
/*     */   {
/* 370 */     return stringsDatabase.getLabel(stringRef);
/*     */   }
/*     */ 
/*     */   public static String getHeading(int stringRef)
/*     */   {
/* 382 */     return stringsDatabase.getHeading(stringRef);
/*     */   }
/*     */ 
/*     */   public static void logException(String text, Throwable exc)
/*     */   {
/* 396 */     System.runFinalization();
/* 397 */     System.gc();
/*     */ 
/* 403 */     if (SwingUtilities.isEventDispatchThread()) {
/* 404 */       StringBuilder string = new StringBuilder(512);
/*     */ 
/* 409 */       string.append("<html><b>");
/* 410 */       string.append(text);
/* 411 */       string.append("</b><br><br>");
/*     */ 
/* 416 */       string.append("<b>");
/* 417 */       string.append(exc.toString());
/* 418 */       string.append("</b><br><br>");
/*     */ 
/* 423 */       StackTraceElement[] trace = exc.getStackTrace();
/* 424 */       int count = 0;
/* 425 */       for (StackTraceElement elem : trace) {
/* 426 */         string.append(elem.toString());
/* 427 */         string.append("<br>");
/* 428 */         count++; if (count == 25) {
/*     */           break;
/*     */         }
/*     */       }
/* 432 */       string.append("</html>");
/* 433 */       JOptionPane.showMessageDialog(mainWindow, string, "Error", 0);
/* 434 */     } else if (deferredException == null) {
/* 435 */       deferredText = text;
/* 436 */       deferredException = exc;
/*     */       try {
/* 438 */         SwingUtilities.invokeAndWait(new Runnable() {
/*     */           public void run() {
/* 440 */             Main.logException(Main.deferredText, Main.deferredException);
/* 441 */             //Main.access$102(null);
/* 442 */             //Main.access$002(null);
/*     */           } } );
/*     */       }
/*     */       catch (Throwable swingException) {
/* 446 */         deferredException = null;
/* 447 */         deferredText = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void dumpData(String text, byte[] data, int offset, int length)
/*     */   {
/* 461 */     System.out.println(text);
/*     */ 
/* 463 */     for (int i = 0; i < length; i++) {
/* 464 */       if (i % 32 == 0)
/* 465 */         System.out.print(String.format(" %14X  ", new Object[] { Integer.valueOf(i) }));
/* 466 */       else if (i % 4 == 0) {
/* 467 */         System.out.print(" ");
/*     */       }
/* 469 */       System.out.print(String.format("%02X", new Object[] { Byte.valueOf(data[(offset + i)]) }));
/*     */ 
/* 471 */       if (i % 32 == 31) {
/* 472 */         System.out.println();
/*     */       }
/*     */     }
/* 475 */     if (length % 32 != 0)
/* 476 */       System.out.println();
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.Main
 * JD-Core Version:    0.6.2
 */