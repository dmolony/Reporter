package com.bytezone.reporter.format;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

public interface RecordFormatter
{
  // public String getFormattedRecord (byte[] buffer);

  public String getFormattedFastRecord (Record record);

  public void setTextMaker (TextMaker textMaker);
}