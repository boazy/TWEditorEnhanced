package TWEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class InventoryPanel extends JPanel
  implements ActionListener
{
  private static final String[] categories = { "Bomb", "Book", "Drink", "Food", "Gem", "Grease", "Ingredient", "Jewelry", "Magical", "Potion", "Quest", "Upgrade", "Other" };
  private static final int TAB_BOMB = 0;
  private static final int TAB_BOOK = 1;
  private static final int TAB_DRINK = 2;
  private static final int TAB_FOOD = 3;
  private static final int TAB_GEM = 4;
  private static final int TAB_GREASE = 5;
  private static final int TAB_INGREDIENT = 6;
  private static final int TAB_JEWELRY = 7;
  private static final int TAB_MAGICAL = 8;
  private static final int TAB_POTION = 9;
  private static final int TAB_QUEST = 10;
  private static final int TAB_UPGRADE = 11;
  private static final int TAB_OTHER = 12;
  private static final int[][] categoryMappings = { { 20, 7 }, { 21, 8 }, { 22, 9 }, { 23, 7 }, { 30, 1 }, { 32, 4 }, { 33, 6 }, { 34, 11 }, { 37, 8 }, { 38, 7 }, { 40, 10 }, { 44, 3 }, { 45, 12 }, { 46, 5 }, { 47, 0 }, { 48, 2 } };

  private static final String[] substanceNames = { "Vitriol", "Rebis", "Aether", "Quebirth", "Hydragenum", "Vermilion", "Albedo", "Nigredo", "Rubedo" };
  private DefaultListModel itemsModel;
  private JList itemsField;
  private DefaultMutableTreeNode rootNode;
  private CategoryNode[] categoryNodes;
  private DefaultTreeModel availModel;
  private JTree availField;
  private boolean availDone = false;
  private List<AlchemyIngredient> ingredients;
  private Map<Integer, AlchemyIngredient> ingredientsMap;
  private boolean[][] slots;

  public InventoryPanel()
  {
    this.slots = new boolean[6][14];

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

    int alchemyID = fieldList.getInteger("AlchIngredient");
    if (alchemyID > 0) {
      AlchemyIngredient ingredient = (AlchemyIngredient)this.ingredientsMap.get(new Integer(alchemyID));
      if (ingredient != null) {
        description.append("<br><ul>");
        List substances = ingredient.getSubstances();
        for (Object substance : substances) {
          description.append("<li>");
          description.append((String)substance);
        }

        description.append("</ul>");
      }

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
    DBList itemList = (DBList)list.getElement("ItemList").getValue();
    int itemCount = itemList.getElementCount();
    for (int i = 0; i < itemCount; i++) {
      if (itemList.getElement(i) == itemElement) {
        itemList.removeElement(i);
        DBList fieldList = (DBList)itemElement.getValue();
        int x = fieldList.getInteger("Repos_PosX");
        int y = fieldList.getInteger("Repos_PosY");
        int questItem = fieldList.getInteger("QuestItem");
        if ((questItem != 0) || (x < 0) || (x >= 14) || (y < 0) || (y >= 6)) break;
        this.slots[y][x] = false;
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
    int questItem = templateList.getInteger("QuestItem");
    int alchemyIngredient = templateList.getInteger("AlchIngredient");

    int x = 0; int y = 0;
    if (questItem == 0) {
      boolean foundSlot = false;
      // Normal inventory item slots (3 rows)
      if (alchemyIngredient == 0) {
        for (y = 0; y < 3; y++) {
          for (x = 0; x < 14; x++) {
            if (this.slots[y][x] == false) {
              foundSlot = true;
              break;
            }
          }

          if (foundSlot)
            break;
        }
      } else {
        // Alchemy ingredients slots (3 rows)
        for (y = 3; y < 6; y++) {
          for (x = 0; x < 14; x++) {
            if (this.slots[y][x] == false) {
              foundSlot = true;
              break;
            }
          }

          if (foundSlot) {
            break;
          }
        }
      }
      if (!foundSlot) {
        JOptionPane.showMessageDialog(this, "No inventory slot available", "Inventory is full", 0);

        return;
      }

    }

    int stackSize = Math.max(templateList.getInteger("MaxStack"), 1);

    DBList fieldList = (DBList)templateList.clone();
    fieldList.setInteger("Dropable", 1, 0);
    fieldList.setInteger("Identified", 1, 0);
    fieldList.setInteger("StackSize", stackSize, 2);
    fieldList.setInteger("Repos_PosX", x, 2);
    fieldList.setInteger("Repos_PosY", y, 2);

    if (questItem == 0) {
      this.slots[y][x] = true;
    }

    DBList list = (DBList)Main.database.getTopLevelStruct().getValue();
    list = (DBList)list.getElement("Mod_PlayerList").getValue();
    list = (DBList)list.getElement(0).getValue();

    DBElement element = list.getElement("ItemList");
    DBList itemList;
    if (element == null) {
      itemList = new DBList(10);
      element = new DBElement(15, 0, "ItemList", itemList);
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

    if (this.ingredients == null) {
      Object resource = Main.resourceFiles.get("alchemy_ingre.2da");
      if (resource == null) {
        throw new IOException("alchemy_ingre.2da not found");
      }
      InputStream in = null;
      if ((resource instanceof File))
        in = new FileInputStream((File)resource);
      else if ((resource instanceof KeyEntry)) {
        in = ((KeyEntry)resource).getInputStream();
      }

      if (in == null) {
        throw new IOException("alchemy_ingre.2da not found");
      }
      TextDatabase textDatabase = new TextDatabase(in);
      int count = textDatabase.getResourceCount();
      this.ingredients = new ArrayList(count);
      this.ingredientsMap = new HashMap(count);
      for (int i = 0; i < count; i++) {
        String name = textDatabase.getString(i, "NameRef");
        if (name.length() > 0) {
          List substances = new ArrayList(4);
          for (int j = 0; j < substanceNames.length; j++) {
            if (textDatabase.getInteger(i, substanceNames[j]) == 1) {
              substances.add(substanceNames[j]);
            }
          }
          AlchemyIngredient ingredient = new AlchemyIngredient(i, substances);
          this.ingredients.add(ingredient);
          this.ingredientsMap.put(new Integer(ingredient.getID()), ingredient);
        }

      }

    }

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

    DBElement element = list.getElement("ItemList");
    if ((element != null) && (element.getType() == 15)) {
      itemList = (DBList)element.getValue();
      itemCount = itemList.getElementCount();
    }

    this.itemsModel = new DefaultListModel();
    if (itemCount != 0) {
      this.itemsModel.ensureCapacity(itemCount);
    }

    for (int y = 0; y < 6; y++) {
      for (int x = 0; x < 14; x++) {
        this.slots[y][x] = false;
      }

    }

    for (int i = 0; i < itemCount; i++) {
      DBElement itemElement = itemList.getElement(i);
      DBList itemFields = (DBList)itemElement.getValue();
      String itemName = itemFields.getString("LocalizedName");
      if (itemName.length() > 0) {
        int questItem = itemFields.getInteger("QuestItem");
        int x = itemFields.getInteger("Repos_PosX");
        int y = itemFields.getInteger("Repos_PosY");
        InventoryItem item = new InventoryItem(itemName, itemElement);
        insertItem(this.itemsModel, item);
        if ((questItem == 0) && (x >= 0) && (x < 14) && (y >= 0) && (y < 6)) {
          this.slots[y][x] = true;
        }

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

