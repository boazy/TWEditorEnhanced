package TWEditor;

import javax.swing.tree.DefaultMutableTreeNode;

public class CategoryNode extends DefaultMutableTreeNode
{
  public CategoryNode(String category)
  {
    super(category);
  }

  public int insert(InventoryNode childNode)
  {
    int count = getChildCount();
    String itemName = ((ItemTemplate)childNode.getUserObject()).getItemName();
    int index;
    for (index = 0; index < count; index++) {
      InventoryNode node = (InventoryNode)getChildAt(index);
      ItemTemplate item = (ItemTemplate)node.getUserObject();
      int diff = itemName.compareTo(item.getItemName());
      if (diff < 0) {
        insert(childNode, index);
        break;
      }
    }

    if (index == count) {
      add(childNode);
    }
    return index;
  }
}

