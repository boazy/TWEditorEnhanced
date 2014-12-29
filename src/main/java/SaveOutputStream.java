/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SaveOutputStream extends OutputStream
/*     */ {
/*     */   private SaveEntry entry;
/*     */   private FileOutputStream outputStream;
/*     */   private List<byte[]> resourceDataList;
/*     */   private int dataIndex;
/*     */   private int dataOffset;
/*     */   private int resourceLength;
/*     */ 
/*     */   public SaveOutputStream(SaveEntry entry)
/*     */     throws IOException
/*     */   {
/*  37 */     this.entry = entry;
/*  38 */     entry.setResourceOffset(0L);
/*  39 */     entry.setResourceLength(0);
/*  40 */     if (entry.isOnDisk()) {
/*  41 */       File file = entry.getResourceFile();
/*  42 */       if (file.exists()) {
/*  43 */         file.delete();
/*     */       }
/*  45 */       this.outputStream = new FileOutputStream(file);
/*     */     } else {
/*  47 */       this.resourceDataList = entry.getResourceDataList();
/*  48 */       this.resourceDataList.clear();
/*  49 */       this.resourceDataList.add(new byte[4096]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(int b)
/*     */     throws IOException
/*     */   {
/*  60 */     if (this.entry == null) {
/*  61 */       throw new IOException("Output stream is not open");
/*     */     }
/*  63 */     if (this.outputStream != null) {
/*  64 */       this.outputStream.write(b);
/*     */     } else {
/*  66 */       byte[] dataBuffer = (byte[])this.resourceDataList.get(this.dataIndex);
/*  67 */       dataBuffer[this.dataOffset] = ((byte)b);
/*  68 */       this.dataOffset += 1;
/*  69 */       if (this.dataOffset == dataBuffer.length) {
/*  70 */         this.resourceDataList.add(new byte[4096]);
/*  71 */         this.dataIndex += 1;
/*  72 */         this.dataOffset = 0;
/*     */       }
/*     */     }
/*     */ 
/*  76 */     this.resourceLength += 1;
/*     */   }
/*     */ 
/*     */   public void write(byte[] buffer)
/*     */     throws IOException
/*     */   {
/*  86 */     write(buffer, 0, buffer.length);
/*     */   }
/*     */ 
/*     */   public void write(byte[] buffer, int bufferOffset, int bufferLength)
/*     */     throws IOException
/*     */   {
/*  98 */     if (this.entry == null) {
/*  99 */       throw new IOException("Output stream is not open");
/*     */     }
/* 101 */     if (this.outputStream != null) {
/* 102 */       this.outputStream.write(buffer, bufferOffset, bufferLength);
/*     */     } else {
/* 104 */       int count = 0;
/* 105 */       while (count < bufferLength) {
/* 106 */         byte[] dataBuffer = (byte[])this.resourceDataList.get(this.dataIndex);
/* 107 */         int length = Math.min(bufferLength - count, dataBuffer.length - this.dataOffset);
/* 108 */         for (int i = 0; i < length; i++) {
/* 109 */           dataBuffer[(this.dataOffset + i)] = buffer[(bufferOffset + count + i)];
/*     */         }
/* 111 */         count += length;
/* 112 */         this.dataOffset += length;
/* 113 */         if (this.dataOffset == dataBuffer.length) {
/* 114 */           this.resourceDataList.add(new byte[4096]);
/* 115 */           this.dataIndex += 1;
/* 116 */           this.dataOffset = 0;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 121 */     this.resourceLength += bufferLength;
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 130 */     if (this.entry == null) {
/* 131 */       throw new IOException("Output stream is not open");
/*     */     }
/* 133 */     if (this.outputStream != null)
/* 134 */       this.outputStream.flush();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 143 */     if (this.entry != null) {
/* 144 */       if (this.outputStream != null) {
/* 145 */         this.outputStream.close();
/* 146 */         this.outputStream = null;
/*     */       }
/*     */ 
/* 149 */       this.entry.setResourceLength(this.resourceLength);
/* 150 */       this.entry = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*     */     try
/*     */     {
/* 159 */       close();
/* 160 */       super.finalize();
/*     */     } catch (Throwable exc) {
/* 162 */       Main.logException("Exception while finalizing output stream", exc);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.SaveOutputStream
 * JD-Core Version:    0.6.2
 */