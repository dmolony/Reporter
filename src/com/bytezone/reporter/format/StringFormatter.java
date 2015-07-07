package com.bytezone.reporter.format;

import com.bytezone.reporter.record.Record;

public class StringFormatter extends DefaultFormatter
{
  @Override
  public String getFormattedFastRecord (Record record)
  {
    return textMaker.getText (record.buffer, record.offset, record.length);
  }
}