package TWEditor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyDatabase
{
  private File file;
  private List<KeyEntry> keyEntries;
  private Map<String, KeyEntry> keyEntriesMap;
  private List<String> archiveNames;

  public KeyDatabase(String filePath)
    throws DBException, IOException
  {
    this(new File(filePath));
  }

  public KeyDatabase(File file)
    throws DBException, IOException
  {
    this.file = file;
    readFile();
  }

  private void readFile()
    throws DBException, IOException
  {
    RandomAccessFile in = new RandomAccessFile(this.file, "r");

    byte[] header = new byte[68];
    int count = in.read(header);
    if (count != header.length) {
      throw new DBException("KEY header length is incorrect");
    }
    String signature = new String(header, 0, 4);
    if (!signature.equals("KEY ")) {
      throw new DBException("KEY header signature is incorrect");
    }
    String version = new String(header, 4, 4);
    if (!version.equals("V1.1")) {
      throw new DBException("KEY header version " + version + " is not supported");
    }
    int fileCount = getInteger(header, 8);
    long fileOffset = getInteger(header, 20);
    int keyCount = getInteger(header, 12);
    long keyOffset = getInteger(header, 24);

    this.archiveNames = new ArrayList(fileCount);
    byte[] fileBuffer = new byte[12];
    byte[] nameBuffer = new byte[256];
    for (int i = 0; i < fileCount; i++) {
      in.seek(fileOffset);
      count = in.read(fileBuffer);
      if (count != fileBuffer.length) {
        throw new DBException("File table truncated");
      }
      long nameOffset = getInteger(fileBuffer, 4);
      int nameLength = getInteger(fileBuffer, 8);
      if (nameLength > nameBuffer.length) {
        nameBuffer = new byte[nameLength];
      }
      in.seek(nameOffset);
      in.read(nameBuffer, 0, nameLength);
      String fileName = new String(nameBuffer, 0, nameLength);
      this.archiveNames.add(fileName);

      fileOffset += 12L;
    }

    this.keyEntries = new ArrayList(keyCount);
    this.keyEntriesMap = new HashMap(keyCount);
    byte[] keyBuffer = new byte[26];
    for (int i = 0; i < keyCount; i++) {
      in.seek(keyOffset);
      count = in.read(keyBuffer);
      if (count != keyBuffer.length) {
        throw new DBException("Key table truncated");
      }
      int nameLength;
      for (nameLength = 1; (nameLength < 16) &&
        (keyBuffer[nameLength] != 0); nameLength++);
      String resourceName = new String(keyBuffer, 0, nameLength);
      int resourceType = getShort(keyBuffer, 16);
      int resourceID = getInteger(keyBuffer, 18);
      int index = getInteger(keyBuffer, 22) >>> 20;
      if (index >= this.archiveNames.size()) {
        throw new DBException("BIF index for resource " + resourceName + " is too large");
      }
      String archivePath = this.file.getParent() + Main.fileSeparator + (String)this.archiveNames.get(index);
      KeyEntry keyEntry = new KeyEntry(resourceName, resourceType, resourceID, archivePath);
      this.keyEntries.add(keyEntry);
      this.keyEntriesMap.put(keyEntry.getFileName().toLowerCase(), keyEntry);
      keyOffset += 26L;
    }

    in.close();
  }

  public String getName()
  {
    return this.file.getName();
  }

  public List<KeyEntry> getEntries()
  {
    return this.keyEntries;
  }

  public KeyEntry getEntry(String fileName)
  {
    return (KeyEntry)this.keyEntriesMap.get(fileName.toLowerCase());
  }

  private int getShort(byte[] buffer, int offset)
  {
    int value = buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8;
    if (value >= 32768) {
      value |= -65536;
    }
    return value;
  }

  private int getInteger(byte[] buffer, int offset)
  {
    return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
  }
}

