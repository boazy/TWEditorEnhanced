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
/*     */ public class AttributesPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*  19 */   private static final String[] tabNames = { "Strength", "Dexterity", "Stamina", "Intelligence" };
/*     */ 
/*  22 */   private static final String[][][] fieldNames = { { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Buzz", "Position", "Vigor", "Bleeding Resistance", "Wound Resistance" }, { "True Grit", "Regeneration", "Knockdown Resistance", "Stone Skin", "Added Vitality" }, { "", "Brawl", "Survival Instinct", "Aggression", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Flaying", "Deflect Arrows", "Bleeding Resistance", "Finesse", "Vigilance" }, { "Predator", "Repel", "Agility", "Feint", "Precision" }, { "", "Fistfight", "Limit Incineration", "Incineration Resistance", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Heavyweight", "Absorption", "Endurance Regeneration", "Stun Resistance", "Potion Tolerance" }, { "Mutation", "Poison Resistance", "Pain Resistance", "Brawn", "Added Endurance" }, { "", "Endurance Regeneration", "Revive", "Altered Metabolism", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Potion Brewing", "Herbalism", "Cleansing Ritual", "Focus", "Mental Endurance" }, { "Rising Moon", "Monster Lore", "Ingredient Extraction", "Life Ritual", "Intensity" }, { "", "Oil Preparation", "Bomb Preparation", "Magic Frenzy", "" } } };
/*     */ 
/*  54 */   private static final String[][][] databaseLabels = { { { "Strength1", "Strength2", "Strength3", "Strength4", "Strength5" }, { "Strength1 Upgrade1", "Strength2 Upgrade1", "Strength3 Upgrade1", "Strength4 Upgrade1", "Strength5 Upgrade1" }, { "Strength1 Upgrade2", "Strength2 Upgrade2", "Strength3 Upgrade2", "Strength4 Upgrade2", "Strength5 Upgrade2" }, { "", "Strength2 Upgrade3", "Strength3 Upgrade3", "Strength4 Upgrade3", "" } }, { { "Dexterity1", "Dexterity2", "Dexterity3", "Dexterity4", "Dexterity5" }, { "Dexterity1 Upgrade1", "Dexterity2 Upgrade1", "Dexterity3 Upgrade1", "Dexterity4 Upgrade1", "Dexterity5 Upgrade1" }, { "Dexterity1 Upgrade2", "Dexterity2 Upgrade2", "Dexterity3 Upgrade2", "Dexterity4 Upgrade2", "Dexterity5 Upgrade2" }, { "", "Dexterity2 Upgrade3", "Dexterity3 Upgrade3", "Dexterity4 Upgrade3", "" } }, { { "Endurance1", "Endurance2", "Endurance3", "Endurance4", "Endurance5" }, { "Endurance1 Upgrade1", "Endurance2 Upgrade1", "Endurance3 Upgrade1", "Endurance4 Upgrade1", "Endurance5 Upgrade1" }, { "Endurance1 Upgrade2", "Endurance2 Upgrade2", "Endurance3 Upgrade2", "Endurance4 Upgrade2", "Endurance5 Upgrade2" }, { "", "Endurance2 Upgrade3", "Endurance3 Upgrade3", "Endurance4 Upgrade3", "" } }, { { "Intelligence1", "Intelligence2", "Intelligence3", "Intelligence4", "Intelligence5" }, { "Intelligence1 Upgrade1", "Intelligence2 Upgrade1", "Intelligence3 Upgrade1", "Intelligence4 Upgrade1", "Intelligence5 Upgrade1" }, { "Intelligence1 Upgrade2", "Intelligence2 Upgrade2", "Intelligence3 Upgrade2", "Intelligence4 Upgrade2", "Intelligence5 Upgrade2" }, { "", "Intelligence2 Upgrade3", "Intelligence3 Upgrade3", "Intelligence4 Upgrade3", "" } } };
/*     */ 
/*  88 */   private static final String[][] associatedLabels = { { "Dexterity1 Upgrade1", "Skinning" }, { "Intelligence2 Upgrade1", "HerbGathering" }, { "Intelligence2 Upgrade3", "GreaseMaking" }, { "Intelligence3 Upgrade1", "RitualOfPurify" }, { "Intelligence3 Upgrade2", "Anatomy" }, { "Intelligence3 Upgrade3", "BombMaking" }, { "Intelligence4 Upgrade2", "RitualOfLife" } };
/*     */   private int[] levels;
/*     */   private Map<String, JCheckBox> labelMap;
/*     */   private JCheckBox[][][] fields;
/*     */   private JTabbedPane tabbedPane;
/*     */ 
/*     */   public AttributesPanel()
/*     */   {
/* 119 */     this.tabbedPane = new JTabbedPane();
/* 120 */     int tabs = fieldNames.length;
/* 121 */     int rows = fieldNames[0].length;
/* 122 */     int cols = fieldNames[0][0].length;
/* 123 */     this.fields = new JCheckBox[tabs][rows][cols];
/* 124 */     this.levels = new int[tabs];
/* 125 */     this.labelMap = new HashMap(tabs * rows * cols);
/* 126 */     for (int tab = 0; tab < tabs; tab++) {
/* 127 */       JPanel panel = new JPanel(new GridLayout(0, cols, 5, 5));
/* 128 */       for (int row = 0; row < rows; row++) {
/* 129 */         for (int col = 0; col < cols; col++) {
/* 130 */           if (fieldNames[tab][row][col].length() > 0) {
/* 131 */             JCheckBox field = new JCheckBox(fieldNames[tab][row][col]);
/* 132 */             field.setActionCommand(Integer.toString(tab * 100 + row * 10 + col));
/* 133 */             field.addActionListener(this);
/* 134 */             this.fields[tab][row][col] = field;
/* 135 */             panel.add(field);
/* 136 */             this.labelMap.put(databaseLabels[tab][row][col], field);
/*     */           } else {
/* 138 */             panel.add(new JLabel());
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 143 */       this.tabbedPane.addTab(tabNames[tab], panel);
/*     */     }
/*     */ 
/* 149 */     add(this.tabbedPane);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/* 162 */     if ((!(ae.getSource() instanceof JCheckBox)) || (Main.dataChanging)) {
/* 163 */       return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 171 */       int value = Integer.parseInt(ae.getActionCommand());
/* 172 */       int tab = value / 100;
/* 173 */       int row = value % 100 / 10;
/* 174 */       int col = value % 10;
/* 175 */       JCheckBox field = this.fields[tab][row][col];
/* 176 */       String abilityLabel = databaseLabels[tab][row][col];
/*     */ 
/* 181 */       DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 182 */       list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 183 */       DBList playerList = (DBList)list.getElement(0).getValue();
/* 184 */       list = (DBList)playerList.getElement("CharAbilities").getValue();
/* 185 */       if (field.isSelected()) {
/* 186 */         boolean addAbility = true;
/*     */ 
/* 192 */         if (row == 0) {
/* 193 */           if (col > this.levels[tab] + 1) {
/* 194 */             JOptionPane.showMessageDialog(this, "Lower ability level must be obtained first", "Missing level", 0);
/*     */ 
/* 196 */             addAbility = false;
/*     */           }
/*     */         }
/* 199 */         else if (col > this.levels[tab]) {
/* 200 */           JOptionPane.showMessageDialog(this, "The ability level must be obtained first", "Missing level", 0);
/*     */ 
/* 202 */           addAbility = false;
/*     */         }
/*     */ 
/* 209 */         if (addAbility) {
/* 210 */           DBList fieldList = new DBList(2);
/* 211 */           fieldList.addElement(new DBElement(10, 0, "RnAbName", abilityLabel));
/* 212 */           fieldList.addElement(new DBElement(0, 0, "RnAbStk", new Integer(0)));
/* 213 */           list.addElement(new DBElement(14, 48879, "", fieldList));
/*     */ 
/* 215 */           for (int i = 0; i < associatedLabels.length; i++) {
/* 216 */             if (abilityLabel.equals(associatedLabels[i][0])) {
/* 217 */               String associatedLabel = associatedLabels[i][1];
/* 218 */               fieldList = new DBList(2);
/* 219 */               fieldList.addElement(new DBElement(10, 0, "RnAbName", associatedLabel));
/* 220 */               fieldList.addElement(new DBElement(0, 0, "RnAbStk", new Integer(0)));
/* 221 */               list.addElement(new DBElement(14, 48879, "", fieldList));
/* 222 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 226 */           if ((row == 0) && (col > this.levels[tab])) {
/* 227 */             this.levels[tab] = col;
/*     */           }
/* 229 */           Main.dataModified = true;
/*     */         } else {
/* 231 */           Main.dataChanging = true;
/* 232 */           field.setSelected(false);
/* 233 */           Main.dataChanging = false;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 238 */         boolean removeAbility = true;
/*     */ 
/* 243 */         if (row == 0) {
/* 244 */           if (col < this.levels[tab]) {
/* 245 */             JOptionPane.showMessageDialog(this, "All higher ability levels must be removed first", "Higher level", 0);
/*     */ 
/* 247 */             removeAbility = false;
/*     */           } else {
/* 249 */             for (int i = 1; i < this.fields[0].length; i++) {
/* 250 */               JCheckBox checkField = this.fields[tab][i][col];
/* 251 */               if ((checkField != null) && (checkField.isSelected())) {
/* 252 */                 JOptionPane.showMessageDialog(this, "All ability level upgrades must be removed first", "Ability upgrades", 0);
/*     */ 
/* 254 */                 removeAbility = false;
/* 255 */                 break;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 264 */         if (removeAbility) {
/* 265 */           int count = list.getElementCount();
/* 266 */           for (int i = 0; i < count; i++) {
/* 267 */             DBList fieldList = (DBList)list.getElement(i).getValue();
/* 268 */             String name = fieldList.getString("RnAbName");
/* 269 */             if (abilityLabel.equals(name)) {
/* 270 */               list.removeElement(i);
/* 271 */               Main.dataModified = true;
/* 272 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 276 */           for (int i = 0; i < associatedLabels.length; i++) {
/* 277 */             if (abilityLabel.equals(associatedLabels[i][0])) {
/* 278 */               String associatedLabel = associatedLabels[i][1];
/* 279 */               count = list.getElementCount();
/* 280 */               for (int j = 0; j < count; j++) {
/* 281 */                 DBList fieldList = (DBList)list.getElement(j).getValue();
/* 282 */                 String name = fieldList.getString("RnAbName");
/* 283 */                 if (name.equals(associatedLabel)) {
/* 284 */                   list.removeElement(j);
/* 285 */                   Main.dataModified = true;
/* 286 */                   break;
/*     */                 }
/*     */               }
/*     */ 
/* 290 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 294 */           if ((row == 0) && (this.levels[tab] == col))
/* 295 */             this.levels[tab] = (col - 1);
/*     */         } else {
/* 297 */           Main.dataChanging = true;
/* 298 */           field.setSelected(true);
/* 299 */           Main.dataChanging = false;
/*     */         }
/*     */       }
/*     */     } catch (DBException exc) {
/* 303 */       Main.logException("Unable to update database field", exc);
/*     */     } catch (Throwable exc) {
/* 305 */       Main.logException("Exception while processing action event", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFields(DBList list)
/*     */     throws DBException
/*     */   {
/* 320 */     for (int tab = 0; tab < this.fields.length; tab++) {
/* 321 */       for (int row = 0; row < this.fields[0].length; row++) {
/* 322 */         for (int col = 0; col < this.fields[0][0].length; col++) {
/* 323 */           if (this.fields[tab][row][col] != null) {
/* 324 */             this.fields[tab][row][col].setSelected(false);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 329 */       this.levels[tab] = -1;
/*     */     }
/*     */ 
/* 335 */     DBElement element = list.getElement("CharAbilities");
/* 336 */     if (element == null) {
/* 337 */       throw new DBException("CharAbilities field not found");
/*     */     }
/* 339 */     DBList abilityList = (DBList)element.getValue();
/* 340 */     int count = abilityList.getElementCount();
/* 341 */     for (int index = 0; index < count; index++) {
/* 342 */       DBList fieldList = (DBList)abilityList.getElement(index).getValue();
/* 343 */       String abilityName = fieldList.getString("RnAbName");
/* 344 */       JCheckBox field = (JCheckBox)this.labelMap.get(abilityName);
/* 345 */       if (field != null) {
/* 346 */         field.setSelected(true);
/* 347 */         int value = Integer.parseInt(field.getActionCommand());
/* 348 */         int tab = value / 100;
/* 349 */         int row = value % 100 / 10;
/* 350 */         int col = value % 10;
/* 351 */         if ((row == 0) && (col > this.levels[tab]))
/* 352 */           this.levels[tab] = col;
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
 * Qualified Name:     TWEditor.AttributesPanel
 * JD-Core Version:    0.6.2
 */