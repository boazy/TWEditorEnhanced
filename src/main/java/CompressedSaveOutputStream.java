package TWEditor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedSaveOutputStream extends GZIPOutputStream
{
  private OutputStream saveOutputStream;

  public CompressedSaveOutputStream(SaveOutputStream outputStream)
    throws IOException
  {
    super(outputStream, 4096);
    this.saveOutputStream = outputStream;
  }

  public void close()
    throws IOException
  {
    if (this.saveOutputStream != null) {
      super.close();
      this.saveOutputStream.close();
      this.saveOutputStream = null;
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

