package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class FbRecordMaker extends DefaultRecordMaker
{
  private final int recordLength;

  public FbRecordMaker (int recordLength)
  {
    this.recordLength = recordLength;
  }

  @Override
  protected List<Record> split (byte[] buffer, int offset, int length)
  {
    List<Record> records = new ArrayList<Record> ();
    int recordNumber = 0;

    int max = Math.min (offset + length, buffer.length);
    for (int ptr = offset; ptr < max; ptr += recordLength)
    {
      int reclen = Math.min (recordLength, buffer.length - ptr);
      if (reclen == recordLength)
      {
        // trim trailing nulls
        int ptr2 = ptr + reclen - 1;
        while (reclen > 0)
        {
          if (buffer[ptr2--] != 0)
            break;
          --reclen;
        }

        records.add (new Record (buffer, ptr, reclen, recordNumber++));
      }
    }
    return records;
  }

  @Override
  protected byte[] join (List<Record> records)
  {
    byte[] buffer = new byte[records.size () * recordLength];
    int ptr = 0;

    for (Record record : records)
    {
      System.arraycopy (record.buffer, record.offset, buffer, ptr, recordLength);
      ptr += recordLength;
    }

    return buffer;
  }
}