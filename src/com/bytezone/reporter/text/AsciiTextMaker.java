package com.bytezone.reporter.text;

import com.bytezone.reporter.record.Record;

// -----------------------------------------------------------------------------------//
public class AsciiTextMaker implements TextMaker
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  @Override
  public String getText (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    return getStringBuilder (buffer, offset, length).toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getTextRightTrim (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    return rightTrim (getStringBuilder (buffer, offset, length)).toString ();
  }

  // ---------------------------------------------------------------------------------//
  private StringBuilder getStringBuilder (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    final StringBuilder textLine = new StringBuilder ();

    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr++)
    {
      int value = buffer[ptr] & 0xFF;
      if (value < 0x20 || value >= 0xC0)
        textLine.append ('.');
      else
        textLine.append ((char) value);
    }

    return textLine;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public char getChar (int value)
  // ---------------------------------------------------------------------------------//
  {
    return (char) value;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean test (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr++)
    {
      int value = buffer[ptr] & 0xFF;
      if (value < 0x20 || value >= 0xC0)
        return false;
    }
    return true;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getText (Record record)
  // ---------------------------------------------------------------------------------//
  {
    return getText (record.buffer, record.offset, record.length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int countAlphanumericBytes (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    int total = 0;
    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr++)
    {
      int value = buffer[ptr] & 0xFF;
      if (value == 0x20 || (value >= 0x41 && value <= 0x5A))
        total++;
    }
    return total;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return "ASCII";
  }
}