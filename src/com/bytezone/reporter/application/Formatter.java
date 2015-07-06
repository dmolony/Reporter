package com.bytezone.reporter.application;

import com.bytezone.reporter.format.HexFormatter;
import com.bytezone.reporter.format.RecordFormatter;
import com.bytezone.reporter.format.StringFormatter;
import com.bytezone.reporter.text.TextMaker;

public class Formatter
{
  private RecordFormatter recordFormatter;
  private final RecordFormatter hexFormatter = new HexFormatter ();
  private final RecordFormatter stringFormatter = new StringFormatter ();

  enum FormatType
  {
    HEX, TEXT
  }

  public void setTextMaker (TextMaker textMaker)
  {
    hexFormatter.setTextMaker (textMaker);
    stringFormatter.setTextMaker (textMaker);
  }

  public void setFormatter (FormatType formatType)
  {
    this.recordFormatter = formatType == FormatType.TEXT ? stringFormatter : hexFormatter;
  }

  public String getFormattedRecord (byte[] record)
  {
    return recordFormatter.getFormattedRecord (record);
  }
}