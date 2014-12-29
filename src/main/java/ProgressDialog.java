package TWEditor;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressDialog extends JDialog
{
  private JFrame parent;
  private JProgressBar progressBar;
  private int deferredProgress;
  private boolean success = false;

  public ProgressDialog(JFrame parent, String message)
  {
    super(parent, "The Witcher Save Editor", true);
    this.parent = parent;

    JPanel progressPane = new JPanel();
    progressPane.setLayout(new BoxLayout(progressPane, 1));
    progressPane.add(Box.createVerticalStrut(15));
    progressPane.add(new JLabel("<html><b>" + message + "</b></html>"));
    progressPane.add(Box.createVerticalStrut(15));
    this.progressBar = new JProgressBar(0, 100);
    this.progressBar.setStringPainted(true);
    progressPane.add(this.progressBar);
    progressPane.add(Box.createVerticalStrut(15));

    JPanel contentPane = new JPanel();
    contentPane.add(progressPane);
    setContentPane(contentPane);
  }

  public boolean showDialog()
  {
    pack();
    setLocationRelativeTo(this.parent);
    setVisible(true);
    return this.success;
  }

  public void closeDialog(boolean success)
  {
    this.success = success;
    setVisible(false);
    dispose();
  }

  public void updateProgress(int progress)
  {
    if (SwingUtilities.isEventDispatchThread()) {
      this.progressBar.setValue(progress);
    } else {
      this.deferredProgress = progress;
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          ProgressDialog.this.progressBar.setValue(ProgressDialog.this.deferredProgress);
        }
      });
    }
  }
}

