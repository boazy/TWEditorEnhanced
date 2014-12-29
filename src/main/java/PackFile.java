/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.List;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class PackFile extends Thread
/*     */ {
/*     */   private ProgressDialog progressDialog;
/*     */   private File extractDirectory;
/*  20 */   private boolean saveSuccessful = false;
/*     */ 
/*     */   public PackFile(ProgressDialog dialog, File dirFile)
/*     */   {
/*  30 */     this.progressDialog = dialog;
/*  31 */     this.extractDirectory = dirFile;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  38 */     FileInputStream in = null;
/*  39 */     OutputStream out = null;
/*  40 */     List entries = Main.saveDatabase.getEntries();
/*     */     try
/*     */     {
/*  49 */       for (Object entryObj : entries) {
                  SaveEntry entry = (SaveEntry)entryObj;
/*  50 */         File file = new File(this.extractDirectory.getPath() + Main.fileSeparator + entry.getResourceName());
/*  51 */         if ((!file.exists()) || (!file.isFile())) {
/*  52 */           throw new IOException("Resource '" + file.getPath() + "' not found");
/*     */         }
/*  54 */         if (entry.isCompressed()) {
/*  55 */           entry.setOnDisk(false);
/*  56 */           in = new FileInputStream(file);
/*  57 */           out = entry.getOutputStream();
/*  58 */           byte[] buffer = new byte[4096];
/*     */           int count;
/*  60 */           while ((count = in.read(buffer)) > 0) {
/*  61 */             out.write(buffer, 0, count);
/*     */           }
/*  63 */           in.close();
/*  64 */           in = null;
/*  65 */           out.close();
/*  66 */           out = null;
/*     */         } else {
/*  68 */           entry.setResourceFile(file, 0, (int)file.length());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  75 */       Main.saveDatabase.save();
/*     */ 
/*  80 */       this.saveSuccessful = true;
/*     */     } catch (IOException exc) {
/*  82 */       Main.logException("Unable to save file", exc);
/*     */     } catch (Throwable exc) {
/*  84 */       Main.logException("Exception while saving file", exc);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  91 */       if (in != null) {
/*  92 */         in.close();
/*     */       }
/*  94 */       if (out != null) {
/*  95 */         out.close();
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException exc)
/*     */     {
/*     */     }
/*     */ 
/* 103 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 105 */         PackFile.this.progressDialog.closeDialog(PackFile.this.saveSuccessful);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.PackFile
 * JD-Core Version:    0.6.2
 */