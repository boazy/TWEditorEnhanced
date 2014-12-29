/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class LoadFile extends Thread
/*     */ {
/*     */   private ProgressDialog progressDialog;
/*     */   private File file;
/*  18 */   private boolean loadSuccessful = false;
/*     */ 
/*     */   public LoadFile(ProgressDialog dialog, File file)
/*     */   {
/*  28 */     this.progressDialog = dialog;
/*  29 */     this.file = file;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  36 */     InputStream in = null;
/*  37 */     FileOutputStream out = null;
/*     */     try
/*     */     {
/*  43 */       SaveDatabase saveDatabase = new SaveDatabase(this.file);
/*  44 */       saveDatabase.load();
/*  45 */       this.progressDialog.updateProgress(25);
/*  46 */       String saveName = saveDatabase.getName();
/*  47 */       Main.savePrefix = saveName + Main.fileSeparator;
/*     */ 
/*  52 */       int sep = saveName.indexOf(' ');
/*  53 */       if ((sep != 6) || (!Character.isDigit(saveName.charAt(0)))) {
/*  54 */         throw new DBException("Save name is not formatted correctly");
/*     */       }
/*  56 */       String fileName = "save_" + saveName.substring(0, 6) + ".smm";
/*  57 */       SaveEntry saveEntry = saveDatabase.getEntry(fileName);
/*  58 */       if (saveEntry == null) {
/*  59 */         throw new DBException("Save does not contain " + fileName);
/*     */       }
/*  61 */       in = saveEntry.getInputStream();
/*  62 */       Database database = new Database();
/*  63 */       database.load(in);
/*  64 */       in.close();
/*  65 */       in = null;
/*  66 */       this.progressDialog.updateProgress(35);
/*     */ 
/*  71 */       DBList list = (DBList)database.getTopLevelStruct().getValue();
/*  72 */       String startingMod = list.getString("StartingMod");
/*  73 */       if (startingMod.length() == 0) {
/*  74 */         throw new DBException("StartingMod not found in SMM database");
/*     */       }
/*  76 */       DBElement element = list.getElement("QuestBase_list");
/*  77 */       if ((element == null) || (element.getType() != 15)) {
/*  78 */         throw new DBException("QuestBaseList not found in SMM database");
/*     */       }
/*  80 */       DBList questList = (DBList)element.getValue();
/*  81 */       if (questList.getElementCount() == 0) {
/*  82 */         throw new DBException("No quest list found in SMM database");
/*     */       }
/*  84 */       DBList fieldList = (DBList)questList.getElement(0).getValue();
/*  85 */       String questDBName = fieldList.getString("QuestBase");
/*  86 */       if (questDBName.length() == 0) {
/*  87 */         throw new DBException("No quest database name found in SMM database");
/*     */       }
/*     */ 
/*  92 */       Main.modName = startingMod + ".sav";
/*  93 */       saveEntry = saveDatabase.getEntry(Main.modName);
/*  94 */       if (saveEntry == null) {
/*  95 */         throw new DBException("Save does not contain " + Main.modName);
/*     */       }
/*  97 */       in = saveEntry.getInputStream();
/*  98 */       if (Main.modFile.exists()) {
/*  99 */         Main.modFile.delete();
/*     */       }
/*     */ 
/* 102 */       byte[] buffer = new byte[4096];
/* 103 */       out = new FileOutputStream(Main.modFile);
                int count;
/* 104 */       while ((count = in.read(buffer)) > 0) {
/* 105 */         out.write(buffer, 0, count);
/*     */       }
/* 107 */       in.close();
/* 108 */       in = null;
/* 109 */       out.close();
/* 110 */       out = null;
/* 111 */       this.progressDialog.updateProgress(50);
/*     */ 
/* 116 */       ResourceDatabase modDatabase = new ResourceDatabase(Main.modFile);
/* 117 */       modDatabase.load();
/* 118 */       this.progressDialog.updateProgress(60);
/*     */ 
/* 123 */       ResourceEntry resourceEntry = modDatabase.getEntry("module.ifo");
/* 124 */       if (resourceEntry == null) {
/* 125 */         throw new DBException("Save does not contain module.ifo");
/*     */       }
/* 127 */       in = resourceEntry.getInputStream();
/* 128 */       if (Main.databaseFile.exists()) {
/* 129 */         Main.databaseFile.delete();
/*     */       }
/* 131 */       out = new FileOutputStream(Main.databaseFile);
/* 132 */       while ((count = in.read(buffer)) > 0) {
/* 133 */         out.write(buffer, 0, count);
/*     */       }
/* 135 */       in.close();
/* 136 */       in = null;
/* 137 */       out.close();
/* 138 */       out = null;
/* 139 */       this.progressDialog.updateProgress(75);
/*     */ 
/* 144 */       database = new Database(Main.databaseFile);
/* 145 */       database.load();
/* 146 */       list = (DBList)database.getTopLevelStruct().getValue();
/* 147 */       element = list.getElement("Mod_PlayerList");
/* 148 */       if ((element == null) || (element.getType() != 15)) {
/* 149 */         throw new DBException("module.ifo does not contain Mod_PlayerList");
/*     */       }
/* 151 */       list = (DBList)element.getValue();
/* 152 */       if (list.getElementCount() == 0) {
/* 153 */         throw new DBException("Mod_PlayerList is empty");
/*     */       }
/* 155 */       this.progressDialog.updateProgress(80);
/*     */ 
/* 160 */       fileName = questDBName + ".qdb";
/* 161 */       saveEntry = saveDatabase.getEntry(fileName);
/* 162 */       if (saveEntry == null) {
/* 163 */         throw new DBException("Save does not contain " + fileName);
/*     */       }
/* 165 */       in = saveEntry.getInputStream();
/* 166 */       Database questDatabase = new Database();
/* 167 */       questDatabase.load(in);
/* 168 */       in.close();
/* 169 */       in = null;
/* 170 */       list = (DBList)questDatabase.getTopLevelStruct().getValue();
/* 171 */       element = list.getElement("Quests");
/* 172 */       if ((element == null) || (element.getType() != 15)) {
/* 173 */         throw new DBException("Quests not found in quest database");
/*     */       }
/* 175 */       questList = (DBList)element.getValue();
/* 176 */       this.progressDialog.updateProgress(85);
/*     */ 
/* 181 */       count = questList.getElementCount();
/* 182 */       Main.quests = new ArrayList(count);
/* 183 */       for (int i = 0; i < count; i++) {
/* 184 */         fieldList = (DBList)questList.getElement(i).getValue();
/* 185 */         String resourceName = fieldList.getString("File");
/* 186 */         fileName = resourceName + ".qst";
/* 187 */         saveEntry = saveDatabase.getEntry(fileName);
/* 188 */         if (saveEntry == null) {
/* 189 */           throw new DBException("Save does not contain " + fileName);
/*     */         }
/* 191 */         in = saveEntry.getInputStream();
/* 192 */         questDatabase = new Database();
/* 193 */         questDatabase.load(in);
/* 194 */         in.close();
/* 195 */         in = null;
/* 196 */         Quest quest = new Quest(resourceName, questDatabase.getTopLevelStruct());
/* 197 */         if (quest.getQuestName().length() > 0) {
/* 198 */           Main.quests.add(quest);
/*     */         }
/*     */       }
/* 201 */       this.progressDialog.updateProgress(100);
/*     */ 
/* 206 */       Main.saveDatabase = saveDatabase;
/* 207 */       Main.modDatabase = modDatabase;
/* 208 */       Main.database = database;
/* 209 */       this.loadSuccessful = true;
/*     */     } catch (DBException exc) {
/* 211 */       Main.logException("Save file structure is not valid", exc);
/*     */     } catch (IOException exc) {
/* 213 */       Main.logException("Unable to read save file", exc);
/*     */     } catch (Throwable exc) {
/* 215 */       Main.logException("Exception while opening save file", exc);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 222 */       if (in != null) {
/* 223 */         in.close();
/*     */       }
/* 225 */       if (out != null) {
/* 226 */         out.close();
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException exc)
/*     */     {
/*     */     }
/*     */ 
/* 234 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 236 */         LoadFile.this.progressDialog.closeDialog(LoadFile.this.loadSuccessful);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.LoadFile
 * JD-Core Version:    0.6.2
 */