/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ 
/*     */ public class KeyInputStream extends InputStream
/*     */ {
/*     */   private RandomAccessFile in;
/*     */   private long dataOffset;
/*     */   private int residualLength;
/*     */ 
/*     */   public KeyInputStream(KeyEntry keyEntry)
/*     */     throws DBException, IOException
/*     */   {
/*  32 */     File file = new File(keyEntry.getArchivePath());
/*  33 */     this.in = new RandomAccessFile(file, "r");
/*     */ 
/*  44 */     byte[] header = new byte[20];
/*  45 */     int count = this.in.read(header);
/*  46 */     if (count != header.length) {
/*  47 */       throw new DBException("BIF header is too short");
/*     */     }
/*  49 */     String type = new String(header, 0, 4);
/*  50 */     if (!type.equals("BIFF")) {
/*  51 */       throw new DBException("BIF signature is not correct");
/*     */     }
/*  53 */     String version = new String(header, 4, 4);
/*  54 */     if (!version.equals("V1.1")) {
/*  55 */       throw new DBException("BIF version " + version + " is not supported");
/*     */     }
/*  57 */     int resourceCount = getInteger(header, 8);
/*  58 */     long resourceOffset = getInteger(header, 16);
/*     */ 
/*  72 */     byte[] buffer = new byte[20];
/*  73 */     this.in.seek(resourceOffset);
/*  74 */     int keyID = keyEntry.getResourceID();
/*  75 */     for (int i = 0; i < resourceCount; i++) {
/*  76 */       count = this.in.read(buffer);
/*  77 */       if (count != buffer.length) {
/*  78 */         throw new DBException("Resource table truncated");
/*     */       }
/*  80 */       int resourceID = getInteger(buffer, 0);
/*  81 */       if (resourceID == keyID) {
/*  82 */         int resourceType = getShort(buffer, 16);
/*  83 */         if (resourceType != keyEntry.getResourceType()) {
/*  84 */           throw new DBException("KEY/BIF resource type mismatch");
/*     */         }
/*  86 */         this.dataOffset = getInteger(buffer, 8);
/*  87 */         this.residualLength = getInteger(buffer, 12);
/*  88 */         break;
/*     */       }
/*     */     }
/*     */ 
/*  92 */     if (this.dataOffset == 0L)
/*  93 */       throw new DBException("KEY resource '" + keyEntry.getFileName() + "' not found in BIF");
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 102 */     if (this.in != null) {
/* 103 */       this.in.close();
/*     */     }
/* 105 */     this.in = null;
/* 106 */     this.residualLength = 0;
/*     */   }
/*     */ 
/*     */   public int available()
/*     */   {
/* 115 */     return this.residualLength;
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/* 125 */     if (this.in == null)
/* 126 */       throw new IOException("Input stream is not open");
/*     */     int result;
/* 129 */     if (this.residualLength == 0) {
/* 130 */       result = -1;
/*     */     } else {
/* 132 */       this.in.seek(this.dataOffset);
/* 133 */       result = this.in.readByte() & 0xFF;
/* 134 */       this.dataOffset += 1L;
/* 135 */       this.residualLength -= 1;
/*     */     }
/*     */ 
/* 138 */     return result;
/*     */   }
/*     */ 
/*     */   public int read(byte[] buffer)
/*     */     throws IOException
/*     */   {
/* 149 */     return read(buffer, 0, buffer.length);
/*     */   }
/*     */ 
/*     */   public int read(byte[] buffer, int bufferOffset, int bufferLength)
/*     */     throws IOException
/*     */   {
/* 162 */     if (this.in == null) {
/* 163 */       throw new IOException("Input stream is not open");
/*     */     }
/* 165 */     int count = 0;
/* 166 */     if (this.residualLength == 0) {
/* 167 */       count = -1;
/*     */     } else {
/* 169 */       this.in.seek(this.dataOffset);
/* 170 */       int length = Math.min(this.residualLength, bufferLength);
/* 171 */       count = this.in.read(buffer, bufferOffset, length);
/* 172 */       if (count < 0) {
/* 173 */         throw new EOFException("Unexpected end of stream");
/*     */       }
/* 175 */       this.dataOffset += count;
/* 176 */       this.residualLength -= count;
/*     */     }
/*     */ 
/* 179 */     return count;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*     */     try
/*     */     {
/* 187 */       close();
/* 188 */       super.finalize();
/*     */     } catch (Throwable exc) {
/* 190 */       Main.logException("Exception while finalizing input stream", exc);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getShort(byte[] buffer, int offset)
/*     */   {
/* 202 */     int value = buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8;
/* 203 */     if (value >= 32768) {
/* 204 */       value |= -65536;
/*     */     }
/* 206 */     return value;
/*     */   }
/*     */ 
/*     */   private int getInteger(byte[] buffer, int offset)
/*     */   {
/* 217 */     return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.KeyInputStream
 * JD-Core Version:    0.6.2
 */