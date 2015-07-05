package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class RdwRecordMaker implements RecordMaker
{
  private final byte[] buffer;
  private List<byte[]> records;

  public RdwRecordMaker (byte[] buffer)
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

  }
}