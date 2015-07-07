package com.bytezone.reporter.record;

public class Record
{
  public final byte[] buffer;
  public final int recordNumber;
  public final int offset;
  public final int length;

  public Record (byte[] buffer, int offset, int length, int recordNumber)
  {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    this.recordNumber = recordNumber;
  }
}