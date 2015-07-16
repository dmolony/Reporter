package com.bytezone.reporter.record;

import java.util.List;

public abstract class DefaultRecordMaker implements RecordMaker
{
  protected byte[] buffer;
  protected List<Record> records;

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
  public List<Record> test (byte[] buffer)
  {
    return test (buffer, 0, buffer.length);
  }

  @Override
  public List<Record> test (byte[] buffer, int offset, int length)
  {
    //    if (offset == 0 && buffer.length <= length)
    //    {
    //      records = split ();
    //      return records;
    //    }
    //    else
    //    {
    //    byte[] saveBuffer = buffer;
    //    buffer = new byte[testBufferSize];
    //    System.arraycopy (saveBuffer, 0, buffer, 0, buffer.length);
    List<Record> tempRecords = split (buffer, offset, length);
    //    buffer = saveBuffer;
    return tempRecords;
    //    }
  }

  @Override
  public List<Record> getRecords ()
  {
    if (records == null)
      records = split (buffer, 0, buffer.length);

    return records;
  }

  @Override
  public byte[] getBuffer ()
  {
    if (buffer == null)
      buffer = join (records);

    return buffer;
  }

  protected abstract List<Record> split (byte[] buffer, int offset, int length);

  protected abstract byte[] join (List<Record> records);
}
