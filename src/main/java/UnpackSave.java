package TWEditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.swing.SwingUtilities;

public class UnpackSave extends Thread
{
  private ProgressDialog progressDialog;
  private File dirFile;
  private boolean unpackSuccessful = false;

  public UnpackSave(ProgressDialog dialog, File dirFile)
  {
    this.progressDialog = dialog;
    this.dirFile = dirFile;
  }

  public void run()
  {
    File file = null;
    InputStream in = null;
    FileOutputStream out = null;
    try {
      List entries = Main.saveDatabase.getEntries();
      byte[] buffer = new byte[4096];
      int total = entries.size();
      int processed = 0;
      int currentProgress = 0;
      for (Object entryObj : entries) {
        SaveEntry entry = (SaveEntry)entryObj;
        String resourceName = entry.getResourceName();
        file = new File(this.dirFile.getPath() + Main.fileSeparator + resourceName);
        if ((file.exists()) && 
          (!file.delete())) {
          throw new IOException("Unable to delete '" + file.getName() + "'");
        }
        out = new FileOutputStream(file);
        in = entry.getInputStream();
        int count;
        while ((count = in.read(buffer)) > 0) {
          out.write(buffer, 0, count);
        }
        out.close();
        out = null;
        in.close();
        in = null;
        processed++;
        int newProgress = processed * 100 / total;
        if (newProgress > currentProgress + 9) {
          currentProgress = newProgress;
          this.progressDialog.updateProgress(currentProgress);
        }
      }

      this.unpackSuccessful = true;
    } catch (IOException exc) {
      Main.logException("I/O error while unpacking save", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while unpacking save", exc);
    }

    try
    {
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
        if (file.exists()) {
          file.delete();
        }
      }

    }
    catch (IOException exc)
    {
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        UnpackSave.this.progressDialog.closeDialog(UnpackSave.this.unpackSuccessful);
      }
    });
  }
}

