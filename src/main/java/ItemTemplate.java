/*     */ package TWEditor;
/*     */ 
/*     */ public class ItemTemplate
/*     */   implements Comparable<ItemTemplate>
/*     */ {
/*     */   private DBList fieldList;
/*     */   private String itemName;
/*     */   private String resourceName;
/*     */   private int baseItem;
/*     */ 
/*     */   public ItemTemplate(DBList fieldList)
/*     */     throws DBException
/*     */   {
/*  30 */     this.fieldList = fieldList;
/*  31 */     setupTemplate();
/*     */   }
/*     */ 
/*     */   private void setupTemplate()
/*     */     throws DBException
/*     */   {
/*  44 */     this.baseItem = this.fieldList.getInteger("BaseItem");
/*     */ 
/*  49 */     this.itemName = this.fieldList.getString("LocalizedName");
/*     */ 
/*  54 */     this.resourceName = this.fieldList.getString("TemplateResRef");
/*     */   }
/*     */ 
/*     */   public String getItemName()
/*     */   {
/*  63 */     return this.itemName;
/*     */   }
/*     */ 
/*     */   public int getBaseItem()
/*     */   {
/*  72 */     return this.baseItem;
/*     */   }
/*     */ 
/*     */   public DBList getFieldList()
/*     */   {
/*  81 */     return this.fieldList;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  91 */     boolean equal = false;
/*  92 */     if ((obj != null) && ((obj instanceof ItemTemplate)) && 
/*  93 */       (((ItemTemplate)obj).getItemName().equals(this.itemName))) {
/*  94 */       equal = true;
/*     */     }
/*  96 */     return equal;
/*     */   }
/*     */ 
/*     */   public int compareTo(ItemTemplate obj)
/*     */   {
/* 106 */     return this.itemName.compareTo(obj.getItemName());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 115 */     return this.itemName + " (" + this.resourceName + ")";
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.ItemTemplate
 * JD-Core Version:    0.6.2
 */