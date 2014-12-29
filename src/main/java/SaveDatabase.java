/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class SaveDatabase
/*     */ {
/*     */   private File file;
/*     */   private String saveName;
/*     */   private int dataOffset;
/*     */   private List<SaveEntry> entries;
/*     */   private Map<String, SaveEntry> entryMap;
/*     */ 
/*     */   public SaveDatabase(String filename)
/*     */   {
/*  32 */     this(new File(filename));
/*     */   }
/*     */ 
/*     */   public SaveDatabase(File file)
/*     */   {
/*  41 */     this.file = file;
/*  42 */     this.entries = new ArrayList(160);
/*  43 */     this.entryMap = new HashMap(160);
/*     */ 
/*  45 */     this.saveName = file.getName();
/*  46 */     int sep = this.saveName.lastIndexOf(46);
/*  47 */     if (sep > 0)
/*  48 */       this.saveName = this.saveName.substring(0, sep);
/*     */   }
/*     */ 
/*     */   public void load()
/*     */     throws DBException, IOException
/*     */   {
/*  58 */     RandomAccessFile in = new RandomAccessFile(this.file, "r");
/*     */     try
/*     */     {
/*  68 */       byte[] buffer = new byte[40];
/*  69 */       int count = in.read(buffer, 0, 12);
/*  70 */       if (count != 12) {
/*  71 */         throw new DBException("Save header truncated");
/*     */       }
/*  73 */       String signature = new String(buffer, 0, 4);
/*  74 */       if (!signature.equals("RGMH")) {
/*  75 */         throw new DBException("Save signature is not valid");
/*     */       }
/*  77 */       int version = getInteger(buffer, 4);
/*  78 */       if (version != 1) {
/*  79 */         throw new DBException("Save version " + version + " is not supported");
/*     */       }
/*  81 */       this.dataOffset = getInteger(buffer, 8);
/*     */ 
/*  89 */       in.seek(in.length() - 8L);
/*  90 */       count = in.read(buffer, 0, 8);
/*  91 */       if (count != 8) {
/*  92 */         throw new DBException("Save trailer truncated");
/*     */       }
/*  94 */       int resourceOffset = getInteger(buffer, 0);
/*  95 */       int resourceCount = getInteger(buffer, 4);
/*  96 */       in.seek(resourceOffset);
/*     */ 
/* 105 */       for (int i = 0; i < resourceCount; i++) {
/* 106 */         count = in.read(buffer, 0, 4);
/* 107 */         if (count != 4) {
/* 108 */           throw new DBException("Resource table truncated");
/*     */         }
/* 110 */         int length = getInteger(buffer, 0);
/* 111 */         if (buffer.length < length) {
/* 112 */           buffer = new byte[length];
/*     */         }
/* 114 */         count = in.read(buffer, 0, length);
/* 115 */         if (count != length) {
/* 116 */           throw new DBException("Resource name truncated");
/*     */         }
/* 118 */         String name = new String(buffer, 0, length, "UTF-8");
/* 119 */         count = in.read(buffer, 0, 8);
/* 120 */         if (count != 8) {
/* 121 */           throw new DBException("Resource table truncated");
/*     */         }
/* 123 */         length = getInteger(buffer, 0);
/* 124 */         int offset = getInteger(buffer, 4);
/* 125 */         SaveEntry saveEntry = new SaveEntry(name, this.file, offset, length);
/* 126 */         this.entries.add(saveEntry);
/* 127 */         this.entryMap.put(saveEntry.getResourceName(), saveEntry);
/*     */       }
/*     */     } finally {
/* 130 */       if (in != null)
/* 131 */         in.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void save()
/*     */     throws IOException
/*     */   {
/* 145 */     File outputFile = new File(this.file.getPath() + ".tmp");
/* 146 */     if (outputFile.exists()) {
/* 147 */       outputFile.delete();
/*     */     }
/* 149 */     OutputStream out = new FileOutputStream(outputFile);
/* 150 */     InputStream in = null;
/* 151 */     byte[] buffer = new byte[4096];
/*     */ 
/* 153 */     int listOffset = this.dataOffset;
/*     */     try
/*     */     {
/* 160 */       in = new FileInputStream(this.file);
/* 161 */       int residualLength = this.dataOffset;
/* 162 */       while (residualLength > 0) {
/* 163 */         int length = Math.min(residualLength, buffer.length);
/* 164 */         int count = in.read(buffer, 0, length);
/* 165 */         if (count != length) {
/* 166 */           throw new IOException("Save game header truncated");
/*     */         }
/* 168 */         out.write(buffer, 0, count);
/* 169 */         residualLength -= count;
/*     */       }
/*     */ 
/* 172 */       in.close();
/*     */ 
/* 181 */       for (SaveEntry entry : this.entries) {
/* 182 */         if (entry.isOnDisk()) {
/* 183 */           in = new FileInputStream(entry.getResourceFile());
/* 184 */           in.skip(entry.getResourceOffset());
/* 185 */           residualLength = entry.getResourceLength();
/* 186 */           listOffset += residualLength;
/* 187 */           while (residualLength > 0) {
/* 188 */             int length = Math.min(residualLength, buffer.length);
/* 189 */             int count = in.read(buffer, 0, length);
/* 190 */             if (count != length) {
/* 191 */               throw new IOException("Resource data truncated for " + entry.getResourceName());
/*     */             }
/* 193 */             out.write(buffer, 0, count);
/* 194 */             residualLength -= count;
/*     */           }
/*     */ 
/* 197 */           in.close();
/*     */         } else {
/* 199 */           List resourceDataList = entry.getResourceDataList();
/* 200 */           residualLength = entry.getResourceLength();
/* 201 */           listOffset += residualLength;
/* 202 */           int index = 0;
/* 203 */           while (residualLength > 0) {
/* 204 */             byte[] dataBuffer = (byte[])resourceDataList.get(index);
/* 205 */             int length = Math.min(residualLength, dataBuffer.length);
/* 206 */             out.write(dataBuffer, 0, length);
/* 207 */             residualLength -= length;
/* 208 */             index++;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 216 */       int offset = this.dataOffset;
/* 217 */       for (SaveEntry entry : this.entries) {
/* 218 */         byte[] nameBytes = entry.getResourcePath().getBytes("UTF-8");
/* 219 */         setInteger(nameBytes.length, buffer, 0);
/* 220 */         out.write(buffer, 0, 4);
/* 221 */         out.write(nameBytes);
/* 222 */         int length = entry.getResourceLength();
/* 223 */         setInteger(length, buffer, 0);
/* 224 */         setInteger(offset, buffer, 4);
/* 225 */         out.write(buffer, 0, 8);
/* 226 */         offset += length;
/*     */       }
/*     */ 
/* 232 */       setInteger(listOffset, buffer, 0);
/* 233 */       setInteger(this.entries.size(), buffer, 4);
/* 234 */       out.write(buffer, 0, 8);
/*     */ 
/* 239 */       out.close();
/* 240 */       out = null;
/*     */ 
/* 245 */       if ((this.file.exists()) && 
/* 246 */         (!this.file.delete())) {
/* 247 */         throw new IOException("Unable to delete '" + this.file.getName() + "'");
/*     */       }
/* 249 */       if (!outputFile.renameTo(this.file)) {
/* 250 */         throw new IOException("Unable to rename '" + outputFile.getName() + "'");
/*     */       }
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 257 */       if (in != null) {
/* 258 */         in.close();
/*     */       }
/*     */ 
/* 263 */       if (out != null) {
/* 264 */         out.close();
/* 265 */         if (outputFile.exists())
/* 266 */           outputFile.delete();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 277 */     return this.saveName;
/*     */   }
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 286 */     return this.file;
/*     */   }
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 295 */     return this.file.getPath();
/*     */   }
/*     */ 
/*     */   public List<SaveEntry> getEntries()
/*     */   {
/* 304 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public SaveEntry getEntry(String resourceName)
/*     */   {
              SaveEntry entry = (SaveEntry)this.entryMap.get(resourceName.toLowerCase());
              if (entry == null) {
                  String resourcePath = this.getName() + "\\" + resourceName;
                  entry = (SaveEntry)this.entryMap.get(resourcePath.toLowerCase());
              }
              return entry;
/*     */   }
/*     */ 
/*     */   public void addEntry(SaveEntry entry)
/*     */   {
/* 324 */     String name = entry.getResourceName();
/* 325 */     SaveEntry oldEntry = (SaveEntry)this.entryMap.get(name);
/* 326 */     if (oldEntry != null) {
/* 327 */       int index = this.entries.indexOf(oldEntry);
/* 328 */       this.entries.set(index, entry);
/*     */     } else {
/* 330 */       this.entries.add(entry);
/*     */     }
/*     */ 
/* 333 */     this.entryMap.put(name, entry);
/*     */   }
/*     */ 
/*     */   private int getInteger(byte[] buffer, int offset)
/*     */   {
/* 344 */     return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   private void setInteger(int number, byte[] buffer, int offset)
/*     */   {
/* 356 */     buffer[offset] = ((byte)number);
/* 357 */     buffer[(offset + 1)] = ((byte)(number >>> 8));
/* 358 */     buffer[(offset + 2)] = ((byte)(number >>> 16));
/* 359 */     buffer[(offset + 3)] = ((byte)(number >>> 24));
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.SaveDatabase
 * JD-Core Version:    0.6.2
 */