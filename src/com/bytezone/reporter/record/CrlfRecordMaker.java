package com.bytezone.reporter.record;

public class CrlfRecordMaker extends DefaultRecordMaker
{
  public CrlfRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  @Override
  protected void split ()
  {
    System.out.println ("slow split");
    int lastPtr = 0;
    for (int ptr = 0, max = buffer.length - 1; ptr < max; ptr++)
    {
      if (buffer[ptr] == 0x0D && buffer[ptr + 1] == 0x0A)
      {
        addRecord (lastPtr, ptr - lastPtr);
        ++ptr;
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

  @Override
  protected void fastSplit ()
  {
    System.out.println ("fast split");
    int start = 0;
    for (int ptr = 0; ptr < buffer.length; ptr++)
    {
      if (buffer[ptr] == 0x0A)
      {
        if (ptr > start && buffer[ptr - 1] == 0x0D)
        {
          fastRecords
              .add (new Record (buffer, start, ptr - start - 1, start, ptr - start + 1));
          start = ptr + 1;
        }
      }
    }
    // if (start < buffer.length)
    // fastRecords.add (new Record (buffer, start, buffer.length - start, start,
    // buffer.length - start));
  }
}