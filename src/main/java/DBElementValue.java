/*    */ package TWEditor;
/*    */ 
/*    */ public abstract class DBElementValue
/*    */   implements Cloneable
/*    */ {
/*    */   public Object clone()
/*    */   {
/*    */     Object clonedObject;
/*    */     try
/*    */     {
/* 22 */       clonedObject = super.clone();
/*    */     } catch (CloneNotSupportedException exc) {
/* 24 */       throw new UnsupportedOperationException("Unable to clone database element value", exc);
/*    */     }
/*    */ 
/* 27 */     return clonedObject;
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.DBElementValue
 * JD-Core Version:    0.6.2
 */