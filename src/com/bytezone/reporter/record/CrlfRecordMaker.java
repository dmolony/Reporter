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
    int start = 0;
    for (int ptr = 0; ptr < buffer.length; ptr++)
    {
      if (buffer[ptr] == 0x0A && ptr > start && buffer[ptr - 1] == 0x0D)
      {
        int len1 = ptr - start - 1;
        int len2 = ptr - start + 1;
        records.add (new Record (buffer, start, len1, start, len2));
        start = ptr + 1;
      }
    }
  }
}