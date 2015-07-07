package com.bytezone.reporter.record;

public class RdwRecordMaker extends DefaultRecordMaker
{
  public RdwRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  @Override
  protected void fastSplit ()
  {
    int ptr = 0;
    while (ptr < buffer.length)
    {
      int start = ptr;
      int reclen = (buffer[ptr++] & 0xFF) << 8;
      reclen |= buffer[ptr++] & 0xFF;

      int filler = (buffer[ptr++] & 0xFF) << 8;
      filler |= buffer[ptr++] & 0xFF;
      if (filler != 0)
        System.out.println ("Non zero");

      int reclen2 = Math.min (reclen - 4, buffer.length - ptr);

      Record record = new Record (buffer, ptr, reclen2, start, reclen);
      ptr += reclen2;

      fastRecords.add (record);
    }
  }
}