package TWEditor;

public class DBElement
  implements Cloneable
{
  public static final int BYTE = 0;
  public static final int CHAR = 1;
  public static final int WORD = 2;
  public static final int SHORT = 3;
  public static final int DWORD = 4;
  public static final int INT = 5;
  public static final int DWORD64 = 6;
  public static final int INT64 = 7;
  public static final int FLOAT = 8;
  public static final int DOUBLE = 9;
  public static final int STRING = 10;
  public static final int RESOURCE = 11;
  public static final int LSTRING = 12;
  public static final int VOID = 13;
  public static final int STRUCT = 14;
  public static final int LIST = 15;
  private int elementType;
  private int elementID;
  private String elementLabel;
  private Object elementValue;

  public DBElement(int type, int id, String label, Object value)
  {
    this.elementType = type;
    this.elementID = id;
    this.elementValue = value;
    if (label != null)
      this.elementLabel = label;
    else
      this.elementLabel = new String();
  }

  public int getType()
  {
    return this.elementType;
  }

  public void setType(int type)
  {
    this.elementType = type;
  }

  public int getID()
  {
    return this.elementID;
  }

  public void setID(int id)
  {
    this.elementID = id;
  }

  public String getLabel()
  {
    return this.elementLabel;
  }

  public void setLabel(String label)
  {
    this.elementLabel = (label != null ? label : new String());
  }

  public Object getValue()
  {
    return this.elementValue;
  }

  public void setValue(Object value)
  {
    if (this.elementValue == null) {
      throw new IllegalArgumentException("No value provided");
    }
    this.elementValue = value;
  }

  public Object clone()
  {
    Object clonedObject;
    try
    {
      clonedObject = super.clone();
      DBElement clonedElement = (DBElement)clonedObject;
      int type = clonedElement.getType();
      if ((type == 15) || (type == 14) || (type == 12)) {
        DBElementValue elementValue = (DBElementValue)clonedElement.getValue();
        clonedElement.setValue(elementValue.clone());
      }
    } catch (CloneNotSupportedException exc) {
      throw new UnsupportedOperationException("Unable to clone database element", exc);
    }

    return clonedObject;
  }
}

