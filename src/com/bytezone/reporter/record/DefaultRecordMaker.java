package com.bytezone.reporter.record;

import java.util.List;

public abstract class DefaultRecordMaker implements RecordMaker
{
  protected byte[] buffer;
  protected List<Record> records;
  protected final String name;

  public DefaultRecordMaker (String name)
  {
    this.name = name;
  }

  @Override
  public void setBuffer (byte[] buffer)
  {
    this.buffer = buffer;
  }

  @Override
  public void setRecords (List<Record> records)
  {
    this.records = records;
  }

  @Override
  public List<Record> test (int length)
  {
    if (length >= buffer.length)
      return getRecords ();

    return split (length);
  }

  @Override
  public List<Record> getRecords ()
  {
    if (records == null)
      records = split (buffer.length);

    return records;
  }

  @Override
  public byte[] getBuffer ()
  {
    if (buffer == null)
      buffer = join (records);

    return buffer;
  }

  protected abstract List<Record> split (int length);

  protected abstract byte[] join (List<Record> records);

  @Override
  public String toString ()
  {
    return name;
  }
}