package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class FbRecordMaker extends DefaultRecordMaker
{
  private final int recordLength;

  public FbRecordMaker (int recordLength)
  {
    super ("FB" + recordLength);
    this.recordLength = recordLength;
  }

  public int getRecordLength ()
  {
    return recordLength;
  }

  @Override
  protected List<Record> split (int length)
  {
    List<Record> records = new ArrayList<Record> ();
    int recordNumber = 0;

    int max = Math.min (length, buffer.length);
    for (int ptr = 0; ptr < max; ptr += recordLength)
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