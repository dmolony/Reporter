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
      int val = buffer[ptr] & 0xFF;
      if (val < 0x20 || val > 0xF0)
        textLine.append ('.');
      else
        textLine.append ((char) val);
    }

    return rightTrim (textLine).toString ();
  }

  @Override
  public boolean test (byte[] buffer, int offset, int length)
  {
    if (length == 0)
      return false;

    for (int ptr = offset, max = offset + length; ptr < max; ptr++)
    {
      int val = buffer[ptr] & 0xFF;
      if (val < 0x20 || val > 0xF0)
        return false;
    }
    return true;
  }
}