package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class NoRecordMaker extends DefaultRecordMaker
{
  public NoRecordMaker ()
  {
    super ("NONE");
  }

  @Override
  protected List<Record> split (byte[] buffer, int offset, int length)
  {
    List<Record> records = new ArrayList<Record> ();
    records.add (new Record (buffer, offset, offset + length, 0));
    return records;
  }

  @Override
  protected byte[] join (List<Record> records)
  {
    return new byte[0];
  }
}