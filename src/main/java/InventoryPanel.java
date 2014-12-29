/*     */ package TWEditor;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.DefaultTreeSelectionModel;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class InventoryPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*  24 */   private static final String[] categories = { "Bomb", "Book", "Drink", "Food", "Gem", "Grease", "Ingredient", "Jewelry", "Magical", "Potion", "Quest", "Upgrade", "Other" };
/*     */   private static final int TAB_BOMB = 0;
/*     */   private static final int TAB_BOOK = 1;
/*     */   private static final int TAB_DRINK = 2;
/*     */   private static final int TAB_FOOD = 3;
/*     */   private static final int TAB_GEM = 4;
/*     */   private static final int TAB_GREASE = 5;
/*     */   private static final int TAB_INGREDIENT = 6;
/*     */   private static final int TAB_JEWELRY = 7;
/*     */   private static final int TAB_MAGICAL = 8;
/*     */   private static final int TAB_POTION = 9;
/*     */   private static final int TAB_QUEST = 10;
/*     */   private static final int TAB_UPGRADE = 11;
/*     */   private static final int TAB_OTHER = 12;
/*  46 */   private static final int[][] categoryMappings = { { 20, 7 }, { 21, 8 }, { 22, 9 }, { 23, 7 }, { 30, 1 }, { 32, 4 }, { 33, 6 }, { 34, 11 }, { 37, 8 }, { 38, 7 }, { 40, 10 }, { 44, 3 }, { 45, 12 }, { 46, 5 }, { 47, 0 }, { 48, 2 } };
/*     */ 
/*  66 */   private static final String[] substanceNames = { "Vitriol", "Rebis", "Aether", "Quebirth", "Hydragenum", "Vermilion", "Albedo", "Nigredo", "Rubedo" };
/*     */   private DefaultListModel itemsModel;
/*     */   private JList itemsField;
/*     */   private DefaultMutableTreeNode rootNode;
/*     */   private CategoryNode[] categoryNodes;
/*     */   private DefaultTreeModel availModel;
/*     */   private JTree availField;
/*  90 */   private boolean availDone = false;
/*     */   private List<AlchemyIngredient> ingredients;
/*     */   private Map<Integer, AlchemyIngredient> ingredientsMap;
/*     */   private boolean[][] slots;
/*     */ 
/*     */   public InventoryPanel()
/*     */   {
/* 115 */     this.slots = new boolean[6][14];
/*     */ 
/* 120 */     this.rootNode = new DefaultMutableTreeNode("Items");
/*     */ 
/* 125 */     this.categoryNodes = new CategoryNode[categories.length];
/* 126 */     for (int i = 0; i < categories.length; i++) {
/* 127 */       CategoryNode node = new CategoryNode(categories[i]);
/* 128 */       this.categoryNodes[i] = node;
/* 129 */       this.rootNode.add(node);
/*     */     }
/*     */ 
/* 135 */     this.itemsModel = new DefaultListModel();
/* 136 */     this.itemsField = new JList(this.itemsModel);
/* 137 */     this.itemsField.setSelectionMode(0);
/* 138 */     this.itemsField.setVisibleRowCount(20);
/* 139 */     this.itemsField.setPrototypeCellValue("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
/* 140 */     JScrollPane scrollPane = new JScrollPane(this.itemsField);
/* 141 */     Dimension preferredSize = scrollPane.getPreferredSize();
/*     */ 
/* 143 */     JPanel buttonPane = new JPanel();
/* 144 */     JButton button = new JButton("Examine Item");
/* 145 */     button.addActionListener(this);
/* 146 */     button.setActionCommand("examine current item");
/* 147 */     buttonPane.add(button);
/*     */ 
/* 149 */     button = new JButton("Remove Item");
/* 150 */     button.addActionListener(this);
/* 151 */     button.setActionCommand("remove current item");
/* 152 */     buttonPane.add(button);
/*     */ 
/* 154 */     JPanel itemsPane = new JPanel(new BorderLayout());
/* 155 */     itemsPane.add(new JLabel("Current Inventory", 0), "North");
/* 156 */     itemsPane.add(scrollPane, "Center");
/* 157 */     itemsPane.add(buttonPane, "South");
/*     */ 
/* 162 */     this.availModel = new DefaultTreeModel(this.rootNode);
/* 163 */     DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
/* 164 */     selectionModel.setSelectionMode(1);
/*     */ 
/* 166 */     this.availField = new JTree(this.availModel);
/* 167 */     this.availField.setSelectionModel(selectionModel);
/* 168 */     scrollPane = new JScrollPane(this.availField);
/* 169 */     scrollPane.setPreferredSize(preferredSize);
/*     */ 
/* 171 */     buttonPane = new JPanel();
/* 172 */     button = new JButton("Examine Item");
/* 173 */     button.addActionListener(this);
/* 174 */     button.setActionCommand("examine available item");
/* 175 */     buttonPane.add(button);
/*     */ 
/* 177 */     button = new JButton("Add Item");
/* 178 */     button.addActionListener(this);
/* 179 */     button.setActionCommand("add available item");
/* 180 */     buttonPane.add(button);
/*     */ 
/* 182 */     JPanel availPane = new JPanel(new BorderLayout());
/* 183 */     availPane.add(new JLabel("Available Items", 0), "North");
/* 184 */     availPane.add(scrollPane, "Center");
/* 185 */     availPane.add(buttonPane, "South");
/* 186 */     availPane.setPreferredSize(itemsPane.getPreferredSize());
/*     */ 
/* 191 */     add(itemsPane);
/* 192 */     add(Box.createHorizontalStrut(15));
/* 193 */     add(availPane);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/*     */     try
/*     */     {
/* 203 */       String action = ae.getActionCommand();
/* 204 */       if (action.equals("examine available item"))
/* 205 */         examineAvailableItem();
/* 206 */       else if (action.equals("examine current item"))
/* 207 */         examineCurrentItem();
/* 208 */       else if (action.equals("add available item"))
/* 209 */         addSelectedItem();
/* 210 */       else if (action.equals("remove current item"))
/* 211 */         removeSelectedItem();
/*     */     }
/*     */     catch (DBException exc) {
/* 214 */       Main.logException("Unable to process database field", exc);
/*     */     } catch (IOException exc) {
/* 216 */       Main.logException("An I/O error occurred", exc);
/*     */     } catch (Throwable exc) {
/* 218 */       Main.logException("Exception while processing action event", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void examineCurrentItem()
/*     */     throws DBException, IOException
/*     */   {
/* 233 */     int sel = this.itemsField.getSelectedIndex();
/* 234 */     if (sel < 0) {
/* 235 */       JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);
/*     */ 
/* 237 */       return;
/*     */     }
/*     */ 
/* 240 */     InventoryItem item = (InventoryItem)this.itemsModel.getElementAt(sel);
/*     */ 
/* 245 */     examineItem(item.getName(), (DBList)item.getElement().getValue());
/*     */   }
/*     */ 
/*     */   private void examineAvailableItem()
/*     */     throws DBException, IOException
/*     */   {
/* 259 */     int count = this.availField.getSelectionCount();
/* 260 */     if (count == 0) {
/* 261 */       JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);
/*     */ 
/* 263 */       return;
/*     */     }
/*     */ 
/* 266 */     TreePath treePath = this.availField.getSelectionPath();
/* 267 */     DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
/* 268 */     Object userObject = node.getUserObject();
/* 269 */     if (!(userObject instanceof ItemTemplate)) {
/* 270 */       JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);
/*     */ 
/* 272 */       return;
/*     */     }
/*     */ 
/* 275 */     ItemTemplate template = (ItemTemplate)userObject;
/*     */ 
/* 280 */     examineItem(template.getItemName(), template.getFieldList());
/*     */   }
/*     */ 
/*     */   private void examineItem(String label, DBList fieldList)
/*     */     throws DBException, IOException
/*     */   {
/* 292 */     StringBuilder description = new StringBuilder(256);
/*     */ 
/* 300 */     String string = fieldList.getString("DescIdentified");
/* 301 */     if (string.length() == 0) {
/* 302 */       string = fieldList.getString("Description");
/*     */     }
/* 304 */     if (string.length() != 0) {
/* 305 */       description.append(string);
/*     */     }
/*     */ 
/* 310 */     int alchemyID = fieldList.getInteger("AlchIngredient");
/* 311 */     if (alchemyID > 0) {
/* 312 */       AlchemyIngredient ingredient = (AlchemyIngredient)this.ingredientsMap.get(new Integer(alchemyID));
/* 313 */       if (ingredient != null) {
/* 314 */         description.append("<br><ul>");
/* 315 */         List substances = ingredient.getSubstances();
/* 316 */         for (Object substance : substances) {
/* 317 */           description.append("<li>");
/* 318 */           description.append((String)substance);
/*     */         }
/*     */ 
/* 321 */         description.append("</ul>");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 328 */     ExamineDialog.showDialog(Main.mainWindow, label, description.toString());
/*     */   }
/*     */ 
/*     */   private void removeSelectedItem()
/*     */     throws DBException
/*     */   {
/* 341 */     int sel = this.itemsField.getSelectedIndex();
/* 342 */     if (sel < 0) {
/* 343 */       JOptionPane.showMessageDialog(this, "You must select an item to remove", "No item selected", 0);
/*     */ 
/* 345 */       return;
/*     */     }
/*     */ 
/* 348 */     InventoryItem item = (InventoryItem)this.itemsModel.getElementAt(sel);
/* 349 */     DBElement itemElement = item.getElement();
/*     */ 
/* 354 */     this.itemsModel.removeElementAt(sel);
/* 355 */     this.itemsField.setSelectedIndex(-1);
/*     */ 
/* 360 */     DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 361 */     list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 362 */     list = (DBList)list.getElement(0).getValue();
/* 363 */     DBList itemList = (DBList)list.getElement("ItemList").getValue();
/* 364 */     int itemCount = itemList.getElementCount();
/* 365 */     for (int i = 0; i < itemCount; i++) {
/* 366 */       if (itemList.getElement(i) == itemElement) {
/* 367 */         itemList.removeElement(i);
/* 368 */         DBList fieldList = (DBList)itemElement.getValue();
/* 369 */         int x = fieldList.getInteger("Repos_PosX");
/* 370 */         int y = fieldList.getInteger("Repos_PosY");
/* 371 */         int questItem = fieldList.getInteger("QuestItem");
/* 372 */         if ((questItem != 0) || (x < 0) || (x >= 14) || (y < 0) || (y >= 6)) break;
/* 373 */         this.slots[y][x] = false;
                  break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 382 */     Main.dataModified = true;
/* 383 */     Main.mainWindow.setTitle(null);
/*     */   }
/*     */ 
/*     */   private void addSelectedItem()
/*     */     throws DBException, IOException
/*     */   {
/* 398 */     int count = this.availField.getSelectionCount();
/* 399 */     if (count == 0) {
/* 400 */       JOptionPane.showMessageDialog(this, "You must select an item to add", "No item selected", 0);
/*     */ 
/* 402 */       return;
/*     */     }
/*     */ 
/* 405 */     TreePath treePath = this.availField.getSelectionPath();
/* 406 */     DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
/* 407 */     Object userObject = node.getUserObject();
/* 408 */     if (!(userObject instanceof ItemTemplate)) {
/* 409 */       JOptionPane.showMessageDialog(this, "You must select an item to add", "No item selected", 0);
/*     */ 
/* 411 */       return;
/*     */     }
/*     */ 
/* 414 */     ItemTemplate template = (ItemTemplate)userObject;
/* 415 */     DBList templateList = template.getFieldList();
/* 416 */     int questItem = templateList.getInteger("QuestItem");
/* 417 */     int alchemyIngredient = templateList.getInteger("AlchIngredient");
/*     */ 
/* 424 */     int x = 0; int y = 0;
/* 425 */     if (questItem == 0) {
/* 426 */       boolean foundSlot = false;
/* 427 */       if (alchemyIngredient == 0) {
/* 428 */         for (y = 0; y < 3; y++) {
/* 429 */           for (x = 0; x < 14; x++) {
/* 430 */             if (this.slots[y][x] == false) {
/* 431 */               foundSlot = true;
/* 432 */               break;
/*     */             }
/*     */           }
/*     */ 
/* 436 */           if (foundSlot)
/*     */             break;
/*     */         }
/*     */       }
/* 440 */       for (y = 3; y < 6; y++) {
/* 441 */         for (x = 0; x < 14; x++) {
/* 442 */           if (this.slots[y][x] == false) {
/* 443 */             foundSlot = true;
/* 444 */             break;
/*     */           }
/*     */         }
/*     */ 
/* 448 */         if (foundSlot)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/* 453 */       if (!foundSlot) {
/* 454 */         JOptionPane.showMessageDialog(this, "No inventory slot available", "Inventory is full", 0);
/*     */ 
/* 456 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 463 */     int stackSize = Math.max(templateList.getInteger("MaxStack"), 1);
/*     */ 
/* 472 */     DBList fieldList = (DBList)templateList.clone();
/* 473 */     fieldList.setInteger("Dropable", 1, 0);
/* 474 */     fieldList.setInteger("Identified", 1, 0);
/* 475 */     fieldList.setInteger("StackSize", stackSize, 2);
/* 476 */     fieldList.setInteger("Repos_PosX", x, 2);
/* 477 */     fieldList.setInteger("Repos_PosY", y, 2);
/*     */ 
/* 479 */     if (questItem == 0) {
/* 480 */       this.slots[y][x] = true;
/*     */     }
/*     */ 
/* 485 */     DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 486 */     list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 487 */     list = (DBList)list.getElement(0).getValue();
/*     */ 
/* 490 */     DBElement element = list.getElement("ItemList");
/*     */     DBList itemList;
/* 491 */     if (element == null) {
/* 492 */       itemList = new DBList(10);
/* 493 */       element = new DBElement(15, 0, "ItemList", itemList);
/* 494 */       list.addElement(element);
/*     */     } else {
/* 496 */       itemList = (DBList)element.getValue();
/*     */     }
/*     */ 
/* 499 */     element = new DBElement(14, 0, "", fieldList);
/* 500 */     itemList.addElement(element);
/*     */ 
/* 505 */     InventoryItem item = new InventoryItem(template.getItemName(), element);
/* 506 */     insertItem(this.itemsModel, item);
/*     */ 
/* 511 */     Main.dataModified = true;
/* 512 */     Main.mainWindow.setTitle(null);
/*     */   }
/*     */ 
/*     */   public void setFields(DBList list)
/*     */     throws DBException, IOException
/*     */   {
/* 523 */     int itemCount = 0;
/* 524 */     DBList itemList = null;
/*     */ 
/* 529 */     if (this.ingredients == null) {
/* 530 */       Object resource = Main.resourceFiles.get("alchemy_ingre.2da");
/* 531 */       if (resource == null) {
/* 532 */         throw new IOException("alchemy_ingre.2da not found");
/*     */       }
/* 534 */       InputStream in = null;
/* 535 */       if ((resource instanceof File))
/* 536 */         in = new FileInputStream((File)resource);
/* 537 */       else if ((resource instanceof KeyEntry)) {
/* 538 */         in = ((KeyEntry)resource).getInputStream();
/*     */       }
/*     */ 
/* 541 */       if (in == null) {
/* 542 */         throw new IOException("alchemy_ingre.2da not found");
/*     */       }
/* 544 */       TextDatabase textDatabase = new TextDatabase(in);
/* 545 */       int count = textDatabase.getResourceCount();
/* 546 */       this.ingredients = new ArrayList(count);
/* 547 */       this.ingredientsMap = new HashMap(count);
/* 548 */       for (int i = 0; i < count; i++) {
/* 549 */         String name = textDatabase.getString(i, "NameRef");
/* 550 */         if (name.length() > 0) {
/* 551 */           List substances = new ArrayList(4);
/* 552 */           for (int j = 0; j < substanceNames.length; j++) {
/* 553 */             if (textDatabase.getInteger(i, substanceNames[j]) == 1) {
/* 554 */               substances.add(substanceNames[j]);
/*     */             }
/*     */           }
/* 557 */           AlchemyIngredient ingredient = new AlchemyIngredient(i, substances);
/* 558 */           this.ingredients.add(ingredient);
/* 559 */           this.ingredientsMap.put(new Integer(ingredient.getID()), ingredient);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 567 */     if (!this.availDone) {
/* 568 */       for (ItemTemplate itemTemplate : Main.itemTemplates) {
/* 569 */         int baseItem = itemTemplate.getBaseItem();
/* 570 */         for (int i = 0; i < categoryMappings.length; i++) {
/* 571 */           if (categoryMappings[i][0] == baseItem) {
/* 572 */             CategoryNode categoryNode = this.categoryNodes[categoryMappings[i][1]];
/* 573 */             InventoryNode inventoryNode = new InventoryNode(itemTemplate);
/* 574 */             categoryNode.insert(inventoryNode);
/* 575 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 580 */       this.availModel.nodeStructureChanged(this.rootNode);
/* 581 */       this.availDone = true;
/*     */     }
/*     */ 
/* 587 */     DBElement element = list.getElement("ItemList");
/* 588 */     if ((element != null) && (element.getType() == 15)) {
/* 589 */       itemList = (DBList)element.getValue();
/* 590 */       itemCount = itemList.getElementCount();
/*     */     }
/*     */ 
/* 596 */     this.itemsModel = new DefaultListModel();
/* 597 */     if (itemCount != 0) {
/* 598 */       this.itemsModel.ensureCapacity(itemCount);
/*     */     }
/*     */ 
/* 603 */     for (int y = 0; y < 6; y++) {
/* 604 */       for (int x = 0; x < 14; x++) {
/* 605 */         this.slots[y][x] = false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 611 */     for (int i = 0; i < itemCount; i++) {
/* 612 */       DBElement itemElement = itemList.getElement(i);
/* 613 */       DBList itemFields = (DBList)itemElement.getValue();
/* 614 */       String itemName = itemFields.getString("LocalizedName");
/* 615 */       if (itemName.length() > 0) {
/* 616 */         int questItem = itemFields.getInteger("QuestItem");
/* 617 */         int x = itemFields.getInteger("Repos_PosX");
/* 618 */         int y = itemFields.getInteger("Repos_PosY");
/* 619 */         InventoryItem item = new InventoryItem(itemName, itemElement);
/* 620 */         insertItem(this.itemsModel, item);
/* 621 */         if ((questItem == 0) && (x >= 0) && (x < 14) && (y >= 0) && (y < 6)) {
/* 622 */           this.slots[y][x] = true;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 629 */     this.itemsField.setModel(this.itemsModel);
/* 630 */     this.itemsField.setSelectedIndex(-1);
/* 631 */     if (this.itemsModel.getSize() > 0)
/* 632 */       this.itemsField.ensureIndexIsVisible(0);
/*     */   }
/*     */ 
/*     */   public void getFields(DBList list)
/*     */     throws DBException
/*     */   {
/*     */   }
/*     */ 
/*     */   private boolean insertItem(DefaultListModel itemModel, InventoryItem item)
/*     */   {
/* 657 */     int listSize = itemModel.getSize();
/* 658 */     boolean inserted = false;
/* 659 */     for (int j = 0; j < listSize; j++) {
/* 660 */       InventoryItem listItem = (InventoryItem)itemModel.getElementAt(j);
/* 661 */       int diff = item.compareTo(listItem);
/* 662 */       if (diff < 0) {
/* 663 */         itemModel.insertElementAt(item, j);
/* 664 */         inserted = true;
/* 665 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 669 */     if (!inserted) {
/* 670 */       itemModel.addElement(item);
/*     */     }
/* 672 */     return true;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.InventoryPanel
 * JD-Core Version:    0.6.2
 */