package com.bytezone.reporter.application;

import java.util.ArrayList;
import java.util.List;

public class CrlfRecordMaker implements RecordMaker
{
  private final byte[] buffer;
  private List<byte[]> records;

  public CrlfRecordMaker (byte[] buffer)
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
    int lastPtr = 0;
    for (int ptr = 0, max = buffer.length - 1; ptr < max; ptr++)
    {
      if (buffer[ptr] == 0x0D && buffer[ptr + 1] == 0x0A)
      {
        addRecord (lastPtr, ptr - lastPtr);
        ++ptr;
        lastPtr = ++ptr;
      }
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