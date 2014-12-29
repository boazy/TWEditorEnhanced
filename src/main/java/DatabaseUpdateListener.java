/*    */ package TWEditor;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.event.DocumentEvent;
/*    */ import javax.swing.event.DocumentListener;
/*    */ 
/*    */ public class DatabaseUpdateListener
/*    */   implements ActionListener, DocumentListener
/*    */ {
/*    */   public void actionPerformed(ActionEvent ae)
/*    */   {
/* 25 */     if ((Main.database != null) && (!Main.dataChanging)) {
/* 26 */       Main.dataModified = true;
/* 27 */       Main.mainWindow.setTitle(null);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void changedUpdate(DocumentEvent de)
/*    */   {
/* 37 */     if ((Main.database != null) && (!Main.dataChanging)) {
/* 38 */       Main.dataModified = true;
/* 39 */       Main.mainWindow.setTitle(null);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void insertUpdate(DocumentEvent de)
/*    */   {
/* 49 */     if ((Main.database != null) && (!Main.dataChanging)) {
/* 50 */       Main.dataModified = true;
/* 51 */       Main.mainWindow.setTitle(null);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void removeUpdate(DocumentEvent de)
/*    */   {
/* 61 */     if ((Main.database != null) && (!Main.dataChanging)) {
/* 62 */       Main.dataModified = true;
/* 63 */       Main.mainWindow.setTitle(null);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.DatabaseUpdateListener
 * JD-Core Version:    0.6.2
 */