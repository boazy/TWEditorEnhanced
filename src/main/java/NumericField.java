/*    */ package TWEditor;
/*    */ 
/*    */ import javax.swing.JTextField;
/*    */ 
/*    */ public class NumericField extends JTextField
/*    */ {
/*    */   public NumericField()
/*    */   {
/* 17 */     this(new String(), 5);
/*    */   }
/*    */ 
/*    */   public NumericField(String string)
/*    */   {
/* 27 */     this(string, Math.max(string.length(), 5));
/*    */   }
/*    */ 
/*    */   public NumericField(int columns)
/*    */   {
/* 37 */     this(new String(), columns);
/*    */   }
/*    */ 
/*    */   public NumericField(String string, int columns)
/*    */   {
/* 48 */     super(new NumericDocument(), string, columns);
/*    */   }
/*    */ 
/*    */   public int getValue()
/*    */   {
/* 58 */     String text = getText();
/* 59 */     return text.length() > 0 ? Integer.parseInt(text) : 0;
/*    */   }
/*    */ 
/*    */   public void setValue(int value)
/*    */   {
/* 68 */     setText(Integer.toString(value));
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.NumericField
 * JD-Core Version:    0.6.2
 */