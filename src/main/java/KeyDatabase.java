/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class KeyDatabase
/*     */ {
/*     */   private File file;
/*     */   private List<KeyEntry> keyEntries;
/*     */   private Map<String, KeyEntry> keyEntriesMap;
/*     */   private List<String> archiveNames;
/*     */ 
/*     */   public KeyDatabase(String filePath)
/*     */     throws DBException, IOException
/*     */   {
/*  31 */     this(new File(filePath));
/*     */   }
/*     */ 
/*     */   public KeyDatabase(File file)
/*     */     throws DBException, IOException
/*     */   {
/*  42 */     this.file = file;
/*  43 */     readFile();
/*     */   }
/*     */ 
/*     */   private void readFile()
/*     */     throws DBException, IOException
/*     */   {
/*  53 */     RandomAccessFile in = new RandomAccessFile(this.file, "r");
/*     */ 
/*  69 */     byte[] header = new byte[68];
/*  70 */     int count = in.read(header);
/*  71 */     if (count != header.length) {
/*  72 */       throw new DBException("KEY header length is incorrect");
/*     */     }
/*  74 */     String signature = new String(header, 0, 4);
/*  75 */     if (!signature.equals("KEY ")) {
/*  76 */       throw new DBException("KEY header signature is incorrect");
/*     */     }
/*  78 */     String version = new String(header, 4, 4);
/*  79 */     if (!version.equals("V1.1")) {
/*  80 */       throw new DBException("KEY header version " + version + " is not supported");
/*     */     }
/*  82 */     int fileCount = getInteger(header, 8);
/*  83 */     long fileOffset = getInteger(header, 20);
/*  84 */     int keyCount = getInteger(header, 12);
/*  85 */     long keyOffset = getInteger(header, 24);
/*     */ 
/*  95 */     this.archiveNames = new ArrayList(fileCount);
/*  96 */     byte[] fileBuffer = new byte[12];
/*  97 */     byte[] nameBuffer = new byte[256];
/*  98 */     for (int i = 0; i < fileCount; i++) {
/*  99 */       in.seek(fileOffset);
/* 100 */       count = in.read(fileBuffer);
/* 101 */       if (count != fileBuffer.length) {
/* 102 */         throw new DBException("File table truncated");
/*     */       }
/* 104 */       long nameOffset = getInteger(fileBuffer, 4);
/* 105 */       int nameLength = getInteger(fileBuffer, 8);
/* 106 */       if (nameLength > nameBuffer.length) {
/* 107 */         nameBuffer = new byte[nameLength];
/*     */       }
/* 109 */       in.seek(nameOffset);
/* 110 */       in.read(nameBuffer, 0, nameLength);
/* 111 */       String fileName = new String(nameBuffer, 0, nameLength);
/* 112 */       this.archiveNames.add(fileName);
/*     */ 
/* 114 */       fileOffset += 12L;
/*     */     }
/*     */ 
/* 126 */     this.keyEntries = new ArrayList(keyCount);
/* 127 */     this.keyEntriesMap = new HashMap(keyCount);
/* 128 */     byte[] keyBuffer = new byte[26];
/* 129 */     for (int i = 0; i < keyCount; i++) {
/* 130 */       in.seek(keyOffset);
/* 131 */       count = in.read(keyBuffer);
/* 132 */       if (count != keyBuffer.length) {
/* 133 */         throw new DBException("Key table truncated");
/*     */       }
/*     */
                int nameLength;
/* 136 */       for (nameLength = 1; (nameLength < 16) &&
/* 137 */         (keyBuffer[nameLength] != 0); nameLength++);
/* 140 */       String resourceName = new String(keyBuffer, 0, nameLength);
/* 141 */       int resourceType = getShort(keyBuffer, 16);
/* 142 */       int resourceID = getInteger(keyBuffer, 18);
/* 143 */       int index = getInteger(keyBuffer, 22) >>> 20;
/* 144 */       if (index >= this.archiveNames.size()) {
/* 145 */         throw new DBException("BIF index for resource " + resourceName + " is too large");
/*     */       }
/* 147 */       String archivePath = this.file.getParent() + Main.fileSeparator + (String)this.archiveNames.get(index);
/* 148 */       KeyEntry keyEntry = new KeyEntry(resourceName, resourceType, resourceID, archivePath);
/* 149 */       this.keyEntries.add(keyEntry);
/* 150 */       this.keyEntriesMap.put(keyEntry.getFileName().toLowerCase(), keyEntry);
/* 151 */       keyOffset += 26L;
/*     */     }
/*     */ 
/* 157 */     in.close();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 166 */     return this.file.getName();
/*     */   }
/*     */ 
/*     */   public List<KeyEntry> getEntries()
/*     */   {
/* 175 */     return this.keyEntries;
/*     */   }
/*     */ 
/*     */   public KeyEntry getEntry(String fileName)
/*     */   {
/* 185 */     return (KeyEntry)this.keyEntriesMap.get(fileName.toLowerCase());
/*     */   }
/*     */ 
/*     */   private int getShort(byte[] buffer, int offset)
/*     */   {
/* 196 */     int value = buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8;
/* 197 */     if (value >= 32768) {
/* 198 */       value |= -65536;
/*     */     }
/* 200 */     return value;
/*     */   }
/*     */ 
/*     */   private int getInteger(byte[] buffer, int offset)
/*     */   {
/* 211 */     return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.KeyDatabase
 * JD-Core Version:    0.6.2
 */