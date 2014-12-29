package TWEditor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class StringsDatabase
{
  private File file;
  private RandomAccessFile in;
  private int stringCount;
  private int entryOffset;
  private int stringOffset;
  private int languageID;

  public StringsDatabase(String filePath)
    throws DBException, IOException
  {
    this(new File(filePath));
  }

  public StringsDatabase(File file)
    throws DBException, IOException
  {
    this.file = file;
    this.in = new RandomAccessFile(file, "r");
    readHeader();
  }

  private void readHeader()
    throws DBException, IOException
  {
    byte[] buffer = new byte[20];
    int count = this.in.read(buffer);
    if (count != buffer.length) {
      throw new DBException("TLK header truncated");
    }
    String type = new String(buffer, 0, 4);
    String version = new String(buffer, 4, 4);
    if (!type.equals("TLK ")) {
      throw new DBException(new StringBuilder().append("File type '").append(type).append("' is not supported").toString());
    }
    if (!version.equals("V3.0")) {
      throw new DBException(new StringBuilder().append("File version '").append(version).append("' is not supported").toString());
    }
    this.languageID = getInteger(buffer, 8);
    this.stringCount = getInteger(buffer, 12);
    this.entryOffset = 20;
    this.stringOffset = getInteger(buffer, 16);
  }

  public String getName()
  {
    return this.file.getName();
  }

  public int getLanguageID()
  {
    return this.languageID;
  }

  public String getString(int stringRef)
  {
    String string = null;
    try
    {
      int refid = stringRef & 0xFFFFFF;
      if (refid < this.stringCount) {
        byte[] buffer = new byte[40];
        this.in.seek(this.entryOffset + refid * 40);
        int count = this.in.read(buffer);
        if (count != buffer.length) {
          throw new DBException(new StringBuilder().append("String entry truncated for reference ").append(refid).toString());
        }

        if ((buffer[0] & 0x1) != 0) {
          int offset = getInteger(buffer, 28);
          int length = getInteger(buffer, 32);
          byte[] data = new byte[length];
          this.in.seek(this.stringOffset + offset);
          count = this.in.read(data);
          if (count != length) {
            throw new DBException(new StringBuilder().append("String data truncated for reference ").append(refid).toString());
          }
          string = new String(data, "UTF-8");
        }
      }
    } catch (DBException exc) {
      Main.logException("String database format error", exc);
    } catch (IOException exc) {
      Main.logException("Unable to read string database", exc);
    }

    return string != null ? string : new String();
  }

  public String getLabel(int stringRef)
  {
    StringBuilder string = new StringBuilder(getString(stringRef).trim());

    int sep = string.length() - 1;
    if (sep > 0) {
      char c = string.charAt(sep);
      if ((c == '.') || (c == ':')) {
        string.deleteCharAt(sep);
      }

    }

    int index = 0;
    while (true) {
      sep = string.indexOf("<", index);
      if (sep < 0) {
        break;
      }
      index = sep;
      sep = string.indexOf(">", index);
      if (sep < 0) {
        break;
      }
      string.delete(index, sep + 1);
    }

    index = 0;
    while (true) {
      sep = string.indexOf("{", index);
      if (sep < 0) {
        break;
      }
      index = sep;
      sep = string.indexOf("}", index);
      if (sep < 0) {
        break;
      }
      string.delete(index, sep + 1);
    }

    return string.toString();
  }

  public String getHeading(int stringRef)
  {
    String heading = null;
    String string = getString(stringRef).trim();
    int start = string.indexOf("<cHEADER>");
    if (start < 0)
      start = string.indexOf("<cHeader>");
    if (start < 0)
      start = string.indexOf("<cBOLD>");
    if (start < 0)
      start = string.indexOf("<cBold>");
    if (start >= 0) {
      start = string.indexOf(62, start) + 1;
      int stop = string.indexOf("</c>", start);
      if (stop > start) {
        heading = string.substring(start, stop);
      }
    }
    return heading != null ? heading : string;
  }

  protected void finalize()
  {
    try
    {
      if (this.in != null) {
        this.in.close();
        this.in = null;
      }
    } catch (IOException exc) {
      this.in = null;
    }
  }

  private int getInteger(byte[] buffer, int offset)
  {
    return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
  }
}

