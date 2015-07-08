package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class VbRecordMaker extends DefaultRecordMaker
{
  public VbRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  public VbRecordMaker (List<Record> records)
  {
    super (records);
  }

  @Override
  protected List<Record> split ()
  {
    List<Record> records = new ArrayList<Record> ();
    int ptr = 0;
    int recordNumber = 0;
    while (ptr < buffer.length)
    {
      int filler = (buffer[ptr++] & 0xFF) << 8;
      filler |= buffer[ptr++] & 0xFF;
      if (filler != 0)
        //        System.out.println ("Non zero");
        break;

      int reclen = (buffer[ptr++] & 0xFF) << 8;
      reclen |= buffer[ptr++] & 0xFF;
      int reclen2 = Math.min (reclen - 4, buffer.length - ptr);

      Record record = new Record (buffer, ptr, reclen2, recordNumber++);
      ptr += reclen2;

      records.add (record);
    }
    return records;
  }

  @Override
  protected byte[] join ()
  {
    int bufferLength = 0;
    for (Record record : records)
      bufferLength += record.length + 4;

    byte[] buffer = new byte[bufferLength];
    int ptr = 0;
    for (Record record : records)
    {
      buffer[ptr++] = (byte) 0;
      buffer[ptr++] = (byte) 0;
      buffer[ptr++] = (byte) ((record.length & 0xFF00) >> 8);
      buffer[ptr++] = (byte) (record.length & 0xFF);
      System.arraycopy (record.buffer, record.offset, buffer, ptr, record.length);
    }

    return buffer;
  }
}