/*    */ package TWEditor;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public class AlchemyIngredient
/*    */ {
/*    */   private int id;
/*    */   private List<String> substances;
/*    */ 
/*    */   public AlchemyIngredient(int id, List<String> substances)
/*    */   {
/* 23 */     this.id = id;
/* 24 */     this.substances = substances;
/*    */   }
/*    */ 
/*    */   public int getID()
/*    */   {
/* 33 */     return this.id;
/*    */   }
/*    */ 
/*    */   public List<String> getSubstances()
/*    */   {
/* 42 */     return this.substances;
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.AlchemyIngredient
 * JD-Core Version:    0.6.2
 */