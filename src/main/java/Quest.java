/*     */ package TWEditor;
/*     */ 
/*     */ public class Quest
/*     */ {
/*     */   public static final int QUEST_NOT_STARTED = 0;
/*     */   public static final int QUEST_STARTED = 1;
/*     */   public static final int QUEST_COMPLETED = 2;
/*     */   public static final int QUEST_FAILED = 3;
/*     */   private String resourceName;
/*     */   private DBElement questElement;
/*     */   private String questName;
/*     */   private int questState;
/*  35 */   private boolean questModified = false;
/*     */ 
/*     */   public Quest(String resource, DBElement element)
/*     */     throws DBException
/*     */   {
/*  45 */     this.resourceName = resource;
/*  46 */     this.questElement = element;
/*  47 */     if (element.getType() != 14) {
/*  48 */       throw new DBException("Top-level quest element is not a structure");
/*     */     }
/*  50 */     DBList fieldList = (DBList)element.getValue();
/*     */ 
/*  55 */     this.questName = fieldList.getString("QuestLocName").trim();
/*     */ 
/*  60 */     DBElement mainPhase = fieldList.getElement("MainPhase");
/*  61 */     if ((mainPhase == null) || (mainPhase.getType() != 15)) {
/*  62 */       throw new DBException("MainPhase not found for quest " + this.resourceName);
/*     */     }
/*  64 */     DBList questList = (DBList)mainPhase.getValue();
/*  65 */     if (questList.getElementCount() == 0) {
/*  66 */       throw new DBException("No quest list for quest " + this.resourceName);
/*     */     }
/*  68 */     fieldList = (DBList)questList.getElement(0).getValue();
/*  69 */     if (fieldList.getInteger("QuestBegan") == 0)
/*  70 */       this.questState = 0;
/*  71 */     else if (fieldList.getInteger("Completed") == 1)
/*  72 */       this.questState = 2;
/*  73 */     else if (fieldList.getInteger("Failed") == 1)
/*  74 */       this.questState = 3;
/*  75 */     else if (fieldList.getInteger("NewQuestInfoSent") == 1)
/*  76 */       this.questState = 1;
/*     */     else
/*  78 */       this.questState = 0;
/*     */   }
/*     */ 
/*     */   public String getResourceName()
/*     */   {
/*  88 */     return this.resourceName;
/*     */   }
/*     */ 
/*     */   public String getQuestName()
/*     */   {
/*  97 */     return this.questName;
/*     */   }
/*     */ 
/*     */   public int getQuestState()
/*     */   {
/* 106 */     return this.questState;
/*     */   }
/*     */ 
/*     */   public boolean isModified()
/*     */   {
/* 115 */     return this.questModified;
/*     */   }
/*     */ 
/*     */   public void setModified(boolean modified)
/*     */   {
/* 124 */     this.questModified = modified;
/*     */   }
/*     */ 
/*     */   public DBElement getQuestElement()
/*     */   {
/* 133 */     return this.questElement;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 142 */     return this.questName;
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.Quest
 * JD-Core Version:    0.6.2
 */