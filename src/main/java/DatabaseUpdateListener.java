package TWEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DatabaseUpdateListener
  implements ActionListener, DocumentListener
{
  public void actionPerformed(ActionEvent ae)
  {
    if ((Main.database != null) && (!Main.dataChanging)) {
      Main.dataModified = true;
      Main.mainWindow.setTitle(null);
    }
  }

  public void changedUpdate(DocumentEvent de)
  {
    if ((Main.database != null) && (!Main.dataChanging)) {
      Main.dataModified = true;
      Main.mainWindow.setTitle(null);
    }
  }

  public void insertUpdate(DocumentEvent de)
  {
    if ((Main.database != null) && (!Main.dataChanging)) {
      Main.dataModified = true;
      Main.mainWindow.setTitle(null);
    }
  }

  public void removeUpdate(DocumentEvent de)
  {
    if ((Main.database != null) && (!Main.dataChanging)) {
      Main.dataModified = true;
      Main.mainWindow.setTitle(null);
    }
  }
}

