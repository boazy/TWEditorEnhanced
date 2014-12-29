package TWEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

public class EquipPanel extends JPanel
  implements ActionListener
{
  private static final String[] categories = { "Armor", "Silver Sword", "Steel Sword", "Trophy" };
  private static final int TAB_ARMOR = 0;
  private static final int TAB_SILVER_SWORD = 1;
  private static final int TAB_STEEL_SWORD = 2;
  private static final int TAB_TROPHY = 3;
  private static final int[][] categoryMappings = { { 1, 2 }, { 2, 1 }, { 29, 0 }, { 39, 3 } };
  private DefaultListModel itemsModel;
  private JList itemsField;
  private DefaultMutableTreeNode rootNode;
  private CategoryNode[] categoryNodes;
  private DefaultTreeModel availModel;
  private JTree availField;
  private boolean availDone = false;

  public EquipPanel()
  {
    this.rootNode = new DefaultMutableTreeNode("Items");

    this.categoryNodes = new CategoryNode[categories.length];
    for (int i = 0; i < categories.length; i++) {
      CategoryNode node = new CategoryNode(categories[i]);
      this.categoryNodes[i] = node;
      this.rootNode.add(node);
    }

    this.itemsModel = new DefaultListModel();
    this.itemsField = new JList(this.itemsModel);
    this.itemsField.setSelectionMode(0);
    this.itemsField.setVisibleRowCount(20);
    this.itemsField.setPrototypeCellValue("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
    JScrollPane scrollPane = new JScrollPane(this.itemsField);
    Dimension preferredSize = scrollPane.getPreferredSize();

    JPanel buttonPane = new JPanel();
    JButton button = new JButton("Examine Item");
    button.addActionListener(this);
    button.setActionCommand("examine current item");
    buttonPane.add(button);

    button = new JButton("Remove Item");
    button.addActionListener(this);
    button.setActionCommand("remove current item");
    buttonPane.add(button);

    JPanel itemsPane = new JPanel(new BorderLayout());
    itemsPane.add(new JLabel("Current Inventory", 0), "North");
    itemsPane.add(scrollPane, "Center");
    itemsPane.add(buttonPane, "South");

    this.availModel = new DefaultTreeModel(this.rootNode);
    DefaultTreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
    selectionModel.setSelectionMode(1);

    this.availField = new JTree(this.availModel);
    this.availField.setSelectionModel(selectionModel);
    scrollPane = new JScrollPane(this.availField);
    scrollPane.setPreferredSize(preferredSize);

    buttonPane = new JPanel();
    button = new JButton("Examine Item");
    button.addActionListener(this);
    button.setActionCommand("examine available item");
    buttonPane.add(button);

    button = new JButton("Add Item");
    button.addActionListener(this);
    button.setActionCommand("add available item");
    buttonPane.add(button);

    JPanel availPane = new JPanel(new BorderLayout());
    availPane.add(new JLabel("Available Items", 0), "North");
    availPane.add(scrollPane, "Center");
    availPane.add(buttonPane, "South");
    availPane.setPreferredSize(itemsPane.getPreferredSize());

    add(itemsPane);
    add(Box.createHorizontalStrut(15));
    add(availPane);
  }

  public void actionPerformed(ActionEvent ae)
  {
    try
    {
      String action = ae.getActionCommand();
      if (action.equals("examine available item"))
        examineAvailableItem();
      else if (action.equals("examine current item"))
        examineCurrentItem();
      else if (action.equals("add available item"))
        addSelectedItem();
      else if (action.equals("remove current item"))
        removeSelectedItem();
    }
    catch (DBException exc) {
      Main.logException("Unable to process database field", exc);
    } catch (IOException exc) {
      Main.logException("An I/O error occurred", exc);
    } catch (Throwable exc) {
      Main.logException("Exception while processing action event", exc);
    }
  }

  private void examineCurrentItem()
    throws DBException, IOException
  {
    int sel = this.itemsField.getSelectedIndex();
    if (sel < 0) {
      JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);

      return;
    }

    InventoryItem item = (InventoryItem)this.itemsModel.getElementAt(sel);

    examineItem(item.getName(), (DBList)item.getElement().getValue());
  }

  private void examineAvailableItem()
    throws DBException, IOException
  {
    int count = this.availField.getSelectionCount();
    if (count == 0) {
      JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);

      return;
    }

    TreePath treePath = this.availField.getSelectionPath();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
    Object userObject = node.getUserObject();
    if (!(userObject instanceof ItemTemplate)) {
      JOptionPane.showMessageDialog(this, "You must select an item to examine", "No item selected", 0);

      return;
    }

    ItemTemplate template = (ItemTemplate)userObject;

    examineItem(template.getItemName(), template.getFieldList());
  }

  private void examineItem(String label, DBList fieldList)
    throws DBException, IOException
  {
    StringBuilder description = new StringBuilder(256);

    String string = fieldList.getString("DescIdentified");
    if (string.length() == 0) {
      string = fieldList.getString("Description");
    }
    if (string.length() != 0) {
      description.append(string);
    }

    ExamineDialog.showDialog(Main.mainWindow, label, description.toString());
  }

  private void removeSelectedItem()
    throws DBException
  {
    int sel = this.itemsField.getSelectedIndex();
    if (sel < 0) {
      JOptionPane.showMessageDialog(this, "You must select an item to remove", "No item selected", 0);

      return;
    }

    InventoryItem item = (InventoryItem)this.itemsModel.getElementAt(sel);
    DBElement itemElement = item.getElement();

    this.itemsModel.removeElementAt(sel);
    this.itemsField.setSelectedIndex(-1);

    DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
    list = (DBList)list.getElement("Mod_PlayerList").getValue();
    list = (DBList)list.getElement(0).getValue();
    DBList itemList = (DBList)list.getElement("Equip_ItemList").getValue();
    int itemCount = itemList.getElementCount();
    for (int i = 0; i < itemCount; i++) {
      if (itemList.getElement(i) == itemElement) {
        itemList.removeElement(i);
        break;
      }

    }

    Main.dataModified = true;
    Main.mainWindow.setTitle(null);
  }

  private void addSelectedItem()
    throws DBException, IOException
  {
    int count = this.availField.getSelectionCount();
    if (count == 0) {
      JOptionPane.showMessageDialog(this, "You must select an item to add", "No item selected", 0);

      return;
    }

    TreePath treePath = this.availField.getSelectionPath();
    DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
    Object userObject = node.getUserObject();
    if (!(userObject instanceof ItemTemplate)) {
      JOptionPane.showMessageDialog(this, "You must select an item to add", "No item selected", 0);

      return;
    }

    ItemTemplate template = (ItemTemplate)userObject;
    DBList templateList = template.getFieldList();
    int weaponSlot = templateList.getInteger("WeaponSlot");

    DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
    list = (DBList)list.getElement("Mod_PlayerList").getValue();
    list = (DBList)list.getElement(0).getValue();
    DBElement element = list.getElement("Equip_ItemList");
    if (element != null) {
      int swordCount = 0;
      DBList itemList = (DBList)element.getValue();
      int itemCount = itemList.getElementCount();
      for (int i = 0; i < itemCount; i++) {
        DBList itemFields = (DBList)itemList.getElement(i).getValue();
        if (itemFields.getInteger("WeaponSlot") == weaponSlot) {
          if (weaponSlot == 1) {
            swordCount++;
          }
          if ((weaponSlot != 1) || (swordCount == 2)) {
            JOptionPane.showMessageDialog(this, "No equipment slot available for this item", "No slot", 0);

            return;
          }

        }

      }

    }

    int stackSize = Math.max(templateList.getInteger("MaxStack"), 1);

    DBList fieldList = (DBList)templateList.clone();
    fieldList.setInteger("Dropable", 1, 0);
    fieldList.setInteger("Identified", 1, 0);
    fieldList.setInteger("StackSize", stackSize, 2);

    element = list.getElement("Equip_ItemList");
    DBList itemList;
    if (element == null) {
      itemList = new DBList(10);
      element = new DBElement(15, 0, "Equip_ItemList", itemList);
      list.addElement(element);
    } else {
      itemList = (DBList)element.getValue();
    }

    element = new DBElement(14, 0, "", fieldList);
    itemList.addElement(element);

    InventoryItem item = new InventoryItem(template.getItemName(), element);
    insertItem(this.itemsModel, item);

    Main.dataModified = true;
    Main.mainWindow.setTitle(null);
  }

  public void setFields(DBList list)
    throws DBException, IOException
  {
    int itemCount = 0;
    DBList itemList = null;

    if (!this.availDone) {
      for (ItemTemplate itemTemplate : Main.itemTemplates) {
        int baseItem = itemTemplate.getBaseItem();
        for (int i = 0; i < categoryMappings.length; i++) {
          if (categoryMappings[i][0] == baseItem) {
            CategoryNode categoryNode = this.categoryNodes[categoryMappings[i][1]];
            InventoryNode inventoryNode = new InventoryNode(itemTemplate);
            categoryNode.insert(inventoryNode);
            break;
          }
        }
      }

      this.availModel.nodeStructureChanged(this.rootNode);
      this.availDone = true;
    }

    DBElement element = list.getElement("Equip_ItemList");
    if ((element != null) && (element.getType() == 15)) {
      itemList = (DBList)element.getValue();
      itemCount = itemList.getElementCount();
    }

    this.itemsModel = new DefaultListModel();
    if (itemCount != 0) {
      this.itemsModel.ensureCapacity(itemCount);
    }

    for (int i = 0; i < itemCount; i++) {
      DBElement itemElement = itemList.getElement(i);
      DBList itemFields = (DBList)itemElement.getValue();
      String itemName = itemFields.getString("LocalizedName");
      if ((itemName.length() > 0) && (itemFields.getInteger("BaseItem") != 36)) {
        InventoryItem item = new InventoryItem(itemName, itemElement);
        insertItem(this.itemsModel, item);
      }

    }

    this.itemsField.setModel(this.itemsModel);
    this.itemsField.setSelectedIndex(-1);
    if (this.itemsModel.getSize() > 0)
      this.itemsField.ensureIndexIsVisible(0);
  }

  public void getFields(DBList list)
    throws DBException
  {
  }

  private boolean insertItem(DefaultListModel itemModel, InventoryItem item)
  {
    int listSize = itemModel.getSize();
    boolean inserted = false;
    for (int j = 0; j < listSize; j++) {
      InventoryItem listItem = (InventoryItem)itemModel.getElementAt(j);
      int diff = item.compareTo(listItem);
      if (diff < 0) {
        itemModel.insertElementAt(item, j);
        inserted = true;
        break;
      }
    }

    if (!inserted) {
      itemModel.addElement(item);
    }
    return true;
  }
}

