/*    */ package TWEditor;
/*    */ 
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ 
/*    */ public class CategoryNode extends DefaultMutableTreeNode
/*    */ {
/*    */   public CategoryNode(String category)
/*    */   {
/* 20 */     super(category);
/*    */   }
/*    */ 
/*    */   public int insert(InventoryNode childNode)
/*    */   {
/* 33 */     int count = getChildCount();
/* 34 */     String itemName = ((ItemTemplate)childNode.getUserObject()).getItemName();
/* 35 */     int index;
             for (index = 0; index < count; index++) {
/* 36 */       InventoryNode node = (InventoryNode)getChildAt(index);
/* 37 */       ItemTemplate item = (ItemTemplate)node.getUserObject();
/* 38 */       int diff = itemName.compareTo(item.getItemName());
/* 39 */       if (diff < 0) {
/* 40 */         insert(childNode, index);
/* 41 */         break;
/*    */       }
/*    */     }
/*    */ 
/* 45 */     if (index == count) {
/* 46 */       add(childNode);
/*    */     }
/* 48 */     return index;
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.CategoryNode
 * JD-Core Version:    0.6.2
 */