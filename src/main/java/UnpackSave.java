/*    */ package TWEditor;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.List;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class UnpackSave extends Thread
/*    */ {
/*    */   private ProgressDialog progressDialog;
/*    */   private File dirFile;
/* 18 */   private boolean unpackSuccessful = false;
/*    */ 
/*    */   public UnpackSave(ProgressDialog dialog, File dirFile)
/*    */   {
/* 28 */     this.progressDialog = dialog;
/* 29 */     this.dirFile = dirFile;
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 36 */     File file = null;
/* 37 */     InputStream in = null;
/* 38 */     FileOutputStream out = null;
/*    */     try {
/* 40 */       List entries = Main.saveDatabase.getEntries();
/* 41 */       byte[] buffer = new byte[4096];
/* 42 */       int total = entries.size();
/* 43 */       int processed = 0;
/* 44 */       int currentProgress = 0;
/* 45 */       for (Object entryObj : entries) {
                 SaveEntry entry = (SaveEntry)entryObj;
/* 46 */         String resourceName = entry.getResourceName();
/* 47 */         file = new File(this.dirFile.getPath() + Main.fileSeparator + resourceName);
/* 48 */         if ((file.exists()) && 
/* 49 */           (!file.delete())) {
/* 50 */           throw new IOException("Unable to delete '" + file.getName() + "'");
/*    */         }
/* 52 */         out = new FileOutputStream(file);
/* 53 */         in = entry.getInputStream();
/*    */         int count;
/* 55 */         while ((count = in.read(buffer)) > 0) {
/* 56 */           out.write(buffer, 0, count);
/*    */         }
/* 58 */         out.close();
/* 59 */         out = null;
/* 60 */         in.close();
/* 61 */         in = null;
/* 62 */         processed++;
/* 63 */         int newProgress = processed * 100 / total;
/* 64 */         if (newProgress > currentProgress + 9) {
/* 65 */           currentProgress = newProgress;
/* 66 */           this.progressDialog.updateProgress(currentProgress);
/*    */         }
/*    */       }
/*    */ 
/* 70 */       this.unpackSuccessful = true;
/*    */     } catch (IOException exc) {
/* 72 */       Main.logException("I/O error while unpacking save", exc);
/*    */     } catch (Throwable exc) {
/* 74 */       Main.logException("Exception while unpacking save", exc);
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 81 */       if (in != null) {
/* 82 */         in.close();
/*    */       }
/* 84 */       if (out != null) {
/* 85 */         out.close();
/* 86 */         if (file.exists()) {
/* 87 */           file.delete();
/*    */         }
/*    */       }
/*    */ 
/*    */     }
/*    */     catch (IOException exc)
/*    */     {
/*    */     }
/*    */ 
/* 96 */     SwingUtilities.invokeLater(new Runnable() {
/*    */       public void run() {
/* 98 */         UnpackSave.this.progressDialog.closeDialog(UnpackSave.this.unpackSuccessful);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.UnpackSave
 * JD-Core Version:    0.6.2
 */