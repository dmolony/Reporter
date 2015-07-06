package com.bytezone.reporter.format;

public class HexFormatter extends DefaultFormatter
{
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

    return text.toString ();
  }
}