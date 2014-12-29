package TWEditor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SignsPanel extends JPanel
  implements ActionListener
{
  private static final String[] tabNames = { "Aard", "Igni", "Quen", "Axii", "Yrden" };

  private static final String[][][] fieldNames = { { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Stun", "Disarm", "Blasting Fist", "Extended Duration", "Gale" }, { "", "Gust", "Thunder", "Added Efficiency", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Harm's Way I", "Harm's Way II", "Burning Blade", "Inferno", "Extended Duration" }, { "", "Incineration", "Wall of Fire", "Added Efficiency", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Barrier I", "Barrier II", "Barrier III", "Survival Zone", "Resonance" }, { "", "Extended Duration", "Added Intensity", "Added Efficiency", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Spell", "Hypnosis", "Faze", "Terror", "Ally" }, { "", "Extended Duration I", "Extended Duration II", "Added Efficiency", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Pain Sign", "Prowess", "Stupor Sign", "Blinding Sign", "Circle of Death" }, { "", "Inscriptions", "Crippling Sign", "Added Efficiency", "" } } };

  private static final String[][][] databaseLabels = { { { "Aard1", "Aard2", "Aard3", "Aard4", "Aard5" }, { "Aard1 Powerup", "Aard2 Powerup", "Aard3 Powerup", "Aard4 Powerup", "Aard5 Powerup" }, { "Aard1 Upgrade1", "Aard2 Upgrade1", "Aard3 Upgrade1", "Aard4 Upgrade1", "Aard5 Upgrade1" }, { "", "Aard2 Upgrade2", "Aard3 Upgrade2", "Aard4 Upgrade2", "" } }, { { "Igni1", "Igni2", "Igni3", "Igni4", "Igni5" }, { "Igni1 Powerup", "Igni2 Powerup", "Igni3 Powerup", "Igni4 Powerup", "Igni5 Powerup" }, { "Igni1 Upgrade1", "Igni2 Upgrade1", "Igni3 Upgrade1", "Igni4 Upgrade1", "Igni5 Upgrade1" }, { "", "Igni2 Upgrade2", "Igni3 Upgrade2", "Igni4 Upgrade2", "" } }, { { "Quen1", "Quen2", "Quen3", "Quen4", "Quen5" }, { "Quen1 Powerup", "Quen2 Powerup", "Quen3 Powerup", "Quen4 Powerup", "Quen5 Powerup" }, { "Quen1 Upgrade1", "Quen2 Upgrade1", "Quen3 Upgrade1", "Quen4 Upgrade1", "Quen5 Upgrade1" }, { "", "Quen2 Upgrade2", "Quen3 Upgrade2", "Quen4 Upgrade2", "" } }, { { "Axi1", "Axi2", "Axi3", "Axi4", "Axi5" }, { "Axi1 Powerup", "Axi2 Powerup", "Axi3 Powerup", "Axi4 Powerup", "Axi5 Powerup" }, { "Axi1 Upgrade1", "Axi2 Upgrade1", "Axi3 Upgrade1", "Axi4 Upgrade1", "Axi5 Upgrade1" }, { "", "Axi2 Upgrade2", "Axi3 Upgrade2", "Axi4 Upgrade2", "" } }, { { "Yrden1", "Yrden2", "Yrden3", "Yrden4", "Yrden5" }, { "Yrden1 Powerup", "Yrden2 Powerup", "Yrden3 Powerup", "Yrden4 Powerup", "Yrden5 Powerup" }, { "Yrden1 Upgrade1", "Yrden2 Upgrade1", "Yrden3 Upgrade1", "Yrden4 Upgrade1", "Yrden5 Upgrade1" }, { "", "Yrden2 Upgrade2", "Yrden3 Upgrade2", "Yrden4 Upgrade2", "" } } };

  private static final int[] associatedSpells = { 0, 3, 1, 4, 2 };
  private int[][] signLevels;
  private Map<String, JCheckBox> labelMap;
  private JCheckBox[][][] fields;
  private JTabbedPane tabbedPane;

  public SignsPanel()
  {
    this.tabbedPane = new JTabbedPane();
    int tabs = fieldNames.length;
    int rows = fieldNames[0].length;
    int cols = fieldNames[0][0].length;
    this.fields = new JCheckBox[tabs][rows][cols];
    this.signLevels = new int[tabs][2];
    this.labelMap = new HashMap(tabs * rows * cols);
    for (int tab = 0; tab < tabs; tab++) {
      JPanel panel = new JPanel(new GridLayout(0, cols, 5, 5));
      for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
          if (fieldNames[tab][row][col].length() > 0) {
            JCheckBox field = new JCheckBox(fieldNames[tab][row][col]);
            field.setActionCommand(Integer.toString(tab * 100 + row * 10 + col));
            field.addActionListener(this);
            this.fields[tab][row][col] = field;
            panel.add(field);
            this.labelMap.put(databaseLabels[tab][row][col], field);
          } else {
            panel.add(new JLabel());
          }
        }
      }

      this.tabbedPane.addTab(tabNames[tab], panel);
    }

    add(this.tabbedPane);
  }

  public void actionPerformed(ActionEvent ae)
  {
    if ((!(ae.getSource() instanceof JCheckBox)) || (Main.dataChanging)) {
      return;
    }

    try
    {
      int value = Integer.parseInt(ae.getActionCommand());
      int tab = value / 100;
      int row = value % 100 / 10;
      int col = value % 10;
      JCheckBox field = this.fields[tab][row][col];
      String abilityLabel = databaseLabels[tab][row][col];

      DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
      list = (DBList)list.getElement("Mod_PlayerList").getValue();
      DBList playerList = (DBList)list.getElement(0).getValue();
      list = (DBList)playerList.getElement("CharAbilities").getValue();

      if (field.isSelected()) {
        boolean addSign = true;

        if (row == 0) {
          if (col > this.signLevels[tab][0] + 1) {
            JOptionPane.showMessageDialog(this, "Lower sign level must be obtained first", "Missing level", 0);

            addSign = false;
          }
        } else if (row == 1) {
          if (col > this.signLevels[tab][1] + 1) {
            JOptionPane.showMessageDialog(this, "Lower sign level powerup must be obtained first", "Missing powerup", 0);

            addSign = false;
          }
        }
        else if (col > this.signLevels[tab][0]) {
          JOptionPane.showMessageDialog(this, "The sign level must be obtained first", "Missing level", 0);

          addSign = false;
        }

        if (addSign) {
          DBList fieldList = new DBList(2);
          fieldList.addElement(new DBElement(10, 0, "RnAbName", abilityLabel));
          fieldList.addElement(new DBElement(0, 0, "RnAbStk", new Integer(0)));
          list.addElement(new DBElement(14, 48879, "", fieldList));

          if ((row < 2) && (col > this.signLevels[tab][row])) {
            boolean updatedSpell = false;
            int low = associatedSpells[tab] * 10;
            int high = associatedSpells[tab] * 10 + 9;
            list = null;
            DBElement element = playerList.getElement("KnownList0");
            if (element != null) {
              list = (DBList)element.getValue();
              int count = list.getElementCount();
              for (int i = 0; i < count; i++) {
                fieldList = (DBList)list.getElement(i).getValue();
                int spell = fieldList.getInteger("Spell");
                if ((spell >= low) && (spell <= high) && ((spell & 0x1) == row)) {
                  fieldList.setInteger("Spell", low + 2 * col + row);
                  updatedSpell = true;
                  break;
                }
              }
            }

            if (!updatedSpell) {
              if (list == null) {
                list = new DBList(1);
                playerList.addElement(new DBElement(15, 0, "KnownList0", list));
              }

              fieldList = new DBList(1);
              element = new DBElement(2, 0, "Spell", new Integer(low + 2 * col + row));
              fieldList.addElement(element);
              list.addElement(new DBElement(14, 2, "", fieldList));
            }

            this.signLevels[tab][row] = col;
          }

          Main.dataModified = true;
        } else {
          Main.dataChanging = true;
          field.setSelected(false);
          Main.dataChanging = false;
        }
      }
      else
      {
        boolean removeSign = true;

        if (row == 0) {
          if (col < this.signLevels[tab][0]) {
            JOptionPane.showMessageDialog(this, "All higher sign levels must be removed first", "Higher level", 0);

            removeSign = false;
          } else {
            for (int i = 1; i < this.fields[0].length; i++) {
              JCheckBox checkField = this.fields[tab][i][col];
              if ((checkField != null) && (checkField.isSelected())) {
                JOptionPane.showMessageDialog(this, "All sign level upgrades must be removed first", "Sign upgrades", 0);

                removeSign = false;
                break;
              }
            }
          }
        } else if ((row == 1) && 
          (col < this.signLevels[tab][1])) {
          JOptionPane.showMessageDialog(this, "All higher sign powerups must be removed first", "Higher powerup", 0);

          removeSign = false;
        }

        if (removeSign) {
          int count = list.getElementCount();
          for (int i = 0; i < count; i++) {
            DBList fieldList = (DBList)list.getElement(i).getValue();
            String name = fieldList.getString("RnAbName");
            if (abilityLabel.equals(name)) {
              list.removeElement(i);
              Main.dataModified = true;
              break;
            }

          }

          if ((row < 2) && (col == this.signLevels[tab][row])) {
            int low = associatedSpells[tab] * 10;
            int high = associatedSpells[tab] * 10 + 9;
            DBElement element = playerList.getElement("KnownList0");
            if (element != null) {
              list = (DBList)element.getValue();
              count = list.getElementCount();
              for (int i = 0; i < count; i++) {
                DBList fieldList = (DBList)list.getElement(i).getValue();
                int spell = fieldList.getInteger("Spell");
                if ((spell >= low) && (spell <= high) && ((spell & 0x1) == row)) {
                  if (col == 0) {
                    list.removeElement(i); break;
                  }
                  fieldList.setInteger("Spell", spell - 2);

                  break;
                }
              }
            }

            this.signLevels[tab][row] = (col - 1);
            Main.dataModified = true;
          }
        } else {
          Main.dataChanging = true;
          field.setSelected(true);
          Main.dataChanging = false;
        }
      }
    } catch (DBException exc) {
      Main.logException("Unable to update database field", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while processing action event", exc);
    }
  }

  public void setFields(DBList list)
    throws DBException
  {
    for (int tab = 0; tab < this.fields.length; tab++) {
      for (int row = 0; row < this.fields[0].length; row++) {
        for (int col = 0; col < this.fields[0][0].length; col++) {
          if (this.fields[tab][row][col] != null) {
            this.fields[tab][row][col].setSelected(false);
          }
        }
      }

      this.signLevels[tab][0] = -1;
      this.signLevels[tab][1] = -1;
    }

    DBElement element = list.getElement("CharAbilities");
    if (element == null) {
      throw new DBException("CharAbilities field not found");
    }
    DBList abilityList = (DBList)element.getValue();
    int count = abilityList.getElementCount();
    for (int index = 0; index < count; index++) {
      DBList fieldList = (DBList)abilityList.getElement(index).getValue();
      String abilityName = fieldList.getString("RnAbName");
      JCheckBox field = (JCheckBox)this.labelMap.get(abilityName);
      if (field != null) {
        field.setSelected(true);
        int value = Integer.parseInt(field.getActionCommand());
        int tab = value / 100;
        int row = value % 100 / 10;
        int col = value % 10;
        if ((row < 2) && (col > this.signLevels[tab][row]))
          this.signLevels[tab][row] = col;
      }
    }
  }

  public void getFields(DBList list)
    throws DBException
  {
  }
}

