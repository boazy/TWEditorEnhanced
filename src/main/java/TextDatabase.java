/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class TextDatabase
/*     */ {
/*     */   private List<String> columns;
/*     */   private Map<String, Integer> columnMap;
/*     */   private List<String[]> resources;
/*     */ 
/*     */   public TextDatabase(String filePath)
/*     */     throws DBException, IOException
/*     */   {
/*  33 */     InputStreamReader reader = new FileReader(filePath);
/*  34 */     readDefinitions(reader);
/*  35 */     reader.close();
/*     */   }
/*     */ 
/*     */   public TextDatabase(File file)
/*     */     throws DBException, IOException
/*     */   {
/*  46 */     InputStreamReader reader = new FileReader(file);
/*  47 */     readDefinitions(reader);
/*  48 */     reader.close();
/*     */   }
/*     */ 
/*     */   public TextDatabase(InputStream inputStream)
/*     */     throws DBException, IOException
/*     */   {
/*  59 */     InputStreamReader reader = new InputStreamReader(inputStream);
/*  60 */     readDefinitions(reader);
/*  61 */     reader.close();
/*     */   }
/*     */ 
/*     */   private void readDefinitions(InputStreamReader reader)
/*     */     throws DBException, IOException
/*     */   {
/*  72 */     this.columns = new ArrayList(16);
/*  73 */     this.columnMap = new HashMap(16);
/*  74 */     this.resources = new ArrayList(100);
/*  75 */     boolean headerDone = false;
/*  76 */     boolean columnsDone = false;
/*     */ 
/*  78 */     String[] values = null;
/*  79 */     BufferedReader in = new BufferedReader(reader);
/*     */     String line;
/*  84 */     while ((line = in.readLine()) != null) {
/*  85 */       int lineLength = line.length();
/*  86 */       if ((lineLength != 0) && (line.charAt(0) != '#'))
/*     */       {
/*  89 */         boolean skipIndex = true;
/*  90 */         int index = 0;
/*  91 */         int value = 0;
/*  92 */         if (columnsDone) {
/*  93 */           values = new String[this.columns.size()];
/*     */         }
/*     */ 
/*  98 */         while (index < lineLength)
/*     */         {
/* 103 */           if (Character.isWhitespace(line.charAt(index))) {
/* 104 */             index++;
/*     */           }
/*     */           else
/*     */           {
/*     */             boolean quoted;
/* 112 */             if (line.charAt(index) == '"') {
/* 113 */               quoted = true;
/* 114 */               index++;
/*     */             } else {
/* 116 */               quoted = false;
/*     */             }
/*     */ 
/* 119 */             int start = index;
/* 120 */             if (start >= lineLength) {
/*     */               break;
/*     */             }
/* 123 */             while ((index < lineLength) && 
/* 124 */               (quoted ? 
/* 125 */               line.charAt(index) != '"' : 
/* 128 */               !Character.isWhitespace(line.charAt(index))))
/*     */             {
/* 132 */               index++;
/*     */             }
/*     */             String token;
/* 136 */             if (start == index)
/* 137 */               token = new String();
/*     */             else {
/* 139 */               token = line.substring(start, index);
/*     */             }
/* 141 */             if ((index < lineLength) && (line.charAt(index) == '"')) {
/* 142 */               index++;
/*     */             }
/*     */ 
/* 147 */             if (!headerDone) {
/* 148 */               if (value == 0) {
/* 149 */                 if (!token.equals("2DA"))
/* 150 */                   throw new DBException("File format '" + token + "' is not supported");
/* 151 */               } else if ((value == 1) && 
/* 152 */                 (!token.equals("V2.0")))
/* 153 */                 throw new DBException("File version '" + token + "' is not supported");
/*     */             }
/* 155 */             else if (!columnsDone) {
/* 156 */               this.columnMap.put(token.toLowerCase(), new Integer(value));
/* 157 */               this.columns.add(token);
/* 158 */             } else if (skipIndex) {
/* 159 */               skipIndex = false;
/* 160 */               value--;
/* 161 */             } else if (value < values.length) {
/* 162 */               values[value] = token;
/*     */             }
/*     */ 
/* 165 */             value++;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 171 */         if (value > 0) {
/* 172 */           if (columnsDone)
/* 173 */             this.resources.add(values);
/* 174 */           else if (headerDone)
/* 175 */             columnsDone = true;
/*     */           else {
/* 177 */             headerDone = true;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 184 */     in.close();
/*     */   }
/*     */ 
/*     */   public List<String> getColumnLabels()
/*     */   {
/* 194 */     return this.columns;
/*     */   }
/*     */ 
/*     */   public int getResourceCount()
/*     */   {
/* 204 */     return this.resources.size();
/*     */   }
/*     */ 
/*     */   public String getString(int resourceIndex, int valueIndex)
/*     */   {
/* 215 */     if (resourceIndex >= this.resources.size()) {
/* 216 */       throw new IllegalArgumentException("Resource index is not valid");
/*     */     }
/* 218 */     if (valueIndex >= this.columns.size()) {
/* 219 */       throw new IllegalArgumentException("Value index is not valid");
/*     */     }
/* 221 */     return ((String[])this.resources.get(resourceIndex))[valueIndex];
/*     */   }
/*     */ 
/*     */   public String getString(int resourceIndex, String valueLabel)
/*     */   {
/* 233 */     if (resourceIndex >= this.resources.size()) {
/* 234 */       throw new IllegalArgumentException("Resource index is not valid");
/*     */     }
/* 236 */     Integer valueIndex = (Integer)this.columnMap.get(valueLabel.toLowerCase());
/* 237 */     if (valueIndex == null) {
/* 238 */       return "";
/*     */     }
/* 240 */     String string = ((String[])this.resources.get(resourceIndex))[valueIndex.intValue()];
/* 241 */     if ((string.length() >= 4) && (string.substring(0, 4).equals("****"))) {
/* 242 */       string = "";
/*     */     }
/* 244 */     return string;
/*     */   }
/*     */ 
/*     */   public int getInteger(int resourceIndex, String valueLabel)
/*     */   {
/* 256 */     if (resourceIndex >= this.resources.size()) {
/* 257 */       throw new IllegalArgumentException("Resource index is not valid");
/*     */     }
/* 259 */     Integer valueIndex = (Integer)this.columnMap.get(valueLabel.toLowerCase());
/* 260 */     if (valueIndex == null) {
/* 261 */       return 0;
/*     */     }
/*     */ 
/* 264 */     String string = ((String[])this.resources.get(resourceIndex))[valueIndex.intValue()];
/*     */     int value;
/* 265 */     if ((string.length() >= 4) && (string.substring(0, 4).equals("****")))
/* 266 */       value = 0;
/*     */     else {
/* 268 */       value = Integer.parseInt(string);
/*     */     }
/* 270 */     return value;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.TextDatabase
 * JD-Core Version:    0.6.2
 */