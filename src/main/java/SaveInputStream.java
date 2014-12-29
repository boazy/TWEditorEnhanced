package TWEditor;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SaveInputStream extends InputStream
{
  private SaveEntry entry;
  private FileInputStream inputStream;
  private List<byte[]> resourceDataList;
  private int dataIndex;
  private int dataOffset;
  private int residualLength;

  public SaveInputStream(SaveEntry entry)
    throws IOException
  {
    this.entry = entry;
    this.residualLength = entry.getResourceLength();
    if (entry.isOnDisk()) {
      this.inputStream = new FileInputStream(entry.getResourceFile());
      this.inputStream.skip(entry.getResourceOffset());
    } else {
      this.resourceDataList = entry.getResourceDataList();
    }
  }

  public void close()
    throws IOException
  {
    if (this.inputStream != null) {
      this.inputStream.close();
    }
    this.inputStream = null;
    this.entry = null;
    this.residualLength = 0;
  }

  public int available()
  {
    return this.residualLength;
  }

  public int read()
    throws IOException
  {
    if (this.entry == null)
      throw new IOException("Input stream is not open");
    int result;
    if (this.residualLength == 0) {
      result = -1;
    } else if (this.inputStream != null) {
      result = this.inputStream.read();
      if (result == -1) {
        throw new EOFException("Unexpected end of stream");
      }
      this.residualLength -= 1;
    } else {
      byte[] dataBuffer = (byte[])this.resourceDataList.get(this.dataIndex);
      result = dataBuffer[this.dataOffset] & 0xFF;
      this.dataOffset += 1;
      this.residualLength -= 1;
      if (this.dataOffset == dataBuffer.length) {
        this.dataIndex += 1;
        this.dataOffset = 0;
      }
    }

    return result;
  }

  public int read(byte[] buffer)
    throws IOException
  {
    return read(buffer, 0, buffer.length);
  }

  public int read(byte[] buffer, int bufferOffset, int bufferLength)
    throws IOException
  {
    if (this.entry == null)
      throw new IOException("Input stream is not open");
    int count;
    if (this.residualLength == 0) {
      count = -1;
    } else if (this.inputStream != null) {
      int length = Math.min(this.residualLength, bufferLength);
      count = this.inputStream.read(buffer, bufferOffset, length);
      if (count < 0) {
        throw new EOFException("Unexpected end of stream");
      }
      this.residualLength -= count;
    } else {
      count = 0;
      int length = Math.min(this.residualLength, bufferLength);
      while (count < length) {
        byte[] dataBuffer = (byte[])this.resourceDataList.get(this.dataIndex);
        int copyLength = Math.min(dataBuffer.length - this.dataOffset, length - count);
        for (int i = 0; i < copyLength; i++) {
          buffer[(bufferOffset + count + i)] = dataBuffer[(this.dataOffset + i)];
        }
        count += copyLength;
        this.dataOffset += copyLength;
        if (this.dataOffset == dataBuffer.length) {
          this.dataIndex += 1;
          this.dataOffset = 0;
        }
      }

      this.residualLength -= count;
    }

    return count;
  }

  protected void finalize()
  {
    try
    {
      close();
      super.finalize();
    } catch (Throwable exc) {
      Main.logException("Exception while finalizing input stream", exc);
    }
  }
}

