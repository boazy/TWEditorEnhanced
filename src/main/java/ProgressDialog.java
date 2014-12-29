/*     */ package TWEditor;
/*     */ 
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class ProgressDialog extends JDialog
/*     */ {
/*     */   private JFrame parent;
/*     */   private JProgressBar progressBar;
/*     */   private int deferredProgress;
/*  25 */   private boolean success = false;
/*     */ 
/*     */   public ProgressDialog(JFrame parent, String message)
/*     */   {
/*  38 */     super(parent, "The Witcher Save Editor", true);
/*  39 */     this.parent = parent;
/*     */ 
/*  44 */     JPanel progressPane = new JPanel();
/*  45 */     progressPane.setLayout(new BoxLayout(progressPane, 1));
/*  46 */     progressPane.add(Box.createVerticalStrut(15));
/*  47 */     progressPane.add(new JLabel("<html><b>" + message + "</b></html>"));
/*  48 */     progressPane.add(Box.createVerticalStrut(15));
/*  49 */     this.progressBar = new JProgressBar(0, 100);
/*  50 */     this.progressBar.setStringPainted(true);
/*  51 */     progressPane.add(this.progressBar);
/*  52 */     progressPane.add(Box.createVerticalStrut(15));
/*     */ 
/*  57 */     JPanel contentPane = new JPanel();
/*  58 */     contentPane.add(progressPane);
/*  59 */     setContentPane(contentPane);
/*     */   }
/*     */ 
/*     */   public boolean showDialog()
/*     */   {
/*  69 */     pack();
/*  70 */     setLocationRelativeTo(this.parent);
/*  71 */     setVisible(true);
/*  72 */     return this.success;
/*     */   }
/*     */ 
/*     */   public void closeDialog(boolean success)
/*     */   {
/*  82 */     this.success = success;
/*  83 */     setVisible(false);
/*  84 */     dispose();
/*     */   }
/*     */ 
/*     */   public void updateProgress(int progress)
/*     */   {
/*  94 */     if (SwingUtilities.isEventDispatchThread()) {
/*  95 */       this.progressBar.setValue(progress);
/*     */     } else {
/*  97 */       this.deferredProgress = progress;
/*  98 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 100 */           ProgressDialog.this.progressBar.setValue(ProgressDialog.this.deferredProgress);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.ProgressDialog
 * JD-Core Version:    0.6.2
 */