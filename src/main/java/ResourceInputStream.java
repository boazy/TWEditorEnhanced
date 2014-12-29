/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class ResourceInputStream extends InputStream
/*     */ {
/*     */   private ResourceEntry entry;
/*     */   private FileInputStream in;
/*     */   private int residualLength;
/*     */ 
/*     */   public ResourceInputStream(ResourceEntry entry)
/*     */     throws IOException
/*     */   {
/*  28 */     this.entry = entry;
/*  29 */     this.residualLength = entry.getLength();
/*  30 */     this.in = new FileInputStream(entry.getFile());
/*  31 */     this.in.skip(entry.getOffset());
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  40 */     if (this.in != null) {
/*  41 */       this.in.close();
/*  42 */       this.in = null;
/*     */     }
/*     */ 
/*  45 */     this.entry = null;
/*  46 */     this.residualLength = 0;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */   {
/*  55 */     return this.residualLength;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  65 */     if (this.in == null)
/*  66 */       throw new IOException("Input stream closed");
/*     */     int b;
/*  69 */     if (this.residualLength > 0) {
/*  70 */       b = this.in.read();
/*  71 */       if (b != -1)
/*  72 */         this.residualLength -= 1;
/*     */     } else {
/*  74 */       b = -1;
/*     */     }
/*     */ 
/*  77 */     return b;
/*     */   }
/*     */ 
/*     */   public int read(byte[] buffer)
/*     */     throws IOException
/*     */   {
/*  88 */     return read(buffer, 0, buffer.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] buffer, int bufferOffset, int bufferLength)
/*     */     throws IOException
/*     */   {
/* 101 */     if (this.in == null)
/* 102 */       throw new IOException("Input stream closed");
/*     */     int count;
/* 105 */     if (this.residualLength > 0) {
/* 106 */       count = this.in.read(buffer, bufferOffset, Math.min(bufferLength, this.residualLength));
/* 107 */       if (count != -1)
/* 108 */         this.residualLength -= count;
/*     */     } else {
/* 110 */       count = -1;
/*     */     }
/*     */ 
/* 113 */     return count;
/*     */   }
/*     */ 
/*     */   public long skip(long count)
/*     */     throws IOException
/*     */   {
/* 124 */     if (this.in == null) {
/* 125 */       throw new IOException("Input stream closed");
/*     */     }
/* 127 */     long skipped = this.in.skip(Math.min(count, this.residualLength));
/* 128 */     this.residualLength = ((int)(this.residualLength - skipped));
/* 129 */     return skipped;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*     */     try
/*     */     {
/* 137 */       close();
/* 138 */       super.finalize();
/*     */     } catch (Throwable exc) {
/* 140 */       Main.logException("Exception while finalizing input stream", exc);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.ResourceInputStream
 * JD-Core Version:    0.6.2
 */