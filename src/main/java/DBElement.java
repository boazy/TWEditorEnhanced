/*     */ package TWEditor;
/*     */ 
/*     */ public class DBElement
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int BYTE = 0;
/*     */   public static final int CHAR = 1;
/*     */   public static final int WORD = 2;
/*     */   public static final int SHORT = 3;
/*     */   public static final int DWORD = 4;
/*     */   public static final int INT = 5;
/*     */   public static final int DWORD64 = 6;
/*     */   public static final int INT64 = 7;
/*     */   public static final int FLOAT = 8;
/*     */   public static final int DOUBLE = 9;
/*     */   public static final int STRING = 10;
/*     */   public static final int RESOURCE = 11;
/*     */   public static final int LSTRING = 12;
/*     */   public static final int VOID = 13;
/*     */   public static final int STRUCT = 14;
/*     */   public static final int LIST = 15;
/*     */   private int elementType;
/*     */   private int elementID;
/*     */   private String elementLabel;
/*     */   private Object elementValue;
/*     */ 
/*     */   public DBElement(int type, int id, String label, Object value)
/*     */   {
/*  99 */     this.elementType = type;
/* 100 */     this.elementID = id;
/* 101 */     this.elementValue = value;
/* 102 */     if (label != null)
/* 103 */       this.elementLabel = label;
/*     */     else
/* 105 */       this.elementLabel = new String();
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/* 114 */     return this.elementType;
/*     */   }
/*     */ 
/*     */   public void setType(int type)
/*     */   {
/* 123 */     this.elementType = type;
/*     */   }
/*     */ 
/*     */   public int getID()
/*     */   {
/* 132 */     return this.elementID;
/*     */   }
/*     */ 
/*     */   public void setID(int id)
/*     */   {
/* 141 */     this.elementID = id;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 150 */     return this.elementLabel;
/*     */   }
/*     */ 
/*     */   public void setLabel(String label)
/*     */   {
/* 159 */     this.elementLabel = (label != null ? label : new String());
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/* 168 */     return this.elementValue;
/*     */   }
/*     */ 
/*     */   public void setValue(Object value)
/*     */   {
/* 177 */     if (this.elementValue == null) {
/* 178 */       throw new IllegalArgumentException("No value provided");
/*     */     }
/* 180 */     this.elementValue = value;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     Object clonedObject;
/*     */     try
/*     */     {
/* 191 */       clonedObject = super.clone();
/* 192 */       DBElement clonedElement = (DBElement)clonedObject;
/* 193 */       int type = clonedElement.getType();
/* 194 */       if ((type == 15) || (type == 14) || (type == 12)) {
/* 195 */         DBElementValue elementValue = (DBElementValue)clonedElement.getValue();
/* 196 */         clonedElement.setValue(elementValue.clone());
/*     */       }
/*     */     } catch (CloneNotSupportedException exc) {
/* 199 */       throw new UnsupportedOperationException("Unable to clone database element", exc);
/*     */     }
/*     */ 
/* 202 */     return clonedObject;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.DBElement
 * JD-Core Version:    0.6.2
 */