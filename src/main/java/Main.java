package TWEditor;

import sun.misc.OSEnvironment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

public class Main
{
  public static JFrame mainWindow;
  public static String fileSeparator;
  public static String lineSeparator;
  public static boolean useShellFolder = true;
  public static String installPath;
  public static String installDataPath;
  public static String gamePath;
  public static String tmpDir;
  public static File propFile;
  public static Properties properties;
  public static StringsDatabase stringsDatabase;
  public static int languageID;
  public static Map<String, Object> resourceFiles;
  public static List<ItemTemplate> itemTemplates;
  public static SaveDatabase saveDatabase;
  public static File databaseFile;
  public static Database database;
  public static String savePrefix;
  public static String modName;
  public static File modFile;
  public static ResourceDatabase modDatabase;
  public static List<Quest> quests;
  public static boolean dataModified = false;

  public static boolean dataChanging = false;
  private static String deferredText;
  private static Throwable deferredException;

  public static void main(String[] args)
  {
    try
    {
      String osName = System.getProperty("os.name").toLowerCase();
      boolean osMac = osName.startsWith("mac");
      boolean osLinux = osName.startsWith("linux");
      boolean osWin = osName.startsWith("windows");
      fileSeparator = System.getProperty("file.separator");
      lineSeparator = System.getProperty("line.separator");
      tmpDir = System.getProperty("java.io.tmpdir");
      if(osLinux) {
          tmpDir = tmpDir + "/";
      }

      databaseFile = new File(new StringBuilder().append(tmpDir).append("TWEditor.ifo").toString());
      modFile = new File(new StringBuilder().append(tmpDir).append("TWEditor.mod").toString());

      String option = System.getProperty("UseShellFolder");
      if ((option != null) && (option.equals("0"))) {
        useShellFolder = false;
      }

      installPath = System.getProperty("TW.install.path");
      String languageString = System.getProperty("TW.language");
      if (languageString != null)
        languageID = Integer.parseInt(languageString);
      else {
        languageID = -1;
      }
      if ((installPath == null) || (languageID == -1)) {
        if (osMac) {
            installPath = "/Applications/The Witcher.app/Contents/Resources/drive_c/Program Files/The Witcher";
            languageID = 3;
        } else if (osLinux) {
            String locateString = "locate dialog_3.tlk | grep \"Witcher.*Data\" | sed -e \"s|/Data/dialog_3.tlk||\"";
            String[] cmd = {
                "/bin/sh",
                "-c",
                locateString
            };
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            installPath = reader.readLine();
            reader.close();

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


        if (installPath == null) {
          throw new IOException("Unable to locate The Witcher installation directory");
        }
        if (languageID == -1) {
          throw new IOException("Unable to determine the installed language");
        }

      }

      installDataPath = new StringBuilder().append(installPath).append(fileSeparator).append("Data").toString();
      File dirFile = new File(installDataPath);
      if (!dirFile.exists()) {
        dirFile.mkdirs();
      }

      gamePath = System.getProperty("TW.data.path");
      if (gamePath == null) {
        File defaultDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        String userSubPath = osMac ? "com.cdprojektred.TheWitcher/The Witcher" : "The Witcher";
        gamePath = new StringBuilder().append(defaultDir).append(fileSeparator).append(userSubPath).toString();
      }

      dirFile = new File(new StringBuilder().append(gamePath).append(fileSeparator).append("saves").toString());
      if (!dirFile.exists()) {
        dirFile.mkdirs();
      }

      File stringsFile = new File(new StringBuilder().append(installDataPath).append(fileSeparator).append("dialog_").append(languageID).append(".tlk").toString());
      if (!stringsFile.exists()) {
        throw new IOException(new StringBuilder().append("Localized strings database ").append(stringsFile.getPath()).append(" does not exist").toString());
      }
      stringsDatabase = new StringsDatabase(stringsFile);

      KeyDatabase keyDatabase = new KeyDatabase(new StringBuilder().append(installDataPath).append(fileSeparator).append("main.key").toString());
      List keyEntries = keyDatabase.getEntries();
      resourceFiles = new HashMap(keyEntries.size());
      for (Object keyEntryObj : keyEntries) {
        KeyEntry keyEntry = (KeyEntry)keyEntryObj;
        String name = keyEntry.getFileName().toLowerCase();
        int sep = name.lastIndexOf('.');
        if (sep > 0) {
          String ext = name.substring(sep);
          if ((ext.equals(".2da")) || (ext.equals(".uti"))) {
            resourceFiles.put(name, keyEntry);
          }

        }

      }

      processOverrides(new File(installDataPath));

      dirFile = new File(new StringBuilder().append(System.getProperty("user.home")).append(fileSeparator).append("Application Data").append(fileSeparator).append("ScripterRon").toString());

      if (!dirFile.exists()) {
        dirFile.mkdirs();
      }
      propFile = new File(new StringBuilder().append(dirFile.getPath()).append(fileSeparator).append("TWEditor.properties").toString());
      properties = new Properties();
      if (propFile.exists()) {
        FileInputStream in = new FileInputStream(propFile);
        properties.load(in);
        in.close();
      }

      properties.setProperty("java.version", System.getProperty("java.version"));
      properties.setProperty("java.home", System.getProperty("java.home"));
      properties.setProperty("os.name", System.getProperty("os.name"));
      properties.setProperty("sun.os.patch.level", System.getProperty("sun.os.patch.level"));
      properties.setProperty("user.name", System.getProperty("user.name"));
      properties.setProperty("user.home", System.getProperty("user.home"));
      properties.setProperty("install.path", installPath);
      properties.setProperty("game.path", gamePath);
      properties.setProperty("temp.path", tmpDir);

      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          Main.createAndShowGUI();
        } } );
    }
    catch (Throwable exc) {
      logException("Exception during program initialization", exc);
    }
  }

  private static void processOverrides(File dirFile)
  {
    File[] files = dirFile.listFiles();
    for (File file : files)
      if (file.isDirectory()) {
        processOverrides(file);
      } else {
        String name = file.getName().toLowerCase();
        int sep = name.lastIndexOf('.');
        if (sep > 0) {
          String ext = name.substring(sep);
          if ((ext.equals(".2da")) || (ext.equals(".uti")))
            resourceFiles.put(name, file);
        }
      }
  }

  public static void createAndShowGUI()
  {
    try
    {
      JFrame.setDefaultLookAndFeelDecorated(true);

      mainWindow = new MainWindow();
      mainWindow.pack();
      mainWindow.setVisible(true);

      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          Main.buildTemplates();
        } } );
    }
    catch (Throwable exc) {
      logException("Exception while initializing application window", exc);
    }
  }

  public static void buildTemplates()
  {
    ProgressDialog dialog = new ProgressDialog(mainWindow, "Loading item templates");
    LoadTemplates task = new LoadTemplates(dialog);
    task.start();
    dialog.showDialog();
  }

  public static void saveProperties()
  {
    try
    {
      FileOutputStream out = new FileOutputStream(propFile);
      properties.store(out, "TWEditor Properties");
      out.close();
    } catch (Throwable exc) {
      logException("Exception while saving application properties", exc);
    }
  }

  public static String getString(int stringRef)
  {
    return stringsDatabase.getString(stringRef);
  }

  public static String getLabel(int stringRef)
  {
    return stringsDatabase.getLabel(stringRef);
  }

  public static String getHeading(int stringRef)
  {
    return stringsDatabase.getHeading(stringRef);
  }

  public static void logException(String text, Throwable exc)
  {
    System.runFinalization();
    System.gc();

    if (SwingUtilities.isEventDispatchThread()) {
      StringBuilder string = new StringBuilder(512);

      string.append("<html><b>");
      string.append(text);
      string.append("</b><br><br>");

      string.append("<b>");
      string.append(exc.toString());
      string.append("</b><br><br>");

      StackTraceElement[] trace = exc.getStackTrace();
      int count = 0;
      for (StackTraceElement elem : trace) {
        string.append(elem.toString());
        string.append("<br>");
        count++; if (count == 25) {
          break;
        }
      }
      string.append("</html>");
      JOptionPane.showMessageDialog(mainWindow, string, "Error", 0);
    } else if (deferredException == null) {
      deferredText = text;
      deferredException = exc;
      try {
        SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            Main.logException(Main.deferredText, Main.deferredException);
            //Main.access$102(null);
            //Main.access$002(null);
          } } );
      }
      catch (Throwable swingException) {
        deferredException = null;
        deferredText = null;
      }
    }
  }

  public static void dumpData(String text, byte[] data, int offset, int length)
  {
    System.out.println(text);

    for (int i = 0; i < length; i++) {
      if (i % 32 == 0)
        System.out.print(String.format(" %14X  ", new Object[] { Integer.valueOf(i) }));
      else if (i % 4 == 0) {
        System.out.print(" ");
      }
      System.out.print(String.format("%02X", new Object[] { Byte.valueOf(data[(offset + i)]) }));

      if (i % 32 == 31) {
        System.out.println();
      }
    }
    if (length % 32 != 0)
      System.out.println();
  }
}

