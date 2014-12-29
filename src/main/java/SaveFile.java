/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class SaveFile extends Thread
/*     */ {
/*     */   private ProgressDialog progressDialog;
/*     */   private File extractDirectory;
/*  18 */   private boolean saveSuccessful = false;
/*     */ 
/*     */   public SaveFile(ProgressDialog dialog)
/*     */   {
/*  27 */     this.progressDialog = dialog;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  34 */     FileInputStream in = null;
/*  35 */     OutputStream out = null;
/*     */     try
/*     */     {
/*  41 */       Main.database.save();
/*  42 */       this.progressDialog.updateProgress(15);
/*     */ 
/*  47 */       ResourceEntry resourceEntry = new ResourceEntry("module.ifo", Main.databaseFile);
/*  48 */       Main.modDatabase.addEntry(resourceEntry);
/*  49 */       Main.modDatabase.save();
/*  50 */       this.progressDialog.updateProgress(30);
/*     */ 
/*  55 */       ResourceDatabase modDatabase = new ResourceDatabase(Main.modDatabase.getPath());
/*  56 */       modDatabase.load();
/*  57 */       Main.modDatabase = modDatabase;
/*  58 */       this.progressDialog.updateProgress(45);
/*     */ 
/*  63 */       SaveEntry saveEntry = new SaveEntry(Main.savePrefix + Main.modName);
/*  64 */       in = new FileInputStream(Main.modFile);
/*  65 */       out = saveEntry.getOutputStream();
/*  66 */       byte[] buffer = new byte[4096];
/*     */       int count;
/*  68 */       while ((count = in.read(buffer)) > 0) {
/*  69 */         out.write(buffer, 0, count);
/*     */       }
/*  71 */       in.close();
/*  72 */       in = null;
/*  73 */       out.close();
/*  74 */       out = null;
/*  75 */       Main.saveDatabase.addEntry(saveEntry);
/*  76 */       this.progressDialog.updateProgress(60);
/*     */ 
/*  81 */       Main.saveDatabase.save();
/*  82 */       this.progressDialog.updateProgress(80);
/*     */ 
/*  87 */       SaveDatabase saveDatabase = new SaveDatabase(Main.saveDatabase.getPath());
/*  88 */       saveDatabase.load();
/*  89 */       Main.saveDatabase = saveDatabase;
/*  90 */       this.progressDialog.updateProgress(100);
/*     */ 
/*  95 */       this.saveSuccessful = true;
/*     */     } catch (DBException exc) {
/*  97 */       Main.logException("Unable to update save database", exc);
/*     */     } catch (IOException exc) {
/*  99 */       Main.logException("Unable to save file", exc);
/*     */     } catch (Throwable exc) {
/* 101 */       Main.logException("Exception while saving file", exc);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 108 */       if (in != null) {
/* 109 */         in.close();
/*     */       }
/* 111 */       if (out != null) {
/* 112 */         out.close();
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException exc)
/*     */     {
/*     */     }
/*     */ 
/* 120 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 122 */         SaveFile.this.progressDialog.closeDialog(SaveFile.this.saveSuccessful);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.SaveFile
 * JD-Core Version:    0.6.2
 */