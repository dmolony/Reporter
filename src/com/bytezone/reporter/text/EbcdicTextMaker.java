package com.bytezone.reporter.text;

import java.io.UnsupportedEncodingException;

import com.bytezone.reporter.record.Record;

// -----------------------------------------------------------------------------------//
public class EbcdicTextMaker implements TextMaker
// -----------------------------------------------------------------------------------//
{
  static final String EBCDIC = "CP1047";

  public static final int[] ebc2asc = new int[256];
  public static final int[] asc2ebc = new int[256];

  // ---------------------------------------------------------------------------------//
  static
  // ---------------------------------------------------------------------------------//
  {
    byte[] values = new byte[256];
    for (int i = 0; i < 256; i++)
      values[i] = (byte) i;

    try
    {
      String s = new String (values, EBCDIC);
      char[] chars = s.toCharArray ();
      for (int i = 0; i < 256; i++)
      {
        int val = chars[i];
        ebc2asc[i] = val;
        asc2ebc[val] = i;
      }
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace ();
    }
  }

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
      if (value != 0x40 && (value < 0x4B || value == 0xFF))
        textLine.append ('.');
      else
        textLine.append ((char) ebc2asc[value]);
    }
    return textLine;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public char getChar (int value)
  // ---------------------------------------------------------------------------------//
  {
    return (char) ebc2asc[value];
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean test (byte[] buffer, int offset, int length)
  // ---------------------------------------------------------------------------------//
  {
    //    if (length == 0)
    //      return false;

    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr++)
    {
      int value = buffer[ptr] & 0xFF;
      if (value != 0x40 && (value < 0x4B || value == 0xFF))
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

  @Override
  public int countAlphanumericBytes (byte[] buffer, int offset, int length)
  {
    int total = 0;
    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr++)
    {
      int value = buffer[ptr] & 0xFF;
      if (value == 0x40 || (value >= 0xC1 && value <= 0xF9))
        total++;
    }
    return total;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return "EBCDIC";
  }
}