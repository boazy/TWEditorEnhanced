package app.tweditor;

import javax.swing.*;
import java.io.IOException;

public class SaveFile extends Thread
{
  private final ProgressDialog progressDialog;
  private boolean saveSuccessful = false;

  public SaveFile(ProgressDialog dialog)
  {
    this.progressDialog = dialog;
  }

  public void run()
  {
    try
    {
      Main.database.save();
      this.progressDialog.updateProgress(15);

      ResourceEntry resourceEntry = new ResourceEntry("module.ifo", Main.databaseFile);
      Main.modDatabase.addEntry(resourceEntry);
      Main.modDatabase.save();
      this.progressDialog.updateProgress(30);

      ResourceDatabase modDatabase = new ResourceDatabase(Main.modDatabase.getPath());
      modDatabase.load();
      Main.modDatabase = modDatabase;
      this.progressDialog.updateProgress(45);

      Main.saveDatabase.addEntry(Main.modName, Main.modFile);
      this.progressDialog.updateProgress(60);

      Main.playerDatabase.save();
      Main.saveDatabase.addEntry(Main.playerName, Main.playerFile);
      this.progressDialog.updateProgress(70);

      Main.smmDatabase.save();
      Main.saveDatabase.addEntry(Main.smmName, Main.smmFile);
      this.progressDialog.updateProgress(80);

      Main.saveDatabase.save();
      this.progressDialog.updateProgress(90);

      SaveDatabase saveDatabase = new SaveDatabase(Main.saveDatabase.getPath());
      saveDatabase.load();
      Main.saveDatabase = saveDatabase;

      this.progressDialog.updateProgress(100);

      this.saveSuccessful = true;
    } catch (DBException exc) {
      Main.logException("Unable to update save database", exc);
    } catch (IOException exc) {
      Main.logException("Unable to save file", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while saving file", exc);
    }

    SwingUtilities.invokeLater(() ->
            SaveFile.this.progressDialog.closeDialog(SaveFile.this.saveSuccessful));
  }
}

