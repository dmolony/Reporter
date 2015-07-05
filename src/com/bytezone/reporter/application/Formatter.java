package com.bytezone.reporter.application;

import com.bytezone.reporter.format.RecordFormatter;

public class Formatter
{
  private RecordFormatter recordFormatter;

  public Formatter ()
  {
  }

  public void setFormatter (RecordFormatter recordFormatter)
  {
    this.recordFormatter = recordFormatter;
  }

  public String getFormattedRecord (byte[] record)
  {
    return recordFormatter.getFormattedRecord (record);
  }
}