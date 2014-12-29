package TWEditor;

public class Quest
{
  public static final int QUEST_NOT_STARTED = 0;
  public static final int QUEST_STARTED = 1;
  public static final int QUEST_COMPLETED = 2;
  public static final int QUEST_FAILED = 3;
  private String resourceName;
  private DBElement questElement;
  private String questName;
  private int questState;
  private boolean questModified = false;

  public Quest(String resource, DBElement element)
    throws DBException
  {
    this.resourceName = resource;
    this.questElement = element;
    if (element.getType() != 14) {
      throw new DBException("Top-level quest element is not a structure");
    }
    DBList fieldList = (DBList)element.getValue();

    this.questName = fieldList.getString("QuestLocName").trim();

    DBElement mainPhase = fieldList.getElement("MainPhase");
    if ((mainPhase == null) || (mainPhase.getType() != 15)) {
      throw new DBException("MainPhase not found for quest " + this.resourceName);
    }
    DBList questList = (DBList)mainPhase.getValue();
    if (questList.getElementCount() == 0) {
      throw new DBException("No quest list for quest " + this.resourceName);
    }
    fieldList = (DBList)questList.getElement(0).getValue();
    if (fieldList.getInteger("QuestBegan") == 0)
      this.questState = 0;
    else if (fieldList.getInteger("Completed") == 1)
      this.questState = 2;
    else if (fieldList.getInteger("Failed") == 1)
      this.questState = 3;
    else if (fieldList.getInteger("NewQuestInfoSent") == 1)
      this.questState = 1;
    else
      this.questState = 0;
  }

  public String getResourceName()
  {
    return this.resourceName;
  }

  public String getQuestName()
  {
    return this.questName;
  }

  public int getQuestState()
  {
    return this.questState;
  }

  public boolean isModified()
  {
    return this.questModified;
  }

  public void setModified(boolean modified)
  {
    this.questModified = modified;
  }

  public DBElement getQuestElement()
  {
    return this.questElement;
  }

  public String toString()
  {
    return this.questName;
  }
}

