package TWEditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class SaveOutputStream extends OutputStream
{
  private SaveEntry entry;
  private FileOutputStream outputStream;
  private List<byte[]> resourceDataList;
  private int dataIndex;
  private int dataOffset;
  private int resourceLength;

  public SaveOutputStream(SaveEntry entry)
    throws IOException
  {
    this.entry = entry;
    entry.setResourceOffset(0L);
    entry.setResourceLength(0);
    if (entry.isOnDisk()) {
      File file = entry.getResourceFile();
      if (file.exists()) {
        file.delete();
      }
      this.outputStream = new FileOutputStream(file);
    } else {
      this.resourceDataList = entry.getResourceDataList();
      this.resourceDataList.clear();
      this.resourceDataList.add(new byte[4096]);
    }
  }

  public void write(int b)
    throws IOException
  {
    if (this.entry == null) {
      throw new IOException("Output stream is not open");
    }
    if (this.outputStream != null) {
      this.outputStream.write(b);
    } else {
      byte[] dataBuffer = (byte[])this.resourceDataList.get(this.dataIndex);
      dataBuffer[this.dataOffset] = ((byte)b);
      this.dataOffset += 1;
      if (this.dataOffset == dataBuffer.length) {
        this.resourceDataList.add(new byte[4096]);
        this.dataIndex += 1;
        this.dataOffset = 0;
      }
    }

    this.resourceLength += 1;
  }

  public void write(byte[] buffer)
    throws IOException
  {
    write(buffer, 0, buffer.length);
  }

  public void write(byte[] buffer, int bufferOffset, int bufferLength)
    throws IOException
  {
    if (this.entry == null) {
      throw new IOException("Output stream is not open");
    }
    if (this.outputStream != null) {
      this.outputStream.write(buffer, bufferOffset, bufferLength);
    } else {
      int count = 0;
      while (count < bufferLength) {
        byte[] dataBuffer = (byte[])this.resourceDataList.get(this.dataIndex);
        int length = Math.min(bufferLength - count, dataBuffer.length - this.dataOffset);
        for (int i = 0; i < length; i++) {
          dataBuffer[(this.dataOffset + i)] = buffer[(bufferOffset + count + i)];
        }
        count += length;
        this.dataOffset += length;
        if (this.dataOffset == dataBuffer.length) {
          this.resourceDataList.add(new byte[4096]);
          this.dataIndex += 1;
          this.dataOffset = 0;
        }
      }
    }

    this.resourceLength += bufferLength;
  }

  public void flush()
    throws IOException
  {
    if (this.entry == null) {
      throw new IOException("Output stream is not open");
    }
    if (this.outputStream != null)
      this.outputStream.flush();
  }

  public void close()
    throws IOException
  {
    if (this.entry != null) {
      if (this.outputStream != null) {
        this.outputStream.close();
        this.outputStream = null;
      }

      this.entry.setResourceLength(this.resourceLength);
      this.entry = null;
    }
  }

  protected void finalize()
  {
    try
    {
      close();
      super.finalize();
    } catch (Throwable exc) {
      Main.logException("Exception while finalizing output stream", exc);
    }
  }
}

