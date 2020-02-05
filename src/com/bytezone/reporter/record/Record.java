package com.bytezone.reporter.record;

import com.bytezone.reporter.text.TextMaker;

// -----------------------------------------------------------------------------------//
public class Record
// -----------------------------------------------------------------------------------//
{
  static final int HEX_LINE_SIZE = 16;

  public final byte[] buffer;
  public final int recordNumber;
  public final int offset;
  public final int length;

  // ---------------------------------------------------------------------------------//
  public Record (byte[] buffer, int offset, int length, int recordNumber)
  // ---------------------------------------------------------------------------------//
  {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    this.recordNumber = recordNumber;
  }

  // ---------------------------------------------------------------------------------//
  public int countTrailingNulls ()
  // ---------------------------------------------------------------------------------//
  {
    int nulls = 0;
    int ptr = offset + length - 1;
    while (ptr >= offset)
    {
      if (buffer[ptr--] == 0)
        nulls++;
      else
        break;
    }
    return nulls;
  }

  // ---------------------------------------------------------------------------------//
  public String toHex (TextMaker textMaker)
  // ---------------------------------------------------------------------------------//
  {
    if (length == 0)
      return String.format ("%06X", offset);

    StringBuilder text = new StringBuilder ();

    int max = offset + length;
    for (int ptr = offset; ptr < max; ptr += HEX_LINE_SIZE)
    {
      StringBuilder hexLine = new StringBuilder ();

      int lineMax = Math.min (ptr + HEX_LINE_SIZE, max);
      for (int linePtr = ptr; linePtr < lineMax; linePtr++)
        hexLine.append (String.format ("%02X ", buffer[linePtr] & 0xFF));

      text.append (String.format ("%06X  %-48s %s%n", ptr, hexLine.toString (),
          textMaker.getText (buffer, ptr, lineMax - ptr)));
    }

    if (text.length () > 0)
      text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%,5d  %,5d  %,5d  %04X  %04X", recordNumber, offset, length,
        offset, length);
  }
}