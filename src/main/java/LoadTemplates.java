package TWEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.SwingUtilities;

public class LoadTemplates extends Thread
{
  private ProgressDialog progressDialog;
  private boolean success = false;

  public LoadTemplates(ProgressDialog dialog)
  {
    this.progressDialog = dialog;
  }

  public void run()
  {
    try
    {
      Set mapSet = Main.resourceFiles.entrySet();
      int entryCount = mapSet.size();
      Main.itemTemplates = new ArrayList(entryCount);
      int processedCount = 0;
      int currentProgress = 0;

      for (Object mapEntryObj : mapSet) {
        Map.Entry mapEntry = (Map.Entry)mapEntryObj;
        String resourceName = null;
        InputStream in = null;
        Object entryObject = mapEntry.getValue();
        if ((entryObject instanceof File)) {
          File file = (File)entryObject;
          String name = file.getName().toLowerCase();
          int sep = name.lastIndexOf('.');
          if ((sep > 0) && (name.substring(sep).equals(".uti"))) {
            resourceName = name.substring(0, sep);
            in = new FileInputStream(file);
          }
        } else if ((entryObject instanceof KeyEntry)) {
          KeyEntry keyEntry = (KeyEntry)entryObject;
          String name = keyEntry.getFileName().toLowerCase();
          int sep = name.lastIndexOf('.');
          if ((sep > 0) && (name.substring(sep).equals(".uti"))) {
            resourceName = keyEntry.getResourceName();
            in = keyEntry.getInputStream();
          }

        }

        if (in != null) {
          Database database = new Database();
          database.load(in);
          in.close();
          DBList fieldList = (DBList)database.getTopLevelStruct().getValue();
          String itemName = fieldList.getString("LocalizedName");
          String itemDescription = fieldList.getString("Description");
          if ((itemName.length() > 0) && (itemDescription.length() > 0)) {
            DBElement resourceElement = new DBElement(11, 0, "TemplateResRef", resourceName);
            fieldList.setElement("TemplateResRef", resourceElement);
            ItemTemplate itemTemplate = new ItemTemplate(fieldList);
            Main.itemTemplates.add(itemTemplate);
          }
        }

        processedCount++;
        int newProgress = processedCount * 100 / entryCount;
        if (newProgress > currentProgress + 9) {
          currentProgress = newProgress;
          this.progressDialog.updateProgress(currentProgress);
        }
      }

      this.success = true;
    } catch (DBException exc) {
      Main.logException("Database error while loading inventory templates", exc);
    } catch (IOException exc) {
      Main.logException("I/O error while loading inventory templates", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while loading inventory templates", exc);
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        LoadTemplates.this.progressDialog.closeDialog(LoadTemplates.this.success);
      }
    });
  }
}

