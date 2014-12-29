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
/*     */ public class SignsPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*  19 */   private static final String[] tabNames = { "Aard", "Igni", "Quen", "Axii", "Yrden" };
/*     */ 
/*  22 */   private static final String[][][] fieldNames = { { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Stun", "Disarm", "Blasting Fist", "Extended Duration", "Gale" }, { "", "Gust", "Thunder", "Added Efficiency", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Harm's Way I", "Harm's Way II", "Burning Blade", "Inferno", "Extended Duration" }, { "", "Incineration", "Wall of Fire", "Added Efficiency", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Barrier I", "Barrier II", "Barrier III", "Survival Zone", "Resonance" }, { "", "Extended Duration", "Added Intensity", "Added Efficiency", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Spell", "Hypnosis", "Faze", "Terror", "Ally" }, { "", "Extended Duration I", "Extended Duration II", "Added Efficiency", "" } }, { { "Level 1", "Level 2", "Level 3", "Level 4", "Level 5" }, { "Student", "Apprentice", "Specialist", "Expert", "Master" }, { "Pain Sign", "Prowess", "Stupor Sign", "Blinding Sign", "Circle of Death" }, { "", "Inscriptions", "Crippling Sign", "Added Efficiency", "" } } };
/*     */ 
/*  61 */   private static final String[][][] databaseLabels = { { { "Aard1", "Aard2", "Aard3", "Aard4", "Aard5" }, { "Aard1 Powerup", "Aard2 Powerup", "Aard3 Powerup", "Aard4 Powerup", "Aard5 Powerup" }, { "Aard1 Upgrade1", "Aard2 Upgrade1", "Aard3 Upgrade1", "Aard4 Upgrade1", "Aard5 Upgrade1" }, { "", "Aard2 Upgrade2", "Aard3 Upgrade2", "Aard4 Upgrade2", "" } }, { { "Igni1", "Igni2", "Igni3", "Igni4", "Igni5" }, { "Igni1 Powerup", "Igni2 Powerup", "Igni3 Powerup", "Igni4 Powerup", "Igni5 Powerup" }, { "Igni1 Upgrade1", "Igni2 Upgrade1", "Igni3 Upgrade1", "Igni4 Upgrade1", "Igni5 Upgrade1" }, { "", "Igni2 Upgrade2", "Igni3 Upgrade2", "Igni4 Upgrade2", "" } }, { { "Quen1", "Quen2", "Quen3", "Quen4", "Quen5" }, { "Quen1 Powerup", "Quen2 Powerup", "Quen3 Powerup", "Quen4 Powerup", "Quen5 Powerup" }, { "Quen1 Upgrade1", "Quen2 Upgrade1", "Quen3 Upgrade1", "Quen4 Upgrade1", "Quen5 Upgrade1" }, { "", "Quen2 Upgrade2", "Quen3 Upgrade2", "Quen4 Upgrade2", "" } }, { { "Axi1", "Axi2", "Axi3", "Axi4", "Axi5" }, { "Axi1 Powerup", "Axi2 Powerup", "Axi3 Powerup", "Axi4 Powerup", "Axi5 Powerup" }, { "Axi1 Upgrade1", "Axi2 Upgrade1", "Axi3 Upgrade1", "Axi4 Upgrade1", "Axi5 Upgrade1" }, { "", "Axi2 Upgrade2", "Axi3 Upgrade2", "Axi4 Upgrade2", "" } }, { { "Yrden1", "Yrden2", "Yrden3", "Yrden4", "Yrden5" }, { "Yrden1 Powerup", "Yrden2 Powerup", "Yrden3 Powerup", "Yrden4 Powerup", "Yrden5 Powerup" }, { "Yrden1 Upgrade1", "Yrden2 Upgrade1", "Yrden3 Upgrade1", "Yrden4 Upgrade1", "Yrden5 Upgrade1" }, { "", "Yrden2 Upgrade2", "Yrden3 Upgrade2", "Yrden4 Upgrade2", "" } } };
/*     */ 
/* 100 */   private static final int[] associatedSpells = { 0, 3, 1, 4, 2 };
/*     */   private int[][] signLevels;
/*     */   private Map<String, JCheckBox> labelMap;
/*     */   private JCheckBox[][][] fields;
/*     */   private JTabbedPane tabbedPane;
/*     */ 
/*     */   public SignsPanel()
/*     */   {
/* 129 */     this.tabbedPane = new JTabbedPane();
/* 130 */     int tabs = fieldNames.length;
/* 131 */     int rows = fieldNames[0].length;
/* 132 */     int cols = fieldNames[0][0].length;
/* 133 */     this.fields = new JCheckBox[tabs][rows][cols];
/* 134 */     this.signLevels = new int[tabs][2];
/* 135 */     this.labelMap = new HashMap(tabs * rows * cols);
/* 136 */     for (int tab = 0; tab < tabs; tab++) {
/* 137 */       JPanel panel = new JPanel(new GridLayout(0, cols, 5, 5));
/* 138 */       for (int row = 0; row < rows; row++) {
/* 139 */         for (int col = 0; col < cols; col++) {
/* 140 */           if (fieldNames[tab][row][col].length() > 0) {
/* 141 */             JCheckBox field = new JCheckBox(fieldNames[tab][row][col]);
/* 142 */             field.setActionCommand(Integer.toString(tab * 100 + row * 10 + col));
/* 143 */             field.addActionListener(this);
/* 144 */             this.fields[tab][row][col] = field;
/* 145 */             panel.add(field);
/* 146 */             this.labelMap.put(databaseLabels[tab][row][col], field);
/*     */           } else {
/* 148 */             panel.add(new JLabel());
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 153 */       this.tabbedPane.addTab(tabNames[tab], panel);
/*     */     }
/*     */ 
/* 159 */     add(this.tabbedPane);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/* 172 */     if ((!(ae.getSource() instanceof JCheckBox)) || (Main.dataChanging)) {
/* 173 */       return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 181 */       int value = Integer.parseInt(ae.getActionCommand());
/* 182 */       int tab = value / 100;
/* 183 */       int row = value % 100 / 10;
/* 184 */       int col = value % 10;
/* 185 */       JCheckBox field = this.fields[tab][row][col];
/* 186 */       String abilityLabel = databaseLabels[tab][row][col];
/*     */ 
/* 191 */       DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 192 */       list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 193 */       DBList playerList = (DBList)list.getElement(0).getValue();
/* 194 */       list = (DBList)playerList.getElement("CharAbilities").getValue();
/*     */ 
/* 196 */       if (field.isSelected()) {
/* 197 */         boolean addSign = true;
/*     */ 
/* 203 */         if (row == 0) {
/* 204 */           if (col > this.signLevels[tab][0] + 1) {
/* 205 */             JOptionPane.showMessageDialog(this, "Lower sign level must be obtained first", "Missing level", 0);
/*     */ 
/* 207 */             addSign = false;
/*     */           }
/* 209 */         } else if (row == 1) {
/* 210 */           if (col > this.signLevels[tab][1] + 1) {
/* 211 */             JOptionPane.showMessageDialog(this, "Lower sign level powerup must be obtained first", "Missing powerup", 0);
/*     */ 
/* 213 */             addSign = false;
/*     */           }
/*     */         }
/* 216 */         else if (col > this.signLevels[tab][0]) {
/* 217 */           JOptionPane.showMessageDialog(this, "The sign level must be obtained first", "Missing level", 0);
/*     */ 
/* 219 */           addSign = false;
/*     */         }
/*     */ 
/* 226 */         if (addSign) {
/* 227 */           DBList fieldList = new DBList(2);
/* 228 */           fieldList.addElement(new DBElement(10, 0, "RnAbName", abilityLabel));
/* 229 */           fieldList.addElement(new DBElement(0, 0, "RnAbStk", new Integer(0)));
/* 230 */           list.addElement(new DBElement(14, 48879, "", fieldList));
/*     */ 
/* 235 */           if ((row < 2) && (col > this.signLevels[tab][row])) {
/* 236 */             boolean updatedSpell = false;
/* 237 */             int low = associatedSpells[tab] * 10;
/* 238 */             int high = associatedSpells[tab] * 10 + 9;
/* 239 */             list = null;
/* 240 */             DBElement element = playerList.getElement("KnownList0");
/* 241 */             if (element != null) {
/* 242 */               list = (DBList)element.getValue();
/* 243 */               int count = list.getElementCount();
/* 244 */               for (int i = 0; i < count; i++) {
/* 245 */                 fieldList = (DBList)list.getElement(i).getValue();
/* 246 */                 int spell = fieldList.getInteger("Spell");
/* 247 */                 if ((spell >= low) && (spell <= high) && ((spell & 0x1) == row)) {
/* 248 */                   fieldList.setInteger("Spell", low + 2 * col + row);
/* 249 */                   updatedSpell = true;
/* 250 */                   break;
/*     */                 }
/*     */               }
/*     */             }
/*     */ 
/* 255 */             if (!updatedSpell) {
/* 256 */               if (list == null) {
/* 257 */                 list = new DBList(1);
/* 258 */                 playerList.addElement(new DBElement(15, 0, "KnownList0", list));
/*     */               }
/*     */ 
/* 261 */               fieldList = new DBList(1);
/* 262 */               element = new DBElement(2, 0, "Spell", new Integer(low + 2 * col + row));
/* 263 */               fieldList.addElement(element);
/* 264 */               list.addElement(new DBElement(14, 2, "", fieldList));
/*     */             }
/*     */ 
/* 267 */             this.signLevels[tab][row] = col;
/*     */           }
/*     */ 
/* 270 */           Main.dataModified = true;
/*     */         } else {
/* 272 */           Main.dataChanging = true;
/* 273 */           field.setSelected(false);
/* 274 */           Main.dataChanging = false;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 279 */         boolean removeSign = true;
/*     */ 
/* 284 */         if (row == 0) {
/* 285 */           if (col < this.signLevels[tab][0]) {
/* 286 */             JOptionPane.showMessageDialog(this, "All higher sign levels must be removed first", "Higher level", 0);
/*     */ 
/* 288 */             removeSign = false;
/*     */           } else {
/* 290 */             for (int i = 1; i < this.fields[0].length; i++) {
/* 291 */               JCheckBox checkField = this.fields[tab][i][col];
/* 292 */               if ((checkField != null) && (checkField.isSelected())) {
/* 293 */                 JOptionPane.showMessageDialog(this, "All sign level upgrades must be removed first", "Sign upgrades", 0);
/*     */ 
/* 295 */                 removeSign = false;
/* 296 */                 break;
/*     */               }
/*     */             }
/*     */           }
/* 300 */         } else if ((row == 1) && 
/* 301 */           (col < this.signLevels[tab][1])) {
/* 302 */           JOptionPane.showMessageDialog(this, "All higher sign powerups must be removed first", "Higher powerup", 0);
/*     */ 
/* 304 */           removeSign = false;
/*     */         }
/*     */ 
/* 311 */         if (removeSign) {
/* 312 */           int count = list.getElementCount();
/* 313 */           for (int i = 0; i < count; i++) {
/* 314 */             DBList fieldList = (DBList)list.getElement(i).getValue();
/* 315 */             String name = fieldList.getString("RnAbName");
/* 316 */             if (abilityLabel.equals(name)) {
/* 317 */               list.removeElement(i);
/* 318 */               Main.dataModified = true;
/* 319 */               break;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 326 */           if ((row < 2) && (col == this.signLevels[tab][row])) {
/* 327 */             int low = associatedSpells[tab] * 10;
/* 328 */             int high = associatedSpells[tab] * 10 + 9;
/* 329 */             DBElement element = playerList.getElement("KnownList0");
/* 330 */             if (element != null) {
/* 331 */               list = (DBList)element.getValue();
/* 332 */               count = list.getElementCount();
/* 333 */               for (int i = 0; i < count; i++) {
/* 334 */                 DBList fieldList = (DBList)list.getElement(i).getValue();
/* 335 */                 int spell = fieldList.getInteger("Spell");
/* 336 */                 if ((spell >= low) && (spell <= high) && ((spell & 0x1) == row)) {
/* 337 */                   if (col == 0) {
/* 338 */                     list.removeElement(i); break;
/*     */                   }
/* 340 */                   fieldList.setInteger("Spell", spell - 2);
/*     */ 
/* 342 */                   break;
/*     */                 }
/*     */               }
/*     */             }
/*     */ 
/* 347 */             this.signLevels[tab][row] = (col - 1);
/* 348 */             Main.dataModified = true;
/*     */           }
/*     */         } else {
/* 351 */           Main.dataChanging = true;
/* 352 */           field.setSelected(true);
/* 353 */           Main.dataChanging = false;
/*     */         }
/*     */       }
/*     */     } catch (DBException exc) {
/* 357 */       Main.logException("Unable to update database field", exc);
/*     */     } catch (Throwable exc) {
/* 359 */       Main.logException("Exception while processing action event", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFields(DBList list)
/*     */     throws DBException
/*     */   {
/* 374 */     for (int tab = 0; tab < this.fields.length; tab++) {
/* 375 */       for (int row = 0; row < this.fields[0].length; row++) {
/* 376 */         for (int col = 0; col < this.fields[0][0].length; col++) {
/* 377 */           if (this.fields[tab][row][col] != null) {
/* 378 */             this.fields[tab][row][col].setSelected(false);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 383 */       this.signLevels[tab][0] = -1;
/* 384 */       this.signLevels[tab][1] = -1;
/*     */     }
/*     */ 
/* 390 */     DBElement element = list.getElement("CharAbilities");
/* 391 */     if (element == null) {
/* 392 */       throw new DBException("CharAbilities field not found");
/*     */     }
/* 394 */     DBList abilityList = (DBList)element.getValue();
/* 395 */     int count = abilityList.getElementCount();
/* 396 */     for (int index = 0; index < count; index++) {
/* 397 */       DBList fieldList = (DBList)abilityList.getElement(index).getValue();
/* 398 */       String abilityName = fieldList.getString("RnAbName");
/* 399 */       JCheckBox field = (JCheckBox)this.labelMap.get(abilityName);
/* 400 */       if (field != null) {
/* 401 */         field.setSelected(true);
/* 402 */         int value = Integer.parseInt(field.getActionCommand());
/* 403 */         int tab = value / 100;
/* 404 */         int row = value % 100 / 10;
/* 405 */         int col = value % 10;
/* 406 */         if ((row < 2) && (col > this.signLevels[tab][row]))
/* 407 */           this.signLevels[tab][row] = col;
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
 * Qualified Name:     TWEditor.SignsPanel
 * JD-Core Version:    0.6.2
 */