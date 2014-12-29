package TWEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.swing.SwingUtilities;

public class PackFile extends Thread
{
  private ProgressDialog progressDialog;
  private File extractDirectory;
  private boolean saveSuccessful = false;

  public PackFile(ProgressDialog dialog, File dirFile)
  {
    this.progressDialog = dialog;
    this.extractDirectory = dirFile;
  }

  public void run()
  {
    FileInputStream in = null;
    OutputStream out = null;
    List entries = Main.saveDatabase.getEntries();
    try
    {
      for (Object entryObj : entries) {
        SaveEntry entry = (SaveEntry)entryObj;
        File file = new File(this.extractDirectory.getPath() + Main.fileSeparator + entry.getResourceName());
        if ((!file.exists()) || (!file.isFile())) {
          throw new IOException("Resource '" + file.getPath() + "' not found");
        }
        if (entry.isCompressed()) {
          entry.setOnDisk(false);
          in = new FileInputStream(file);
          out = entry.getOutputStream();
          byte[] buffer = new byte[4096];
          int count;
          while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
          }
          in.close();
          in = null;
          out.close();
          out = null;
        } else {
          entry.setResourceFile(file, 0, (int)file.length());
        }

      }

      Main.saveDatabase.save();

      this.saveSuccessful = true;
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
        PackFile.this.progressDialog.closeDialog(PackFile.this.saveSuccessful);
      }
    });
  }
}

