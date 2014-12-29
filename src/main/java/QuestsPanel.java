package TWEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

public class QuestsPanel extends JPanel
  implements ActionListener
{
  private JTabbedPane tabbedPane;
  private List<Quest> startedList;
  private JList startedField;
  private List<Quest> completedList;
  private JList completedField;
  private List<Quest> failedList;
  private JList failedField;
  private List<Quest> notStartedList;
  private JList notStartedField;

  public QuestsPanel()
  {
    this.tabbedPane = new JTabbedPane(2);

    this.startedField = new JList();
    this.startedField.setVisibleRowCount(26);
    JScrollPane scrollPane = new JScrollPane(this.startedField);

    JPanel buttonPane = new JPanel();
    JButton button = new JButton("Examine");
    button.addActionListener(this);
    button.setActionCommand("examine started");
    buttonPane.add(button);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, 1));
    panel.add(scrollPane);
    panel.add(Box.createVerticalStrut(5));
    panel.add(buttonPane);
    this.tabbedPane.add("Started", panel);

    this.completedField = new JList();
    this.completedField.setVisibleRowCount(26);
    scrollPane = new JScrollPane(this.completedField);

    button = new JButton("Examine");
    button.addActionListener(this);
    button.setActionCommand("examine completed");

    buttonPane = new JPanel();
    buttonPane.add(button);

    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, 1));
    panel.add(scrollPane);
    panel.add(Box.createVerticalStrut(5));
    panel.add(buttonPane);
    this.tabbedPane.add("Completed", panel);

    this.failedField = new JList();
    this.failedField.setVisibleRowCount(26);
    scrollPane = new JScrollPane(this.failedField);

    button = new JButton("Examine");
    button.addActionListener(this);
    button.setActionCommand("examine failed");

    buttonPane = new JPanel();
    buttonPane.add(button);

    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, 1));
    panel.add(scrollPane);
    panel.add(Box.createVerticalStrut(5));
    panel.add(buttonPane);
    this.tabbedPane.add("Failed", panel);

    this.notStartedField = new JList();
    this.notStartedField.setVisibleRowCount(26);
    scrollPane = new JScrollPane(this.notStartedField);

    button = new JButton("Examine");
    button.addActionListener(this);
    button.setActionCommand("examine not started");

    buttonPane = new JPanel();
    buttonPane.add(button);

    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, 1));
    panel.add(scrollPane);
    panel.add(Box.createVerticalStrut(5));
    panel.add(buttonPane);
    this.tabbedPane.add("Not Started", panel);

    add(this.tabbedPane);
  }

  public void actionPerformed(ActionEvent ae)
  {
    try
    {
      String action = ae.getActionCommand();

      if (action.equals("examine started")) {
        int sel = this.startedField.getSelectedIndex();
        if (sel < 0) {
          JOptionPane.showMessageDialog(this, "You must select a quest to examine", "No quest selected", 0);
        }
        else
          examineQuest((Quest)this.startedList.get(sel));
      }
      else if (action.equals("examine completed")) {
        int sel = this.completedField.getSelectedIndex();
        if (sel < 0) {
          JOptionPane.showMessageDialog(this, "You must select a quest to examine", "No quest selected", 0);
        }
        else
          examineQuest((Quest)this.completedList.get(sel));
      }
      else if (action.equals("examine failed")) {
        int sel = this.failedField.getSelectedIndex();
        if (sel < 0) {
          JOptionPane.showMessageDialog(this, "You must select a quest to examine", "No quest selected", 0);
        }
        else
          examineQuest((Quest)this.failedList.get(sel));
      }
      else if (action.equals("examine not started")) {
        int sel = this.notStartedField.getSelectedIndex();
        if (sel < 0) {
          JOptionPane.showMessageDialog(this, "You must select a quest to examine", "No quest selected", 0);
        }
        else
          examineQuest((Quest)this.notStartedList.get(sel));
      }
    }
    catch (DBException exc) {
      Main.logException("Unable to access database field", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while processing action event", exc);
    }
  }

  private void examineQuest(Quest quest)
    throws DBException
  {
    DBList fieldList = (DBList)quest.getQuestElement().getValue();
    fieldList = (DBList)fieldList.getElement("MainPhase").getValue();
    fieldList = (DBList)fieldList.getElement(0).getValue();
    int currentPhase = fieldList.getInteger("CurrPhase");
    DBElement element = fieldList.getElement("Phases");
    if ((element == null) || (element.getType() != 15)) {
      throw new DBException(new StringBuilder().append("No phase list found for quest ").append(quest.getResourceName()).toString());
    }
    DBList phaseList = (DBList)element.getValue();
    currentPhase = Math.min(currentPhase, phaseList.getElementCount());

    while (currentPhase > 0) {
      DBList phaseFields = (DBList)phaseList.getElement(currentPhase - 1).getValue();
      DBList subquestFields = locateSubquest(phaseFields);
      if (((phaseFields.getInteger("Completed") == 1) || (phaseFields.getInteger("Failed") == 1)) && 
        (phaseFields.getString("LocDescription").length() > 0)) {
        fieldList = phaseFields;
        break;
      }

      if (subquestFields != null) {
        fieldList = subquestFields;
        break;
      }

      currentPhase--;
    }

    StringBuilder description = new StringBuilder(256);
    String string = fieldList.getString("LocPhaseName");
    description.append("<b>");
    description.append(string.length() > 0 ? string : quest.getQuestName());
    description.append("</b><br><br>");

    string = fieldList.getString("LocDescription");
    if (string.length() > 0) {
      description.append(string);
      description.append("<br><br>");
    }

    string = fieldList.getString("LocShortDescript");
    if (string.length() > 0) {
      description.append("<i>");
      description.append(string);
      description.append("</i><br><br>");
    }

    description.append("Quest file: ");
    description.append(quest.getResourceName());
    ExamineDialog.showDialog(Main.mainWindow, quest.getQuestName(), description.toString());
  }

  private DBList locateSubquest(DBList fieldList)
    throws DBException
  {
    DBList subquestList = null;
    DBElement element = fieldList.getElement("Phases");
    if ((element != null) && (element.getType() == 15)) {
      DBList questList = (DBList)element.getValue();
      int count = questList.getElementCount();
      for (int i = count - 1; i >= 0; i--) {
        DBList questFields = (DBList)questList.getElement(i).getValue();
        DBList subquestFields = locateSubquest(questFields);
        if (subquestFields != null) {
          subquestList = subquestFields;
          break;
        }

        if (((questFields.getInteger("Completed") == 1) || (questFields.getInteger("Failed") == 1)) && 
          (questFields.getString("LocDescription").length() > 0)) {
          subquestList = questFields;
          break;
        }
      }

    }

    return subquestList;
  }

  public void setFields(DBList list)
    throws DBException
  {
    int count = Main.quests.size();
    this.startedList = new ArrayList(count);
    this.completedList = new ArrayList(count);
    this.failedList = new ArrayList(count);
    this.notStartedList = new ArrayList(count);

    for (Quest quest : Main.quests) {
      switch (quest.getQuestState()) {
      case 1:
        insertItem(this.startedList, quest);
        break;
      case 2:
        insertItem(this.completedList, quest);
        break;
      case 3:
        insertItem(this.failedList, quest);
        break;
      case 0:
        insertItem(this.notStartedList, quest);
      }

    }

    this.startedField.setListData(this.startedList.toArray());
    this.startedField.setSelectedIndex(-1);

    this.completedField.setListData(this.completedList.toArray());
    this.completedField.setSelectedIndex(-1);

    this.failedField.setListData(this.failedList.toArray());
    this.failedField.setSelectedIndex(-1);

    this.notStartedField.setListData(this.notStartedList.toArray());
    this.notStartedField.setSelectedIndex(-1);
  }

  public void getFields(DBList list)
    throws DBException
  {
  }

  private void insertItem(List<Quest> list, Quest quest)
  {
    String questName = quest.getQuestName();
    int count = list.size();
    int index;
    for (index = 0; index < count; index++) {
      Quest listItem = (Quest)list.get(index);
      if (questName.compareTo(listItem.getQuestName()) < 0) {
        break;
      }
    }
    list.add(index, quest);
  }
}

