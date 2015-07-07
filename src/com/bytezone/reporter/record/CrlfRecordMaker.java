package com.bytezone.reporter.record;

public class CrlfRecordMaker extends DefaultRecordMaker
{
  public CrlfRecordMaker (byte[] buffer)
  {
    super (buffer);
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
  }
}