package com.bytezone.reporter.record;

import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

public class NvbRecordMaker extends DefaultRecordMaker
{
  private static final int HEADER_SIZE = 24;
  private static final int SOURCE_SIZE = 94;
  private static final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();

  public NvbRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  @Override
  protected void split ()
  {
    int linesLeft = 0;
    int ptr = 0;

    while (ptr < buffer.length)
    {
      int reclen = linesLeft == 0 ? HEADER_SIZE : SOURCE_SIZE;
      if (buffer.length - ptr < reclen)
      {
        System.out.println ("short buffer");
        break;
      }

      if (linesLeft > 0)
        --linesLeft;
      else if (buffer[ptr] != 0 && buffer[ptr] != (byte) 0xFF)
        linesLeft = Integer.parseInt (ebcdicTextMaker.getText (buffer, ptr + 18, 3));

      records.add (new Record (buffer, ptr, reclen, ptr, reclen));
      ptr += reclen;
    }
  }
}