package TWEditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class LoadFile extends Thread
{
  private ProgressDialog progressDialog;
  private File file;
  private boolean loadSuccessful = false;

  public LoadFile(ProgressDialog dialog, File file)
  {
    this.progressDialog = dialog;
    this.file = file;
  }

  public void run()
  {
    InputStream in = null;
    FileOutputStream out = null;
    try
    {
      SaveDatabase saveDatabase = new SaveDatabase(this.file);
      saveDatabase.load();
      this.progressDialog.updateProgress(25);
      String saveName = saveDatabase.getName();
      Main.savePrefix = saveName + Main.fileSeparator;

      int sep = saveName.indexOf(' ');
      if ((sep != 6) || (!Character.isDigit(saveName.charAt(0)))) {
        throw new DBException("Save name is not formatted correctly");
      }
      String fileName = "save_" + saveName.substring(0, 6) + ".smm";
      SaveEntry saveEntry = saveDatabase.getEntry(fileName);
      if (saveEntry == null) {
        throw new DBException("Save does not contain " + fileName);
      }
      in = saveEntry.getInputStream();
      Database database = new Database();
      database.load(in);
      in.close();
      in = null;
      this.progressDialog.updateProgress(35);

      DBList list = (DBList)database.getTopLevelStruct().getValue();
      String startingMod = list.getString("StartingMod");
      if (startingMod.length() == 0) {
        throw new DBException("StartingMod not found in SMM database");
      }
      DBElement element = list.getElement("QuestBase_list");
      if ((element == null) || (element.getType() != 15)) {
        throw new DBException("QuestBaseList not found in SMM database");
      }
      DBList questList = (DBList)element.getValue();
      if (questList.getElementCount() == 0) {
        throw new DBException("No quest list found in SMM database");
      }
      DBList fieldList = (DBList)questList.getElement(0).getValue();
      String questDBName = fieldList.getString("QuestBase");
      if (questDBName.length() == 0) {
        throw new DBException("No quest database name found in SMM database");
      }

      Main.modName = startingMod + ".sav";
      saveEntry = saveDatabase.getEntry(Main.modName);
      if (saveEntry == null) {
        throw new DBException("Save does not contain " + Main.modName);
      }
      in = saveEntry.getInputStream();
      if (Main.modFile.exists()) {
        Main.modFile.delete();
      }

      byte[] buffer = new byte[4096];
      out = new FileOutputStream(Main.modFile);
      int count;
      while ((count = in.read(buffer)) > 0) {
        out.write(buffer, 0, count);
      }
      in.close();
      in = null;
      out.close();
      out = null;
      this.progressDialog.updateProgress(50);

      ResourceDatabase modDatabase = new ResourceDatabase(Main.modFile);
      modDatabase.load();
      this.progressDialog.updateProgress(60);

      ResourceEntry resourceEntry = modDatabase.getEntry("module.ifo");
      if (resourceEntry == null) {
        throw new DBException("Save does not contain module.ifo");
      }
      in = resourceEntry.getInputStream();
      if (Main.databaseFile.exists()) {
        Main.databaseFile.delete();
      }
      out = new FileOutputStream(Main.databaseFile);
      while ((count = in.read(buffer)) > 0) {
        out.write(buffer, 0, count);
      }
      in.close();
      in = null;
      out.close();
      out = null;
      this.progressDialog.updateProgress(75);

      database = new Database(Main.databaseFile);
      database.load();
      list = (DBList)database.getTopLevelStruct().getValue();
      element = list.getElement("Mod_PlayerList");
      if ((element == null) || (element.getType() != 15)) {
        throw new DBException("module.ifo does not contain Mod_PlayerList");
      }
      list = (DBList)element.getValue();
      if (list.getElementCount() == 0) {
        throw new DBException("Mod_PlayerList is empty");
      }
      this.progressDialog.updateProgress(80);

      fileName = questDBName + ".qdb";
      saveEntry = saveDatabase.getEntry(fileName);
      if (saveEntry == null) {
        throw new DBException("Save does not contain " + fileName);
      }
      in = saveEntry.getInputStream();
      Database questDatabase = new Database();
      questDatabase.load(in);
      in.close();
      in = null;
      list = (DBList)questDatabase.getTopLevelStruct().getValue();
      element = list.getElement("Quests");
      if ((element == null) || (element.getType() != 15)) {
        throw new DBException("Quests not found in quest database");
      }
      questList = (DBList)element.getValue();
      this.progressDialog.updateProgress(85);

      count = questList.getElementCount();
      Main.quests = new ArrayList(count);
      for (int i = 0; i < count; i++) {
        fieldList = (DBList)questList.getElement(i).getValue();
        String resourceName = fieldList.getString("File");
        fileName = resourceName + ".qst";
        saveEntry = saveDatabase.getEntry(fileName);
        if (saveEntry == null) {
          throw new DBException("Save does not contain " + fileName);
        }
        in = saveEntry.getInputStream();
        questDatabase = new Database();
        questDatabase.load(in);
        in.close();
        in = null;
        Quest quest = new Quest(resourceName, questDatabase.getTopLevelStruct());
        if (quest.getQuestName().length() > 0) {
          Main.quests.add(quest);
        }
      }
      this.progressDialog.updateProgress(100);

      Main.saveDatabase = saveDatabase;
      Main.modDatabase = modDatabase;
      Main.database = database;
      this.loadSuccessful = true;
    } catch (DBException exc) {
      Main.logException("Save file structure is not valid", exc);
    } catch (IOException exc) {
      Main.logException("Unable to read save file", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while opening save file", exc);
    }

    try
    {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }

    }
    catch (IOException exc)
    {
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        LoadFile.this.progressDialog.closeDialog(LoadFile.this.loadSuccessful);
      }
    });
  }
}

