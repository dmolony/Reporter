package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

// -----------------------------------------------------------------------------------//
public class FbRecordMaker extends DefaultRecordMaker
// -----------------------------------------------------------------------------------//
{
  private final int recordLength;
  private final boolean trimNulls;

  // ---------------------------------------------------------------------------------//
  public FbRecordMaker (int recordLength)
  // ---------------------------------------------------------------------------------//
  {
    super ("FB" + recordLength);
    this.recordLength = recordLength;
    trimNulls = recordLength != 63;

    if (recordLength == 80)
      weight = 0.91;
    else
      weight = 0.9;
  }

  // ---------------------------------------------------------------------------------//
  public int getRecordLength ()
  // ---------------------------------------------------------------------------------//
  {
    return recordLength;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected List<Record> split (int length)
  // ---------------------------------------------------------------------------------//
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
        if (trimNulls)
        {
          int ptr2 = ptr + reclen - 1;
          while (reclen >= 0 && buffer[ptr2--] == 0)
            --reclen;
        }

        records.add (new Record (buffer, ptr, reclen, recordNumber++));
      }
    }
    return records;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected byte[] join (List<Record> records)
  // ---------------------------------------------------------------------------------//
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