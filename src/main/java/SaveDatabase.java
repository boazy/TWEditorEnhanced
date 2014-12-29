package TWEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveDatabase
{
  private File file;
  private String saveName;
  private int dataOffset;
  private List<SaveEntry> entries;
  private Map<String, SaveEntry> entryMap;

  public SaveDatabase(String filename)
  {
    this(new File(filename));
  }

  public SaveDatabase(File file)
  {
    this.file = file;
    this.entries = new ArrayList(160);
    this.entryMap = new HashMap(160);

    this.saveName = file.getName();
    int sep = this.saveName.lastIndexOf(46);
    if (sep > 0)
      this.saveName = this.saveName.substring(0, sep);
  }

  public void load()
    throws DBException, IOException
  {
    RandomAccessFile in = new RandomAccessFile(this.file, "r");
    try
    {
      byte[] buffer = new byte[40];
      int count = in.read(buffer, 0, 12);
      if (count != 12) {
        throw new DBException("Save header truncated");
      }
      String signature = new String(buffer, 0, 4);
      if (!signature.equals("RGMH")) {
        throw new DBException("Save signature is not valid");
      }
      int version = getInteger(buffer, 4);
      if (version != 1) {
        throw new DBException("Save version " + version + " is not supported");
      }
      this.dataOffset = getInteger(buffer, 8);

      in.seek(in.length() - 8L);
      count = in.read(buffer, 0, 8);
      if (count != 8) {
        throw new DBException("Save trailer truncated");
      }
      int resourceOffset = getInteger(buffer, 0);
      int resourceCount = getInteger(buffer, 4);
      in.seek(resourceOffset);

      for (int i = 0; i < resourceCount; i++) {
        count = in.read(buffer, 0, 4);
        if (count != 4) {
          throw new DBException("Resource table truncated");
        }
        int length = getInteger(buffer, 0);
        if (buffer.length < length) {
          buffer = new byte[length];
        }
        count = in.read(buffer, 0, length);
        if (count != length) {
          throw new DBException("Resource name truncated");
        }
        String name = new String(buffer, 0, length, "UTF-8");
        count = in.read(buffer, 0, 8);
        if (count != 8) {
          throw new DBException("Resource table truncated");
        }
        length = getInteger(buffer, 0);
        int offset = getInteger(buffer, 4);
        SaveEntry saveEntry = new SaveEntry(name, this.file, offset, length);
        this.entries.add(saveEntry);
        this.entryMap.put(saveEntry.getResourceName(), saveEntry);
      }
    } finally {
      if (in != null)
        in.close();
    }
  }

  public void save()
    throws IOException
  {
    File outputFile = new File(this.file.getPath() + ".tmp");
    if (outputFile.exists()) {
      outputFile.delete();
    }
    OutputStream out = new FileOutputStream(outputFile);
    InputStream in = null;
    byte[] buffer = new byte[4096];

    int listOffset = this.dataOffset;
    try
    {
      in = new FileInputStream(this.file);
      int residualLength = this.dataOffset;
      while (residualLength > 0) {
        int length = Math.min(residualLength, buffer.length);
        int count = in.read(buffer, 0, length);
        if (count != length) {
          throw new IOException("Save game header truncated");
        }
        out.write(buffer, 0, count);
        residualLength -= count;
      }

      in.close();

      for (SaveEntry entry : this.entries) {
        if (entry.isOnDisk()) {
          in = new FileInputStream(entry.getResourceFile());
          in.skip(entry.getResourceOffset());
          residualLength = entry.getResourceLength();
          listOffset += residualLength;
          while (residualLength > 0) {
            int length = Math.min(residualLength, buffer.length);
            int count = in.read(buffer, 0, length);
            if (count != length) {
              throw new IOException("Resource data truncated for " + entry.getResourceName());
            }
            out.write(buffer, 0, count);
            residualLength -= count;
          }

          in.close();
        } else {
          List resourceDataList = entry.getResourceDataList();
          residualLength = entry.getResourceLength();
          listOffset += residualLength;
          int index = 0;
          while (residualLength > 0) {
            byte[] dataBuffer = (byte[])resourceDataList.get(index);
            int length = Math.min(residualLength, dataBuffer.length);
            out.write(dataBuffer, 0, length);
            residualLength -= length;
            index++;
          }

        }

      }

      int offset = this.dataOffset;
      for (SaveEntry entry : this.entries) {
        byte[] nameBytes = entry.getResourcePath().getBytes("UTF-8");
        setInteger(nameBytes.length, buffer, 0);
        out.write(buffer, 0, 4);
        out.write(nameBytes);
        int length = entry.getResourceLength();
        setInteger(length, buffer, 0);
        setInteger(offset, buffer, 4);
        out.write(buffer, 0, 8);
        offset += length;
      }

      setInteger(listOffset, buffer, 0);
      setInteger(this.entries.size(), buffer, 4);
      out.write(buffer, 0, 8);

      out.close();
      out = null;

      if ((this.file.exists()) && 
        (!this.file.delete())) {
        throw new IOException("Unable to delete '" + this.file.getName() + "'");
      }
      if (!outputFile.renameTo(this.file)) {
        throw new IOException("Unable to rename '" + outputFile.getName() + "'");
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
    return this.saveName;
  }

  public File getFile()
  {
    return this.file;
  }

  public String getPath()
  {
    return this.file.getPath();
  }

  public List<SaveEntry> getEntries()
  {
    return this.entries;
  }

  public SaveEntry getEntry(String resourceName)
  {
    SaveEntry entry = (SaveEntry)this.entryMap.get(resourceName.toLowerCase());
    if (entry == null) {
        String resourcePath = this.getName() + "\\" + resourceName;
        entry = (SaveEntry)this.entryMap.get(resourcePath.toLowerCase());
    }
    return entry;
  }

  public void addEntry(SaveEntry entry)
  {
    String name = entry.getResourceName();
    SaveEntry oldEntry = (SaveEntry)this.entryMap.get(name);
    if (oldEntry != null) {
      int index = this.entries.indexOf(oldEntry);
      this.entries.set(index, entry);
    } else {
      this.entries.add(entry);
    }

    this.entryMap.put(name, entry);
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
}

