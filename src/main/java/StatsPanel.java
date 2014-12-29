/*    */ package TWEditor;
/*    */ 
/*    */ import java.awt.GridLayout;
/*    */ import javax.swing.Box;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.text.Document;
/*    */ 
/*    */ public class StatsPanel extends JPanel
/*    */ {
/* 14 */   private static final String[][] fieldNames = { { "Level", "Vitality", "Bronze Talents" }, { "Experience", "Endurance", "Silver Talents" }, { "Gold", "Toxicity", "Gold Talents" } };
/*    */ 
/* 21 */   private static final String[][] databaseNames = { { "ExpLevel", "CurrentHitPoints", "TalentBronze" }, { "Experience", "CurrentEndurance", "TalentSilver" }, { "Gold", "CurrentToxicity", "TalentGold" } };
/*    */   private NumericField[][] statFields;
/*    */ 
/*    */   public StatsPanel()
/*    */   {
/* 38 */     super(new GridLayout(0, 3, 40, 0));
/* 39 */     DatabaseUpdateListener listener = new DatabaseUpdateListener();
/* 40 */     this.statFields = new NumericField[fieldNames.length][3];
/*    */ 
/* 42 */     add(Box.createVerticalStrut(5));
/* 43 */     add(Box.createVerticalStrut(5));
/* 44 */     add(Box.createVerticalStrut(5));
/*    */ 
/* 46 */     for (int i = 0; i < fieldNames.length; i++) {
/* 47 */       for (int j = 0; j < 3; j++) {
/* 48 */         if (fieldNames[i][j].length() > 0)
/* 49 */           add(new JLabel(fieldNames[i][j]));
/*    */         else {
/* 51 */           add(new JLabel());
/*    */         }
/*    */       }
/* 54 */       for (int j = 0; j < 3; j++) {
/* 55 */         if (fieldNames[i][j].length() > 0) {
/* 56 */           NumericField field = new NumericField(5);
/* 57 */           field.getDocument().addDocumentListener(listener);
/* 58 */           add(field);
/* 59 */           this.statFields[i][j] = field;
/*    */         }
/*    */       }
/*    */ 
/* 63 */       add(Box.createVerticalStrut(5));
/* 64 */       add(Box.createVerticalStrut(5));
/* 65 */       add(Box.createVerticalStrut(5));
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setFields(DBList list)
/*    */     throws DBException
/*    */   {
/* 76 */     for (int i = 0; i < databaseNames.length; i++)
/* 77 */       for (int j = 0; j < 3; j++)
/* 78 */         if (this.statFields[i][j] != null)
/* 79 */           this.statFields[i][j].setValue(list.getInteger(databaseNames[i][j]));
/*    */   }
/*    */ 
/*    */   public void getFields(DBList list)
/*    */     throws DBException
/*    */   {
/* 89 */     for (int i = 0; i < databaseNames.length; i++)
/* 90 */       for (int j = 0; j < 3; j++)
/* 91 */         if (this.statFields[i][j] != null)
/* 92 */           list.setInteger(databaseNames[i][j], this.statFields[i][j].getValue());
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.StatsPanel
 * JD-Core Version:    0.6.2
 */