package com.bytezone.reporter.record;

public class LfRecordMaker extends DefaultRecordMaker
{
  public LfRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  @Override
  protected void fastSplit ()
  {
    int start = 0;
    for (int ptr = 0; ptr < buffer.length; ptr++)
    {
      if (buffer[ptr] == 0x0A)
      {
        fastRecords.add (new Record (buffer, start, ptr - start, start, ptr - start + 1));
        start = ptr + 1;
      }
    }
  }
}