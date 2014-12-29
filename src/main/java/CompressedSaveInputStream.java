package TWEditor;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class CompressedSaveInputStream extends GZIPInputStream
{
  private InputStream saveInputStream;

  public CompressedSaveInputStream(SaveInputStream inputStream)
    throws IOException
  {
    super(inputStream, 4096);
    this.saveInputStream = inputStream;
  }

  public void close()
    throws IOException
  {
    if (this.saveInputStream != null) {
      super.close();
      this.saveInputStream.close();
      this.saveInputStream = null;
    }
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

