package TWEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.SwingUtilities;

public class SaveFile extends Thread
{
  private ProgressDialog progressDialog;
  private File extractDirectory;
  private boolean saveSuccessful = false;

  public SaveFile(ProgressDialog dialog)
  {
    this.progressDialog = dialog;
  }

  public void run()
  {
    FileInputStream in = null;
    OutputStream out = null;
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

      SaveEntry saveEntry = new SaveEntry(Main.savePrefix + Main.modName);
      in = new FileInputStream(Main.modFile);
      out = saveEntry.getOutputStream();
      byte[] buffer = new byte[4096];
      int count;
      while ((count = in.read(buffer)) > 0) {
        out.write(buffer, 0, count);
      }
      in.close();
      in = null;
      out.close();
      out = null;
      Main.saveDatabase.addEntry(saveEntry);
      this.progressDialog.updateProgress(60);

      Main.saveDatabase.save();
      this.progressDialog.updateProgress(80);

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
        SaveFile.this.progressDialog.closeDialog(SaveFile.this.saveSuccessful);
      }
    });
  }
}

