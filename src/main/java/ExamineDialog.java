package TWEditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class ExamineDialog extends JDialog
  implements ActionListener
{
  private JScrollPane scrollPane;
  private JTextPane textPane;

  public ExamineDialog(JFrame parent, String label, String description)
  {
    super(parent, label, true);
    setDefaultCloseOperation(2);

    StringBuilder stringBuilder = new StringBuilder(description);
    stringBuilder.insert(0, "<html>");
    stringBuilder.append("</html>");

    int start = 6;
    while (true) {
      start = stringBuilder.indexOf("<", start);
      if (start < 0) {
        break;
      }
      int stop = stringBuilder.indexOf(">", start);
      if (stop < 0) {
        break;
      }
      String control = stringBuilder.substring(start + 1, stop).toLowerCase();
      if (control.equals("/html")) {
        break;
      }
      String html = null;
      String strref = null;
      if (control.equals("cbold"))
        html = "b";
      else if (control.equals("citalic"))
        html = "i";
      else if ((control.length() >= 7) && (control.substring(0, 7).equals("strref:"))) {
        try {
          int refid = Integer.parseInt(control.substring(7));
          strref = Main.stringsDatabase.getString(refid);
        } catch (NumberFormatException exc) {
          strref = "";
        }
      }

      if (html != null) {
        stringBuilder.replace(start + 1, stop, html);
        start = stringBuilder.indexOf("</c>", stop);
        if (start < 0) {
          stringBuilder.append(new StringBuilder().append("</").append(html).append(">").toString());
          break;
        }

        stringBuilder.replace(start + 2, start + 3, html);
        start += 4;
      } else if (strref != null) {
        stringBuilder.replace(start, stop + 1, strref);
      } else {
        stringBuilder.delete(start, stop + 1);
      }

    }

    int sep = 0;
    while ((sep = stringBuilder.indexOf("\n", sep)) >= 0) {
      stringBuilder.replace(sep, sep + 1, "<br>");
    }

    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BoxLayout(contentPane, 1));
    contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    this.textPane = new JTextPane();
    this.textPane.setContentType("text/html");
    this.textPane.setText(stringBuilder.toString());
    this.textPane.setCaretPosition(0);

    this.scrollPane = new JScrollPane(this.textPane);
    this.scrollPane.setPreferredSize(new Dimension(400, 500));

    JPanel buttonPane = new JPanel();
    JButton button = new JButton("OK");
    button.addActionListener(this);
    button.setActionCommand("ok");
    buttonPane.add(button);

    contentPane.add(this.scrollPane);
    contentPane.add(Box.createVerticalStrut(10));
    contentPane.add(buttonPane);
    setContentPane(contentPane);
  }

  public void actionPerformed(ActionEvent ae)
  {
    try
    {
      String action = ae.getActionCommand();
      if (action.equals("ok")) {
        setVisible(false);
        dispose();
      }
    } catch (Throwable exc) {
      Main.logException("Exception while processing action event", exc);
    }
  }

  public static void showDialog(JFrame parent, String label, String description)
  {
    ExamineDialog dialog = new ExamineDialog(parent, label, description);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }
}

