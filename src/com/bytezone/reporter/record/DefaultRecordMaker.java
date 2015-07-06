package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultRecordMaker implements RecordMaker
{
  protected final byte[] buffer;
  protected List<byte[]> records;

  public DefaultRecordMaker (byte[] buffer)
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

  protected abstract void split ();
}
