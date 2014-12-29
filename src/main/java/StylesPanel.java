/*     */ package TWEditor;
/*     */ 
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTabbedPane;
/*     */ 
/*     */ public class StylesPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*  19 */   private static final String[] tabNames = { "Strong Steel", "Fast Steel", "Group Steel", "Strong Silver", "Fast Silver", "Group Silver" };
/*     */ 
/*  23 */   private static final String[][][] fieldNames = { { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Cut at the Jugular I", "Cut at the Jugular II", "Cut at the Jugular III", "", "" }, { "Crushing Blow I", "Crushing Blow II", "Crushing Blow III", "", "" }, { "Bloody Rage I", "Bloody Rage II", "Bloody Rage III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Paralysis I", "Paralysis II", "Paralysis III", "", "" }, { "Hail of Blows I", "Hail of Blows II", "Hail of Blows III", "", "" }, { "Sever Sinews I", "Sever Sinews II", "Sever Sinews III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Precise Hit I", "Precise Hit II", "Precise Hit III", "", "" }, { "Half-Spin I", "Half-Spin II", "Half-Spin III", "", "" }, { "Trip I", "Trip II", "Trip III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Deep Cut I", "Deep Cut II", "Deep Cut III", "", "" }, { "Mortal Blow I", "Mortal Blow II", "Mortal Blow III", "", "" }, { "Patinado I", "Patinado II", "Patinado III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Crippling Pain I", "Crippling Pain II", "Crippling Pain III", "", "" }, { "Flash Cuts I", "Flash Cuts II", "Flash Cuts III", "", "" }, { "Sinister I", "Sinister II", "Sinister III", "", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Critical Hit I", "Critical Hit II", "Critical Hit III", "", "" }, { "Tempest I", "Tempest II", "Tempest III", "", "" }, { "Tempest I", "Tempest II", "Tempest III", "", "" } } };
/*     */ 
/*  69 */   private static final String[][][] databaseLabels = { { { "StyleSteelStrong1", "StyleSteelStrong2", "StyleSteelStrong3", "StyleSteelStrong4", "StyleSteelStrong5" }, { "StyleSteelStrong1 Upgrade1", "StyleSteelStrong2 Upgrade1", "StyleSteelStrong3 Upgrade1", "", "" }, { "StyleSteelStrong1 Upgrade2", "StyleSteelStrong2 Upgrade2", "StyleSteelStrong3 Upgrade2", "", "" }, { "StyleSteelStrong1 Upgrade3", "StyleSteelStrong2 Upgrade3", "StyleSteelStrong3 Upgrade3", "", "" } }, { { "StyleSteelFast1", "StyleSteelFast2", "StyleSteelFast3", "StyleSteelFast4", "StyleSteelFast5" }, { "StyleSteelFast1 Upgrade1", "StyleSteelFast2 Upgrade1", "StyleSteelFast3 Upgrade1", "", "" }, { "StyleSteelFast1 Upgrade2", "StyleSteelFast2 Upgrade2", "StyleSteelFast3 Upgrade2", "", "" }, { "StyleSteelFast1 Upgrade3", "StyleSteelFast2 Upgrade3", "StyleSteelFast3 Upgrade3", "", "" } }, { { "StyleSteelGroup1", "StyleSteelGroup2", "StyleSteelGroup3", "StyleSteelGroup4", "StyleSteelGroup5" }, { "StyleSteelGroup1 Upgrade1", "StyleSteelGroup2 Upgrade1", "StyleSteelGroup3 Upgrade1", "", "" }, { "StyleSteelGroup1 Upgrade2", "StyleSteelGroup2 Upgrade2", "StyleSteelGroup3 Upgrade2", "", "" }, { "StyleSteelGroup1 Upgrade3", "StyleSteelGroup2 Upgrade3", "StyleSteelGroup3 Upgrade3", "", "" } }, { { "StyleSilverStrong1", "StyleSilverStrong2", "StyleSilverStrong3", "StyleSilverStrong4", "StyleSilverStrong5" }, { "StyleSilverStrong1 Upgrade1", "StyleSilverStrong2 Upgrade1", "StyleSilverStrong3 Upgrade1", "", "" }, { "StyleSilverStrong1 Upgrade2", "StyleSilverStrong2 Upgrade2", "StyleSilverStrong3 Upgrade2", "", "" }, { "StyleSilverStrong1 Upgrade3", "StyleSilverStrong2 Upgrade3", "StyleSilverStrong3 Upgrade3", "", "" } }, { { "StyleSilverFast1", "StyleSilverFast2", "StyleSilverFast3", "StyleSilverFast4", "StyleSilverFast5" }, { "StyleSilverFast1 Upgrade1", "StyleSilverFast2 Upgrade1", "StyleSilverFast3 Upgrade1", "", "" }, { "StyleSilverFast1 Upgrade2", "StyleSilverFast2 Upgrade2", "StyleSilverFast3 Upgrade2", "", "" }, { "StyleSilverFast1 Upgrade3", "StyleSilverFast2 Upgrade3", "StyleSilverFast3 Upgrade3", "", "" } }, { { "StyleSilverGroup1", "StyleSilverGroup2", "StyleSilverGroup3", "StyleSilverGroup4", "StyleSilverGroup5" }, { "StyleSilverGroup1 Upgrade1", "StyleSilverGroup2 Upgrade1", "StyleSilverGroup3 Upgrade1", "", "" }, { "StyleSilverGroup1 Upgrade2", "StyleSilverGroup2 Upgrade2", "StyleSilverGroup3 Upgrade2", "", "" }, { "StyleSilverGroup1 Upgrade3", "StyleSilverGroup2 Upgrade3", "StyleSilverGroup3 Upgrade3", "", "" } } };
/*     */   private int[] levels;
/*     */   private Map<String, JCheckBox> labelMap;
/*     */   private JCheckBox[][][] fields;
/*     */   private JTabbedPane tabbedPane;
/*     */ 
/*     */   public StylesPanel()
/*     */   {
/* 135 */     this.tabbedPane = new JTabbedPane();
/* 136 */     int tabs = fieldNames.length;
/* 137 */     int rows = fieldNames[0].length;
/* 138 */     int cols = fieldNames[0][0].length;
/* 139 */     this.fields = new JCheckBox[tabs][rows][cols];
/* 140 */     this.levels = new int[tabs];
/* 141 */     this.labelMap = new HashMap(tabs * rows * cols);
/* 142 */     for (int tab = 0; tab < tabs; tab++) {
/* 143 */       JPanel panel = new JPanel(new GridLayout(0, cols, 5, 5));
/* 144 */       for (int row = 0; row < rows; row++) {
/* 145 */         for (int col = 0; col < cols; col++) {
/* 146 */           if (fieldNames[tab][row][col].length() > 0) {
/* 147 */             JCheckBox field = new JCheckBox(fieldNames[tab][row][col]);
/* 148 */             field.setActionCommand(Integer.toString(tab * 100 + row * 10 + col));
/* 149 */             field.addActionListener(this);
/* 150 */             this.fields[tab][row][col] = field;
/* 151 */             panel.add(field);
/* 152 */             this.labelMap.put(databaseLabels[tab][row][col], field);
/*     */           } else {
/* 154 */             panel.add(new JLabel());
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 159 */       this.tabbedPane.addTab(tabNames[tab], panel);
/*     */     }
/*     */ 
/* 165 */     add(this.tabbedPane);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/* 178 */     if ((!(ae.getSource() instanceof JCheckBox)) || (Main.dataChanging)) {
/* 179 */       return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 187 */       int value = Integer.parseInt(ae.getActionCommand());
/* 188 */       int tab = value / 100;
/* 189 */       int row = value % 100 / 10;
/* 190 */       int col = value % 10;
/* 191 */       JCheckBox field = this.fields[tab][row][col];
/* 192 */       String abilityLabel = databaseLabels[tab][row][col];
/*     */ 
/* 197 */       DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 198 */       list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 199 */       DBList playerList = (DBList)list.getElement(0).getValue();
/* 200 */       list = (DBList)playerList.getElement("CharAbilities").getValue();
/* 201 */       if (field.isSelected()) {
/* 202 */         boolean addAbility = true;
/*     */ 
/* 208 */         if (row == 0) {
/* 209 */           if (col > this.levels[tab] + 1) {
/* 210 */             JOptionPane.showMessageDialog(this, "Lower ability level must be obtained first", "Missing level", 0);
/*     */ 
/* 212 */             addAbility = false;
/*     */           }
/*     */         }
/* 215 */         else if (col > this.levels[tab]) {
/* 216 */           JOptionPane.showMessageDialog(this, "The ability level must be obtained first", "Missing level", 0);
/*     */ 
/* 218 */           addAbility = false;
/*     */         }
/*     */ 
/* 225 */         if (addAbility) {
/* 226 */           DBList fieldList = new DBList(2);
/* 227 */           fieldList.addElement(new DBElement(10, 0, "RnAbName", abilityLabel));
/* 228 */           fieldList.addElement(new DBElement(0, 0, "RnAbStk", new Integer(0)));
/* 229 */           list.addElement(new DBElement(14, 48879, "", fieldList));
/*     */ 
/* 231 */           if ((row == 0) && (col > this.levels[tab])) {
/* 232 */             this.levels[tab] = col;
/*     */           }
/* 234 */           Main.dataModified = true;
/*     */         } else {
/* 236 */           Main.dataChanging = true;
/* 237 */           field.setSelected(false);
/* 238 */           Main.dataChanging = false;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 243 */         boolean removeAbility = true;
/*     */ 
/* 248 */         if (row == 0) {
/* 249 */           if (col < this.levels[tab]) {
/* 250 */             JOptionPane.showMessageDialog(this, "All higher ability levels must be removed first", "Higher level", 0);
/*     */ 
/* 252 */             removeAbility = false;
/*     */           } else {
/* 254 */             for (int i = 1; i < this.fields[0].length; i++) {
/* 255 */               JCheckBox checkField = this.fields[tab][i][col];
/* 256 */               if ((checkField != null) && (checkField.isSelected())) {
/* 257 */                 JOptionPane.showMessageDialog(this, "All ability level upgrades must be removed first", "Ability upgrades", 0);
/*     */ 
/* 259 */                 removeAbility = false;
/* 260 */                 break;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 269 */         if (removeAbility) {
/* 270 */           int count = list.getElementCount();
/* 271 */           for (int i = 0; i < count; i++) {
/* 272 */             DBList fieldList = (DBList)list.getElement(i).getValue();
/* 273 */             String name = fieldList.getString("RnAbName");
/* 274 */             if (abilityLabel.equals(name)) {
/* 275 */               list.removeElement(i);
/* 276 */               Main.dataModified = true;
/* 277 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 281 */           if ((row == 0) && (col == this.levels[tab]))
/* 282 */             this.levels[tab] = (col - 1);
/*     */         } else {
/* 284 */           Main.dataChanging = true;
/* 285 */           field.setSelected(true);
/* 286 */           Main.dataChanging = false;
/*     */         }
/*     */       }
/*     */     } catch (DBException exc) {
/* 290 */       Main.logException("Unable to update database field", exc);
/*     */     } catch (Throwable exc) {
/* 292 */       Main.logException("Exception while processing action event", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFields(DBList list)
/*     */     throws DBException
/*     */   {
/* 307 */     for (int tab = 0; tab < this.fields.length; tab++) {
/* 308 */       for (int row = 0; row < this.fields[0].length; row++) {
/* 309 */         for (int col = 0; col < this.fields[0][0].length; col++) {
/* 310 */           if (this.fields[tab][row][col] != null) {
/* 311 */             this.fields[tab][row][col].setSelected(false);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 316 */       this.levels[tab] = -1;
/*     */     }
/*     */ 
/* 322 */     DBElement element = list.getElement("CharAbilities");
/* 323 */     if (element == null) {
/* 324 */       throw new DBException("CharAbilities field not found");
/*     */     }
/* 326 */     DBList abilityList = (DBList)element.getValue();
/* 327 */     int count = abilityList.getElementCount();
/* 328 */     for (int index = 0; index < count; index++) {
/* 329 */       DBList fieldList = (DBList)abilityList.getElement(index).getValue();
/* 330 */       String abilityName = fieldList.getString("RnAbName");
/* 331 */       JCheckBox field = (JCheckBox)this.labelMap.get(abilityName);
/* 332 */       if (field != null) {
/* 333 */         field.setSelected(true);
/* 334 */         int value = Integer.parseInt(field.getActionCommand());
/* 335 */         int tab = value / 100;
/* 336 */         int row = value % 100 / 10;
/* 337 */         int col = value % 10;
/* 338 */         if ((row == 0) && (col > this.levels[tab]))
/* 339 */           this.levels[tab] = col;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void getFields(DBList list)
/*     */     throws DBException
/*     */   {
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.StylesPanel
 * JD-Core Version:    0.6.2
 */