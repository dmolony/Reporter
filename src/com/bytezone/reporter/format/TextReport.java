package com.bytezone.reporter.format;

import java.util.List;

import com.bytezone.reporter.application.Report;
import com.bytezone.reporter.record.Record;

public class TextReport extends Report
{
  public TextReport (List<Record> records)
  {
    super (records);
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    return textMaker.getText (record.buffer, record.offset, record.length);
  }
}