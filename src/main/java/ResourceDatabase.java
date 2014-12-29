package TWEditor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceDatabase
{
  public static final String[] databaseTypes = { "ERF ", "HAK ", "MOD ", "NWM ", "SAV " };

  public static final String[] databaseVersions = { "V1.0", "V1.1" };
  private File file;
  private String databaseType;
  private String databaseVersion;
  private LocalizedString description;
  private List<ResourceEntry> entries;
  private Map<String, ResourceEntry> entryMap;

  public ResourceDatabase(String filePath)
  {
    this(new File(filePath));
  }

  public ResourceDatabase(File file)
  {
    this.file = file;
    this.databaseType = "ERF ";
    this.databaseVersion = "V1.0";
    this.description = new LocalizedString(-1);
    this.entries = new ArrayList(64);
    this.entryMap = new HashMap(64);
  }

  public void load()
    throws DBException, IOException
  {
    RandomAccessFile in = new RandomAccessFile(this.file, "r");
    try
    {
      byte[] header = new byte[' '];
      int count = in.read(header);
      if (count != 160) {
        throw new DBException("Database header is too short");
      }
      boolean validType = false;
      this.databaseType = new String(header, 0, 4);
      for (int i = 0; i < databaseTypes.length; i++) {
        if (this.databaseType.equals(databaseTypes[i])) {
          validType = true;
          break;
        }
      }

      if (!validType) {
        throw new DBException("Database type '" + this.databaseType + "' is not supported");
      }
      boolean validVersion = false;
      this.databaseVersion = new String(header, 4, 4);
      for (int i = 0; i < databaseVersions.length; i++) {
        if (this.databaseVersion.equals(databaseVersions[i])) {
          validVersion = true;
          break;
        }
      }

      if (!validVersion) {
        throw new DBException("Database version '" + this.databaseVersion + "' is not supported");
      }
      int stringCount = getInteger(header, 8);
      int stringSize = getInteger(header, 12);
      int entryCount = getInteger(header, 16);
      int stringOffset = getInteger(header, 20);
      int keyOffset = getInteger(header, 24);
      int resourceOffset = getInteger(header, 28);
      int stringReference = getInteger(header, 40);

      this.description = new LocalizedString(stringReference);
      this.entries = new ArrayList(Math.max(entryCount, 10));
      this.entryMap = new HashMap(Math.max(entryCount, 10));

      if (stringCount > 0) {
        in.seek(stringOffset);
        byte[] buffer = new byte[''];
        for (int i = 0; i < stringCount; i++) {
          count = in.read(buffer, 0, 8);
          if (count != 8) {
            throw new DBException("String list truncated");
          }
          int language = getInteger(buffer, 0);
          int stringLength = getInteger(buffer, 4);
          int gender = language & 0x1;
          language >>= 1;
          String string;
          if (stringLength > 0) {
            if (stringLength > buffer.length) {
              buffer = new byte[stringLength];
            }
            count = in.read(buffer, 0, stringLength);
            if (count != stringLength) {
              throw new DBException("String list truncated");
            }
            string = new String(buffer, 0, stringLength, "UTF-8");
            stringLength = string.length();
            if (string.charAt(stringLength - 1) == 0)
              string = string.substring(0, stringLength - 1);
          } else {
            string = new String();
          }

          this.description.addSubstring(new LocalizedSubstring(string, language, gender));
        }

      }

      List resourceNames = new ArrayList(entryCount);
      List resourceTypes = new ArrayList(entryCount);
      if (entryCount > 0) {
        in.seek(keyOffset);
        int nameLength;
        int keyLength;
        if (this.databaseVersion.equals("V1.0")) {
          keyLength = 24;
          nameLength = 16;
        } else {
          keyLength = 40;
          nameLength = 32;
        }

        byte[] key = new byte[keyLength];
        for (int i = 0; i < entryCount; i++) {
          count = in.read(key);
          if (count != keyLength) {
            throw new DBException("Key list truncated");
          }
          for (count = 0; (count < nameLength) && 
            (key[count] != 0); count++);
          resourceNames.add(new String(key, 0, count));
          resourceTypes.add(new Integer(getShort(key, nameLength + 4)));
        }

      }

      if (entryCount > 0) {
        in.seek(resourceOffset);
        byte[] element = new byte[8];
        for (int i = 0; i < entryCount; i++) {
          count = in.read(element);
          if (count != 8) {
            throw new DBException("Resource list truncated");
          }
          long offset = getInteger(element, 0);
          int length = getInteger(element, 4);
          String resourceName = (String)resourceNames.get(i);
          int resourceType = ((Integer)resourceTypes.get(i)).intValue();
          if ((resourceName.length() > 0) && (resourceType != 65535)) {
            ResourceEntry entry = new ResourceEntry(resourceName, resourceType, this.file, offset, length);
            this.entries.add(entry);
            this.entryMap.put(entry.getName(), entry);
          }
        }
      }
    }
    finally
    {
      in.close();
    }
  }

  public void save()
    throws DBException, IOException
  {
    File outputFile = new File(this.file.getPath() + ".tmp");
    if (outputFile.exists()) {
      outputFile.delete();
    }
    RandomAccessFile out = new RandomAccessFile(outputFile, "rw");
    RandomAccessFile in = null;
    try
    {
      byte[] header = new byte[' '];
      out.write(header);

      byte[] buffer = new byte[''];
      int stringOffset = (int)out.getFilePointer();
      int stringSize = 0;
      int stringCount = this.description.getSubstringCount();
      for (int i = 0; i < stringCount; i++) {
        LocalizedSubstring substring = this.description.getSubstring(i);
        String string = substring.getString();
        byte[] stringBytes = string.getBytes();
        int length = stringBytes.length;
        if (length + 8 > buffer.length) {
          buffer = new byte[length + 8];
        }
        setInteger(substring.getLanguage() * 2 + substring.getGender(), buffer, 0);
        setInteger(length, buffer, 4);
        for (int j = 0; j < length; j++) {
          buffer[(j + 8)] = stringBytes[j];
        }
        out.write(buffer, 0, length + 8);
        stringSize += length + 8;
      }

      int entryCount = this.entries.size();
      int keyOffset = (int)out.getFilePointer();
      int resourceID = 0;
      int entryLength;
      int nameLength;
      if (this.databaseVersion.equals("V1.1")) {
        nameLength = 32;
        entryLength = 40;
      } else {
        nameLength = 16;
        entryLength = 24;
      }

      byte[] keyBuffer = new byte[entryLength];
      for (ResourceEntry entry : this.entries) {
        byte[] nameBytes = entry.getResourceName().getBytes();
        if (nameBytes.length > nameLength) {
          throw new DBException("Resource name '" + entry.getResourceName() + "' is too long");
        }
        int index;
        for (index = 0; index < nameBytes.length; index++) {
          keyBuffer[index] = nameBytes[index];
        }
        for (; index < nameLength; index++) {
          keyBuffer[index] = 0;
        }
        setInteger(resourceID, keyBuffer, nameLength);
        setShort(entry.getResourceType(), keyBuffer, nameLength + 4);
        setShort(0, keyBuffer, nameLength + 6);
        out.write(keyBuffer);
        resourceID++;
      }

      int resourceOffset = (int)out.getFilePointer();
      int dataOffset = resourceOffset + entryCount * 8;

      for (ResourceEntry entry : this.entries) {
        int length = entry.getLength();
        setInteger(dataOffset, buffer, 0);
        setInteger(length, buffer, 4);
        out.write(buffer, 0, 8);
        dataOffset += length;
      }

      buffer = new byte[4096];
      for (ResourceEntry entry : this.entries) {
        in = new RandomAccessFile(entry.getFile(), "r");
        in.seek(entry.getOffset());
        int residualLength = entry.getLength();
        while (residualLength > 0) {
          int length = Math.min(residualLength, buffer.length);
          int count = in.read(buffer, 0, length);
          if (count != length) {
            throw new DBException("Data truncated for resource " + entry.getName());
          }
          out.write(buffer, 0, count);
          residualLength -= count;
        }

        in.close();
        in = null;
      }

      Calendar calendar = new GregorianCalendar();
      calendar.setTime(new Date());

      byte[] typeBytes = this.databaseType.getBytes();
      for (int i = 0; i < 4; i++) {
        header[i] = typeBytes[i];
      }
      byte[] versionBytes = this.databaseVersion.getBytes();
      for (int i = 0; i < 4; i++) {
        header[(i + 4)] = versionBytes[i];
      }
      setInteger(stringCount, header, 8);
      setInteger(stringSize, header, 12);
      setInteger(entryCount, header, 16);
      setInteger(stringOffset, header, 20);
      setInteger(keyOffset, header, 24);
      setInteger(resourceOffset, header, 28);
      setInteger(calendar.get(1) - 1970, header, 32);
      setInteger(calendar.get(6) - 1, header, 36);
      setInteger(this.description.getStringReference(), header, 40);

      out.seek(0L);
      out.write(header, 0, 44);
      out.close();
      out = null;

      if ((this.file.exists()) && 
        (!this.file.delete())) {
        throw new IOException("Unable to delete " + this.file.getName());
      }
      if (!outputFile.renameTo(this.file)) {
        throw new IOException("Unable to rename " + outputFile.getName() + " to " + this.file.getName());
      }

    }
    finally
    {
      if (in != null) {
        in.close();
      }

      if (out != null) {
        out.close();
        if (outputFile.exists())
          outputFile.delete();
      }
    }
  }

  public String getName()
  {
    return this.file.getName();
  }

  public String getPath()
  {
    return this.file.getPath();
  }

  public String getType()
  {
    return this.databaseType;
  }

  public void setType(String type)
  {
    boolean validType = false;
    for (int i = 0; i < databaseTypes.length; i++) {
      if (type.equals(databaseTypes[i])) {
        validType = true;
        break;
      }
    }

    if (!validType) {
      throw new IllegalArgumentException("Database type '" + type + "' is not supported");
    }
    this.databaseType = type;
  }

  public String getVersion()
  {
    return this.databaseVersion;
  }

  public void setVersion(String version)
  {
    boolean validVersion = false;
    for (int i = 0; i < databaseVersions.length; i++) {
      if (version.equals(databaseVersions[i])) {
        validVersion = true;
        break;
      }
    }

    if (!validVersion) {
      throw new IllegalArgumentException("Database version '" + version + "' is not supported");
    }
    this.databaseVersion = version;
  }

  public LocalizedString getDescription()
  {
    return this.description;
  }

  public int getEntryCount()
  {
    return this.entries.size();
  }

  public List<ResourceEntry> getEntries()
  {
    return this.entries;
  }

  public ResourceEntry getEntry(int index)
  {
    ResourceEntry entry;
    if (index < this.entries.size())
      entry = (ResourceEntry)this.entries.get(index);
    else {
      entry = null;
    }
    return entry;
  }

  public ResourceEntry getEntry(String entryName)
  {
    return (ResourceEntry)this.entryMap.get(entryName.toLowerCase());
  }

  public int addEntry(ResourceEntry entry)
  {
    ResourceEntry oldEntry = (ResourceEntry)this.entryMap.get(entry.getName());
    int index;
    if (oldEntry != null) {
      index = this.entries.indexOf(oldEntry);
      this.entries.set(index, entry);
    } else {
      index = this.entries.size();
      this.entries.add(entry);
    }

    this.entryMap.put(entry.getName(), entry);
    return index;
  }

  public int removeEntry(ResourceEntry entry)
  {
    ResourceEntry oldEntry = (ResourceEntry)this.entryMap.get(entry.getName());
    int index;
    if (oldEntry == null) {
      index = -1;
    } else {
      index = this.entries.indexOf(oldEntry);
      this.entries.remove(index);
      this.entryMap.remove(entry.getName());
    }

    return index;
  }

  public void removeEntry(int index)
  {
    ResourceEntry entry = (ResourceEntry)this.entries.remove(index);
    this.entryMap.remove(entry.getName());
  }

  private int getShort(byte[] buffer, int offset)
  {
    return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8;
  }

  private void setShort(int number, byte[] buffer, int offset)
  {
    buffer[offset] = ((byte)number);
    buffer[(offset + 1)] = ((byte)(number >>> 8));
  }

  private int getInteger(byte[] buffer, int offset)
  {
    return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
  }

  private void setInteger(int number, byte[] buffer, int offset)
  {
    buffer[offset] = ((byte)number);
    buffer[(offset + 1)] = ((byte)(number >>> 8));
    buffer[(offset + 2)] = ((byte)(number >>> 16));
    buffer[(offset + 3)] = ((byte)(number >>> 24));
  }

  public String toString()
  {
    return this.file.getPath();
  }
}

