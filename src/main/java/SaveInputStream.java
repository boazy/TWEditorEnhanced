/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SaveInputStream extends InputStream
/*     */ {
/*     */   private SaveEntry entry;
/*     */   private FileInputStream inputStream;
/*     */   private List<byte[]> resourceDataList;
/*     */   private int dataIndex;
/*     */   private int dataOffset;
/*     */   private int residualLength;
/*     */ 
/*     */   public SaveInputStream(SaveEntry entry)
/*     */     throws IOException
/*     */   {
/*  36 */     this.entry = entry;
/*  37 */     this.residualLength = entry.getResourceLength();
/*  38 */     if (entry.isOnDisk()) {
/*  39 */       this.inputStream = new FileInputStream(entry.getResourceFile());
/*  40 */       this.inputStream.skip(entry.getResourceOffset());
/*     */     } else {
/*  42 */       this.resourceDataList = entry.getResourceDataList();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  52 */     if (this.inputStream != null) {
/*  53 */       this.inputStream.close();
/*     */     }
/*  55 */     this.inputStream = null;
/*  56 */     this.entry = null;
/*  57 */     this.residualLength = 0;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */   {
/*  66 */     return this.residualLength;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  76 */     if (this.entry == null)
/*  77 */       throw new IOException("Input stream is not open");
/*     */     int result;
/*  80 */     if (this.residualLength == 0) {
/*  81 */       result = -1;
/*  82 */     } else if (this.inputStream != null) {
/*  83 */       result = this.inputStream.read();
/*  84 */       if (result == -1) {
/*  85 */         throw new EOFException("Unexpected end of stream");
/*     */       }
/*  87 */       this.residualLength -= 1;
/*     */     } else {
/*  89 */       byte[] dataBuffer = (byte[])this.resourceDataList.get(this.dataIndex);
/*  90 */       result = dataBuffer[this.dataOffset] & 0xFF;
/*  91 */       this.dataOffset += 1;
/*  92 */       this.residualLength -= 1;
/*  93 */       if (this.dataOffset == dataBuffer.length) {
/*  94 */         this.dataIndex += 1;
/*  95 */         this.dataOffset = 0;
/*     */       }
/*     */     }
/*     */ 
/*  99 */     return result;
/*     */   }
/*     */ 
/*     */   public int read(byte[] buffer)
/*     */     throws IOException
/*     */   {
/* 110 */     return read(buffer, 0, buffer.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] buffer, int bufferOffset, int bufferLength)
/*     */     throws IOException
/*     */   {
/* 123 */     if (this.entry == null)
/* 124 */       throw new IOException("Input stream is not open");
/*     */     int count;
/* 127 */     if (this.residualLength == 0) {
/* 128 */       count = -1;
/* 129 */     } else if (this.inputStream != null) {
/* 130 */       int length = Math.min(this.residualLength, bufferLength);
/* 131 */       count = this.inputStream.read(buffer, bufferOffset, length);
/* 132 */       if (count < 0) {
/* 133 */         throw new EOFException("Unexpected end of stream");
/*     */       }
/* 135 */       this.residualLength -= count;
/*     */     } else {
/* 137 */       count = 0;
/* 138 */       int length = Math.min(this.residualLength, bufferLength);
/* 139 */       while (count < length) {
/* 140 */         byte[] dataBuffer = (byte[])this.resourceDataList.get(this.dataIndex);
/* 141 */         int copyLength = Math.min(dataBuffer.length - this.dataOffset, length - count);
/* 142 */         for (int i = 0; i < copyLength; i++) {
/* 143 */           buffer[(bufferOffset + count + i)] = dataBuffer[(this.dataOffset + i)];
/*     */         }
/* 145 */         count += copyLength;
/* 146 */         this.dataOffset += copyLength;
/* 147 */         if (this.dataOffset == dataBuffer.length) {
/* 148 */           this.dataIndex += 1;
/* 149 */           this.dataOffset = 0;
/*     */         }
/*     */       }
/*     */ 
/* 153 */       this.residualLength -= count;
/*     */     }
/*     */ 
/* 156 */     return count;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*     */     try
/*     */     {
/* 164 */       close();
/* 165 */       super.finalize();
/*     */     } catch (Throwable exc) {
/* 167 */       Main.logException("Exception while finalizing input stream", exc);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.SaveInputStream
 * JD-Core Version:    0.6.2
 */