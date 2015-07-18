package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class CrRecordMaker extends DefaultRecordMaker
{
  public CrRecordMaker ()
  {
    super ("CR");
  }

  @Override
  protected List<Record> split (byte[] buffer, int offset, int length)
  {
    List<Record> records = new ArrayList<Record> ();
    int start = offset;
    int recordNumber = 0;

    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr++)
    {
      if (buffer[ptr] == 0x0D)
      {
        records.add (new Record (buffer, start, ptr - start, recordNumber));
        start = ptr + 1;
      }
    }

    if (start < max)
      records.add (new Record (buffer, start, max - start, recordNumber++));

    return records;
  }

  @Override
  protected byte[] join (List<Record> records)
  {
    int bufferLength = 0;

    for (Record record : records)
      bufferLength += record.length + 1;

    byte[] buffer = new byte[bufferLength];

    int ptr = 0;
    for (Record record : records)
    {
      System.arraycopy (record.buffer, record.offset, buffer, ptr, record.length);
      ptr += record.length;
      buffer[ptr++] = 0x0D;
    }

    return buffer;
  }
}