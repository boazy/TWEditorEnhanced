/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ 
/*     */ public class StringsDatabase
/*     */ {
/*     */   private File file;
/*     */   private RandomAccessFile in;
/*     */   private int stringCount;
/*     */   private int entryOffset;
/*     */   private int stringOffset;
/*     */   private int languageID;
/*     */ 
/*     */   public StringsDatabase(String filePath)
/*     */     throws DBException, IOException
/*     */   {
/*  37 */     this(new File(filePath));
/*     */   }
/*     */ 
/*     */   public StringsDatabase(File file)
/*     */     throws DBException, IOException
/*     */   {
/*  48 */     this.file = file;
/*  49 */     this.in = new RandomAccessFile(file, "r");
/*  50 */     readHeader();
/*     */   }
/*     */ 
/*     */   private void readHeader()
/*     */     throws DBException, IOException
/*     */   {
/*  70 */     byte[] buffer = new byte[20];
/*  71 */     int count = this.in.read(buffer);
/*  72 */     if (count != buffer.length) {
/*  73 */       throw new DBException("TLK header truncated");
/*     */     }
/*  75 */     String type = new String(buffer, 0, 4);
/*  76 */     String version = new String(buffer, 4, 4);
/*  77 */     if (!type.equals("TLK ")) {
/*  78 */       throw new DBException(new StringBuilder().append("File type '").append(type).append("' is not supported").toString());
/*     */     }
/*  80 */     if (!version.equals("V3.0")) {
/*  81 */       throw new DBException(new StringBuilder().append("File version '").append(version).append("' is not supported").toString());
/*     */     }
/*  83 */     this.languageID = getInteger(buffer, 8);
/*  84 */     this.stringCount = getInteger(buffer, 12);
/*  85 */     this.entryOffset = 20;
/*  86 */     this.stringOffset = getInteger(buffer, 16);
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  95 */     return this.file.getName();
/*     */   }
/*     */ 
/*     */   public int getLanguageID()
/*     */   {
/* 104 */     return this.languageID;
/*     */   }
/*     */ 
/*     */   public String getString(int stringRef)
/*     */   {
/* 116 */     String string = null;
/*     */     try
/*     */     {
/* 122 */       int refid = stringRef & 0xFFFFFF;
/* 123 */       if (refid < this.stringCount) {
/* 124 */         byte[] buffer = new byte[40];
/* 125 */         this.in.seek(this.entryOffset + refid * 40);
/* 126 */         int count = this.in.read(buffer);
/* 127 */         if (count != buffer.length) {
/* 128 */           throw new DBException(new StringBuilder().append("String entry truncated for reference ").append(refid).toString());
/*     */         }
/*     */ 
/* 140 */         if ((buffer[0] & 0x1) != 0) {
/* 141 */           int offset = getInteger(buffer, 28);
/* 142 */           int length = getInteger(buffer, 32);
/* 143 */           byte[] data = new byte[length];
/* 144 */           this.in.seek(this.stringOffset + offset);
/* 145 */           count = this.in.read(data);
/* 146 */           if (count != length) {
/* 147 */             throw new DBException(new StringBuilder().append("String data truncated for reference ").append(refid).toString());
/*     */           }
/* 149 */           string = new String(data, "UTF-8");
/*     */         }
/*     */       }
/*     */     } catch (DBException exc) {
/* 153 */       Main.logException("String database format error", exc);
/*     */     } catch (IOException exc) {
/* 155 */       Main.logException("Unable to read string database", exc);
/*     */     }
/*     */ 
/* 158 */     return string != null ? string : new String();
/*     */   }
/*     */ 
/*     */   public String getLabel(int stringRef)
/*     */   {
/* 169 */     StringBuilder string = new StringBuilder(getString(stringRef).trim());
/*     */ 
/* 174 */     int sep = string.length() - 1;
/* 175 */     if (sep > 0) {
/* 176 */       char c = string.charAt(sep);
/* 177 */       if ((c == '.') || (c == ':')) {
/* 178 */         string.deleteCharAt(sep);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 184 */     int index = 0;
/*     */     while (true) {
/* 186 */       sep = string.indexOf("<", index);
/* 187 */       if (sep < 0) {
/*     */         break;
/*     */       }
/* 190 */       index = sep;
/* 191 */       sep = string.indexOf(">", index);
/* 192 */       if (sep < 0) {
/*     */         break;
/*     */       }
/* 195 */       string.delete(index, sep + 1);
/*     */     }
/*     */ 
/* 201 */     index = 0;
/*     */     while (true) {
/* 203 */       sep = string.indexOf("{", index);
/* 204 */       if (sep < 0) {
/*     */         break;
/*     */       }
/* 207 */       index = sep;
/* 208 */       sep = string.indexOf("}", index);
/* 209 */       if (sep < 0) {
/*     */         break;
/*     */       }
/* 212 */       string.delete(index, sep + 1);
/*     */     }
/*     */ 
/* 215 */     return string.toString();
/*     */   }
/*     */ 
/*     */   public String getHeading(int stringRef)
/*     */   {
/* 227 */     String heading = null;
/* 228 */     String string = getString(stringRef).trim();
/* 229 */     int start = string.indexOf("<cHEADER>");
/* 230 */     if (start < 0)
/* 231 */       start = string.indexOf("<cHeader>");
/* 232 */     if (start < 0)
/* 233 */       start = string.indexOf("<cBOLD>");
/* 234 */     if (start < 0)
/* 235 */       start = string.indexOf("<cBold>");
/* 236 */     if (start >= 0) {
/* 237 */       start = string.indexOf(62, start) + 1;
/* 238 */       int stop = string.indexOf("</c>", start);
/* 239 */       if (stop > start) {
/* 240 */         heading = string.substring(start, stop);
/*     */       }
/*     */     }
/* 243 */     return heading != null ? heading : string;
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/*     */     try
/*     */     {
/* 251 */       if (this.in != null) {
/* 252 */         this.in.close();
/* 253 */         this.in = null;
/*     */       }
/*     */     } catch (IOException exc) {
/* 256 */       this.in = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getInteger(byte[] buffer, int offset)
/*     */   {
/* 268 */     return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.StringsDatabase
 * JD-Core Version:    0.6.2
 */