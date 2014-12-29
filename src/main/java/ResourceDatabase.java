/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ResourceDatabase
/*     */ {
/*  12 */   public static final String[] databaseTypes = { "ERF ", "HAK ", "MOD ", "NWM ", "SAV " };
/*     */ 
/*  15 */   public static final String[] databaseVersions = { "V1.0", "V1.1" };
/*     */   private File file;
/*     */   private String databaseType;
/*     */   private String databaseVersion;
/*     */   private LocalizedString description;
/*     */   private List<ResourceEntry> entries;
/*     */   private Map<String, ResourceEntry> entryMap;
/*     */ 
/*     */   public ResourceDatabase(String filePath)
/*     */   {
/*  42 */     this(new File(filePath));
/*     */   }
/*     */ 
/*     */   public ResourceDatabase(File file)
/*     */   {
/*  52 */     this.file = file;
/*  53 */     this.databaseType = "ERF ";
/*  54 */     this.databaseVersion = "V1.0";
/*  55 */     this.description = new LocalizedString(-1);
/*  56 */     this.entries = new ArrayList(64);
/*  57 */     this.entryMap = new HashMap(64);
/*     */   }
/*     */ 
/*     */   public void load()
/*     */     throws DBException, IOException
/*     */   {
/*  73 */     RandomAccessFile in = new RandomAccessFile(this.file, "r");
/*     */     try
/*     */     {
/*  91 */       byte[] header = new byte[' '];
/*  92 */       int count = in.read(header);
/*  93 */       if (count != 160) {
/*  94 */         throw new DBException("Database header is too short");
/*     */       }
/*  96 */       boolean validType = false;
/*  97 */       this.databaseType = new String(header, 0, 4);
/*  98 */       for (int i = 0; i < databaseTypes.length; i++) {
/*  99 */         if (this.databaseType.equals(databaseTypes[i])) {
/* 100 */           validType = true;
/* 101 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 105 */       if (!validType) {
/* 106 */         throw new DBException("Database type '" + this.databaseType + "' is not supported");
/*     */       }
/* 108 */       boolean validVersion = false;
/* 109 */       this.databaseVersion = new String(header, 4, 4);
/* 110 */       for (int i = 0; i < databaseVersions.length; i++) {
/* 111 */         if (this.databaseVersion.equals(databaseVersions[i])) {
/* 112 */           validVersion = true;
/* 113 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 117 */       if (!validVersion) {
/* 118 */         throw new DBException("Database version '" + this.databaseVersion + "' is not supported");
/*     */       }
/* 120 */       int stringCount = getInteger(header, 8);
/* 121 */       int stringSize = getInteger(header, 12);
/* 122 */       int entryCount = getInteger(header, 16);
/* 123 */       int stringOffset = getInteger(header, 20);
/* 124 */       int keyOffset = getInteger(header, 24);
/* 125 */       int resourceOffset = getInteger(header, 28);
/* 126 */       int stringReference = getInteger(header, 40);
/*     */ 
/* 128 */       this.description = new LocalizedString(stringReference);
/* 129 */       this.entries = new ArrayList(Math.max(entryCount, 10));
/* 130 */       this.entryMap = new HashMap(Math.max(entryCount, 10));
/*     */ 
/* 143 */       if (stringCount > 0) {
/* 144 */         in.seek(stringOffset);
/* 145 */         byte[] buffer = new byte[''];
/* 146 */         for (int i = 0; i < stringCount; i++) {
/* 147 */           count = in.read(buffer, 0, 8);
/* 148 */           if (count != 8) {
/* 149 */             throw new DBException("String list truncated");
/*     */           }
/* 151 */           int language = getInteger(buffer, 0);
/* 152 */           int stringLength = getInteger(buffer, 4);
/* 153 */           int gender = language & 0x1;
/* 154 */           language >>= 1;
/*     */           String string;
/* 156 */           if (stringLength > 0) {
/* 157 */             if (stringLength > buffer.length) {
/* 158 */               buffer = new byte[stringLength];
/*     */             }
/* 160 */             count = in.read(buffer, 0, stringLength);
/* 161 */             if (count != stringLength) {
/* 162 */               throw new DBException("String list truncated");
/*     */             }
/* 164 */             string = new String(buffer, 0, stringLength, "UTF-8");
/* 165 */             stringLength = string.length();
/* 166 */             if (string.charAt(stringLength - 1) == 0)
/* 167 */               string = string.substring(0, stringLength - 1);
/*     */           } else {
/* 169 */             string = new String();
/*     */           }
/*     */ 
/* 172 */           this.description.addSubstring(new LocalizedSubstring(string, language, gender));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 192 */       List resourceNames = new ArrayList(entryCount);
/* 193 */       List resourceTypes = new ArrayList(entryCount);
/* 194 */       if (entryCount > 0) {
/* 195 */         in.seek(keyOffset);
/*     */         int nameLength;
/*     */         int keyLength;
/* 198 */         if (this.databaseVersion.equals("V1.0")) {
/* 199 */           keyLength = 24;
/* 200 */           nameLength = 16;
/*     */         } else {
/* 202 */           keyLength = 40;
/* 203 */           nameLength = 32;
/*     */         }
/*     */ 
/* 206 */         byte[] key = new byte[keyLength];
/* 207 */         for (int i = 0; i < entryCount; i++) {
/* 208 */           count = in.read(key);
/* 209 */           if (count != keyLength) {
/* 210 */             throw new DBException("Key list truncated");
/*     */           }
/* 212 */           for (count = 0; (count < nameLength) && 
/* 213 */             (key[count] != 0); count++);
/* 216 */           resourceNames.add(new String(key, 0, count));
/* 217 */           resourceTypes.add(new Integer(getShort(key, nameLength + 4)));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 227 */       if (entryCount > 0) {
/* 228 */         in.seek(resourceOffset);
/* 229 */         byte[] element = new byte[8];
/* 230 */         for (int i = 0; i < entryCount; i++) {
/* 231 */           count = in.read(element);
/* 232 */           if (count != 8) {
/* 233 */             throw new DBException("Resource list truncated");
/*     */           }
/* 235 */           long offset = getInteger(element, 0);
/* 236 */           int length = getInteger(element, 4);
/* 237 */           String resourceName = (String)resourceNames.get(i);
/* 238 */           int resourceType = ((Integer)resourceTypes.get(i)).intValue();
/* 239 */           if ((resourceName.length() > 0) && (resourceType != 65535)) {
/* 240 */             ResourceEntry entry = new ResourceEntry(resourceName, resourceType, this.file, offset, length);
/* 241 */             this.entries.add(entry);
/* 242 */             this.entryMap.put(entry.getName(), entry);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 249 */       in.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void save()
/*     */     throws DBException, IOException
/*     */   {
/* 267 */     File outputFile = new File(this.file.getPath() + ".tmp");
/* 268 */     if (outputFile.exists()) {
/* 269 */       outputFile.delete();
/*     */     }
/* 271 */     RandomAccessFile out = new RandomAccessFile(outputFile, "rw");
/* 272 */     RandomAccessFile in = null;
/*     */     try
/*     */     {
/* 279 */       byte[] header = new byte[' '];
/* 280 */       out.write(header);
/*     */ 
/* 289 */       byte[] buffer = new byte[''];
/* 290 */       int stringOffset = (int)out.getFilePointer();
/* 291 */       int stringSize = 0;
/* 292 */       int stringCount = this.description.getSubstringCount();
/* 293 */       for (int i = 0; i < stringCount; i++) {
/* 294 */         LocalizedSubstring substring = this.description.getSubstring(i);
/* 295 */         String string = substring.getString();
/* 296 */         byte[] stringBytes = string.getBytes();
/* 297 */         int length = stringBytes.length;
/* 298 */         if (length + 8 > buffer.length) {
/* 299 */           buffer = new byte[length + 8];
/*     */         }
/* 301 */         setInteger(substring.getLanguage() * 2 + substring.getGender(), buffer, 0);
/* 302 */         setInteger(length, buffer, 4);
/* 303 */         for (int j = 0; j < length; j++) {
/* 304 */           buffer[(j + 8)] = stringBytes[j];
/*     */         }
/* 306 */         out.write(buffer, 0, length + 8);
/* 307 */         stringSize += length + 8;
/*     */       }
/*     */ 
/* 326 */       int entryCount = this.entries.size();
/* 327 */       int keyOffset = (int)out.getFilePointer();
/* 328 */       int resourceID = 0;
/*     */       int entryLength;
/*     */       int nameLength;
/* 331 */       if (this.databaseVersion.equals("V1.1")) {
/* 332 */         nameLength = 32;
/* 333 */         entryLength = 40;
/*     */       } else {
/* 335 */         nameLength = 16;
/* 336 */         entryLength = 24;
/*     */       }
/*     */ 
/* 339 */       byte[] keyBuffer = new byte[entryLength];
/* 340 */       for (ResourceEntry entry : this.entries) {
/* 341 */         byte[] nameBytes = entry.getResourceName().getBytes();
/* 342 */         if (nameBytes.length > nameLength) {
/* 343 */           throw new DBException("Resource name '" + entry.getResourceName() + "' is too long");
/*     */         }
/*     */
                  int index;
/* 346 */         for (index = 0; index < nameBytes.length; index++) {
/* 347 */           keyBuffer[index] = nameBytes[index];
/*     */         }
/* 349 */         for (; index < nameLength; index++) {
/* 350 */           keyBuffer[index] = 0;
/*     */         }
/* 352 */         setInteger(resourceID, keyBuffer, nameLength);
/* 353 */         setShort(entry.getResourceType(), keyBuffer, nameLength + 4);
/* 354 */         setShort(0, keyBuffer, nameLength + 6);
/* 355 */         out.write(keyBuffer);
/* 356 */         resourceID++;
/*     */       }
/*     */ 
/* 359 */       int resourceOffset = (int)out.getFilePointer();
/* 360 */       int dataOffset = resourceOffset + entryCount * 8;
/*     */ 
/* 368 */       for (ResourceEntry entry : this.entries) {
/* 369 */         int length = entry.getLength();
/* 370 */         setInteger(dataOffset, buffer, 0);
/* 371 */         setInteger(length, buffer, 4);
/* 372 */         out.write(buffer, 0, 8);
/* 373 */         dataOffset += length;
/*     */       }
/*     */ 
/* 379 */       buffer = new byte[4096];
/* 380 */       for (ResourceEntry entry : this.entries) {
/* 381 */         in = new RandomAccessFile(entry.getFile(), "r");
/* 382 */         in.seek(entry.getOffset());
/* 383 */         int residualLength = entry.getLength();
/* 384 */         while (residualLength > 0) {
/* 385 */           int length = Math.min(residualLength, buffer.length);
/* 386 */           int count = in.read(buffer, 0, length);
/* 387 */           if (count != length) {
/* 388 */             throw new DBException("Data truncated for resource " + entry.getName());
/*     */           }
/* 390 */           out.write(buffer, 0, count);
/* 391 */           residualLength -= count;
/*     */         }
/*     */ 
/* 394 */         in.close();
/* 395 */         in = null;
/*     */       }
/*     */ 
/* 413 */       Calendar calendar = new GregorianCalendar();
/* 414 */       calendar.setTime(new Date());
/*     */ 
/* 416 */       byte[] typeBytes = this.databaseType.getBytes();
/* 417 */       for (int i = 0; i < 4; i++) {
/* 418 */         header[i] = typeBytes[i];
/*     */       }
/* 420 */       byte[] versionBytes = this.databaseVersion.getBytes();
/* 421 */       for (int i = 0; i < 4; i++) {
/* 422 */         header[(i + 4)] = versionBytes[i];
/*     */       }
/* 424 */       setInteger(stringCount, header, 8);
/* 425 */       setInteger(stringSize, header, 12);
/* 426 */       setInteger(entryCount, header, 16);
/* 427 */       setInteger(stringOffset, header, 20);
/* 428 */       setInteger(keyOffset, header, 24);
/* 429 */       setInteger(resourceOffset, header, 28);
/* 430 */       setInteger(calendar.get(1) - 1970, header, 32);
/* 431 */       setInteger(calendar.get(6) - 1, header, 36);
/* 432 */       setInteger(this.description.getStringReference(), header, 40);
/*     */ 
/* 434 */       out.seek(0L);
/* 435 */       out.write(header, 0, 44);
/* 436 */       out.close();
/* 437 */       out = null;
/*     */ 
/* 442 */       if ((this.file.exists()) && 
/* 443 */         (!this.file.delete())) {
/* 444 */         throw new IOException("Unable to delete " + this.file.getName());
/*     */       }
/* 446 */       if (!outputFile.renameTo(this.file)) {
/* 447 */         throw new IOException("Unable to rename " + outputFile.getName() + " to " + this.file.getName());
/*     */       }
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/* 454 */       if (in != null) {
/* 455 */         in.close();
/*     */       }
/*     */ 
/* 460 */       if (out != null) {
/* 461 */         out.close();
/* 462 */         if (outputFile.exists())
/* 463 */           outputFile.delete();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 474 */     return this.file.getName();
/*     */   }
/*     */ 
/*     */   public String getPath()
/*     */   {
/* 483 */     return this.file.getPath();
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 492 */     return this.databaseType;
/*     */   }
/*     */ 
/*     */   public void setType(String type)
/*     */   {
/* 501 */     boolean validType = false;
/* 502 */     for (int i = 0; i < databaseTypes.length; i++) {
/* 503 */       if (type.equals(databaseTypes[i])) {
/* 504 */         validType = true;
/* 505 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 509 */     if (!validType) {
/* 510 */       throw new IllegalArgumentException("Database type '" + type + "' is not supported");
/*     */     }
/* 512 */     this.databaseType = type;
/*     */   }
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 521 */     return this.databaseVersion;
/*     */   }
/*     */ 
/*     */   public void setVersion(String version)
/*     */   {
/* 530 */     boolean validVersion = false;
/* 531 */     for (int i = 0; i < databaseVersions.length; i++) {
/* 532 */       if (version.equals(databaseVersions[i])) {
/* 533 */         validVersion = true;
/* 534 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 538 */     if (!validVersion) {
/* 539 */       throw new IllegalArgumentException("Database version '" + version + "' is not supported");
/*     */     }
/* 541 */     this.databaseVersion = version;
/*     */   }
/*     */ 
/*     */   public LocalizedString getDescription()
/*     */   {
/* 550 */     return this.description;
/*     */   }
/*     */ 
/*     */   public int getEntryCount()
/*     */   {
/* 559 */     return this.entries.size();
/*     */   }
/*     */ 
/*     */   public List<ResourceEntry> getEntries()
/*     */   {
/* 568 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public ResourceEntry getEntry(int index)
/*     */   {
/*     */     ResourceEntry entry;
/* 579 */     if (index < this.entries.size())
/* 580 */       entry = (ResourceEntry)this.entries.get(index);
/*     */     else {
/* 582 */       entry = null;
/*     */     }
/* 584 */     return entry;
/*     */   }
/*     */ 
/*     */   public ResourceEntry getEntry(String entryName)
/*     */   {
/* 594 */     return (ResourceEntry)this.entryMap.get(entryName.toLowerCase());
/*     */   }
/*     */ 
/*     */   public int addEntry(ResourceEntry entry)
/*     */   {
/* 606 */     ResourceEntry oldEntry = (ResourceEntry)this.entryMap.get(entry.getName());
/*     */     int index;
/* 607 */     if (oldEntry != null) {
/* 608 */       index = this.entries.indexOf(oldEntry);
/* 609 */       this.entries.set(index, entry);
/*     */     } else {
/* 611 */       index = this.entries.size();
/* 612 */       this.entries.add(entry);
/*     */     }
/*     */ 
/* 615 */     this.entryMap.put(entry.getName(), entry);
/* 616 */     return index;
/*     */   }
/*     */ 
/*     */   public int removeEntry(ResourceEntry entry)
/*     */   {
/* 627 */     ResourceEntry oldEntry = (ResourceEntry)this.entryMap.get(entry.getName());
/*     */     int index;
/* 628 */     if (oldEntry == null) {
/* 629 */       index = -1;
/*     */     } else {
/* 631 */       index = this.entries.indexOf(oldEntry);
/* 632 */       this.entries.remove(index);
/* 633 */       this.entryMap.remove(entry.getName());
/*     */     }
/*     */ 
/* 636 */     return index;
/*     */   }
/*     */ 
/*     */   public void removeEntry(int index)
/*     */   {
/* 645 */     ResourceEntry entry = (ResourceEntry)this.entries.remove(index);
/* 646 */     this.entryMap.remove(entry.getName());
/*     */   }
/*     */ 
/*     */   private int getShort(byte[] buffer, int offset)
/*     */   {
/* 657 */     return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8;
/*     */   }
/*     */ 
/*     */   private void setShort(int number, byte[] buffer, int offset)
/*     */   {
/* 668 */     buffer[offset] = ((byte)number);
/* 669 */     buffer[(offset + 1)] = ((byte)(number >>> 8));
/*     */   }
/*     */ 
/*     */   private int getInteger(byte[] buffer, int offset)
/*     */   {
/* 680 */     return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   private void setInteger(int number, byte[] buffer, int offset)
/*     */   {
/* 692 */     buffer[offset] = ((byte)number);
/* 693 */     buffer[(offset + 1)] = ((byte)(number >>> 8));
/* 694 */     buffer[(offset + 2)] = ((byte)(number >>> 16));
/* 695 */     buffer[(offset + 3)] = ((byte)(number >>> 24));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 704 */     return this.file.getPath();
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.ResourceDatabase
 * JD-Core Version:    0.6.2
 */