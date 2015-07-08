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

  public int countTrailingNulls ()
  {
    int nulls = 0;
    int ptr = offset + length - 1;
    while (ptr >= offset)
    {
      if (buffer[ptr--] == 0)
        nulls++;
      else
        break;
    }
    return nulls;
  }
}