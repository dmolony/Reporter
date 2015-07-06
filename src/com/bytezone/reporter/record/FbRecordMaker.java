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
  protected void split ()
  {
    for (int ptr = 0; ptr < buffer.length; ptr += recordLength)
      addRecord (ptr, Math.min (recordLength, buffer.length - ptr));
  }

  private void addRecord (int ptr, int reclen)
  {
    byte[] record = new byte[reclen];
    System.arraycopy (buffer, ptr, record, 0, reclen);
    records.add (record);
  }
}