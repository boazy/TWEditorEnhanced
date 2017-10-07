package TWEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DifficultyPanel extends JPanel
  implements ActionListener
{
  private final String EASY = "Easy";
  private final String MEDIUM = "Medium";
  private final String HARD = "Hard";

  private final String EASY_DIFF = "Difficulty_easy";
  private final String MEDIUM_DIFF = "Difficulty_normal";

  private final int EASY_INT = 0;
  private final int MEDIUM_INT = 1;
  private final int HARD_INT = 2;

  private JRadioButton easyButton, mediumButton, hardButton;
  private String level;

  public DifficultyPanel()
  {
    easyButton = new JRadioButton(EASY);
    easyButton.setActionCommand(EASY);
    easyButton.addActionListener(this);

    mediumButton = new JRadioButton(MEDIUM);
    mediumButton.setActionCommand(MEDIUM);
    mediumButton.addActionListener(this);

    hardButton = new JRadioButton(HARD);
    hardButton.setActionCommand(HARD);
    hardButton.addActionListener(this);

    ButtonGroup group = new ButtonGroup();
    group.add(easyButton);
    group.add(mediumButton);
    group.add(hardButton);

    JPanel panel = new JPanel(new GridLayout(0, 3, 5, 5));
    panel.add(easyButton);
    panel.add(mediumButton);
    panel.add(hardButton);

    panel.setBorder(BorderFactory.createTitledBorder("Level"));

    add(panel);
  }

  private void processCharAbilities(DBList list, String cmd) {
    try {
      DBList abilityList = (DBList)list.getElement("CharAbilities").getValue();

      if(level.equals(EASY) || level.equals(MEDIUM)) {
        for (int i = 0; i < abilityList.getElementCount(); i++) {
          DBList fieldList = (DBList)abilityList.getElement(i).getValue();

          DBElement e = fieldList.getElement(0);
          Object value = e.getValue();
          if(value.equals(EASY_DIFF) || value.equals(MEDIUM_DIFF)) {
            if(cmd.equals(EASY)) {
              fieldList.setString("RnAbName", EASY_DIFF);
            } else if(cmd.equals(MEDIUM)) {
              fieldList.setString("RnAbName", MEDIUM_DIFF);
            } else {
              abilityList.removeElement(i);
            }

            break;
          }
        }
      } else {
        for (int i = 0; i < abilityList.getElementCount(); i++) {
          DBList fieldList = (DBList)abilityList.getElement(i).getValue();

          DBElement e = fieldList.getElement(0);
          Object value = e.getValue();
          if(value.equals("StyleSilverGroup1")) {
            DBList levelList = new DBList(2);
            if(cmd.equals(EASY)) {
              levelList.addElement(new DBElement(10, 0, "RnAbName", EASY_DIFF));
            } else if(cmd.equals(MEDIUM)) {
              levelList.addElement(new DBElement(10, 0, "RnAbName", MEDIUM_DIFF));
            }
            levelList.addElement(new DBElement(0, 0, "RnAbStk", new Integer(0)));

            abilityList.insertElement(i + 1, new DBElement(14, 48879, "", levelList));
            break;
          }
        }
      }

      level = cmd;
      Main.dataModified = true;
    } catch (DBException exc) {
      Main.logException("Unable to update database field", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while processing action event", exc);
    }
  }

  private void processGameDiffSetting(DBList list, String cmd) {
    try {
      if(cmd.equals(EASY)) {
        list.setInteger("GameDiffSetting", EASY_INT);
      } else if(cmd.equals(MEDIUM)) {
        list.setInteger("GameDiffSetting", MEDIUM_INT);
      } else {
        list.setInteger("GameDiffSetting", HARD_INT);
      }
    } catch (DBException exc) {
      Main.logException("Unable to update database field", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while processing action event", exc);
    }
  }

  public void actionPerformed(ActionEvent ae)
  {
    if ((!(ae.getSource() instanceof JRadioButton)) || (Main.dataChanging)) {
      return;
    }

    String cmd = ae.getActionCommand();

    if(cmd.equals(level)) {
      return;
    }

    DBList top = (DBList)Main.database.getTopLevelStruct().getValue();
    DBList mod = (DBList)top.getElement("Mod_PlayerList").getValue();
    DBList modPlayerList = (DBList)mod.getElement(0).getValue();
    processCharAbilities(modPlayerList, cmd);

    DBList playerList = (DBList)Main.playerDatabase.getTopLevelStruct().getValue();
    processCharAbilities(playerList, cmd);

    DBList smm = (DBList)Main.smmDatabase.getTopLevelStruct().getValue();
    processGameDiffSetting(smm, cmd);
  }

  private void print(DBElement e) {
    System.out.println(e.getType() + " " + e.getLabel() + " " + e.getID());
  }

  public void setFields(DBList list)
    throws DBException
  {
    level = HARD;
    hardButton.setSelected(true);

    DBElement element = list.getElement("CharAbilities");
    if (element == null) {
      throw new DBException("CharAbilities field not found");
    }
    DBList abilityList = (DBList)element.getValue();
    for (int i = 0; i < abilityList.getElementCount(); i++) {
      DBList fieldList = (DBList)abilityList.getElement(i).getValue();

      DBElement e = fieldList.getElement(0);
      Object value = e.getValue();
      if(value.equals(EASY_DIFF)) {
        level = EASY;
        easyButton.setSelected(true);
        break;
      } else if(value.equals(MEDIUM_DIFF)) {
        level = MEDIUM;
        mediumButton.setSelected(true);
        break;
      }
    }
  }

  public void getFields(DBList list)
    throws DBException
  {
  }
}

