package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class NoRecordMaker implements RecordMaker
{
  private final byte[] buffer;
  private List<byte[]> records;

  public NoRecordMaker (byte[] buffer)
  {
    this.buffer = buffer;
  }

  @Override
  public List<byte[]> getRecords ()
  {
    if (records == null)
    {
      records = new ArrayList<> ();
      split ();
    }
    return records;
  }

  private void split ()
  {
    records.add (buffer);
  }
}