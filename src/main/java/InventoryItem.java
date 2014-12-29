/*     */ package TWEditor;
/*     */ 
/*     */ public class InventoryItem
/*     */   implements Comparable<InventoryItem>
/*     */ {
/*     */   private DBElement element;
/*     */   private String name;
/*     */   private int count;
/*     */ 
/*     */   public InventoryItem(String name, DBElement element)
/*     */     throws DBException
/*     */   {
/*  27 */     this.name = name;
/*  28 */     this.element = element;
/*  29 */     DBList fieldList = (DBList)element.getValue();
/*  30 */     this.count = fieldList.getInteger("StackSize");
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  39 */     return this.name;
/*     */   }
/*     */ 
/*     */   public DBElement getElement()
/*     */   {
/*  48 */     return this.element;
/*     */   }
/*     */ 
/*     */   public int getCount()
/*     */   {
/*  57 */     return this.count;
/*     */   }
/*     */ 
/*     */   public void setCount(int count)
/*     */   {
/*  66 */     this.count = count;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  76 */     boolean equal = false;
/*  77 */     if ((obj != null) && ((obj instanceof InventoryItem)) && 
/*  78 */       (((InventoryItem)obj).getName().equals(this.name)) && 
/*  79 */       (((InventoryItem)obj).getCount() == this.count)) {
/*  80 */       equal = true;
/*     */     }
/*  82 */     return equal;
/*     */   }
/*     */ 
/*     */   public int compareTo(InventoryItem obj)
/*     */   {
/*  93 */     int diff = this.name.compareTo(obj.getName());
/*  94 */     if (diff == 0) {
/*  95 */       int objCount = obj.getCount();
/*  96 */       if (this.count < objCount)
/*  97 */         diff = -1;
/*  98 */       else if (this.count > objCount)
/*  99 */         diff = 1;
/*     */       else {
/* 101 */         diff = 0;
/*     */       }
/*     */     }
/* 104 */     return diff;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 113 */     return String.format("%s (%d)", new Object[] { this.name, Integer.valueOf(this.count) });
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.InventoryItem
 * JD-Core Version:    0.6.2
 */