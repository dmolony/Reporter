package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

// -----------------------------------------------------------------------------------//
public class NvbRecordMaker extends DefaultRecordMaker
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public NvbRecordMaker ()
  // ---------------------------------------------------------------------------------//
  {
    super ("NVB");
  }

  private static final int HEADER_SIZE = 24;
  private static final int SOURCE_SIZE = 94;
  private static final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();

  // ---------------------------------------------------------------------------------//
  @Override
  protected List<Record> split (int length)
  // ---------------------------------------------------------------------------------//
  {
    List<Record> records = new ArrayList<> ();
    if (buffer.length < 16 || buffer[0] != (byte) 0xFF || buffer[7] != (byte) 0xFF
        || buffer[14] != (byte) 0xFF)
      return records;

    int linesLeft = 0;
    int ptr = 0;
    int recordNumber = 0;

    int max = Math.min (length, buffer.length);
    while (ptr < max)
    {
      int reclen = linesLeft == 0 ? HEADER_SIZE : SOURCE_SIZE;
      if (buffer.length - ptr < reclen)
        break;

      if (linesLeft > 0)
        --linesLeft;
      else if (buffer[ptr] != 0 && buffer[ptr] != (byte) 0xFF)
        try
        {
          linesLeft = Integer.parseInt (ebcdicTextMaker.getText (buffer, ptr + 18, 3));
        }
        catch (NumberFormatException e)
        {
          records.clear ();
          break;
        }

      records.add (new Record (buffer, ptr, reclen, recordNumber++));
      ptr += reclen;
    }
    return records;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected byte[] join (List<Record> records)
  // ---------------------------------------------------------------------------------//
  {
    int bufferLength = 0;
    for (Record record : records)
      bufferLength += record.length;

    byte[] buffer = new byte[bufferLength];

    int ptr = 0;
    for (Record record : records)
    {
      System.arraycopy (record.buffer, record.offset, buffer, ptr, record.length);
      ptr += record.length;
    }

    return buffer;
  }
}