package com.bytezone.reporter.text;

import java.io.UnsupportedEncodingException;

public class EbcdicTextMaker implements TextMaker
{
  @Override
  public String getText (byte[] buffer, int offset, int length)
  {
    final StringBuilder textLine = new StringBuilder ();

    try
    {
      int max = Math.min (offset + length, buffer.length);
      for (int ptr = offset; ptr < max; ptr++)
      {
        int val = buffer[ptr] & 0xFF;
        if (val < 0x40 || val == 0xFF)
          textLine.append ('.');
        else
          textLine.append (new String (buffer, ptr, 1, "CP1047"));
      }
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace ();
    }

    return textLine.toString ();
  }
}