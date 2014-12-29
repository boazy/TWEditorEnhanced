/*     */ package TWEditor;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTabbedPane;
/*     */ 
/*     */ public class QuestsPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   private JTabbedPane tabbedPane;
/*     */   private List<Quest> startedList;
/*     */   private JList startedField;
/*     */   private List<Quest> completedList;
/*     */   private JList completedField;
/*     */   private List<Quest> failedList;
/*     */   private JList failedField;
/*     */   private List<Quest> notStartedList;
/*     */   private JList notStartedField;
/*     */ 
/*     */   public QuestsPanel()
/*     */   {
/*  52 */     this.tabbedPane = new JTabbedPane(2);
/*     */ 
/*  57 */     this.startedField = new JList();
/*  58 */     this.startedField.setVisibleRowCount(26);
/*  59 */     JScrollPane scrollPane = new JScrollPane(this.startedField);
/*     */ 
/*  61 */     JPanel buttonPane = new JPanel();
/*  62 */     JButton button = new JButton("Examine");
/*  63 */     button.addActionListener(this);
/*  64 */     button.setActionCommand("examine started");
/*  65 */     buttonPane.add(button);
/*     */ 
/*  71 */     JPanel panel = new JPanel();
/*  72 */     panel.setLayout(new BoxLayout(panel, 1));
/*  73 */     panel.add(scrollPane);
/*  74 */     panel.add(Box.createVerticalStrut(5));
/*  75 */     panel.add(buttonPane);
/*  76 */     this.tabbedPane.add("Started", panel);
/*     */ 
/*  81 */     this.completedField = new JList();
/*  82 */     this.completedField.setVisibleRowCount(26);
/*  83 */     scrollPane = new JScrollPane(this.completedField);
/*     */ 
/*  85 */     button = new JButton("Examine");
/*  86 */     button.addActionListener(this);
/*  87 */     button.setActionCommand("examine completed");
/*     */ 
/*  89 */     buttonPane = new JPanel();
/*  90 */     buttonPane.add(button);
/*     */ 
/*  92 */     panel = new JPanel();
/*  93 */     panel.setLayout(new BoxLayout(panel, 1));
/*  94 */     panel.add(scrollPane);
/*  95 */     panel.add(Box.createVerticalStrut(5));
/*  96 */     panel.add(buttonPane);
/*  97 */     this.tabbedPane.add("Completed", panel);
/*     */ 
/* 102 */     this.failedField = new JList();
/* 103 */     this.failedField.setVisibleRowCount(26);
/* 104 */     scrollPane = new JScrollPane(this.failedField);
/*     */ 
/* 106 */     button = new JButton("Examine");
/* 107 */     button.addActionListener(this);
/* 108 */     button.setActionCommand("examine failed");
/*     */ 
/* 110 */     buttonPane = new JPanel();
/* 111 */     buttonPane.add(button);
/*     */ 
/* 113 */     panel = new JPanel();
/* 114 */     panel.setLayout(new BoxLayout(panel, 1));
/* 115 */     panel.add(scrollPane);
/* 116 */     panel.add(Box.createVerticalStrut(5));
/* 117 */     panel.add(buttonPane);
/* 118 */     this.tabbedPane.add("Failed", panel);
/*     */ 
/* 123 */     this.notStartedField = new JList();
/* 124 */     this.notStartedField.setVisibleRowCount(26);
/* 125 */     scrollPane = new JScrollPane(this.notStartedField);
/*     */ 
/* 127 */     button = new JButton("Examine");
/* 128 */     button.addActionListener(this);
/* 129 */     button.setActionCommand("examine not started");
/*     */ 
/* 131 */     buttonPane = new JPanel();
/* 132 */     buttonPane.add(button);
/*     */ 
/* 134 */     panel = new JPanel();
/* 135 */     panel.setLayout(new BoxLayout(panel, 1));
/* 136 */     panel.add(scrollPane);
/* 137 */     panel.add(Box.createVerticalStrut(5));
/* 138 */     panel.add(buttonPane);
/* 139 */     this.tabbedPane.add("Not Started", panel);
/*     */ 
/* 141 */     add(this.tabbedPane);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/*     */     try
/*     */     {
/* 151 */       String action = ae.getActionCommand();
/*     */ 
/* 153 */       if (action.equals("examine started")) {
/* 154 */         int sel = this.startedField.getSelectedIndex();
/* 155 */         if (sel < 0) {
/* 156 */           JOptionPane.showMessageDialog(this, "You must select a quest to examine", "No quest selected", 0);
/*     */         }
/*     */         else
/* 159 */           examineQuest((Quest)this.startedList.get(sel));
/*     */       }
/* 161 */       else if (action.equals("examine completed")) {
/* 162 */         int sel = this.completedField.getSelectedIndex();
/* 163 */         if (sel < 0) {
/* 164 */           JOptionPane.showMessageDialog(this, "You must select a quest to examine", "No quest selected", 0);
/*     */         }
/*     */         else
/* 167 */           examineQuest((Quest)this.completedList.get(sel));
/*     */       }
/* 169 */       else if (action.equals("examine failed")) {
/* 170 */         int sel = this.failedField.getSelectedIndex();
/* 171 */         if (sel < 0) {
/* 172 */           JOptionPane.showMessageDialog(this, "You must select a quest to examine", "No quest selected", 0);
/*     */         }
/*     */         else
/* 175 */           examineQuest((Quest)this.failedList.get(sel));
/*     */       }
/* 177 */       else if (action.equals("examine not started")) {
/* 178 */         int sel = this.notStartedField.getSelectedIndex();
/* 179 */         if (sel < 0) {
/* 180 */           JOptionPane.showMessageDialog(this, "You must select a quest to examine", "No quest selected", 0);
/*     */         }
/*     */         else
/* 183 */           examineQuest((Quest)this.notStartedList.get(sel));
/*     */       }
/*     */     }
/*     */     catch (DBException exc) {
/* 187 */       Main.logException("Unable to access database field", exc);
/*     */     } catch (Throwable exc) {
/* 189 */       Main.logException("Exception while processing action event", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void examineQuest(Quest quest)
/*     */     throws DBException
/*     */   {
/* 200 */     DBList fieldList = (DBList)quest.getQuestElement().getValue();
/* 201 */     fieldList = (DBList)fieldList.getElement("MainPhase").getValue();
/* 202 */     fieldList = (DBList)fieldList.getElement(0).getValue();
/* 203 */     int currentPhase = fieldList.getInteger("CurrPhase");
/* 204 */     DBElement element = fieldList.getElement("Phases");
/* 205 */     if ((element == null) || (element.getType() != 15)) {
/* 206 */       throw new DBException(new StringBuilder().append("No phase list found for quest ").append(quest.getResourceName()).toString());
/*     */     }
/* 208 */     DBList phaseList = (DBList)element.getValue();
/* 209 */     currentPhase = Math.min(currentPhase, phaseList.getElementCount());
/*     */ 
/* 215 */     while (currentPhase > 0) {
/* 216 */       DBList phaseFields = (DBList)phaseList.getElement(currentPhase - 1).getValue();
/* 217 */       DBList subquestFields = locateSubquest(phaseFields);
/* 218 */       if (((phaseFields.getInteger("Completed") == 1) || (phaseFields.getInteger("Failed") == 1)) && 
/* 219 */         (phaseFields.getString("LocDescription").length() > 0)) {
/* 220 */         fieldList = phaseFields;
/* 221 */         break;
/*     */       }
/*     */ 
/* 225 */       if (subquestFields != null) {
/* 226 */         fieldList = subquestFields;
/* 227 */         break;
/*     */       }
/*     */ 
/* 230 */       currentPhase--;
/*     */     }
/*     */ 
/* 236 */     StringBuilder description = new StringBuilder(256);
/* 237 */     String string = fieldList.getString("LocPhaseName");
/* 238 */     description.append("<b>");
/* 239 */     description.append(string.length() > 0 ? string : quest.getQuestName());
/* 240 */     description.append("</b><br><br>");
/*     */ 
/* 242 */     string = fieldList.getString("LocDescription");
/* 243 */     if (string.length() > 0) {
/* 244 */       description.append(string);
/* 245 */       description.append("<br><br>");
/*     */     }
/*     */ 
/* 248 */     string = fieldList.getString("LocShortDescript");
/* 249 */     if (string.length() > 0) {
/* 250 */       description.append("<i>");
/* 251 */       description.append(string);
/* 252 */       description.append("</i><br><br>");
/*     */     }
/*     */ 
/* 255 */     description.append("Quest file: ");
/* 256 */     description.append(quest.getResourceName());
/* 257 */     ExamineDialog.showDialog(Main.mainWindow, quest.getQuestName(), description.toString());
/*     */   }
/*     */ 
/*     */   private DBList locateSubquest(DBList fieldList)
/*     */     throws DBException
/*     */   {
/* 268 */     DBList subquestList = null;
/* 269 */     DBElement element = fieldList.getElement("Phases");
/* 270 */     if ((element != null) && (element.getType() == 15)) {
/* 271 */       DBList questList = (DBList)element.getValue();
/* 272 */       int count = questList.getElementCount();
/* 273 */       for (int i = count - 1; i >= 0; i--) {
/* 274 */         DBList questFields = (DBList)questList.getElement(i).getValue();
/* 275 */         DBList subquestFields = locateSubquest(questFields);
/* 276 */         if (subquestFields != null) {
/* 277 */           subquestList = subquestFields;
/* 278 */           break;
/*     */         }
/*     */ 
/* 281 */         if (((questFields.getInteger("Completed") == 1) || (questFields.getInteger("Failed") == 1)) && 
/* 282 */           (questFields.getString("LocDescription").length() > 0)) {
/* 283 */           subquestList = questFields;
/* 284 */           break;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 290 */     return subquestList;
/*     */   }
/*     */ 
/*     */   public void setFields(DBList list)
/*     */     throws DBException
/*     */   {
/* 304 */     int count = Main.quests.size();
/* 305 */     this.startedList = new ArrayList(count);
/* 306 */     this.completedList = new ArrayList(count);
/* 307 */     this.failedList = new ArrayList(count);
/* 308 */     this.notStartedList = new ArrayList(count);
/*     */ 
/* 310 */     for (Quest quest : Main.quests) {
/* 311 */       switch (quest.getQuestState()) {
/*     */       case 1:
/* 313 */         insertItem(this.startedList, quest);
/* 314 */         break;
/*     */       case 2:
/* 317 */         insertItem(this.completedList, quest);
/* 318 */         break;
/*     */       case 3:
/* 321 */         insertItem(this.failedList, quest);
/* 322 */         break;
/*     */       case 0:
/* 325 */         insertItem(this.notStartedList, quest);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 333 */     this.startedField.setListData(this.startedList.toArray());
/* 334 */     this.startedField.setSelectedIndex(-1);
/*     */ 
/* 336 */     this.completedField.setListData(this.completedList.toArray());
/* 337 */     this.completedField.setSelectedIndex(-1);
/*     */ 
/* 339 */     this.failedField.setListData(this.failedList.toArray());
/* 340 */     this.failedField.setSelectedIndex(-1);
/*     */ 
/* 342 */     this.notStartedField.setListData(this.notStartedList.toArray());
/* 343 */     this.notStartedField.setSelectedIndex(-1);
/*     */   }
/*     */ 
/*     */   public void getFields(DBList list)
/*     */     throws DBException
/*     */   {
/*     */   }
/*     */ 
/*     */   private void insertItem(List<Quest> list, Quest quest)
/*     */   {
/* 366 */     String questName = quest.getQuestName();
/* 367 */     int count = list.size();
/*     */
              int index;
/* 369 */     for (index = 0; index < count; index++) {
/* 370 */       Quest listItem = (Quest)list.get(index);
/* 371 */       if (questName.compareTo(listItem.getQuestName()) < 0) {
/*     */         break;
/*     */       }
/*     */     }
/* 375 */     list.add(index, quest);
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.QuestsPanel
 * JD-Core Version:    0.6.2
 */