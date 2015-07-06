package com.bytezone.reporter.format;

import com.bytezone.reporter.record.Record;

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

  @Override
  public String getFormattedFastRecord (Record record)
  {
    final int lineSize = 16;
    StringBuilder text = new StringBuilder ();

    int max = record.offset + record.length;
    for (int ptr = record.offset; ptr < max; ptr += lineSize)
    {
      StringBuilder hexLine = new StringBuilder ();

      for (int linePtr = 0; linePtr < lineSize; linePtr++)
      {
        if (ptr + linePtr >= max)
          break;
        int val = record.buffer[ptr + linePtr] & 0xFF;
        hexLine.append (String.format ("%02X ", val));
      }

      text.append (String.format ("%06X  %-48s %s%n", ptr, hexLine.toString (),
                                  textMaker.getText (record.buffer, ptr, lineSize)));
    }

    return text.toString ();
  }
}