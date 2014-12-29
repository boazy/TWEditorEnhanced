/*    */ package TWEditor;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.StringWriter;
/*    */ 
/*    */ public class StreamReader extends Thread
/*    */ {
/*    */   private InputStreamReader reader;
/*    */   private StringWriter writer;
/*    */   private StringBuffer buffer;
/* 21 */   private int index = 0;
/*    */ 
/*    */   public StreamReader(InputStream inputStream)
/*    */   {
/* 29 */     this.reader = new InputStreamReader(inputStream);
/* 30 */     this.writer = new StringWriter(1024);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/*    */       int c;
/* 42 */       while ((c = this.reader.read()) != -1) {
/* 43 */         this.writer.write(c);
/*    */       }
/* 45 */       this.reader.close();
/* 46 */       this.buffer = this.writer.getBuffer();
/*    */     } catch (IOException exc) {
/* 48 */       Main.logException("Unable to read from input stream", exc);
/*    */     }
/*    */   }
/*    */ 
/*    */   public StringBuffer getBuffer()
/*    */     throws IllegalThreadStateException
/*    */   {
/* 60 */     if (this.buffer == null) {
/* 61 */       throw new IllegalThreadStateException("Input stream is still open");
/*    */     }
/* 63 */     return this.buffer;
/*    */   }
/*    */ 
/*    */   public String getLine()
/*    */     throws IllegalThreadStateException
/*    */   {
/* 75 */     if (this.buffer == null) {
/* 76 */       throw new IllegalThreadStateException("Input stream is still open");
/*    */     }
/* 78 */     String line = null;
/* 79 */     int length = this.buffer.length();
/* 80 */     if (this.index < length) {
/* 81 */       int sep = this.buffer.indexOf(Main.lineSeparator, this.index);
/* 82 */       if (sep < 0) {
/* 83 */         line = this.buffer.substring(this.index);
/* 84 */         this.index = length;
/*    */       } else {
/* 86 */         line = this.buffer.substring(this.index, sep);
/* 87 */         this.index = (sep + Main.lineSeparator.length());
/*    */       }
/*    */     }
/*    */ 
/* 91 */     return line;
/*    */   }
/*    */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.StreamReader
 * JD-Core Version:    0.6.2
 */