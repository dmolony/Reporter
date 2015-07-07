package com.bytezone.reporter.record;

import java.util.List;

public class FbRecordMaker extends DefaultRecordMaker
{
  private final int recordLength;

  public FbRecordMaker (byte[] buffer, int recordLength)
  {
    super (buffer);
    this.recordLength = recordLength;
  }

  public FbRecordMaker (List<Record> records, int recordLength)
  {
    super (records);
    this.recordLength = recordLength;
  }

  @Override
  protected void split ()
  {
    int recordNumber = 0;
    for (int ptr = 0; ptr < buffer.length; ptr += recordLength)
    {
      int reclen = Math.min (recordLength, buffer.length - ptr);
      if (reclen == recordLength)
        records.add (new Record (buffer, ptr, reclen, recordNumber++));
    }
  }

  @Override
  protected byte[] join ()
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