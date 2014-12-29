package TWEditor;

public class DBException extends Exception
{
  public DBException()
  {
  }

  public DBException(String exceptionMsg)
  {
    super(exceptionMsg);
  }

  public DBException(String exceptionMsg, Throwable cause)
  {
    super(exceptionMsg, cause);
  }
}

