/*     */ package TWEditor;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextPane;
/*     */ 
/*     */ public class ExamineDialog extends JDialog
/*     */   implements ActionListener
/*     */ {
/*     */   private JScrollPane scrollPane;
/*     */   private JTextPane textPane;
/*     */ 
/*     */   public ExamineDialog(JFrame parent, String label, String description)
/*     */   {
/*  33 */     super(parent, label, true);
/*  34 */     setDefaultCloseOperation(2);
/*     */ 
/*  39 */     StringBuilder stringBuilder = new StringBuilder(description);
/*  40 */     stringBuilder.insert(0, "<html>");
/*  41 */     stringBuilder.append("</html>");
/*     */ 
/*  47 */     int start = 6;
/*     */     while (true) {
/*  49 */       start = stringBuilder.indexOf("<", start);
/*  50 */       if (start < 0) {
/*     */         break;
/*     */       }
/*  53 */       int stop = stringBuilder.indexOf(">", start);
/*  54 */       if (stop < 0) {
/*     */         break;
/*     */       }
/*  57 */       String control = stringBuilder.substring(start + 1, stop).toLowerCase();
/*  58 */       if (control.equals("/html")) {
/*     */         break;
/*     */       }
/*  61 */       String html = null;
/*  62 */       String strref = null;
/*  63 */       if (control.equals("cbold"))
/*  64 */         html = "b";
/*  65 */       else if (control.equals("citalic"))
/*  66 */         html = "i";
/*  67 */       else if ((control.length() >= 7) && (control.substring(0, 7).equals("strref:"))) {
/*     */         try {
/*  69 */           int refid = Integer.parseInt(control.substring(7));
/*  70 */           strref = Main.stringsDatabase.getString(refid);
/*     */         } catch (NumberFormatException exc) {
/*  72 */           strref = "";
/*     */         }
/*     */       }
/*     */ 
/*  76 */       if (html != null) {
/*  77 */         stringBuilder.replace(start + 1, stop, html);
/*  78 */         start = stringBuilder.indexOf("</c>", stop);
/*  79 */         if (start < 0) {
/*  80 */           stringBuilder.append(new StringBuilder().append("</").append(html).append(">").toString());
/*  81 */           break;
/*     */         }
/*     */ 
/*  84 */         stringBuilder.replace(start + 2, start + 3, html);
/*  85 */         start += 4;
/*  86 */       } else if (strref != null) {
/*  87 */         stringBuilder.replace(start, stop + 1, strref);
/*     */       } else {
/*  89 */         stringBuilder.delete(start, stop + 1);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  96 */     int sep = 0;
/*  97 */     while ((sep = stringBuilder.indexOf("\n", sep)) >= 0) {
/*  98 */       stringBuilder.replace(sep, sep + 1, "<br>");
/*     */     }
/*     */ 
/* 103 */     JPanel contentPane = new JPanel();
/* 104 */     contentPane.setLayout(new BoxLayout(contentPane, 1));
/* 105 */     contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
/*     */ 
/* 107 */     this.textPane = new JTextPane();
/* 108 */     this.textPane.setContentType("text/html");
/* 109 */     this.textPane.setText(stringBuilder.toString());
/* 110 */     this.textPane.setCaretPosition(0);
/*     */ 
/* 112 */     this.scrollPane = new JScrollPane(this.textPane);
/* 113 */     this.scrollPane.setPreferredSize(new Dimension(400, 500));
/*     */ 
/* 115 */     JPanel buttonPane = new JPanel();
/* 116 */     JButton button = new JButton("OK");
/* 117 */     button.addActionListener(this);
/* 118 */     button.setActionCommand("ok");
/* 119 */     buttonPane.add(button);
/*     */ 
/* 121 */     contentPane.add(this.scrollPane);
/* 122 */     contentPane.add(Box.createVerticalStrut(10));
/* 123 */     contentPane.add(buttonPane);
/* 124 */     setContentPane(contentPane);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/*     */     try
/*     */     {
/* 134 */       String action = ae.getActionCommand();
/* 135 */       if (action.equals("ok")) {
/* 136 */         setVisible(false);
/* 137 */         dispose();
/*     */       }
/*     */     } catch (Throwable exc) {
/* 140 */       Main.logException("Exception while processing action event", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void showDialog(JFrame parent, String label, String description)
/*     */   {
/* 152 */     ExamineDialog dialog = new ExamineDialog(parent, label, description);
/* 153 */     dialog.pack();
/* 154 */     dialog.setLocationRelativeTo(parent);
/* 155 */     dialog.setVisible(true);
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.ExamineDialog
 * JD-Core Version:    0.6.2
 */