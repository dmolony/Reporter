package com.bytezone.reporter.text;

import java.io.UnsupportedEncodingException;

public class EbcdicTextMaker implements TextMaker
{
  static final String EBCDIC = "CP1047";

  public static final int[] ebc2asc = new int[256];
  public static final int[] asc2ebc = new int[256];

  static
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
        textLine.append ((char) ebc2asc[val]);
    }

    rightTrim (textLine);

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
      if (val < 0x40 || val == 0xFF)
        return false;
    }
    return true;
  }
}