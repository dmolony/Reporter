package com.bytezone.reporter.record;

public class CrRecordMaker extends DefaultRecordMaker
{
  public CrRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  @Override
  protected void split ()
  {
    int start = 0;
    for (int ptr = 0; ptr < buffer.length; ptr++)
    {
      if (buffer[ptr] == 0x0D)
      {
        records.add (new Record (buffer, start, ptr - start, start, ptr - start + 1));
        start = ptr + 1;
      }
    }
  }
}