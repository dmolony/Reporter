package com.bytezone.reporter.text;

import com.bytezone.reporter.record.Record;

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

    while (textLine.length () > 0 && textLine.charAt (textLine.length () - 1) == ' ')
      textLine.deleteCharAt (textLine.length () - 1);

    return textLine.toString ();
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

  @Override
  public boolean test (Record record)
  {
    return test (record.buffer, record.offset, record.length);
  }
}