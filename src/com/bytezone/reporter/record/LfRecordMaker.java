package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class LfRecordMaker extends DefaultRecordMaker
{
  public LfRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  public LfRecordMaker (List<Record> records)
  {
    super (records);
  }

  @Override
  protected List<Record> split ()
  {
    List<Record> records = new ArrayList<Record> ();
    int start = 0;
    int recordNumber = 0;
    for (int ptr = 0; ptr < buffer.length; ptr++)
    {
      if (buffer[ptr] == 0x0A)
      {
        records.add (new Record (buffer, start, ptr - start, recordNumber++));
        start = ptr + 1;
      }
    }
    return records;
  }

  @Override
  protected byte[] join ()
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
      buffer[ptr++] = 0x0A;
    }

    return buffer;
  }
}