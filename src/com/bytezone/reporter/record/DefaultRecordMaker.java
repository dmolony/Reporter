package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public abstract class DefaultRecordMaker implements RecordMaker
{
  protected final byte[] buffer;
  protected List<Record> fastRecords;

  public DefaultRecordMaker (byte[] buffer)
  {
    this.buffer = buffer;
  }

  @Override
  public List<Record> getFastRecords ()

  {
    if (fastRecords == null)
    {
      fastRecords = new ArrayList<> ();
      fastSplit ();
    }
    return fastRecords;
  }

  protected abstract void fastSplit ();
}
