/*    */ package TWEditor;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.zip.GZIPInputStream;
/*    */ 
/*    */ public class CompressedSaveInputStream extends GZIPInputStream
/*    */ {
/*    */   private InputStream saveInputStream;
/*    */ 
/*    */   public CompressedSaveInputStream(SaveInputStream inputStream)
/*    */     throws IOException
/*    */   {
/* 22 */     super(inputStream, 4096);
/* 23 */     this.saveInputStream = inputStream;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/* 32 */     if (this.saveInputStream != null) {
/* 33 */       super.close();
/* 34 */       this.saveInputStream.close();
/* 35 */       this.saveInputStream = null;
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
/* 47 */       Main.logException("Exception while finalizing input stream", exc);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.CompressedSaveInputStream
 * JD-Core Version:    0.6.2
 */