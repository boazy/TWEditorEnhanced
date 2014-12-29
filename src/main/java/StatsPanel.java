package TWEditor;

import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.Document;

public class StatsPanel extends JPanel
{
  private static final String[][] fieldNames = { { "Level", "Vitality", "Bronze Talents" }, { "Experience", "Endurance", "Silver Talents" }, { "Gold", "Toxicity", "Gold Talents" } };

  private static final String[][] databaseNames = { { "ExpLevel", "CurrentHitPoints", "TalentBronze" }, { "Experience", "CurrentEndurance", "TalentSilver" }, { "Gold", "CurrentToxicity", "TalentGold" } };
  private NumericField[][] statFields;

  public StatsPanel()
  {
    super(new GridLayout(0, 3, 40, 0));
    DatabaseUpdateListener listener = new DatabaseUpdateListener();
    this.statFields = new NumericField[fieldNames.length][3];

    add(Box.createVerticalStrut(5));
    add(Box.createVerticalStrut(5));
    add(Box.createVerticalStrut(5));

    for (int i = 0; i < fieldNames.length; i++) {
      for (int j = 0; j < 3; j++) {
        if (fieldNames[i][j].length() > 0)
          add(new JLabel(fieldNames[i][j]));
        else {
          add(new JLabel());
        }
      }
      for (int j = 0; j < 3; j++) {
        if (fieldNames[i][j].length() > 0) {
          NumericField field = new NumericField(5);
          field.getDocument().addDocumentListener(listener);
          add(field);
          this.statFields[i][j] = field;
        }
      }

      add(Box.createVerticalStrut(5));
      add(Box.createVerticalStrut(5));
      add(Box.createVerticalStrut(5));
    }
  }

  public void setFields(DBList list)
    throws DBException
  {
    for (int i = 0; i < databaseNames.length; i++)
      for (int j = 0; j < 3; j++)
        if (this.statFields[i][j] != null)
          this.statFields[i][j].setValue(list.getInteger(databaseNames[i][j]));
  }

  public void getFields(DBList list)
    throws DBException
  {
    for (int i = 0; i < databaseNames.length; i++)
      for (int j = 0; j < 3; j++)
        if (this.statFields[i][j] != null)
          list.setInteger(databaseNames[i][j], this.statFields[i][j].getValue());
  }
}

