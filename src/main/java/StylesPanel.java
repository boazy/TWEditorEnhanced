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

public class StylesPanel extends JPanel
  implements ActionListener
{
  private static final String[] tabNames = { "Strong Steel", "Fast Steel", "Group Steel", "Strong Silver", "Fast Silver", "Group Silver" };

  private static final String[][][] fieldNames = { { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Cut at the Jugular I", "Cut at the Jugular II", "Cut at the Jugular III", "", "" }, { "Crushing Blow I", "Crushing Blow II", "Crushing Blow III", "", "" }, { "Bloody Rage I", "Bloody Rage II", "Bloody Rage III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Paralysis I", "Paralysis II", "Paralysis III", "", "" }, { "Hail of Blows I", "Hail of Blows II", "Hail of Blows III", "", "" }, { "Sever Sinews I", "Sever Sinews II", "Sever Sinews III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Precise Hit I", "Precise Hit II", "Precise Hit III", "", "" }, { "Half-Spin I", "Half-Spin II", "Half-Spin III", "", "" }, { "Trip I", "Trip II", "Trip III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Deep Cut I", "Deep Cut II", "Deep Cut III", "", "" }, { "Mortal Blow I", "Mortal Blow II", "Mortal Blow III", "", "" }, { "Patinado I", "Patinado II", "Patinado III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Crippling Pain I", "Crippling Pain II", "Crippling Pain III", "", "" }, { "Flash Cuts I", "Flash Cuts II", "Flash Cuts III", "", "" }, { "Sinister I", "Sinister II", "Sinister III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Critical Hit I", "Critical Hit II", "Critical Hit III", "", "" }, { "Tempest I", "Tempest II", "Tempest III", "", "" }, { "Tempest I", "Tempest II", "Tempest III", "", "" } } };

  private static final String[][][] databaseLabels = { { { "StyleSteelStrong1", "StyleSteelStrong2", "StyleSteelStrong3", "StyleSteelStrong4", "StyleSteelStrong5" }, { "StyleSteelStrong1 Upgrade1", "StyleSteelStrong2 Upgrade1", "StyleSteelStrong3 Upgrade1", "", "" }, { "StyleSteelStrong1 Upgrade2", "StyleSteelStrong2 Upgrade2", "StyleSteelStrong3 Upgrade2", "", "" }, { "StyleSteelStrong1 Upgrade3", "StyleSteelStrong2 Upgrade3", "StyleSteelStrong3 Upgrade3", "", "" } }, { { "StyleSteelFast1", "StyleSteelFast2", "StyleSteelFast3", "StyleSteelFast4", "StyleSteelFast5" }, { "StyleSteelFast1 Upgrade1", "StyleSteelFast2 Upgrade1", "StyleSteelFast3 Upgrade1", "", "" }, { "StyleSteelFast1 Upgrade2", "StyleSteelFast2 Upgrade2", "StyleSteelFast3 Upgrade2", "", "" }, { "StyleSteelFast1 Upgrade3", "StyleSteelFast2 Upgrade3", "StyleSteelFast3 Upgrade3", "", "" } }, { { "StyleSteelGroup1", "StyleSteelGroup2", "StyleSteelGroup3", "StyleSteelGroup4", "StyleSteelGroup5" }, { "StyleSteelGroup1 Upgrade1", "StyleSteelGroup2 Upgrade1", "StyleSteelGroup3 Upgrade1", "", "" }, { "StyleSteelGroup1 Upgrade2", "StyleSteelGroup2 Upgrade2", "StyleSteelGroup3 Upgrade2", "", "" }, { "StyleSteelGroup1 Upgrade3", "StyleSteelGroup2 Upgrade3", "StyleSteelGroup3 Upgrade3", "", "" } }, { { "StyleSilverStrong1", "StyleSilverStrong2", "StyleSilverStrong3", "StyleSilverStrong4", "StyleSilverStrong5" }, { "StyleSilverStrong1 Upgrade1", "StyleSilverStrong2 Upgrade1", "StyleSilverStrong3 Upgrade1", "", "" }, { "StyleSilverStrong1 Upgrade2", "StyleSilverStrong2 Upgrade2", "StyleSilverStrong3 Upgrade2", "", "" }, { "StyleSilverStrong1 Upgrade3", "StyleSilverStrong2 Upgrade3", "StyleSilverStrong3 Upgrade3", "", "" } }, { { "StyleSilverFast1", "StyleSilverFast2", "StyleSilverFast3", "StyleSilverFast4", "StyleSilverFast5" }, { "StyleSilverFast1 Upgrade1", "StyleSilverFast2 Upgrade1", "StyleSilverFast3 Upgrade1", "", "" }, { "StyleSilverFast1 Upgrade2", "StyleSilverFast2 Upgrade2", "StyleSilverFast3 Upgrade2", "", "" }, { "StyleSilverFast1 Upgrade3", "StyleSilverFast2 Upgrade3", "StyleSilverFast3 Upgrade3", "", "" } }, { { "StyleSilverGroup1", "StyleSilverGroup2", "StyleSilverGroup3", "StyleSilverGroup4", "StyleSilverGroup5" }, { "StyleSilverGroup1 Upgrade1", "StyleSilverGroup2 Upgrade1", "StyleSilverGroup3 Upgrade1", "", "" }, { "StyleSilverGroup1 Upgrade2", "StyleSilverGroup2 Upgrade2", "StyleSilverGroup3 Upgrade2", "", "" }, { "StyleSilverGroup1 Upgrade3", "StyleSilverGroup2 Upgrade3", "StyleSilverGroup3 Upgrade3", "", "" } } };
  private int[] levels;
  private Map<String, JCheckBox> labelMap;
  private JCheckBox[][][] fields;
  private JTabbedPane tabbedPane;

  public StylesPanel()
  {
    this.tabbedPane = new JTabbedPane();
    int tabs = fieldNames.length;
    int rows = fieldNames[0].length;
    int cols = fieldNames[0][0].length;
    this.fields = new JCheckBox[tabs][rows][cols];
    this.levels = new int[tabs];
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
        boolean addAbility = true;

        if (row == 0) {
          if (col > this.levels[tab] + 1) {
            JOptionPane.showMessageDialog(this, "Lower ability level must be obtained first", "Missing level", 0);

            addAbility = false;
          }
        }
        else if (col > this.levels[tab]) {
          JOptionPane.showMessageDialog(this, "The ability level must be obtained first", "Missing level", 0);

          addAbility = false;
        }

        if (addAbility) {
          DBList fieldList = new DBList(2);
          fieldList.addElement(new DBElement(10, 0, "RnAbName", abilityLabel));
          fieldList.addElement(new DBElement(0, 0, "RnAbStk", new Integer(0)));
          list.addElement(new DBElement(14, 48879, "", fieldList));

          if ((row == 0) && (col > this.levels[tab])) {
            this.levels[tab] = col;
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
        boolean removeAbility = true;

        if (row == 0) {
          if (col < this.levels[tab]) {
            JOptionPane.showMessageDialog(this, "All higher ability levels must be removed first", "Higher level", 0);

            removeAbility = false;
          } else {
            for (int i = 1; i < this.fields[0].length; i++) {
              JCheckBox checkField = this.fields[tab][i][col];
              if ((checkField != null) && (checkField.isSelected())) {
                JOptionPane.showMessageDialog(this, "All ability level upgrades must be removed first", "Ability upgrades", 0);

                removeAbility = false;
                break;
              }

            }

          }

        }

        if (removeAbility) {
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

          if ((row == 0) && (col == this.levels[tab]))
            this.levels[tab] = (col - 1);
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

      this.levels[tab] = -1;
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
        if ((row == 0) && (col > this.levels[tab]))
          this.levels[tab] = col;
      }
    }
  }

  public void getFields(DBList list)
    throws DBException
  {
  }
}

