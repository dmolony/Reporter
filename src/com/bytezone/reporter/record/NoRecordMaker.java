package com.bytezone.reporter.record;

import java.util.List;

public class NoRecordMaker extends DefaultRecordMaker
{
  public NoRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  public NoRecordMaker (List<Record> records)
  {
    super (records);
  }

  @Override
  protected void split ()
  {
    records.add (new Record (buffer, 0, buffer.length, 0));
  }

  @Override
  protected byte[] join ()
  {
    return new byte[0];
  }
}