package app.tweditor;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SaveEntry
{
  private boolean onDisk;
  private final boolean compressed;
  private final String resourceName;
  private final String resourcePath;
  private File resourceFile;
  private long resourceOffset;
  private int resourceLength;
  private List<byte[]> resourceDataList;

  public SaveEntry(String path) {
    this(path, null, 0, 0, false);
  }

  public SaveEntry(String path, File file, long offset, int length) {
    this(path, file, offset, length, true);
  }

  private SaveEntry(String path, File file, long offset, int length, boolean onDisk)
  {
    this.resourcePath = path;
    int sep = this.resourcePath.lastIndexOf(Main.fileSeparator);
    if (sep >= 0)
      this.resourceName = this.resourcePath.substring(sep + 1).toLowerCase();
    else {
      this.resourceName = this.resourcePath.toLowerCase();
    }

    if (onDisk) {
      this.resourceFile = file;
      this.resourceOffset = offset;
      this.resourceLength = length;
    } else {
      this.resourceDataList = new ArrayList<>();
    }
    this.onDisk = onDisk;

    sep = this.resourceName.lastIndexOf('.');
    this.compressed = (sep > 0) && (this.resourceName.substring(sep).equals(".sav"));
  }

  public void readFromFile(File file) throws IOException {
      try (FileInputStream in = new FileInputStream(file);
           OutputStream out = getOutputStream()) {
        ByteStreams.copy(in, out);
      }
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
      this.resourceDataList = new ArrayList<>();
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

