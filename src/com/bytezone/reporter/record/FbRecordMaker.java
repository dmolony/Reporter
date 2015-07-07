package com.bytezone.reporter.record;

public class FbRecordMaker extends DefaultRecordMaker
{
  private final int recordLength;

  public FbRecordMaker (byte[] buffer, int recordLength)
  {
    super (buffer);
    this.recordLength = recordLength;
  }

  @Override
  protected void fastSplit ()
  {
    for (int ptr = 0; ptr < buffer.length; ptr += recordLength)
    {
      int reclen = Math.min (recordLength, buffer.length - ptr);
      if (reclen == recordLength)
        fastRecords.add (new Record (buffer, ptr, reclen, ptr, reclen));
    }
  }
}