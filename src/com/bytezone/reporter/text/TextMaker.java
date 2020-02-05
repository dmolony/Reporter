package com.bytezone.reporter.text;

import com.bytezone.reporter.record.Record;

// -----------------------------------------------------------------------------------//
public interface TextMaker
// -----------------------------------------------------------------------------------//
{
  public String getText (byte[] buffer, int offset, int length);

  public String getText (Record record);

  public String getTextRightTrim (byte[] buffer, int offset, int length);

  public char getChar (int value);

  public boolean test (byte[] buffer, int offset, int length);

  public int countAlphanumericBytes (byte[] buffer, int offset, int length);

  public default int countAlphanumericBytes (Record record)
  {
    return countAlphanumericBytes (record.buffer, record.offset, record.length);
  }

  public default boolean test (Record record)
  {
    return test (record.buffer, record.offset, record.length);
  }

  default StringBuilder rightTrim (StringBuilder text)
  {
    while (text.length () > 0 && text.charAt (text.length () - 1) == ' ')
      text.deleteCharAt (text.length () - 1);
    return text;
  }
}