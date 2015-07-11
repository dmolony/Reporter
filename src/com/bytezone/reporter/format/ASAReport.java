package com.bytezone.reporter.format;

import java.util.List;

import com.bytezone.reporter.application.Report;
import com.bytezone.reporter.record.Record;

public class ASAReport extends Report
{
  public ASAReport (List<Record> records)
  {
    super (records);
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    return textMaker.getText (record.buffer, record.offset + 1, record.length - 1);
  }
}