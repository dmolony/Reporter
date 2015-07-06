package com.bytezone.reporter.format;

public class StringFormatter extends DefaultFormatter
{
  @Override
  public String getFormattedRecord (byte[] buffer)
  {
    return textMaker.getText (buffer, 0, buffer.length);
  }
}