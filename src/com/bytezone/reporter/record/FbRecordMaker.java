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
    int lastPtr = 0;
    for (int ptr = 0; ptr < buffer.length; ptr += recordLength)
    {
      int reclen = Math.min (recordLength, buffer.length - ptr);
      addRecord (ptr, reclen);
      lastPtr = ptr;
    }
    if (lastPtr < buffer.length)
      addRecord (lastPtr, buffer.length - lastPtr);
  }

  private void addRecord (int ptr, int reclen)
  {
    byte[] record = new byte[reclen];
    System.arraycopy (buffer, ptr, record, 0, reclen);
    records.add (record);
  }
}