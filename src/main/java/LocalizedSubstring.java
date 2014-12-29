/*    */ package TWEditor;
/*    */ 
/*    */ public class LocalizedSubstring
/*    */   implements Cloneable
/*    */ {
/*    */   private int language;
/*    */   private int gender;
/*    */   private String string;
/*    */ 
/*    */   public LocalizedSubstring(String string, int language, int gender)
/*    */   {
/* 27 */     this.string = string;
/* 28 */     this.language = language;
/* 29 */     this.gender = gender;
/*    */   }
/*    */ 
/*    */   public String getString()
/*    */   {
/* 38 */     return this.string;
/*    */   }
/*    */ 
/*    */   public void setString(String string)
/*    */   {
/* 47 */     this.string = string;
/*    */   }
/*    */ 
/*    */   public int getLanguage()
/*    */   {
/* 56 */     return this.language;
/*    */   }
/*    */ 
/*    */   public void setLanguage(int language)
/*    */   {
/* 65 */     this.language = language;
/*    */   }
/*    */ 
/*    */   public int getGender()
/*    */   {
/* 74 */     return this.gender;
/*    */   }
/*    */ 
/*    */   public void setGender(int gender)
/*    */   {
/* 83 */     this.gender = gender;
/*    */   }
/*    */ 
/*    */   public Object clone()
/*    */   {
/*    */     Object clonedObject;
/*    */     try
/*    */     {
/* 94 */       clonedObject = super.clone();
/*    */     } catch (CloneNotSupportedException exc) {
/* 96 */       throw new UnsupportedOperationException("Unable to clone localized substring", exc);
/*    */     }
/*    */ 
/* 99 */     return clonedObject;
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.LocalizedSubstring
 * JD-Core Version:    0.6.2
 */