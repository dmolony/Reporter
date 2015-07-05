package com.bytezone.reporter.text;

public class AsciiTextMaker implements TextMaker
{
  @Override
  public String getText (byte[] buffer, int offset, int length)
  {
    final StringBuilder textLine = new StringBuilder ();

    for (int ptr = offset, max = offset + length; ptr < max; ptr++)
    {
      int val = buffer[ptr] & 0xFF;
      if (val < 0x20 || val > 0xF0)
        textLine.append ('.');
      else
        textLine.append (new String (buffer, ptr, 1));
    }

    return textLine.toString ();
  }
}