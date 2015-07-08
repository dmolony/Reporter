package com.bytezone.reporter.record;

import java.util.List;

public abstract class DefaultRecordMaker implements RecordMaker
{
  protected byte[] buffer;
  protected List<Record> records;

  public DefaultRecordMaker (byte[] buffer)
  {
    this.buffer = buffer;
  }

  public DefaultRecordMaker (List<Record> records)
  {
    this.records = records;
  }

  @Override
  public List<Record> getRecords ()
  {
    if (records == null)
      records = split ();

    return records;
  }

  @Override
  public byte[] getBuffer ()
  {
    if (buffer == null)
      buffer = join ();

    return buffer;
  }

  protected abstract List<Record> split ();

  protected abstract byte[] join ();
}
