/*     */ package TWEditor;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DBList extends DBElementValue
/*     */   implements Cloneable
/*     */ {
/*     */   private List<DBElement> elementList;
/*     */   private Map<String, DBElement> labelMap;
/*     */ 
/*     */   public DBList(int capacity)
/*     */   {
/*  24 */     this.elementList = new ArrayList(capacity);
/*  25 */     this.labelMap = new HashMap(capacity);
/*     */   }
/*     */ 
/*     */   public boolean addElement(DBElement element)
/*     */   {
/*  36 */     String label = element.getLabel();
/*  37 */     if (label.length() == 0) {
/*  38 */       this.elementList.add(element);
/*  39 */       return true;
/*     */     }
/*     */ 
/*  42 */     if (this.labelMap.get(label) != null) {
/*  43 */       return false;
/*     */     }
/*  45 */     this.elementList.add(element);
/*  46 */     this.labelMap.put(label, element);
/*  47 */     return true;
/*     */   }
/*     */ 
/*     */   public DBElement removeElement(int index)
/*     */   {
/*  57 */     DBElement element = (DBElement)this.elementList.get(index);
/*  58 */     this.elementList.remove(index);
/*  59 */     String label = element.getLabel();
/*  60 */     if (label.length() != 0) {
/*  61 */       this.labelMap.remove(label);
/*     */     }
/*  63 */     return element;
/*     */   }
/*     */ 
/*     */   public boolean removeElement(String label)
/*     */   {
/*  73 */     if ((label == null) || (label.length() == 0)) {
/*  74 */       throw new IllegalArgumentException("No database element label supplied");
/*     */     }
/*  76 */     DBElement element = (DBElement)this.labelMap.get(label);
/*  77 */     if (element == null) {
/*  78 */       return false;
/*     */     }
/*  80 */     boolean removed = this.elementList.remove(element);
/*  81 */     if (removed) {
/*  82 */       this.labelMap.remove(label);
/*     */     }
/*  84 */     return removed;
/*     */   }
/*     */ 
/*     */   public boolean removeElement(DBElement element)
/*     */   {
/*  94 */     boolean removed = this.elementList.remove(element);
/*  95 */     if (removed) {
/*  96 */       String label = element.getLabel();
/*  97 */       if (label.length() != 0) {
/*  98 */         this.labelMap.remove(label);
/*     */       }
/*     */     }
/* 101 */     return removed;
/*     */   }
/*     */ 
/*     */   public DBElement getElement(String label)
/*     */   {
/* 111 */     if ((label == null) || (label.length() == 0)) {
/* 112 */       throw new IllegalArgumentException("No database element label supplied");
/*     */     }
/* 114 */     return (DBElement)this.labelMap.get(label);
/*     */   }
/*     */ 
/*     */   public void setElement(String label, DBElement element)
/*     */   {
/* 125 */     if ((label == null) || (label.length() == 0)) {
/* 126 */       throw new IllegalArgumentException("No database element label supplied");
/*     */     }
/* 128 */     DBElement oldElement = (DBElement)this.labelMap.get(label);
/* 129 */     if (oldElement != null) {
/* 130 */       int index = this.elementList.indexOf(oldElement);
/* 131 */       this.elementList.set(index, element);
/*     */     } else {
/* 133 */       this.elementList.add(element);
/*     */     }
/*     */ 
/* 136 */     this.labelMap.put(label, element);
/*     */   }
/*     */ 
/*     */   public int getElementCount()
/*     */   {
/* 145 */     return this.elementList.size();
/*     */   }
/*     */ 
/*     */   public DBElement getElement(int index)
/*     */   {
/* 155 */     return (DBElement)this.elementList.get(index);
/*     */   }
/*     */ 
/*     */   public void setElement(int index, DBElement element)
/*     */   {
/* 166 */     DBElement oldElement = (DBElement)this.elementList.get(index);
/* 167 */     String oldLabel = oldElement.getLabel();
/* 168 */     String label = element.getLabel();
/* 169 */     if (!label.equals(oldLabel)) {
/* 170 */       throw new IllegalArgumentException("New label is not the same as old label");
/*     */     }
/* 172 */     this.elementList.set(index, element);
/* 173 */     this.labelMap.put(label, element);
/*     */   }
/*     */ 
/*     */   public String getString(String label)
/*     */     throws DBException
/*     */   {
/* 191 */     DBElement element = getElement(label);
/*     */     String value;
/* 192 */     if (element != null) {
/* 193 */       int fieldType = element.getType();
/* 194 */       if (fieldType == 10) {
/* 195 */         value = (String)element.getValue();
/*     */       }
/*     */       else
/*     */       {
/* 196 */         if (fieldType == 11) {
/* 197 */           value = (String)element.getValue();
/*     */         }
/*     */         else
/*     */         {
/* 198 */           if (fieldType == 12) {
/* 199 */             LocalizedString string = (LocalizedString)element.getValue();
/* 200 */             if (string.getSubstringCount() > 0) {
/* 201 */               LocalizedSubstring substring = string.getSubstring(Main.languageID, 0);
/* 202 */               if (substring != null)
/* 203 */                 value = substring.getString();
/*     */               else
/* 205 */                 value = string.getSubstring(0).getString();
/*     */             } else {
/* 207 */               int refid = string.getStringReference();
/* 208 */               if (refid >= 0)
/* 209 */                 value = Main.getString(refid);
/*     */               else
/* 211 */                 value = new String();
/*     */             }
/*     */           } else {
/* 214 */             throw new DBException("Field " + label + " is not a string");
/*     */           }
/*     */         }
/*     */       } } else { value = new String(); }
/*     */ 
/*     */ 
/* 220 */     return value;
/*     */   }
/*     */ 
/*     */   public void setString(String label, String value)
/*     */     throws DBException
/*     */   {
/* 232 */     DBElement element = getElement(label);
/* 233 */     if (element != null) {
/* 234 */       int fieldType = element.getType();
/* 235 */       if (fieldType == 10) {
/* 236 */         element.setValue(value);
/* 237 */       } else if (fieldType == 11) {
/* 238 */         element.setValue(value);
/* 239 */       } else if (fieldType == 12) {
/* 240 */         LocalizedString string = (LocalizedString)element.getValue();
/* 241 */         LocalizedSubstring substring = new LocalizedSubstring(value, Main.languageID, 0);
/* 242 */         string.addSubstring(substring);
/*     */       } else {
/* 244 */         throw new DBException("Field " + label + " is not a string");
/*     */       }
/*     */     } else {
/* 247 */       addElement(new DBElement(10, 0, label, value));
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getInteger(String label)
/*     */     throws DBException
/*     */   {
/* 262 */     DBElement element = getElement(label);
/*     */     int value;
/* 263 */     if (element != null) {
/* 264 */       int fieldType = element.getType();
/* 265 */       if ((fieldType == 0) || (fieldType == 2) || (fieldType == 3) || (fieldType == 5))
/*     */       {
/* 267 */         value = ((Integer)element.getValue()).intValue();
/*     */       }
/*     */       else
/*     */       {
/* 268 */         if ((fieldType == 6) || (fieldType == 7) || (fieldType == 4)) {
/* 269 */           value = ((Long)element.getValue()).intValue();
/*     */         }
/*     */         else
/*     */         {
/* 270 */           if (fieldType == 1) {
/* 271 */             value = ((Character)element.getValue()).charValue();
/*     */           }
/*     */           else
/*     */           {
/* 272 */             if (fieldType == 8) {
/* 273 */               value = ((Float)element.getValue()).intValue();
/*     */             }
/*     */             else
/*     */             {
/* 274 */               if (fieldType == 9)
/* 275 */                 value = ((Double)element.getValue()).intValue();
/*     */               else
/* 277 */                 throw new DBException("Field " + label + " is not numeric");  } 
/*     */           }
/*     */         }
/*     */       } } else { value = 0; }
/*     */ 
/*     */ 
/* 283 */     return value;
/*     */   }
/*     */ 
/*     */   public void setInteger(String label, int value)
/*     */     throws DBException
/*     */   {
/* 295 */     setInteger(label, value, 5);
/*     */   }
/*     */ 
/*     */   public void setInteger(String label, int value, int type)
/*     */     throws DBException
/*     */   {
/* 308 */     DBElement element = getElement(label);
/* 309 */     if (element != null) {
/* 310 */       int fieldType = element.getType();
/* 311 */       if (fieldType == 0) {
/* 312 */         element.setValue(new Integer(value & 0xFF));
/* 313 */       } else if (fieldType == 2) {
/* 314 */         element.setValue(new Integer(value & 0xFFFF));
/* 315 */       } else if (fieldType == 3) {
/* 316 */         int shortValue = value & 0xFFFF;
/* 317 */         if (shortValue > 32767)
/* 318 */           shortValue |= -65536;
/* 319 */         element.setValue(new Integer(shortValue));
/* 320 */       } else if (fieldType == 5) {
/* 321 */         element.setValue(new Integer(value));
/* 322 */       } else if (fieldType == 4) {
/* 323 */         element.setValue(new Long(value & 0xFFFFFFFF));
/* 324 */       } else if ((fieldType == 6) || (fieldType == 7)) {
/* 325 */         element.setValue(new Long(value));
/* 326 */       } else if (fieldType == 1) {
/* 327 */         element.setValue(new Character((char)value));
/* 328 */       } else if (fieldType == 8) {
/* 329 */         element.setValue(new Float(value));
/* 330 */       } else if (fieldType == 9) {
/* 331 */         element.setValue(new Double(value));
/*     */       } else {
/* 333 */         throw new DBException("Field " + label + " is not numeric");
/*     */       }
/*     */     } else {
/* 336 */       addElement(new DBElement(type, 0, label, new Integer(value)));
/*     */     }
/*     */   }
/*     */ 
/*     */   public float getFloat(String label)
/*     */     throws DBException
/*     */   {
/* 350 */     DBElement element = getElement(label);
/*     */     float value;
/* 351 */     if (element != null) {
/* 352 */       int fieldType = element.getType();
/* 353 */       if (fieldType == 8)
/* 354 */         value = ((Float)element.getValue()).floatValue();
/*     */       else
/* 356 */         throw new DBException("Field " + label + " is not floating-point");
/*     */     }
/*     */     else {
/* 359 */       value = 0.0F;
/*     */     }
/*     */ 
/* 362 */     return value;
/*     */   }
/*     */ 
/*     */   public void setFloat(String label, float value)
/*     */     throws DBException
/*     */   {
/* 374 */     DBElement element = getElement(label);
/* 375 */     if (element != null) {
/* 376 */       int fieldType = element.getType();
/* 377 */       if (fieldType == 8)
/* 378 */         element.setValue(new Float(value));
/*     */       else
/* 380 */         throw new DBException("Field " + label + " is not floating-point");
/*     */     }
/*     */     else {
/* 383 */       addElement(new DBElement(8, 0, label, new Float(value)));
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 397 */     Object clonedObject = super.clone();
/* 398 */     DBList clonedList = (DBList)clonedObject;
/*     */ 
/* 403 */     int count = this.elementList.size();
/* 404 */     clonedList.elementList = new ArrayList(count);
/* 405 */     clonedList.labelMap = new HashMap(count);
/* 406 */     for (DBElement element : this.elementList) {
/* 407 */       clonedList.addElement((DBElement)element.clone());
/*     */     }
/* 409 */     return clonedObject;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.DBList
 * JD-Core Version:    0.6.2
 */