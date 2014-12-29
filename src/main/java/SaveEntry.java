/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SaveEntry
/*     */ {
/*     */   private boolean onDisk;
/*     */   private boolean compressed;
/*     */   private String resourceName;
/*     */   private String resourcePath;
/*     */   private File resourceFile;
/*     */   private long resourceOffset;
/*     */   private int resourceLength;
/*     */   private List<byte[]> resourceDataList;
/*     */ 
/*     */   public SaveEntry(String path)
/*     */   {
/*  42 */     this.resourcePath = path;
/*  43 */     int sep = this.resourcePath.lastIndexOf(Main.fileSeparator);
/*  44 */     if (sep >= 0)
/*  45 */       this.resourceName = this.resourcePath.substring(sep + 1).toLowerCase();
/*     */     else {
/*  47 */       this.resourceName = this.resourcePath.toLowerCase();
/*     */     }
/*     */ 
/*  52 */     this.resourceDataList = new ArrayList();
/*  53 */     this.onDisk = false;
/*     */ 
/*  58 */     sep = this.resourceName.lastIndexOf('.');
/*  59 */     if ((sep > 0) && (this.resourceName.substring(sep).equals(".sav")))
/*  60 */       this.compressed = true;
/*     */     else
/*  62 */       this.compressed = false;
/*     */   }
/*     */ 
/*     */   public SaveEntry(String path, File file, long offset, int length)
/*     */   {
/*  74 */     this.resourcePath = path;
/*  75 */     int sep = this.resourcePath.lastIndexOf(Main.fileSeparator);
/*  76 */     if (sep >= 0)
/*  77 */       this.resourceName = this.resourcePath.substring(sep + 1).toLowerCase();
/*     */     else {
/*  79 */       this.resourceName = this.resourcePath.toLowerCase();
/*     */     }
/*     */ 
/*  84 */     this.resourceFile = file;
/*  85 */     this.resourceOffset = offset;
/*  86 */     this.resourceLength = length;
/*  87 */     this.onDisk = true;
/*     */ 
/*  92 */     sep = this.resourceName.lastIndexOf('.');
/*  93 */     if ((sep > 0) && (this.resourceName.substring(sep).equals(".sav")))
/*  94 */       this.compressed = true;
/*     */     else
/*  96 */       this.compressed = false;
/*     */   }
/*     */ 
/*     */   public String getResourceName()
/*     */   {
/* 105 */     return this.resourceName;
/*     */   }
/*     */ 
/*     */   public String getResourcePath()
/*     */   {
/* 114 */     return this.resourcePath;
/*     */   }
/*     */ 
/*     */   public boolean isOnDisk()
/*     */   {
/* 123 */     return this.onDisk;
/*     */   }
/*     */ 
/*     */   public void setOnDisk(boolean onDisk)
/*     */   {
/* 133 */     this.onDisk = onDisk;
/* 134 */     this.resourceOffset = 0L;
/* 135 */     this.resourceLength = 0;
/* 136 */     this.resourceFile = null;
/*     */ 
/* 138 */     if (onDisk)
/* 139 */       this.resourceDataList = null;
/*     */     else
/* 141 */       this.resourceDataList = new ArrayList();
/*     */   }
/*     */ 
/*     */   public boolean isCompressed()
/*     */   {
/* 150 */     return this.compressed;
/*     */   }
/*     */ 
/*     */   public File getResourceFile()
/*     */   {
/* 160 */     return this.resourceFile;
/*     */   }
/*     */ 
/*     */   public void setResourceFile(File file, int offset, int length)
/*     */   {
/* 171 */     this.resourceFile = file;
/* 172 */     this.resourceOffset = offset;
/* 173 */     this.resourceLength = length;
/* 174 */     this.resourceDataList = null;
/* 175 */     this.onDisk = true;
/*     */   }
/*     */ 
/*     */   public long getResourceOffset()
/*     */   {
/* 184 */     return this.resourceOffset;
/*     */   }
/*     */ 
/*     */   public void setResourceOffset(long offset)
/*     */   {
/* 193 */     this.resourceOffset = offset;
/*     */   }
/*     */ 
/*     */   public int getResourceLength()
/*     */   {
/* 202 */     return this.resourceLength;
/*     */   }
/*     */ 
/*     */   public void setResourceLength(int length)
/*     */   {
/* 211 */     this.resourceLength = length;
/*     */   }
/*     */ 
/*     */   public List<byte[]> getResourceDataList()
/*     */   {
/* 221 */     return this.resourceDataList;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/*     */     InputStream inputStream;
/* 233 */     if (this.compressed)
/* 234 */       inputStream = new CompressedSaveInputStream(new SaveInputStream(this));
/*     */     else {
/* 236 */       inputStream = new SaveInputStream(this);
/*     */     }
/* 238 */     return inputStream;
/*     */   }
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */     throws IOException
/*     */   {
/*     */     OutputStream outputStream;
/* 250 */     if (this.compressed)
/* 251 */       outputStream = new CompressedSaveOutputStream(new SaveOutputStream(this));
/*     */     else {
/* 253 */       outputStream = new SaveOutputStream(this);
/*     */     }
/* 255 */     return outputStream;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.SaveEntry
 * JD-Core Version:    0.6.2
 */