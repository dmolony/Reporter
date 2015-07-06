package com.bytezone.reporter.format;

import com.bytezone.reporter.text.TextMaker;

public interface RecordFormatter
{
  public String getFormattedRecord (byte[] buffer);

  public void setTextMaker (TextMaker textMaker);
}