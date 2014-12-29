/*     */ package TWEditor;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ 
/*     */ public class LocalizedString extends DBElementValue
/*     */   implements Cloneable
/*     */ {
/*     */   private int stringReference;
/*     */   private List<LocalizedSubstring> substringList;
/*     */ 
/*     */   public LocalizedString(int reference)
/*     */   {
/*  25 */     this.stringReference = reference;
/*  26 */     this.substringList = new ArrayList(4);
/*     */   }
/*     */ 
/*     */   public void addSubstring(LocalizedSubstring substring)
/*     */   {
/*  36 */     int language = substring.getLanguage();
/*  37 */     int gender = substring.getGender();
/*  38 */     ListIterator li = this.substringList.listIterator();
/*  39 */     boolean found = false;
/*  40 */     while (li.hasNext()) {
/*  41 */       LocalizedSubstring oldSubstring = (LocalizedSubstring)li.next();
/*  42 */       if ((oldSubstring.getLanguage() == language) && (oldSubstring.getGender() == gender)) {
/*  43 */         li.set(substring);
/*  44 */         found = true;
/*  45 */         break;
/*     */       }
/*     */     }
/*     */ 
/*  49 */     if (!found)
/*  50 */       this.substringList.add(substring);
/*     */   }
/*     */ 
/*     */   public int getStringReference()
/*     */   {
/*  59 */     return this.stringReference;
/*     */   }
/*     */ 
/*     */   public void setStringReference(int reference)
/*     */   {
/*  68 */     this.stringReference = reference;
/*     */   }
/*     */ 
/*     */   public int getSubstringCount()
/*     */   {
/*  77 */     return this.substringList.size();
/*     */   }
/*     */ 
/*     */   public LocalizedSubstring getSubstring(int index)
/*     */   {
/*  87 */     return (LocalizedSubstring)this.substringList.get(index);
/*     */   }
/*     */ 
/*     */   public void setSubstring(int index, LocalizedSubstring substring)
/*     */   {
/*  97 */     this.substringList.set(index, substring);
/*     */   }
/*     */ 
/*     */   public LocalizedSubstring getSubstring(int language, int gender)
/*     */   {
/* 108 */     LocalizedSubstring value = null;
/* 109 */     for (LocalizedSubstring substring : this.substringList) {
/* 110 */       if ((substring.getLanguage() == language) && (substring.getGender() == gender)) {
/* 111 */         value = substring;
/* 112 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 116 */     return value;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 129 */     Object clonedObject = super.clone();
/* 130 */     LocalizedString clonedString = (LocalizedString)clonedObject;
/*     */ 
/* 135 */     int count = this.substringList.size();
/* 136 */     clonedString.substringList = new ArrayList(count);
/* 137 */     for (int i = 0; i < count; i++) {
/* 138 */       clonedString.substringList.add((LocalizedSubstring)((LocalizedSubstring)this.substringList.get(i)).clone());
/*     */     }
/* 140 */     return clonedObject;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.LocalizedString
 * JD-Core Version:    0.6.2
 */