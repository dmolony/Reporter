package com.bytezone.reporter.record;

import java.util.ArrayList;
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
  protected List<Record> split ()
  {
    List<Record> records = new ArrayList<Record> ();
    records.add (new Record (buffer, 0, buffer.length, 0));
    return records;
  }

  @Override
  protected byte[] join ()
  {
    return new byte[0];
  }
}