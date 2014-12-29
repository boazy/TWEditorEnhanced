/*     */ package TWEditor;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.swing.SwingUtilities;
/*     */ 
/*     */ public class LoadTemplates extends Thread
/*     */ {
/*     */   private ProgressDialog progressDialog;
/*  15 */   private boolean success = false;
/*     */ 
/*     */   public LoadTemplates(ProgressDialog dialog)
/*     */   {
/*  24 */     this.progressDialog = dialog;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*  32 */       Set mapSet = Main.resourceFiles.entrySet();
/*  33 */       int entryCount = mapSet.size();
/*  34 */       Main.itemTemplates = new ArrayList(entryCount);
/*  35 */       int processedCount = 0;
/*  36 */       int currentProgress = 0;
/*     */ 
/*  41 */       for (Object mapEntryObj : mapSet) {
                  Map.Entry mapEntry = (Map.Entry)mapEntryObj;
/*  42 */         String resourceName = null;
/*  43 */         InputStream in = null;
/*  44 */         Object entryObject = mapEntry.getValue();
/*  45 */         if ((entryObject instanceof File)) {
/*  46 */           File file = (File)entryObject;
/*  47 */           String name = file.getName().toLowerCase();
/*  48 */           int sep = name.lastIndexOf('.');
/*  49 */           if ((sep > 0) && (name.substring(sep).equals(".uti"))) {
/*  50 */             resourceName = name.substring(0, sep);
/*  51 */             in = new FileInputStream(file);
/*     */           }
/*  53 */         } else if ((entryObject instanceof KeyEntry)) {
/*  54 */           KeyEntry keyEntry = (KeyEntry)entryObject;
/*  55 */           String name = keyEntry.getFileName().toLowerCase();
/*  56 */           int sep = name.lastIndexOf('.');
/*  57 */           if ((sep > 0) && (name.substring(sep).equals(".uti"))) {
/*  58 */             resourceName = keyEntry.getResourceName();
/*  59 */             in = keyEntry.getInputStream();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*  66 */         if (in != null) {
/*  67 */           Database database = new Database();
/*  68 */           database.load(in);
/*  69 */           in.close();
/*  70 */           DBList fieldList = (DBList)database.getTopLevelStruct().getValue();
/*  71 */           String itemName = fieldList.getString("LocalizedName");
/*  72 */           String itemDescription = fieldList.getString("Description");
/*  73 */           if ((itemName.length() > 0) && (itemDescription.length() > 0)) {
/*  74 */             DBElement resourceElement = new DBElement(11, 0, "TemplateResRef", resourceName);
/*  75 */             fieldList.setElement("TemplateResRef", resourceElement);
/*  76 */             ItemTemplate itemTemplate = new ItemTemplate(fieldList);
/*  77 */             Main.itemTemplates.add(itemTemplate);
/*     */           }
/*     */         }
/*     */ 
/*  81 */         processedCount++;
/*  82 */         int newProgress = processedCount * 100 / entryCount;
/*  83 */         if (newProgress > currentProgress + 9) {
/*  84 */           currentProgress = newProgress;
/*  85 */           this.progressDialog.updateProgress(currentProgress);
/*     */         }
/*     */       }
/*     */ 
/*  89 */       this.success = true;
/*     */     } catch (DBException exc) {
/*  91 */       Main.logException("Database error while loading inventory templates", exc);
/*     */     } catch (IOException exc) {
/*  93 */       Main.logException("I/O error while loading inventory templates", exc);
/*     */     } catch (Throwable exc) {
/*  95 */       Main.logException("Exception while loading inventory templates", exc);
/*     */     }
/*     */ 
/* 102 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 104 */         LoadTemplates.this.progressDialog.closeDialog(LoadTemplates.this.success);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/boaz/Downloads/TWEditorEnhanced/TWEditor.jar
 * Qualified Name:     TWEditor.LoadTemplates
 * JD-Core Version:    0.6.2
 */