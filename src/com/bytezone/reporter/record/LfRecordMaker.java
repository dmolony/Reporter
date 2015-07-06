package com.bytezone.reporter.record;

public class LfRecordMaker extends DefaultRecordMaker
{
  public LfRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  @Override
  protected void split ()
  {
    int lastPtr = 0;
    for (int ptr = 0, max = buffer.length; ptr < max; ptr++)
    {
      if (buffer[ptr] == 0x0A)
      {
        addRecord (lastPtr, ptr - lastPtr);
        lastPtr = ++ptr;
      }
    }
    if (lastPtr < buffer.length)
      addRecord (lastPtr, buffer.length - lastPtr);
  }

  private void addRecord (int ptr, int reclen)
  {
    byte[] record = new byte[reclen];
    System.arraycopy (buffer, ptr, record, 0, reclen);
    records.add (record);
  }
}