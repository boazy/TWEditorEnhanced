/*    */ package TWEditor;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.util.zip.GZIPOutputStream;
/*    */ 
/*    */ public class CompressedSaveOutputStream extends GZIPOutputStream
/*    */ {
/*    */   private OutputStream saveOutputStream;
/*    */ 
/*    */   public CompressedSaveOutputStream(SaveOutputStream outputStream)
/*    */     throws IOException
/*    */   {
/* 22 */     super(outputStream, 4096);
/* 23 */     this.saveOutputStream = outputStream;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 32 */     if (this.saveOutputStream != null) {
/* 33 */       super.close();
/* 34 */       this.saveOutputStream.close();
/* 35 */       this.saveOutputStream = null;
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void finalize()
/*    */   {
/*    */     try
/*    */     {
/* 44 */       close();
/* 45 */       super.finalize();
/*    */     } catch (Throwable exc) {
/* 47 */       Main.logException("Exception while finalizing output stream", exc);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.CompressedSaveOutputStream
 * JD-Core Version:    0.6.2
 */