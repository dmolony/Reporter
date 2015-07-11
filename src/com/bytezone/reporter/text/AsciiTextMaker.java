package com.bytezone.reporter.text;

public class AsciiTextMaker implements TextMaker
{
  @Override
  public String getText (byte[] buffer, int offset, int length)
  {
    final StringBuilder textLine = new StringBuilder ();

    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr++)
    {
      int value = buffer[ptr] & 0xFF;
      if (value < 0x20 || value > 0xF0)
        textLine.append ('.');
      else
        textLine.append ((char) value);
    }

    return rightTrim (textLine).toString ();
  }

  @Override
  public char getChar (int value)
  {
    return (char) value;
  }

  @Override
  public boolean test (byte[] buffer, int offset, int length)
  {
    if (length == 0)
      return false;

    for (int ptr = offset, max = offset + length; ptr < max; ptr++)
    {
      int value = buffer[ptr] & 0xFF;
      if (value < 0x20 || value > 0xF0)
        return false;
    }
    return true;
  }
}