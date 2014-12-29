package TWEditor;

public class ItemTemplate
  implements Comparable<ItemTemplate>
{
  private DBList fieldList;
  private String itemName;
  private String resourceName;
  private int baseItem;

  public ItemTemplate(DBList fieldList)
    throws DBException
  {
    this.fieldList = fieldList;
    setupTemplate();
  }

  private void setupTemplate()
    throws DBException
  {
    this.baseItem = this.fieldList.getInteger("BaseItem");

    this.itemName = this.fieldList.getString("LocalizedName");

    this.resourceName = this.fieldList.getString("TemplateResRef");
  }

  public String getItemName()
  {
    return this.itemName;
  }

  public int getBaseItem()
  {
    return this.baseItem;
  }

  public DBList getFieldList()
  {
    return this.fieldList;
  }

  public boolean equals(Object obj)
  {
    boolean equal = false;
    if ((obj != null) && ((obj instanceof ItemTemplate)) && 
      (((ItemTemplate)obj).getItemName().equals(this.itemName))) {
      equal = true;
    }
    return equal;
  }

  public int compareTo(ItemTemplate obj)
  {
    return this.itemName.compareTo(obj.getItemName());
  }

  public String toString()
  {
    return this.itemName + " (" + this.resourceName + ")";
  }
}

