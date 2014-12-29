package TWEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class LocalizedString extends DBElementValue
  implements Cloneable
{
  private int stringReference;
  private List<LocalizedSubstring> substringList;

  public LocalizedString(int reference)
  {
    this.stringReference = reference;
    this.substringList = new ArrayList(4);
  }

  public void addSubstring(LocalizedSubstring substring)
  {
    int language = substring.getLanguage();
    int gender = substring.getGender();
    ListIterator li = this.substringList.listIterator();
    boolean found = false;
    while (li.hasNext()) {
      LocalizedSubstring oldSubstring = (LocalizedSubstring)li.next();
      if ((oldSubstring.getLanguage() == language) && (oldSubstring.getGender() == gender)) {
        li.set(substring);
        found = true;
        break;
      }
    }

    if (!found)
      this.substringList.add(substring);
  }

  public int getStringReference()
  {
    return this.stringReference;
  }

  public void setStringReference(int reference)
  {
    this.stringReference = reference;
  }

  public int getSubstringCount()
  {
    return this.substringList.size();
  }

  public LocalizedSubstring getSubstring(int index)
  {
    return (LocalizedSubstring)this.substringList.get(index);
  }

  public void setSubstring(int index, LocalizedSubstring substring)
  {
    this.substringList.set(index, substring);
  }

  public LocalizedSubstring getSubstring(int language, int gender)
  {
    LocalizedSubstring value = null;
    for (LocalizedSubstring substring : this.substringList) {
      if ((substring.getLanguage() == language) && (substring.getGender() == gender)) {
        value = substring;
        break;
      }
    }

    return value;
  }

  public Object clone()
  {
    Object clonedObject = super.clone();
    LocalizedString clonedString = (LocalizedString)clonedObject;

    int count = this.substringList.size();
    clonedString.substringList = new ArrayList(count);
    for (int i = 0; i < count; i++) {
      clonedString.substringList.add((LocalizedSubstring)((LocalizedSubstring)this.substringList.get(i)).clone());
    }
    return clonedObject;
  }
}

