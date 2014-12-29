/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ public class ResourceEntry
/*     */ {
/*  12 */   private static final int[] resourceTypes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 2000, 2001, 2002, 2003, 2005, 2007, 2008, 2009, 2010, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2022, 2023, 2024, 2025, 2026, 2027, 2029, 2030, 2031, 2032, 2033, 2034, 2035, 2036, 2037, 2038, 2039, 2040, 2041, 2042, 2043, 2044, 2045, 2046, 2047, 2048, 2049, 2050, 2051, 2052, 2053, 2054, 2055, 2056, 2057, 2058, 2059, 2060, 2061, 2062, 2063, 2064, 2065, 2066, 2067, 2068, 2069, 2070, 2071, 2072, 2073, 2074, 2075, 2076, 2077, 2078, 2079, 2080, 2081, 2082, 2083, 2084, 2085, 2086, 2087, 2088, 2089, 2090, 2091, 2092, 2093, 2094, 2095, 2096, 2097, 2099, 2100, 2101, 2103, 2104, 2105, 2106, 2107, 2108, 2110, 3000, 3001, 3002, 3003, 3004, 3005, 3006, 3007, 3008, 3009, 3010, 3011, 3012, 3013, 3014, 3015, 3016, 3017, 3018, 3019, 3020, 3021, 3022, 3033, 3034, 3035, 4000, 4001, 4002, 4003, 4004, 4005, 4007, 4008, 9996, 9997, 9998, 9999 };
/*     */ 
/*  26 */   private static final String[] fileExtensions = { "res", "bmp", "mve", "tga", "wav", "wfx", "plt", "ini", "mp3", "mpg", "txt", "plh", "tex", "mdl", "thg", "fnt", "lua", "slt", "nss", "ncs", "are", "set", "ifo", "bic", "wok", "2da", "tlk", "txi", "git", "bti", "uti", "btc", "utc", "dlg", "itp", "btt", "utt", "dds", "bts", "uts", "ltr", "gff", "fac", "bte", "ute", "btd", "utd", "btp", "utp", "dft", "gic", "gui", "css", "ccs", "btm", "utm", "dwk", "pwk", "btg", "utg", "jrl", "sav", "utw", "4pc", "ssf", "hak", "nwm", "bik", "ndb", "ptm", "ptt", "ncm", "mfx", "mat", "mdb", "say", "ttf", "ttc", "cut", "ka", "jpg", "ico", "ogg", "spt", "spw", "wfx", "ugm", "qdb", "qst", "npc", "spn", "utx", "mmd", "smm", "uta", "mde", "mdv", "mda", "mba", "oct", "bfx", "pdb", "pvs", "cfx", "luc", "prb", "cam", "vds", "bin", "wob", "api", "png", "osc", "usc", "trn", "utr", "uen", "ult", "sef", "pfx", "cam", "lfx", "bfx", "upe", "ros", "rst", "ifx", "pfb", "zip", "wmp", "bbx", "tfx", "wlk", "xml", "scc", "ptx", "ltx", "trx", "mdb", "mda", "spt", "gr2", "fxa", "fxe", "jpg", "pwc", "isd", "erf", "bif", "key" };
/*     */   private String resourceName;
/*     */   private int resourceType;
/*     */   private String entryName;
/*     */   private File dataFile;
/*     */   private long dataOffset;
/*     */   private int dataLength;
/*     */ 
/*     */   public ResourceEntry(File dataFile)
/*     */     throws DBException
/*     */   {
/*  65 */     this(dataFile.getName(), dataFile);
/*     */   }
/*     */ 
/*     */   public ResourceEntry(String entryName, File dataFile)
/*     */     throws DBException
/*     */   {
/*  76 */     this.entryName = entryName.toLowerCase();
/*  77 */     this.dataFile = dataFile;
/*  78 */     this.dataOffset = 0L;
/*  79 */     this.dataLength = ((int)dataFile.length());
/*     */ 
/*  81 */     int sep = entryName.lastIndexOf(46);
/*  82 */     if ((sep <= 0) || (sep == entryName.length() - 1)) {
/*  83 */       throw new DBException("Resource file name does not have an extension");
/*     */     }
/*  85 */     this.resourceName = entryName.substring(0, sep);
/*     */ 
/*  90 */     String ext = entryName.substring(sep + 1);
/*  91 */     this.resourceType = -1;
/*  92 */     for (int i = 0; i < fileExtensions.length; i++) {
/*  93 */       if (ext.equals(fileExtensions[i])) {
/*  94 */         this.resourceType = resourceTypes[i];
/*  95 */         break;
/*     */       }
/*     */     }
/*     */ 
/*  99 */     if (this.resourceType < 0)
/* 100 */       throw new DBException("Resource file extension '" + ext + "' is not supported");
/*     */   }
/*     */ 
/*     */   public ResourceEntry(String resourceName, int resourceType, File dataFile, long dataOffset, int dataLength)
/*     */     throws DBException
/*     */   {
/* 115 */     this.resourceName = resourceName.toLowerCase();
/* 116 */     this.resourceType = resourceType;
/* 117 */     this.dataFile = dataFile;
/* 118 */     this.dataOffset = dataOffset;
/* 119 */     this.dataLength = dataLength;
/*     */ 
/* 124 */     String ext = null;
/* 125 */     for (int i = 0; i < resourceTypes.length; i++) {
/* 126 */       if (resourceTypes[i] == resourceType) {
/* 127 */         ext = fileExtensions[i];
/* 128 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 132 */     if (ext == null) {
/* 133 */       throw new DBException("Resource type " + resourceType + " is not supported for resource " + resourceName);
/*     */     }
/* 135 */     this.entryName = (this.resourceName + "." + ext);
/*     */   }
/*     */ 
/*     */   public String getResourceName()
/*     */   {
/* 144 */     return this.resourceName;
/*     */   }
/*     */ 
/*     */   public int getResourceType()
/*     */   {
/* 153 */     return this.resourceType;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 163 */     return this.entryName;
/*     */   }
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 172 */     return this.dataFile;
/*     */   }
/*     */ 
/*     */   public long getOffset()
/*     */   {
/* 181 */     return this.dataOffset;
/*     */   }
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 190 */     return this.dataLength;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 200 */     return new ResourceInputStream(this);
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.ResourceEntry
 * JD-Core Version:    0.6.2
 */