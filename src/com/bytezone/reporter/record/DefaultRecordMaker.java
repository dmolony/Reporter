package com.bytezone.reporter.record;

import java.util.List;

public abstract class DefaultRecordMaker implements RecordMaker
{
  //  private static final int TEST_BUFFER_SIZE = 1024;
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
  public int test (int testBufferSize)
  {
    assert records == null;

    if (buffer.length <= testBufferSize)
    {
      records = split ();
      return records.size ();
    }
    else
    {
      byte[] tempBuffer = buffer;
      buffer = new byte[testBufferSize];
      System.arraycopy (tempBuffer, 0, buffer, 0, buffer.length);
      List<Record> tempRecords = split ();
      buffer = tempBuffer;
      return tempRecords.size ();
    }
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
