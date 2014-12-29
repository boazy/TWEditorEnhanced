package TWEditor;

import javax.swing.JTextField;

public class NumericField extends JTextField
{
  public NumericField()
  {
    this(new String(), 5);
  }

  public NumericField(String string)
  {
    this(string, Math.max(string.length(), 5));
  }

  public NumericField(int columns)
  {
    this(new String(), columns);
  }

  public NumericField(String string, int columns)
  {
    super(new NumericDocument(), string, columns);
  }

  public int getValue()
  {
    String text = getText();
    return text.length() > 0 ? Integer.parseInt(text) : 0;
  }

  public void setValue(int value)
  {
    setText(Integer.toString(value));
  }
}

