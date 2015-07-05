package com.bytezone.format;

import com.bytezone.text.TextMaker;

public class HexFormatter implements RecordFormatter
{
  TextMaker textMaker;

  public HexFormatter (TextMaker textMaker)
  {
    this.textMaker = textMaker;
  }

  @Override
  public String getFormattedRecord (byte[] buffer)
  {
    final int lineSize = 16;
    StringBuilder text = new StringBuilder ();

    for (int ptr = 0, max = buffer.length; ptr < max; ptr += lineSize)
    {
      final StringBuilder hexLine = new StringBuilder ();

      for (int linePtr = 0; linePtr < lineSize; linePtr++)
      {
        if (ptr + linePtr >= max)
          break;
        int val = buffer[ptr + linePtr] & 0xFF;
        hexLine.append (String.format ("%02X ", val));
      }

      text.append (String.format ("%06X  %-48s %s%n", ptr, hexLine.toString (),
                                  textMaker.getText (buffer, ptr, lineSize)));
    }

    if (text.length () > 0)
      text.deleteCharAt (text.length () - 1);
    return text.toString ();
  }
}