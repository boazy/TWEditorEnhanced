/*      */ package TWEditor;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ 
/*      */ public class Database
/*      */ {
/*      */   private File file;
/*      */   private String name;
/*      */   private String fileType;
/*      */   private String fileVersion;
/*      */   private DBElement topLevelStruct;
/*      */   private byte[] structBuffer;
/*      */   private int structArraySize;
/*      */   private int structArrayCount;
/*      */   private byte[] fieldBuffer;
/*      */   private int fieldArraySize;
/*      */   private int fieldArrayCount;
/*      */   private byte[] labelBuffer;
/*      */   private int labelArraySize;
/*      */   private int labelArrayCount;
/*      */   private byte[] fieldDataBuffer;
/*      */   private int fieldDataSize;
/*      */   private int fieldDataLength;
/*      */   private byte[] fieldIndicesBuffer;
/*      */   private int fieldIndicesSize;
/*      */   private int fieldIndicesLength;
/*      */   private byte[] listIndicesBuffer;
/*      */   private int listIndicesSize;
/*      */   private int listIndicesLength;
/*      */ 
/*      */   public Database()
/*      */   {
/*   86 */     this.name = new String();
/*      */   }
/*      */ 
/*      */   public Database(String filePath)
/*      */   {
/*   95 */     this(new File(filePath));
/*      */   }
/*      */ 
/*      */   public Database(File file)
/*      */   {
/*  104 */     this.file = file;
/*  105 */     this.name = file.getName();
/*      */   }
/*      */ 
/*      */   public File getFile()
/*      */   {
/*  114 */     return this.file;
/*      */   }
/*      */ 
/*      */   public String getName()
/*      */   {
/*  123 */     return this.name;
/*      */   }
/*      */ 
/*      */   public void setName(String name)
/*      */   {
/*  132 */     this.name = name;
/*      */   }
/*      */ 
/*      */   public String getType()
/*      */   {
/*  141 */     return this.fileType;
/*      */   }
/*      */ 
/*      */   public void setType(String type)
/*      */   {
/*  150 */     if (type.length() != 4) {
/*  151 */       throw new IllegalArgumentException("The file type is not 4 characters");
/*      */     }
/*  153 */     this.fileType = type;
/*      */   }
/*      */ 
/*      */   public String getVersion()
/*      */   {
/*  162 */     return this.fileVersion;
/*      */   }
/*      */ 
/*      */   public void setVersion(String version)
/*      */   {
/*  171 */     if ((!version.equals("V3.2")) && (!version.equals("V3.3"))) {
/*  172 */       throw new IllegalArgumentException("File version " + version + " is not supported");
/*      */     }
/*  174 */     this.fileVersion = version;
/*      */   }
/*      */ 
/*      */   public DBElement getTopLevelStruct()
/*      */   {
/*  184 */     return this.topLevelStruct;
/*      */   }
/*      */ 
/*      */   public void setTopLevelStruct(DBElement struct)
/*      */   {
/*  193 */     if (struct.getType() != 14) {
/*  194 */       throw new IllegalArgumentException("Database element is not a structure");
/*      */     }
/*  196 */     this.topLevelStruct = struct;
/*      */   }
/*      */ 
/*      */   public void load()
/*      */     throws DBException, IOException
/*      */   {
/*  206 */     if (this.file == null) {
/*  207 */       throw new IllegalStateException("No database file is available");
/*      */     }
/*  209 */     FileInputStream in = new FileInputStream(this.file);
/*      */     try {
/*  211 */       load(in);
/*      */     } finally {
/*  213 */       in.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void load(InputStream in)
/*      */     throws DBException, IOException
/*      */   {
/*      */     try
/*      */     {
/*  232 */       byte[] headerBuffer = new byte[56];
/*  233 */       int count = in.read(headerBuffer);
/*  234 */       if (count != 56) {
/*  235 */         throw new DBException(this.name + ": GFF header is too short");
/*      */       }
/*      */ 
/*  254 */       this.fileType = new String(headerBuffer, 0, 4);
/*  255 */       this.fileVersion = new String(headerBuffer, 4, 4);
/*  256 */       if ((!this.fileVersion.equals("V3.2")) && (!this.fileVersion.equals("V3.3"))) {
/*  257 */         throw new DBException(this.name + ": GFF version " + this.fileVersion + " is not supported");
/*      */       }
/*  259 */       int structBaseOffset = getInteger(headerBuffer, 8);
/*  260 */       this.structArrayCount = getInteger(headerBuffer, 12);
/*  261 */       this.structArraySize = this.structArrayCount;
/*  262 */       int fieldBaseOffset = getInteger(headerBuffer, 16);
/*  263 */       this.fieldArrayCount = getInteger(headerBuffer, 20);
/*  264 */       this.fieldArraySize = this.fieldArrayCount;
/*  265 */       int labelBaseOffset = getInteger(headerBuffer, 24);
/*  266 */       this.labelArrayCount = getInteger(headerBuffer, 28);
/*  267 */       this.labelArraySize = this.labelArrayCount;
/*  268 */       int fieldDataOffset = getInteger(headerBuffer, 32);
/*  269 */       this.fieldDataLength = getInteger(headerBuffer, 36);
/*  270 */       this.fieldDataSize = this.fieldDataLength;
/*  271 */       int fieldIndicesOffset = getInteger(headerBuffer, 40);
/*  272 */       this.fieldIndicesLength = getInteger(headerBuffer, 44);
/*  273 */       this.fieldIndicesSize = this.fieldIndicesLength;
/*  274 */       int listIndicesOffset = getInteger(headerBuffer, 48);
/*  275 */       this.listIndicesLength = getInteger(headerBuffer, 52);
/*  276 */       this.listIndicesSize = this.listIndicesLength;
/*      */ 
/*  282 */       if (this.structArrayCount < 1) {
/*  283 */         throw new DBException(this.name + ": GFF file contains no structures");
/*      */       }
/*  285 */       int size = 12 * this.structArraySize;
/*  286 */       this.structBuffer = new byte[size];
/*  287 */       count = in.read(this.structBuffer);
/*  288 */       if (count != size) {
/*  289 */         throw new DBException(this.name + ": Structure array data truncated");
/*      */       }
/*      */ 
/*  294 */       if (this.fieldArrayCount > 0) {
/*  295 */         size = 12 * this.fieldArraySize;
/*  296 */         this.fieldBuffer = new byte[size];
/*  297 */         count = in.read(this.fieldBuffer);
/*  298 */         if (count != size) {
/*  299 */           throw new DBException(this.name + ": Field array data truncated");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  305 */       if (this.labelArrayCount > 0) {
/*  306 */         size = 16 * this.labelArraySize;
/*  307 */         this.labelBuffer = new byte[size];
/*  308 */         count = in.read(this.labelBuffer);
/*  309 */         if (count != size) {
/*  310 */           throw new DBException(this.name + ": Label array data truncated");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  316 */       if (this.fieldDataLength > 0) {
/*  317 */         this.fieldDataBuffer = new byte[this.fieldDataSize];
/*  318 */         count = in.read(this.fieldDataBuffer);
/*  319 */         if (count != this.fieldDataSize) {
/*  320 */           throw new DBException(this.name + ": Field data truncated");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  326 */       if (this.fieldIndicesLength > 0) {
/*  327 */         this.fieldIndicesBuffer = new byte[this.fieldIndicesSize];
/*  328 */         count = in.read(this.fieldIndicesBuffer);
/*  329 */         if (count != this.fieldIndicesSize) {
/*  330 */           throw new DBException(this.name + ": Field indices truncated");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  336 */       if (this.listIndicesLength > 0) {
/*  337 */         this.listIndicesBuffer = new byte[this.listIndicesSize];
/*  338 */         count = in.read(this.listIndicesBuffer);
/*  339 */         if (count != this.listIndicesSize) {
/*  340 */           throw new DBException(this.name + ": List indices truncated");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  347 */       this.topLevelStruct = decodeStruct(new String(), 0);
/*      */     }
/*      */     finally
/*      */     {
/*  354 */       this.structBuffer = null;
/*  355 */       this.fieldBuffer = null;
/*  356 */       this.labelBuffer = null;
/*  357 */       this.fieldDataBuffer = null;
/*  358 */       this.fieldIndicesBuffer = null;
/*  359 */       this.listIndicesBuffer = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private DBElement decodeField(int index)
/*      */     throws DBException
/*      */   {
/*  371 */     if (index >= this.fieldArrayCount) {
/*  372 */       throw new DBException(this.name + ": Field index " + index + " exceeds array size");
/*      */     }
/*      */ 
/*  380 */     int offset = 12 * index;
/*  381 */     int fieldType = getInteger(this.fieldBuffer, offset);
/*  382 */     int labelIndex = getInteger(this.fieldBuffer, offset + 4);
/*  383 */     int dataOffset = getInteger(this.fieldBuffer, offset + 8);
/*  384 */     if (labelIndex >= this.labelArrayCount) {
/*  385 */       throw new DBException(this.name + ": Label index " + labelIndex + " exceeds array size");
/*      */     }
/*      */ 
/*  391 */     int labelOffset = 16 * labelIndex;
               int labelLength;
/*  393 */     for (labelLength = 16; (labelLength > 0) &&
/*  394 */       (this.labelBuffer[(labelOffset + labelLength - 1)] == 0); labelLength--);
/*  397 */     String label = new String(this.labelBuffer, labelOffset, labelLength);
/*      */     DBElement element;
/*  406 */     switch (fieldType) {
/*      */     case 15:
/*  408 */       element = decodeList(label, dataOffset);
/*  409 */       break;
/*      */     case 14:
/*  412 */       element = decodeStruct(label, dataOffset);
/*  413 */       break;
/*      */     case 0:
/*  416 */       element = new DBElement(fieldType, 0, label, new Integer(dataOffset & 0xFF));
/*  417 */       break;
/*      */     case 1:
/*  420 */       element = new DBElement(fieldType, 0, label, new Character((char)dataOffset));
/*  421 */       break;
/*      */     case 2:
/*  424 */       element = new DBElement(fieldType, 0, label, new Integer(dataOffset & 0xFFFF));
/*  425 */       break;
/*      */     case 3:
/*  428 */       dataOffset &= 65535;
/*  429 */       if (dataOffset > 32767)
/*  430 */         dataOffset |= -65536;
/*  431 */       element = new DBElement(fieldType, 0, label, new Integer(dataOffset));
/*  432 */       break;
/*      */     case 4:
/*  435 */       element = new DBElement(fieldType, 0, label, new Long(dataOffset & 0xFFFFFFFF));
/*  436 */       break;
/*      */     case 5:
/*  439 */       element = new DBElement(fieldType, 0, label, new Integer(dataOffset));
/*  440 */       break;
/*      */     case 6:
/*      */     case 7:
/*  444 */       if (dataOffset + 8 > this.fieldDataLength) {
/*  445 */         throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
/*      */       }
/*  447 */       long longValue = this.fieldDataBuffer[(dataOffset + 0)] & 0xFF | (this.fieldDataBuffer[(dataOffset + 1)] & 0xFF) << 8 | (this.fieldDataBuffer[(dataOffset + 2)] & 0xFF) << 16 | (this.fieldDataBuffer[(dataOffset + 3)] & 0xFF) << 24 | (this.fieldDataBuffer[(dataOffset + 4)] & 0xFF) << 32 | (this.fieldDataBuffer[(dataOffset + 5)] & 0xFF) << 40 | (this.fieldDataBuffer[(dataOffset + 6)] & 0xFF) << 48 | (this.fieldDataBuffer[(dataOffset + 7)] & 0xFF) << 56;
/*      */ 
/*  455 */       if ((fieldType == 6) && (longValue < 0L)) {
/*  456 */         throw new DBException("DWORD64 value is too large for Java representation");
/*      */       }
/*  458 */       element = new DBElement(fieldType, 0, label, new Long(longValue));
/*  459 */       break;
/*      */     case 8:
/*  462 */       element = new DBElement(fieldType, 0, label, new Float(Float.intBitsToFloat(dataOffset)));
/*  463 */       break;
/*      */     case 9:
/*  466 */       if (dataOffset + 8 > this.fieldDataLength) {
/*  467 */         throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
/*      */       }
/*  469 */       long longBits = this.fieldDataBuffer[(dataOffset + 0)] & 0xFF | (this.fieldDataBuffer[(dataOffset + 1)] & 0xFF) << 8 | (this.fieldDataBuffer[(dataOffset + 2)] & 0xFF) << 16 | (this.fieldDataBuffer[(dataOffset + 3)] & 0xFF) << 24 | (this.fieldDataBuffer[(dataOffset + 4)] & 0xFF) << 32 | (this.fieldDataBuffer[(dataOffset + 5)] & 0xFF) << 40 | (this.fieldDataBuffer[(dataOffset + 6)] & 0xFF) << 48 | (this.fieldDataBuffer[(dataOffset + 7)] & 0xFF) << 56;
/*      */ 
/*  477 */       element = new DBElement(fieldType, 0, label, new Double(Double.longBitsToDouble(longBits)));
/*  478 */       break;
/*      */     case 13:
/*  487 */       if (dataOffset + 4 > this.fieldDataLength) {
/*  488 */         throw new DBException("Field data offset " + dataOffset + " exceeds field data");
/*      */       }
/*  490 */       int byteLength = getInteger(this.fieldDataBuffer, dataOffset);
/*  491 */       dataOffset += 4;
/*  492 */       if (dataOffset + byteLength > this.fieldDataLength) {
/*  493 */         throw new DBException("Void data length " + byteLength + " exceeds field data");
/*      */       }
/*  495 */       byte[] byteData = new byte[byteLength];
/*  496 */       if (byteLength > 0) {
/*  497 */         System.arraycopy(this.fieldDataBuffer, dataOffset, byteData, 0, byteLength);
/*      */       }
/*  499 */       element = new DBElement(fieldType, 0, label, byteData);
/*  500 */       break;
/*      */     case 11:
/*  509 */       if (dataOffset + 1 > this.fieldDataLength) {
/*  510 */         throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
/*      */       }
/*  512 */       int resourceLength = this.fieldDataBuffer[dataOffset] & 0xFF;
/*  513 */       dataOffset++;
/*  514 */       if (dataOffset + resourceLength > this.fieldDataLength)
/*  515 */         throw new DBException(this.name + ": Resource length " + resourceLength + " exceeds field data");
/*      */       String resourceString;
/*  518 */       if (resourceLength > 0)
/*      */         try {
/*  520 */           resourceString = new String(this.fieldDataBuffer, dataOffset, resourceLength, "UTF-8");
/*      */         } catch (UnsupportedEncodingException exc) {
/*  522 */           throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
/*      */         }
/*      */       else {
/*  525 */         resourceString = new String();
/*      */       }
/*      */ 
/*  528 */       element = new DBElement(fieldType, 0, label, resourceString);
/*  529 */       break;
/*      */     case 10:
/*  538 */       if (dataOffset + 4 > this.fieldDataLength) {
/*  539 */         throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
/*      */       }
/*  541 */       int stringLength = getInteger(this.fieldDataBuffer, dataOffset);
/*  542 */       dataOffset += 4;
/*  543 */       if (dataOffset + stringLength > this.fieldDataLength)
/*  544 */         throw new DBException(this.name + ": String length " + stringLength + " exceeds field data");
/*      */       String string;
/*  547 */       if (stringLength > 0)
/*      */         try {
/*  549 */           string = new String(this.fieldDataBuffer, dataOffset, stringLength, "UTF-8");
/*      */         } catch (UnsupportedEncodingException exc) {
/*  551 */           throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
/*      */         }
/*      */       else {
/*  554 */         string = new String();
/*      */       }
/*      */ 
/*  557 */       element = new DBElement(fieldType, 0, label, string);
/*  558 */       break;
/*      */     case 12:
/*  568 */       if (dataOffset + 12 > this.fieldDataLength) {
/*  569 */         throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
/*      */       }
/*  571 */       int localizedLength = getInteger(this.fieldDataBuffer, dataOffset);
/*  572 */       int stringReference = getInteger(this.fieldDataBuffer, dataOffset + 4);
/*  573 */       int substringCount = getInteger(this.fieldDataBuffer, dataOffset + 8);
/*  574 */       dataOffset += 12;
/*  575 */       localizedLength -= 8;
/*  576 */       LocalizedString localizedString = new LocalizedString(stringReference);
/*      */ 
/*  584 */       for (int i = 0; i < substringCount; i++) {
/*  585 */         if (dataOffset + 8 > this.fieldDataLength) {
/*  586 */           throw new DBException(this.name + ": Localized substring " + i + " exceeds field data");
/*      */         }
/*  588 */         if (localizedLength < 8) {
/*  589 */           throw new DBException(this.name + ": Localized substring " + i + " exceeds localized string");
/*      */         }
/*  591 */         int stringID = getInteger(this.fieldDataBuffer, dataOffset);
/*  592 */         int substringLength = getInteger(this.fieldDataBuffer, dataOffset + 4);
/*  593 */         dataOffset += 8;
/*  594 */         localizedLength -= 8;
/*  595 */         if (dataOffset + substringLength > this.fieldDataLength) {
/*  596 */           throw new DBException(this.name + ": Localized substring " + i + " exceeds field data");
/*      */         }
/*  598 */         if (substringLength > localizedLength)
/*  599 */           throw new DBException(this.name + ": Localized substring " + i + " exceeds localized string");
/*      */         String substring;
/*  602 */         if (substringLength > 0)
/*      */           try {
/*  604 */             substring = new String(this.fieldDataBuffer, dataOffset, substringLength, "UTF-8");
/*      */           } catch (UnsupportedEncodingException exc) {
/*  606 */             throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
/*      */           }
/*      */         else {
/*  609 */           substring = new String();
/*      */         }
/*      */ 
/*  612 */         localizedString.addSubstring(new LocalizedSubstring(substring, stringID / 2, stringID & 0x1));
/*  613 */         dataOffset += substringLength;
/*  614 */         localizedLength -= substringLength;
/*      */       }
/*      */ 
/*  617 */       element = new DBElement(fieldType, 0, label, localizedString);
/*  618 */       break;
/*      */     default:
/*  621 */       throw new DBException(this.name + ": Unrecognized field type " + fieldType);
/*      */     }
/*      */ 
/*  624 */     return element;
/*      */   }
/*      */ 
/*      */   private DBElement decodeStruct(String label, int index)
/*      */     throws DBException
/*      */   {
/*  636 */     if (index >= this.structArrayCount) {
/*  637 */       throw new DBException(this.name + ": Structure index " + index + " exceeds array size");
/*      */     }
/*      */ 
/*  645 */     int offset = 12 * index;
/*  646 */     int id = getInteger(this.structBuffer, offset);
/*  647 */     int fieldIndex = getInteger(this.structBuffer, offset + 4);
/*  648 */     int fieldCount = getInteger(this.structBuffer, offset + 8);
/*  649 */     DBList list = new DBList(fieldCount);
/*  650 */     if (fieldCount == 1) {
/*  651 */       DBElement field = decodeField(fieldIndex);
/*  652 */       list.addElement(field);
/*  653 */     } else if (fieldCount > 1) {
/*  654 */       offset = fieldIndex;
/*  655 */       for (int i = 0; i < fieldCount; i++) {
/*  656 */         if (offset + 4 > this.fieldIndicesLength) {
/*  657 */           throw new DBException("Field indices offset " + offset + " exceeds indices size");
/*      */         }
/*  659 */         fieldIndex = getInteger(this.fieldIndicesBuffer, offset);
/*  660 */         offset += 4;
/*  661 */         DBElement field = decodeField(fieldIndex);
/*  662 */         list.addElement(field);
/*      */       }
/*      */     }
/*      */ 
/*  666 */     return new DBElement(14, id, label, list);
/*      */   }
/*      */ 
/*      */   private DBElement decodeList(String label, int offset)
/*      */     throws DBException
/*      */   {
/*  678 */     if (offset + 4 > this.listIndicesLength) {
/*  679 */       throw new DBException(this.name + ": List indices offset " + offset + " exceeds indices size");
/*      */     }
/*      */ 
/*  687 */     int structCount = getInteger(this.listIndicesBuffer, offset);
/*  688 */     DBList list = new DBList(structCount);
/*  689 */     int listOffset = offset + 4;
/*  690 */     for (int i = 0; i < structCount; i++) {
/*  691 */       if (listOffset + 4 > this.listIndicesLength) {
/*  692 */         throw new DBException(this.name + ": List indices offset " + listOffset + " exceeds indices size");
/*      */       }
/*  694 */       int structIndex = getInteger(this.listIndicesBuffer, listOffset);
/*  695 */       listOffset += 4;
/*  696 */       list.addElement(decodeStruct(new String(), structIndex));
/*      */     }
/*      */ 
/*  699 */     return new DBElement(15, 0, label, list);
/*      */   }
/*      */ 
/*      */   public void save()
/*      */     throws DBException, IOException
/*      */   {
/*  709 */     File tmpFile = null;
/*  710 */     FileOutputStream out = null;
/*  711 */     if (this.file == null) {
/*  712 */       throw new IllegalStateException("No database file is available");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  718 */       tmpFile = new File(this.file.getPath() + ".new");
/*  719 */       out = new FileOutputStream(tmpFile);
/*  720 */       save(out);
/*  721 */       out.close();
/*  722 */       out = null;
/*      */ 
/*  727 */       if ((this.file.exists()) && 
/*  728 */         (!this.file.delete())) {
/*  729 */         throw new IOException("Unable to delete " + this.file.getName());
/*      */       }
/*  731 */       if (!tmpFile.renameTo(this.file)) {
/*  732 */         throw new IOException("Unable to rename " + tmpFile.getName() + " to " + this.file.getName());
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  739 */       if (tmpFile != null) {
/*  740 */         if (out != null) {
/*  741 */           out.close();
/*      */         }
/*  743 */         if (tmpFile.exists())
/*  744 */           tmpFile.delete();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void save(OutputStream out)
/*      */     throws DBException, IOException
/*      */   {
/*      */     try
/*      */     {
/*  761 */       this.structBuffer = new byte[48000];
/*  762 */       this.structArraySize = 4000;
/*  763 */       this.structArrayCount = 0;
/*      */ 
/*  765 */       this.fieldBuffer = new byte[144000];
/*  766 */       this.fieldArraySize = 12000;
/*  767 */       this.fieldArrayCount = 0;
/*      */ 
/*  769 */       this.labelBuffer = new byte[16000];
/*  770 */       this.labelArraySize = 1000;
/*  771 */       this.labelArrayCount = 0;
/*      */ 
/*  773 */       this.fieldDataBuffer = new byte[20000];
/*  774 */       this.fieldDataSize = 20000;
/*  775 */       this.fieldDataLength = 0;
/*      */ 
/*  777 */       this.fieldIndicesBuffer = new byte[36000];
/*  778 */       this.fieldIndicesSize = 36000;
/*  779 */       this.fieldIndicesLength = 0;
/*      */ 
/*  781 */       this.listIndicesBuffer = new byte[8000];
/*  782 */       this.listIndicesSize = 8000;
/*  783 */       this.listIndicesLength = 0;
/*      */ 
/*  788 */       if (this.topLevelStruct == null) {
/*  789 */         throw new DBException(this.name + ": No top-level structure");
/*      */       }
/*  791 */       if ((this.fileType == null) || (this.fileType.length() != 4)) {
/*  792 */         throw new DBException(this.name + ": File type is not set");
/*      */       }
/*  794 */       if ((this.fileVersion == null) || (this.fileVersion.length() != 4)) {
/*  795 */         throw new DBException(this.name + ": File version is not set");
/*      */       }
/*  797 */       encodeStruct(this.topLevelStruct);
/*      */ 
/*  802 */       byte[] headerBuffer = new byte[56];
/*  803 */       byte[] buffer = this.fileType.getBytes();
/*  804 */       System.arraycopy(buffer, 0, headerBuffer, 0, 4);
/*  805 */       buffer = this.fileVersion.getBytes();
/*  806 */       System.arraycopy(buffer, 0, headerBuffer, 4, 4);
/*  807 */       int offset = 56;
/*  808 */       int structLength = 12 * this.structArrayCount;
/*  809 */       setInteger(offset, headerBuffer, 8);
/*  810 */       setInteger(this.structArrayCount, headerBuffer, 12);
/*  811 */       offset += structLength;
/*  812 */       int fieldLength = 12 * this.fieldArrayCount;
/*  813 */       setInteger(offset, headerBuffer, 16);
/*  814 */       setInteger(this.fieldArrayCount, headerBuffer, 20);
/*  815 */       offset += fieldLength;
/*  816 */       int labelLength = 16 * this.labelArrayCount;
/*  817 */       setInteger(offset, headerBuffer, 24);
/*  818 */       setInteger(this.labelArrayCount, headerBuffer, 28);
/*  819 */       offset += labelLength;
/*  820 */       setInteger(offset, headerBuffer, 32);
/*  821 */       setInteger(this.fieldDataLength, headerBuffer, 36);
/*  822 */       offset += this.fieldDataLength;
/*  823 */       setInteger(offset, headerBuffer, 40);
/*  824 */       setInteger(this.fieldIndicesLength, headerBuffer, 44);
/*  825 */       offset += this.fieldIndicesLength;
/*  826 */       setInteger(offset, headerBuffer, 48);
/*  827 */       setInteger(this.listIndicesLength, headerBuffer, 52);
/*      */ 
/*  832 */       out.write(headerBuffer);
/*  833 */       out.write(this.structBuffer, 0, structLength);
/*  834 */       if (fieldLength != 0)
/*  835 */         out.write(this.fieldBuffer, 0, fieldLength);
/*  836 */       if (labelLength != 0)
/*  837 */         out.write(this.labelBuffer, 0, labelLength);
/*  838 */       if (this.fieldDataLength != 0)
/*  839 */         out.write(this.fieldDataBuffer, 0, this.fieldDataLength);
/*  840 */       if (this.fieldIndicesLength != 0)
/*  841 */         out.write(this.fieldIndicesBuffer, 0, this.fieldIndicesLength);
/*  842 */       if (this.listIndicesLength != 0) {
/*  843 */         out.write(this.listIndicesBuffer, 0, this.listIndicesLength);
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*  850 */       this.structBuffer = null;
/*  851 */       this.fieldBuffer = null;
/*  852 */       this.labelBuffer = null;
/*  853 */       this.fieldDataBuffer = null;
/*  854 */       this.fieldIndicesBuffer = null;
/*  855 */       this.listIndicesBuffer = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private int encodeField(DBElement element)
/*      */     throws DBException
/*      */   {
/*  867 */     int fieldType = element.getType();
/*      */ 
/*  876 */     String fieldLabel = element.getLabel();
/*  877 */     if (fieldLabel.length() == 0) {
/*  878 */       throw new DBException("Field does not have a label");
/*      */     }
/*  880 */     byte[] labelBytes = fieldLabel.getBytes();
/*  881 */     byte[] label = new byte[16];
/*  882 */     boolean match = false;
/*  883 */     System.arraycopy(labelBytes, 0, label, 0, Math.min(labelBytes.length, 16));
               int labelIndex;
/*  884 */     for (labelIndex = 0; labelIndex < this.labelArrayCount; labelIndex++) {
/*  885 */       int labelOffset = labelIndex * 16;
/*  886 */       match = true;
/*  887 */       for (int i = 0; i < 16; i++) {
/*  888 */         if (this.labelBuffer[(labelOffset + i)] != label[i]) {
/*  889 */           match = false;
/*  890 */           break;
/*      */         }
/*      */       }
/*      */ 
/*  894 */       if (match) {
/*      */         break;
/*      */       }
/*      */     }
/*  898 */     if (!match) {
/*  899 */       if (this.labelArrayCount == this.labelArraySize) {
/*  900 */         this.labelArraySize += 1000;
/*  901 */         byte[] buffer = new byte[16 * this.labelArraySize];
/*  902 */         System.arraycopy(this.labelBuffer, 0, buffer, 0, this.labelArrayCount * 16);
/*  903 */         this.labelBuffer = buffer;
/*      */       }
/*      */ 
/*  906 */       labelIndex = this.labelArrayCount++;
/*  907 */       int labelOffset = labelIndex * 16;
/*  908 */       System.arraycopy(label, 0, this.labelBuffer, labelOffset, 16);
/*      */     }
/*      */ 
/*  914 */     Object fieldValue = element.getValue();
/*      */     int dataOffset;
/*  915 */     switch (fieldType) {
/*      */     case 15:
/*  917 */       dataOffset = encodeList(element);
/*  918 */       break;
/*      */     case 14:
/*  921 */       dataOffset = encodeStruct(element);
/*  922 */       break;
/*      */     case 0:
/*  925 */       dataOffset = ((Integer)fieldValue).intValue() & 0xFF;
/*  926 */       break;
/*      */     case 1:
/*  929 */       dataOffset = ((Character)fieldValue).charValue() & 0xFFFF;
/*  930 */       break;
/*      */     case 2:
/*      */     case 3:
/*  934 */       dataOffset = ((Integer)fieldValue).intValue() & 0xFFFF;
/*  935 */       break;
/*      */     case 4:
/*  938 */       dataOffset = ((Long)fieldValue).intValue();
/*  939 */       break;
/*      */     case 5:
/*  942 */       dataOffset = ((Integer)fieldValue).intValue();
/*  943 */       break;
/*      */     case 6:
/*      */     case 7:
/*  947 */       dataOffset = setFieldData(((Long)fieldValue).longValue());
/*  948 */       break;
/*      */     case 8:
/*  951 */       dataOffset = Float.floatToIntBits(((Float)fieldValue).floatValue());
/*  952 */       break;
/*      */     case 9:
/*  955 */       dataOffset = setFieldData(Double.doubleToLongBits(((Double)fieldValue).doubleValue()));
/*  956 */       break;
/*      */     case 13:
/*  965 */       byte[] voidData = (byte[])fieldValue;
/*  966 */       int voidLength = voidData.length;
/*  967 */       byte[] voidBuffer = new byte[4 + voidLength];
/*  968 */       setInteger(voidLength, voidBuffer, 0);
/*  969 */       System.arraycopy(voidData, 0, voidBuffer, 4, voidLength);
/*  970 */       dataOffset = setFieldData(voidBuffer);
/*  971 */       break;
/*      */     case 11:
/*  980 */       String resourceString = (String)fieldValue;
/*      */       byte[] resourceData;
/*      */       try
/*      */       {
/*  983 */         resourceData = resourceString.getBytes("UTF-8");
/*      */       } catch (UnsupportedEncodingException exc) {
/*  985 */         throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
/*      */       }
/*      */ 
/*  988 */       int resourceLength = resourceData.length;
/*  989 */       if (resourceLength > 255) {
/*  990 */         throw new DBException("Resource length is greater than 255");
/*      */       }
/*  992 */       byte[] resourceBuffer = new byte[1 + resourceLength];
/*  993 */       resourceBuffer[0] = ((byte)resourceLength);
/*  994 */       System.arraycopy(resourceData, 0, resourceBuffer, 1, resourceLength);
/*  995 */       dataOffset = setFieldData(resourceBuffer);
/*  996 */       break;
/*      */     case 10:
/* 1005 */       String string = (String)fieldValue;
/*      */       byte[] stringBuffer;
/* 1007 */       if (string.length() > 0) {
/*      */         byte[] stringData;
/*      */         try {
/* 1010 */           stringData = string.getBytes("UTF-8");
/*      */         } catch (UnsupportedEncodingException exc) {
/* 1012 */           throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
/*      */         }
/*      */ 
/* 1015 */         int stringLength = stringData.length;
/* 1016 */         stringBuffer = new byte[4 + stringLength];
/* 1017 */         setInteger(stringLength, stringBuffer, 0);
/* 1018 */         System.arraycopy(stringData, 0, stringBuffer, 4, stringLength);
/*      */       } else {
/* 1020 */         stringBuffer = new byte[4];
/* 1021 */         setInteger(0, stringBuffer, 0);
/*      */       }
/*      */ 
/* 1024 */       dataOffset = setFieldData(stringBuffer);
/* 1025 */       break;
/*      */     case 12:
/* 1040 */       LocalizedString localizedString = (LocalizedString)fieldValue;
/* 1041 */       int substringCount = localizedString.getSubstringCount();
/* 1042 */       int localizedLength = 8;
/* 1043 */       List substringList = new ArrayList(substringCount);
/*      */ 
/* 1045 */       for (int i = 0; i < substringCount; i++) {
/* 1046 */         LocalizedSubstring localizedSubstring = localizedString.getSubstring(i);
/* 1047 */         String substring = localizedSubstring.getString();
/*      */         byte[] substringData;
/* 1049 */         if (substring.length() > 0)
/*      */           try {
/* 1051 */             substringData = substring.getBytes("UTF-8");
/*      */           } catch (UnsupportedEncodingException exc) {
/* 1053 */             throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
/*      */           }
/*      */         else {
/* 1056 */           substringData = new byte[0];
/*      */         }
/*      */ 
/* 1059 */         substringList.add(substringData);
/* 1060 */         localizedLength += 8 + substringData.length;
/*      */       }
/*      */ 
/* 1063 */       byte[] localizedBuffer = new byte[4 + localizedLength];
/* 1064 */       setInteger(localizedLength, localizedBuffer, 0);
/* 1065 */       setInteger(localizedString.getStringReference(), localizedBuffer, 4);
/* 1066 */       setInteger(substringCount, localizedBuffer, 8);
/* 1067 */       int substringOffset = 12;
/*      */ 
/* 1069 */       for (int i = 0; i < substringCount; i++) {
/* 1070 */         LocalizedSubstring localizedSubstring = localizedString.getSubstring(i);
/* 1071 */         byte[] substringData = (byte[])substringList.get(i);
/* 1072 */         int substringLength = substringData.length;
/* 1073 */         setInteger(localizedSubstring.getLanguage() * 2 + localizedSubstring.getGender(), localizedBuffer, substringOffset);
/*      */ 
/* 1075 */         setInteger(substringLength, localizedBuffer, substringOffset + 4);
/* 1076 */         if (substringLength > 0)
/* 1077 */           System.arraycopy(substringData, 0, localizedBuffer, substringOffset + 8, substringLength);
/* 1078 */         substringOffset += 8 + substringLength;
/*      */       }
/*      */ 
/* 1081 */       dataOffset = setFieldData(localizedBuffer);
/* 1082 */       break;
/*      */     default:
/* 1085 */       throw new DBException(this.name + ": Unrecognized field type " + fieldType);
/*      */     }
/*      */ 
/* 1094 */     if (this.fieldArrayCount == this.fieldArraySize) {
/* 1095 */       this.fieldArraySize += 4000;
/* 1096 */       byte[] buffer = new byte[12 * this.fieldArraySize];
/* 1097 */       System.arraycopy(this.fieldBuffer, 0, buffer, 0, this.fieldArrayCount * 12);
/* 1098 */       this.fieldBuffer = buffer;
/*      */     }
/*      */ 
/* 1101 */     int fieldIndex = this.fieldArrayCount++;
/* 1102 */     int fieldOffset = fieldIndex * 12;
/* 1103 */     setInteger(fieldType, this.fieldBuffer, fieldOffset);
/* 1104 */     setInteger(labelIndex, this.fieldBuffer, fieldOffset + 4);
/* 1105 */     setInteger(dataOffset, this.fieldBuffer, fieldOffset + 8);
/* 1106 */     return fieldIndex;
/*      */   }
/*      */ 
/*      */   private int encodeStruct(DBElement element)
/*      */     throws DBException
/*      */   {
/* 1117 */     DBList list = (DBList)element.getValue();
/* 1118 */     int fieldCount = list.getElementCount();
/* 1119 */     int fieldOffset = 0;
/*      */ 
/* 1127 */     if (this.structArrayCount == this.structArraySize) {
/* 1128 */       this.structArraySize += 2000;
/* 1129 */       byte[] buffer = new byte[12 * this.structArraySize];
/* 1130 */       System.arraycopy(this.structBuffer, 0, buffer, 0, this.structArrayCount * 12);
/* 1131 */       this.structBuffer = buffer;
/*      */     }
/*      */ 
/* 1134 */     int structIndex = this.structArrayCount++;
/*      */ 
/* 1140 */     if (fieldCount == 1) {
/* 1141 */       fieldOffset = encodeField(list.getElement(0));
/* 1142 */     } else if (fieldCount > 1) {
/* 1143 */       int indexLength = 4 * fieldCount;
/* 1144 */       if (this.fieldIndicesLength + indexLength > this.fieldIndicesSize) {
/* 1145 */         int increment = Math.max(indexLength, 8000);
/* 1146 */         this.fieldIndicesSize += increment;
/* 1147 */         byte[] buffer = new byte[this.fieldIndicesSize];
/* 1148 */         System.arraycopy(this.fieldIndicesBuffer, 0, buffer, 0, this.fieldIndicesLength);
/* 1149 */         this.fieldIndicesBuffer = buffer;
/*      */       }
/*      */ 
/* 1152 */       fieldOffset = this.fieldIndicesLength;
/* 1153 */       this.fieldIndicesLength += indexLength;
/* 1154 */       for (int i = 0; i < fieldCount; i++) {
/* 1155 */         int fieldIndex = encodeField(list.getElement(i));
/* 1156 */         setInteger(fieldIndex, this.fieldIndicesBuffer, fieldOffset + 4 * i);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1163 */     int structOffset = structIndex * 12;
/* 1164 */     setInteger(element.getID(), this.structBuffer, structOffset);
/* 1165 */     setInteger(fieldOffset, this.structBuffer, structOffset + 4);
/* 1166 */     setInteger(fieldCount, this.structBuffer, structOffset + 8);
/* 1167 */     return structIndex;
/*      */   }
/*      */ 
/*      */   private int encodeList(DBElement element)
/*      */     throws DBException
/*      */   {
/* 1185 */     DBList list = (DBList)element.getValue();
/* 1186 */     int listCount = list.getElementCount();
/* 1187 */     int listLength = (listCount + 1) * 4;
/* 1188 */     if (this.listIndicesLength + listLength > this.listIndicesSize) {
/* 1189 */       int increment = Math.max(listLength, 2000);
/* 1190 */       this.listIndicesSize += increment;
/* 1191 */       byte[] buffer = new byte[this.listIndicesSize];
/* 1192 */       System.arraycopy(this.listIndicesBuffer, 0, buffer, 0, this.listIndicesLength);
/* 1193 */       this.listIndicesBuffer = buffer;
/*      */     }
/*      */ 
/* 1196 */     int listOffset = this.listIndicesLength;
/* 1197 */     this.listIndicesLength += listLength;
/* 1198 */     setInteger(listCount, this.listIndicesBuffer, listOffset);
/*      */ 
/* 1203 */     for (int i = 0; i < listCount; i++) {
/* 1204 */       int structIndex = encodeStruct(list.getElement(i));
/* 1205 */       setInteger(structIndex, this.listIndicesBuffer, listOffset + 4 * (i + 1));
/*      */     }
/*      */ 
/* 1208 */     return listOffset;
/*      */   }
/*      */ 
/*      */   private int setFieldData(byte[] data)
/*      */   {
/* 1218 */     int dataLength = data.length;
/* 1219 */     if (this.fieldDataLength + dataLength > this.fieldDataSize) {
/* 1220 */       int increment = Math.max(dataLength, 8000);
/* 1221 */       this.fieldDataSize += increment;
/* 1222 */       byte[] buffer = new byte[this.fieldDataSize];
/* 1223 */       System.arraycopy(this.fieldDataBuffer, 0, buffer, 0, this.fieldDataLength);
/* 1224 */       this.fieldDataBuffer = buffer;
/*      */     }
/*      */ 
/* 1227 */     int dataOffset = this.fieldDataLength;
/* 1228 */     this.fieldDataLength += dataLength;
/* 1229 */     System.arraycopy(data, 0, this.fieldDataBuffer, dataOffset, dataLength);
/* 1230 */     return dataOffset;
/*      */   }
/*      */ 
/*      */   private int setFieldData(long data)
/*      */   {
/* 1240 */     if (this.fieldDataLength + 8 > this.fieldDataSize) {
/* 1241 */       this.fieldDataSize += 8000;
/* 1242 */       byte[] buffer = new byte[this.fieldDataSize];
/* 1243 */       System.arraycopy(this.fieldDataBuffer, 0, buffer, 0, this.fieldDataLength);
/* 1244 */       this.fieldDataBuffer = buffer;
/*      */     }
/*      */ 
/* 1247 */     int dataOffset = this.fieldDataLength;
/* 1248 */     this.fieldDataLength += 8;
/* 1249 */     this.fieldDataBuffer[(dataOffset + 0)] = ((byte)(int)data);
/* 1250 */     this.fieldDataBuffer[(dataOffset + 1)] = ((byte)(int)(data >> 8));
/* 1251 */     this.fieldDataBuffer[(dataOffset + 2)] = ((byte)(int)(data >> 16));
/* 1252 */     this.fieldDataBuffer[(dataOffset + 3)] = ((byte)(int)(data >> 24));
/* 1253 */     this.fieldDataBuffer[(dataOffset + 4)] = ((byte)(int)(data >> 32));
/* 1254 */     this.fieldDataBuffer[(dataOffset + 5)] = ((byte)(int)(data >> 40));
/* 1255 */     this.fieldDataBuffer[(dataOffset + 6)] = ((byte)(int)(data >> 48));
/* 1256 */     this.fieldDataBuffer[(dataOffset + 7)] = ((byte)(int)(data >> 56));
/* 1257 */     return dataOffset;
/*      */   }
/*      */ 
/*      */   private int getInteger(byte[] buffer, int offset)
/*      */   {
/* 1268 */     return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
/*      */   }
/*      */ 
/*      */   private void setInteger(int number, byte[] buffer, int offset)
/*      */   {
/* 1280 */     buffer[offset] = ((byte)number);
/* 1281 */     buffer[(offset + 1)] = ((byte)(number >>> 8));
/* 1282 */     buffer[(offset + 2)] = ((byte)(number >>> 16));
/* 1283 */     buffer[(offset + 3)] = ((byte)(number >>> 24));
/*      */   }
/*      */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.Database
 * JD-Core Version:    0.6.2
 */