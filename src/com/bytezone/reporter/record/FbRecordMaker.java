package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class FbRecordMaker implements RecordMaker
{
  private final int recordLength;
  private final byte[] buffer;
  private List<byte[]> records;

  public FbRecordMaker (byte[] buffer, int recordLength)
  {
    this.recordLength = recordLength;
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
    for (int ptr = 0; ptr < buffer.length; ptr += recordLength)
      addRecord (ptr, Math.min (recordLength, buffer.length - ptr));
  }

  private void addRecord (int ptr, int reclen)
  {
    byte[] record = new byte[reclen];
    System.arraycopy (buffer, ptr, record, 0, reclen);
    records.add (record);
  }
}