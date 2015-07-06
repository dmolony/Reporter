package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultRecordMaker implements RecordMaker
{
  protected final byte[] buffer;
  protected List<byte[]> records;
  protected List<Record> fastRecords;

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
      System.out.println ("Building records");
      split ();
    }
    return records;
  }

  public List<Record> getFastRecords ()

  {
    if (fastRecords == null)
    {
      fastRecords = new ArrayList<> ();
      fastSplit ();
    }
    return fastRecords;
  }

  protected abstract void split ();

  protected abstract void fastSplit ();
}
