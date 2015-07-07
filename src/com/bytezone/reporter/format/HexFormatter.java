package com.bytezone.reporter.format;

import com.bytezone.reporter.record.Record;

public class HexFormatter extends DefaultFormatter
{
  static final int HEX_LINE_SIZE = 16;

  // @Override
  // public String getFormattedRecord (byte[] buffer)
  // {
  // final int lineSize = 16;
  // StringBuilder text = new StringBuilder ();
  //
  // for (int ptr = 0, max = buffer.length; ptr < max; ptr += lineSize)
  // {
  // final StringBuilder hexLine = new StringBuilder ();
  //
  // for (int linePtr = 0; linePtr < lineSize; linePtr++)
  // {
  // if (ptr + linePtr >= max)
  // break;
  // int val = buffer[ptr + linePtr] & 0xFF;
  // hexLine.append (String.format ("%02X ", val));
  // }
  //
  // text.append (String.format ("%06X %-48s %s%n", ptr, hexLine.toString (),
  // textMaker.getText (buffer, ptr, lineSize)));
  // }
  //
  // return text.toString ();
  // }

  @Override
  public String getFormattedFastRecord (Record record)
  {
    StringBuilder text = new StringBuilder ();

    int max = record.offset + record.length;
    for (int ptr = record.offset; ptr < max; ptr += HEX_LINE_SIZE)
    {
      StringBuilder hexLine = new StringBuilder ();

      int lineMax = Math.min (ptr + HEX_LINE_SIZE, max);
      for (int linePtr = ptr; linePtr < lineMax; linePtr++)
        hexLine.append (String.format ("%02X ", record.buffer[linePtr] & 0xFF));

      text.append (String.format ("%06X  %-48s %s%n", ptr, hexLine.toString (),
                                  textMaker.getText (record.buffer, ptr, lineMax - ptr)));
    }

    return text.toString ();
  }
}