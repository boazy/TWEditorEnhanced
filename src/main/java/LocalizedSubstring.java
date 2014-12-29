package TWEditor;

public class LocalizedSubstring
  implements Cloneable
{
  private int language;
  private int gender;
  private String string;

  public LocalizedSubstring(String string, int language, int gender)
  {
    this.string = string;
    this.language = language;
    this.gender = gender;
  }

  public String getString()
  {
    return this.string;
  }

  public void setString(String string)
  {
    this.string = string;
  }

  public int getLanguage()
  {
    return this.language;
  }

  public void setLanguage(int language)
  {
    this.language = language;
  }

  public int getGender()
  {
    return this.gender;
  }

  public void setGender(int gender)
  {
    this.gender = gender;
  }

  public Object clone()
  {
    Object clonedObject;
    try
    {
      clonedObject = super.clone();
    } catch (CloneNotSupportedException exc) {
      throw new UnsupportedOperationException("Unable to clone localized substring", exc);
    }

    return clonedObject;
  }
}

