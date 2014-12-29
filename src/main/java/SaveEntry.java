package TWEditor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SaveEntry
{
  private boolean onDisk;
  private boolean compressed;
  private String resourceName;
  private String resourcePath;
  private File resourceFile;
  private long resourceOffset;
  private int resourceLength;
  private List<byte[]> resourceDataList;

  public SaveEntry(String path)
  {
    this.resourcePath = path;
    int sep = this.resourcePath.lastIndexOf(Main.fileSeparator);
    if (sep >= 0)
      this.resourceName = this.resourcePath.substring(sep + 1).toLowerCase();
    else {
      this.resourceName = this.resourcePath.toLowerCase();
    }

    this.resourceDataList = new ArrayList();
    this.onDisk = false;

    sep = this.resourceName.lastIndexOf('.');
    if ((sep > 0) && (this.resourceName.substring(sep).equals(".sav")))
      this.compressed = true;
    else
      this.compressed = false;
  }

  public SaveEntry(String path, File file, long offset, int length)
  {
    this.resourcePath = path;
    int sep = this.resourcePath.lastIndexOf(Main.fileSeparator);
    if (sep >= 0)
      this.resourceName = this.resourcePath.substring(sep + 1).toLowerCase();
    else {
      this.resourceName = this.resourcePath.toLowerCase();
    }

    this.resourceFile = file;
    this.resourceOffset = offset;
    this.resourceLength = length;
    this.onDisk = true;

    sep = this.resourceName.lastIndexOf('.');
    if ((sep > 0) && (this.resourceName.substring(sep).equals(".sav")))
      this.compressed = true;
    else
      this.compressed = false;
  }

  public String getResourceName()
  {
    return this.resourceName;
  }

  public String getResourcePath()
  {
    return this.resourcePath;
  }

  public boolean isOnDisk()
  {
    return this.onDisk;
  }

  public void setOnDisk(boolean onDisk)
  {
    this.onDisk = onDisk;
    this.resourceOffset = 0L;
    this.resourceLength = 0;
    this.resourceFile = null;

    if (onDisk)
      this.resourceDataList = null;
    else
      this.resourceDataList = new ArrayList();
  }

  public boolean isCompressed()
  {
    return this.compressed;
  }

  public File getResourceFile()
  {
    return this.resourceFile;
  }

  public void setResourceFile(File file, int offset, int length)
  {
    this.resourceFile = file;
    this.resourceOffset = offset;
    this.resourceLength = length;
    this.resourceDataList = null;
    this.onDisk = true;
  }

  public long getResourceOffset()
  {
    return this.resourceOffset;
  }

  public void setResourceOffset(long offset)
  {
    this.resourceOffset = offset;
  }

  public int getResourceLength()
  {
    return this.resourceLength;
  }

  public void setResourceLength(int length)
  {
    this.resourceLength = length;
  }

  public List<byte[]> getResourceDataList()
  {
    return this.resourceDataList;
  }

  public InputStream getInputStream()
    throws IOException
  {
    InputStream inputStream;
    if (this.compressed)
      inputStream = new CompressedSaveInputStream(new SaveInputStream(this));
    else {
      inputStream = new SaveInputStream(this);
    }
    return inputStream;
  }

  public OutputStream getOutputStream()
    throws IOException
  {
    OutputStream outputStream;
    if (this.compressed)
      outputStream = new CompressedSaveOutputStream(new SaveOutputStream(this));
    else {
      outputStream = new SaveOutputStream(this);
    }
    return outputStream;
  }
}

