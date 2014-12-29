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

public class AttributesPanel extends JPanel
  implements ActionListener
{
  private static final String[] tabNames = { "Strength", "Dexterity", "Stamina", "Intelligence" };

  private static final String[][][] fieldNames = { { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Buzz", "Position", "Vigor", "Bleeding Resistance", "Wound Resistance" }, { "True Grit", "Regeneration", "Knockdown Resistance", "Stone Skin", "Added Vitality" }, { "", "Brawl", "Survival Instinct", "Aggression", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Flaying", "Deflect Arrows", "Bleeding Resistance", "Finesse", "Vigilance" }, { "Predator", "Repel", "Agility", "Feint", "Precision" }, { "", "Fistfight", "Limit Incineration", "Incineration Resistance", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Heavyweight", "Absorption", "Endurance Regeneration", "Stun Resistance", "Potion Tolerance" }, { "Mutation", "Poison Resistance", "Pain Resistance", "Brawn", "Added Endurance" }, { "", "Endurance Regeneration", "Revive", "Altered Metabolism", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Potion Brewing", "Herbalism", "Cleansing Ritual", "Focus", "Mental Endurance" }, { "Rising Moon", "Monster Lore", "Ingredient Extraction", "Life Ritual", "Intensity" }, { "", "Oil Preparation", "Bomb Preparation", "Magic Frenzy", "" } } };

  private static final String[][][] databaseLabels = { { { "Strength1", "Strength2", "Strength3", "Strength4", "Strength5" }, { "Strength1 Upgrade1", "Strength2 Upgrade1", "Strength3 Upgrade1", "Strength4 Upgrade1", "Strength5 Upgrade1" }, { "Strength1 Upgrade2", "Strength2 Upgrade2", "Strength3 Upgrade2", "Strength4 Upgrade2", "Strength5 Upgrade2" }, { "", "Strength2 Upgrade3", "Strength3 Upgrade3", "Strength4 Upgrade3", "" } }, { { "Dexterity1", "Dexterity2", "Dexterity3", "Dexterity4", "Dexterity5" }, { "Dexterity1 Upgrade1", "Dexterity2 Upgrade1", "Dexterity3 Upgrade1", "Dexterity4 Upgrade1", "Dexterity5 Upgrade1" }, { "Dexterity1 Upgrade2", "Dexterity2 Upgrade2", "Dexterity3 Upgrade2", "Dexterity4 Upgrade2", "Dexterity5 Upgrade2" }, { "", "Dexterity2 Upgrade3", "Dexterity3 Upgrade3", "Dexterity4 Upgrade3", "" } }, { { "Endurance1", "Endurance2", "Endurance3", "Endurance4", "Endurance5" }, { "Endurance1 Upgrade1", "Endurance2 Upgrade1", "Endurance3 Upgrade1", "Endurance4 Upgrade1", "Endurance5 Upgrade1" }, { "Endurance1 Upgrade2", "Endurance2 Upgrade2", "Endurance3 Upgrade2", "Endurance4 Upgrade2", "Endurance5 Upgrade2" }, { "", "Endurance2 Upgrade3", "Endurance3 Upgrade3", "Endurance4 Upgrade3", "" } }, { { "Intelligence1", "Intelligence2", "Intelligence3", "Intelligence4", "Intelligence5" }, { "Intelligence1 Upgrade1", "Intelligence2 Upgrade1", "Intelligence3 Upgrade1", "Intelligence4 Upgrade1", "Intelligence5 Upgrade1" }, { "Intelligence1 Upgrade2", "Intelligence2 Upgrade2", "Intelligence3 Upgrade2", "Intelligence4 Upgrade2", "Intelligence5 Upgrade2" }, { "", "Intelligence2 Upgrade3", "Intelligence3 Upgrade3", "Intelligence4 Upgrade3", "" } } };

  private static final String[][] associatedLabels = { { "Dexterity1 Upgrade1", "Skinning" }, { "Intelligence2 Upgrade1", "HerbGathering" }, { "Intelligence2 Upgrade3", "GreaseMaking" }, { "Intelligence3 Upgrade1", "RitualOfPurify" }, { "Intelligence3 Upgrade2", "Anatomy" }, { "Intelligence3 Upgrade3", "BombMaking" }, { "Intelligence4 Upgrade2", "RitualOfLife" } };
  private int[] levels;
  private Map<String, JCheckBox> labelMap;
  private JCheckBox[][][] fields;
  private JTabbedPane tabbedPane;

  public AttributesPanel()
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

          for (int i = 0; i < associatedLabels.length; i++) {
            if (abilityLabel.equals(associatedLabels[i][0])) {
              String associatedLabel = associatedLabels[i][1];
              fieldList = new DBList(2);
              fieldList.addElement(new DBElement(10, 0, "RnAbName", associatedLabel));
              fieldList.addElement(new DBElement(0, 0, "RnAbStk", new Integer(0)));
              list.addElement(new DBElement(14, 48879, "", fieldList));
              break;
            }
          }

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

          for (int i = 0; i < associatedLabels.length; i++) {
            if (abilityLabel.equals(associatedLabels[i][0])) {
              String associatedLabel = associatedLabels[i][1];
              count = list.getElementCount();
              for (int j = 0; j < count; j++) {
                DBList fieldList = (DBList)list.getElement(j).getValue();
                String name = fieldList.getString("RnAbName");
                if (name.equals(associatedLabel)) {
                  list.removeElement(j);
                  Main.dataModified = true;
                  break;
                }
              }

              break;
            }
          }

          if ((row == 0) && (this.levels[tab] == col))
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

