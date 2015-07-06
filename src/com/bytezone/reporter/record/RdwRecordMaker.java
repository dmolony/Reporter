package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class RdwRecordMaker implements RecordMaker
{
  private final byte[] buffer;
  private List<byte[]> records;

  public RdwRecordMaker (byte[] buffer)
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
    int ptr = 0;
    while (ptr < buffer.length)
    {
      int filler = (buffer[ptr++] & 0xFF) << 8;
      filler |= buffer[ptr++] & 0xFF;
      if (filler != 0)
        System.out.println ("Non zero");

      int reclen = (buffer[ptr++] & 0xFF) << 8;
      reclen |= buffer[ptr++] & 0xFF;

      reclen = Math.min (reclen - 4, buffer.length - ptr);

      byte[] record = new byte[reclen];
      System.arraycopy (buffer, ptr, record, 0, reclen);
      ptr += reclen;

      records.add (record);
    }
  }
}