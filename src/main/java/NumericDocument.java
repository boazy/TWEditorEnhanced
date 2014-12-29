/*    */ package TWEditor;
/*    */ 
/*    */ import javax.swing.text.AttributeSet;
/*    */ import javax.swing.text.BadLocationException;
/*    */ import javax.swing.text.PlainDocument;
/*    */ 
/*    */ public class NumericDocument extends PlainDocument
/*    */ {
/*    */   public void insertString(int offset, String string, AttributeSet attributes)
/*    */     throws BadLocationException
/*    */   {
/* 28 */     if (string != null) {
/* 29 */       int stringLength = string.length();
/*    */       char initialChar;
/* 31 */       if (getLength() > 0)
/* 32 */         initialChar = getText(0, 1).charAt(0);
/*    */       else {
/* 34 */         initialChar = ' ';
/*    */       }
/* 36 */       if (stringLength == 0) {
/* 37 */         super.insertString(offset, string, attributes);
/* 38 */       } else if (stringLength == 1) {
/* 39 */         char c = string.charAt(0);
/* 40 */         if (Character.isDigit(c)) {
/* 41 */           if ((offset != 0) || (initialChar != '-'))
/* 42 */             super.insertString(offset, string, attributes);
/* 43 */         } else if ((c == '-') && 
/* 44 */           (offset == 0) && (initialChar != '-'))
/* 45 */           super.insertString(offset, string, attributes);
/*    */       }
/*    */       else {
/* 48 */         StringBuilder buffer = new StringBuilder(string);
/* 49 */         int index = 0;
/* 50 */         while (index < stringLength) {
/* 51 */           if ((offset == 0) && (index == 0)) {
/* 52 */             char c = buffer.charAt(0);
/* 53 */             if (Character.isDigit(c)) {
/* 54 */               if (initialChar == '-') {
/* 55 */                 buffer.deleteCharAt(index);
/* 56 */                 stringLength--;
/*    */               } else {
/* 58 */                 index++;
/*    */               }
/* 60 */             } else if (c == '-') {
/* 61 */               if (initialChar != '-') {
/* 62 */                 index++;
/*    */               } else {
/* 64 */                 buffer.deleteCharAt(index);
/* 65 */                 stringLength--;
/*    */               }
/*    */             } else {
/* 68 */               buffer.deleteCharAt(index);
/* 69 */               stringLength--;
/*    */             }
/*    */           }
/* 72 */           else if (Character.isDigit(buffer.charAt(index))) {
/* 73 */             index++;
/*    */           } else {
/* 75 */             buffer.deleteCharAt(index);
/* 76 */             stringLength--;
/*    */           }
/*    */ 
/*    */         }
/*    */ 
/* 81 */         super.insertString(offset, buffer.toString(), attributes);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.NumericDocument
 * JD-Core Version:    0.6.2
 */