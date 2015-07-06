package com.bytezone.reporter.record;

public class Record
{
  public final byte[] buffer;
  public final int offset;
  public final int length;
  public final int actualOffset;
  public final int actualLength;

  public Record (byte[] buffer, int offset, int length, int actualOffset,
      int actualLength)
  {
    this.buffer = buffer;
    this.offset = offset;
    this.length = length;
    this.actualOffset = actualOffset;
    this.actualLength = actualLength;
  }
}