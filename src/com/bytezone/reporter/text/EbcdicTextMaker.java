package com.bytezone.reporter.text;

import com.bytezone.reporter.application.Utility;

public class EbcdicTextMaker implements TextMaker
{
  @Override
  public String getText (byte[] buffer, int offset, int length)
  {
    final StringBuilder textLine = new StringBuilder ();

    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr++)
    {
      int val = buffer[ptr] & 0xFF;
      if (val < 0x40 || val == 0xFF)
        textLine.append ('.');
      else
        textLine.append ((char) Utility.ebc2asc[val]);
    }

    return textLine.toString ();
  }
}