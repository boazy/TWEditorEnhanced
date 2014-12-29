/*     */ package TWEditor;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.IOException;
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
/*     */ public class EquipPanel extends JPanel
/*     */   implements ActionListener
/*     */ {
/*  24 */   private static final String[] categories = { "Armor", "Silver Sword", "Steel Sword", "Trophy" };
/*     */   private static final int TAB_ARMOR = 0;
/*     */   private static final int TAB_SILVER_SWORD = 1;
/*     */   private static final int TAB_STEEL_SWORD = 2;
/*     */   private static final int TAB_TROPHY = 3;
/*  35 */   private static final int[][] categoryMappings = { { 1, 2 }, { 2, 1 }, { 29, 0 }, { 39, 3 } };
/*     */   private DefaultListModel itemsModel;
/*     */   private JList itemsField;
/*     */   private DefaultMutableTreeNode rootNode;
/*     */   private CategoryNode[] categoryNodes;
/*     */   private DefaultTreeModel availModel;
/*     */   private JTree availField;
/*  61 */   private boolean availDone = false;
/*     */ 
/*     */   public EquipPanel()
/*     */   {
/*  76 */     this.rootNode = new DefaultMutableTreeNode("Items");
/*     */ 
/*  81 */     this.categoryNodes = new CategoryNode[categories.length];
/*  82 */     for (int i = 0; i < categories.length; i++) {
/*  83 */       CategoryNode node = new CategoryNode(categories[i]);
/*  84 */       this.categoryNodes[i] = node;
/*  85 */       this.rootNode.add(node);
/*     */     }
/*     */ 
/*  91 */     this.itemsModel = new DefaultListModel();
/*  92 */     this.itemsField = new JList(this.itemsModel);
/*  93 */     this.itemsField.setSelectionMode(0);
/*  94 */     this.itemsField.setVisibleRowCount(20);
/*  95 */     this.itemsField.setPrototypeCellValue("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
/*  96 */     JScrollPane scrollPane = new JScrollPane(this.itemsField);
/*  97 */     Dimension preferredSize = scrollPane.getPreferredSize();
/*     */ 
/*  99 */     JPanel buttonPane = new JPanel();
/* 100 */     JButton button = new JButton("Examine Item");
/* 101 */     button.addActionListener(this);
/* 102 */     button.setActionCommand("examine current item");
/* 103 */     buttonPane.add(button);
/*     */ 
/* 105 */     button = new JButton("Remove Item");
/* 106 */     button.addActionListener(this);
/* 107 */     button.setActionCommand("remove current item");
/* 108 */     buttonPane.add(button);
/*     */ 
/* 110 */     JPanel itemsPane = new JPanel(new BorderLayout());
/* 111 */     itemsPane.add(new JLabel("Current Inventory", 0), "North");
/* 112 */     itemsPane.add(scrollPane, "Center");
/* 113 */     itemsPane.add(buttonPane, "South");
/*     */ 
/* 118 */     this.availModel = new DefaultTreeModel(this.rootNode);
/* 119 */     DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
/* 120 */     selectionModel.setSelectionMode(1);
/*     */ 
/* 122 */     this.availField = new JTree(this.availModel);
/* 123 */     this.availField.setSelectionModel(selectionModel);
/* 124 */     scrollPane = new JScrollPane(this.availField);
/* 125 */     scrollPane.setPreferredSize(preferredSize);
/*     */ 
/* 127 */     buttonPane = new JPanel();
/* 128 */     button = new JButton("Examine Item");
/* 129 */     button.addActionListener(this);
/* 130 */     button.setActionCommand("examine available item");
/* 131 */     buttonPane.add(button);
/*     */ 
/* 133 */     button = new JButton("Add Item");
/* 134 */     button.addActionListener(this);
/* 135 */     button.setActionCommand("add available item");
/* 136 */     buttonPane.add(button);
/*     */ 
/* 138 */     JPanel availPane = new JPanel(new BorderLayout());
/* 139 */     availPane.add(new JLabel("Available Items", 0), "North");
/* 140 */     availPane.add(scrollPane, "Center");
/* 141 */     availPane.add(buttonPane, "South");
/* 142 */     availPane.setPreferredSize(itemsPane.getPreferredSize());
/*     */ 
/* 147 */     add(itemsPane);
/* 148 */     add(Box.createHorizontalStrut(15));
/* 149 */     add(availPane);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/*     */     try
/*     */     {
/* 159 */       String action = ae.getActionCommand();
/* 160 */       if (action.equals("examine available item"))
/* 161 */         examineAvailableItem();
/* 162 */       else if (action.equals("examine current item"))
/* 163 */         examineCurrentItem();
/* 164 */       else if (action.equals("add available item"))
/* 165 */         addSelectedItem();
/* 166 */       else if (action.equals("remove current item"))
/* 167 */         removeSelectedItem();
/*     */     }
/*     */     catch (DBException exc) {
/* 170 */       Main.logException("Unable to process database field", exc);
/*     */     } catch (IOException exc) {
/* 172 */       Main.logException("An I/O error occurred", exc);
/*     */     } catch (Throwable exc) {
/* 174 */       Main.logException("Exception while processing action event", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void examineCurrentItem()
/*     */     throws DBException, IOException
/*     */   {
/* 189 */     int sel = this.itemsField.getSelectedIndex();
/* 190 */     if (sel < 0) {
/* 191 */       JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);
/*     */ 
/* 193 */       return;
/*     */     }
/*     */ 
/* 196 */     InventoryItem item = (InventoryItem)this.itemsModel.getElementAt(sel);
/*     */ 
/* 201 */     examineItem(item.getName(), (DBList)item.getElement().getValue());
/*     */   }
/*     */ 
/*     */   private void examineAvailableItem()
/*     */     throws DBException, IOException
/*     */   {
/* 215 */     int count = this.availField.getSelectionCount();
/* 216 */     if (count == 0) {
/* 217 */       JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);
/*     */ 
/* 219 */       return;
/*     */     }
/*     */ 
/* 222 */     TreePath treePath = this.availField.getSelectionPath();
/* 223 */     DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
/* 224 */     Object userObject = node.getUserObject();
/* 225 */     if (!(userObject instanceof ItemTemplate)) {
/* 226 */       JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);
/*     */ 
/* 228 */       return;
/*     */     }
/*     */ 
/* 231 */     ItemTemplate template = (ItemTemplate)userObject;
/*     */ 
/* 236 */     examineItem(template.getItemName(), template.getFieldList());
/*     */   }
/*     */ 
/*     */   private void examineItem(String label, DBList fieldList)
/*     */     throws DBException, IOException
/*     */   {
/* 248 */     StringBuilder description = new StringBuilder(256);
/*     */ 
/* 256 */     String string = fieldList.getString("DescIdentified");
/* 257 */     if (string.length() == 0) {
/* 258 */       string = fieldList.getString("Description");
/*     */     }
/* 260 */     if (string.length() != 0) {
/* 261 */       description.append(string);
/*     */     }
/*     */ 
/* 266 */     ExamineDialog.showDialog(Main.mainWindow, label, description.toString());
/*     */   }
/*     */ 
/*     */   private void removeSelectedItem()
/*     */     throws DBException
/*     */   {
/* 279 */     int sel = this.itemsField.getSelectedIndex();
/* 280 */     if (sel < 0) {
/* 281 */       JOptionPane.showMessageDialog(this, "You must select an item to remove", "No item selected", 0);
/*     */ 
/* 283 */       return;
/*     */     }
/*     */ 
/* 286 */     InventoryItem item = (InventoryItem)this.itemsModel.getElementAt(sel);
/* 287 */     DBElement itemElement = item.getElement();
/*     */ 
/* 292 */     this.itemsModel.removeElementAt(sel);
/* 293 */     this.itemsField.setSelectedIndex(-1);
/*     */ 
/* 298 */     DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 299 */     list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 300 */     list = (DBList)list.getElement(0).getValue();
/* 301 */     DBList itemList = (DBList)list.getElement("Equip_ItemList").getValue();
/* 302 */     int itemCount = itemList.getElementCount();
/* 303 */     for (int i = 0; i < itemCount; i++) {
/* 304 */       if (itemList.getElement(i) == itemElement) {
/* 305 */         itemList.removeElement(i);
/* 306 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 313 */     Main.dataModified = true;
/* 314 */     Main.mainWindow.setTitle(null);
/*     */   }
/*     */ 
/*     */   private void addSelectedItem()
/*     */     throws DBException, IOException
/*     */   {
/* 331 */     int count = this.availField.getSelectionCount();
/* 332 */     if (count == 0) {
/* 333 */       JOptionPane.showMessageDialog(this, "You must select an item to add", "No item selected", 0);
/*     */ 
/* 335 */       return;
/*     */     }
/*     */ 
/* 338 */     TreePath treePath = this.availField.getSelectionPath();
/* 339 */     DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
/* 340 */     Object userObject = node.getUserObject();
/* 341 */     if (!(userObject instanceof ItemTemplate)) {
/* 342 */       JOptionPane.showMessageDialog(this, "You must select an item to add", "No item selected", 0);
/*     */ 
/* 344 */       return;
/*     */     }
/*     */ 
/* 347 */     ItemTemplate template = (ItemTemplate)userObject;
/* 348 */     DBList templateList = template.getFieldList();
/* 349 */     int weaponSlot = templateList.getInteger("WeaponSlot");
/*     */ 
/* 357 */     DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
/* 358 */     list = (DBList)list.getElement("Mod_PlayerList").getValue();
/* 359 */     list = (DBList)list.getElement(0).getValue();
/* 360 */     DBElement element = list.getElement("Equip_ItemList");
/* 361 */     if (element != null) {
/* 362 */       int swordCount = 0;
/* 363 */       DBList itemList = (DBList)element.getValue();
/* 364 */       int itemCount = itemList.getElementCount();
/* 365 */       for (int i = 0; i < itemCount; i++) {
/* 366 */         DBList itemFields = (DBList)itemList.getElement(i).getValue();
/* 367 */         if (itemFields.getInteger("WeaponSlot") == weaponSlot) {
/* 368 */           if (weaponSlot == 1) {
/* 369 */             swordCount++;
/*     */           }
/* 371 */           if ((weaponSlot != 1) || (swordCount == 2)) {
/* 372 */             JOptionPane.showMessageDialog(this, "No equipment slot available for this item", "No slot", 0);
/*     */ 
/* 374 */             return;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 383 */     int stackSize = Math.max(templateList.getInteger("MaxStack"), 1);
/*     */ 
/* 391 */     DBList fieldList = (DBList)templateList.clone();
/* 392 */     fieldList.setInteger("Dropable", 1, 0);
/* 393 */     fieldList.setInteger("Identified", 1, 0);
/* 394 */     fieldList.setInteger("StackSize", stackSize, 2);
/*     */ 
/* 399 */     element = list.getElement("Equip_ItemList");
/*     */     DBList itemList;
/* 400 */     if (element == null) {
/* 401 */       itemList = new DBList(10);
/* 402 */       element = new DBElement(15, 0, "Equip_ItemList", itemList);
/* 403 */       list.addElement(element);
/*     */     } else {
/* 405 */       itemList = (DBList)element.getValue();
/*     */     }
/*     */ 
/* 408 */     element = new DBElement(14, 0, "", fieldList);
/* 409 */     itemList.addElement(element);
/*     */ 
/* 414 */     InventoryItem item = new InventoryItem(template.getItemName(), element);
/* 415 */     insertItem(this.itemsModel, item);
/*     */ 
/* 420 */     Main.dataModified = true;
/* 421 */     Main.mainWindow.setTitle(null);
/*     */   }
/*     */ 
/*     */   public void setFields(DBList list)
/*     */     throws DBException, IOException
/*     */   {
/* 432 */     int itemCount = 0;
/* 433 */     DBList itemList = null;
/*     */ 
/* 438 */     if (!this.availDone) {
/* 439 */       for (ItemTemplate itemTemplate : Main.itemTemplates) {
/* 440 */         int baseItem = itemTemplate.getBaseItem();
/* 441 */         for (int i = 0; i < categoryMappings.length; i++) {
/* 442 */           if (categoryMappings[i][0] == baseItem) {
/* 443 */             CategoryNode categoryNode = this.categoryNodes[categoryMappings[i][1]];
/* 444 */             InventoryNode inventoryNode = new InventoryNode(itemTemplate);
/* 445 */             categoryNode.insert(inventoryNode);
/* 446 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 451 */       this.availModel.nodeStructureChanged(this.rootNode);
/* 452 */       this.availDone = true;
/*     */     }
/*     */ 
/* 458 */     DBElement element = list.getElement("Equip_ItemList");
/* 459 */     if ((element != null) && (element.getType() == 15)) {
/* 460 */       itemList = (DBList)element.getValue();
/* 461 */       itemCount = itemList.getElementCount();
/*     */     }
/*     */ 
/* 467 */     this.itemsModel = new DefaultListModel();
/* 468 */     if (itemCount != 0) {
/* 469 */       this.itemsModel.ensureCapacity(itemCount);
/*     */     }
/*     */ 
/* 474 */     for (int i = 0; i < itemCount; i++) {
/* 475 */       DBElement itemElement = itemList.getElement(i);
/* 476 */       DBList itemFields = (DBList)itemElement.getValue();
/* 477 */       String itemName = itemFields.getString("LocalizedName");
/* 478 */       if ((itemName.length() > 0) && (itemFields.getInteger("BaseItem") != 36)) {
/* 479 */         InventoryItem item = new InventoryItem(itemName, itemElement);
/* 480 */         insertItem(this.itemsModel, item);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 487 */     this.itemsField.setModel(this.itemsModel);
/* 488 */     this.itemsField.setSelectedIndex(-1);
/* 489 */     if (this.itemsModel.getSize() > 0)
/* 490 */       this.itemsField.ensureIndexIsVisible(0);
/*     */   }
/*     */ 
/*     */   public void getFields(DBList list)
/*     */     throws DBException
/*     */   {
/*     */   }
/*     */ 
/*     */   private boolean insertItem(DefaultListModel itemModel, InventoryItem item)
/*     */   {
/* 515 */     int listSize = itemModel.getSize();
/* 516 */     boolean inserted = false;
/* 517 */     for (int j = 0; j < listSize; j++) {
/* 518 */       InventoryItem listItem = (InventoryItem)itemModel.getElementAt(j);
/* 519 */       int diff = item.compareTo(listItem);
/* 520 */       if (diff < 0) {
/* 521 */         itemModel.insertElementAt(item, j);
/* 522 */         inserted = true;
/* 523 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 527 */     if (!inserted) {
/* 528 */       itemModel.addElement(item);
/*     */     }
/* 530 */     return true;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.EquipPanel
 * JD-Core Version:    0.6.2
 */