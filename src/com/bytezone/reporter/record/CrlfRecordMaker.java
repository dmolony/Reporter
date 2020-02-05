package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

// -----------------------------------------------------------------------------------//
public class CrlfRecordMaker extends DefaultRecordMaker
// -----------------------------------------------------------------------------------//
{
  private final boolean trimNumbers;
  private final boolean trimSpaces;

  // ---------------------------------------------------------------------------------//
  public CrlfRecordMaker ()
  // ---------------------------------------------------------------------------------//
  {
    super ("CR/LF");
    trimNumbers = false;
    trimSpaces = false;
    weight = 0.95;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected List<Record> split (int length)
  // ---------------------------------------------------------------------------------//
  {
    List<Record> records = new ArrayList<Record> ();
    int start = 0;
    int recordNumber = 0;
    int trimSize = 0;

    int max = Math.min (length, buffer.length);
    for (int ptr = 0; ptr < max; ptr++)
    {
      if (buffer[ptr] == 0x0A && ptr > 0 && buffer[ptr - 1] == 0x0D)
      {
        if (trimNumbers)
        {
          boolean hasNumbers = hasNumbers (buffer, ptr - 9, 8, 0x30, 0x39);
          trimSize = hasNumbers ? 8 : 0;
          if (trimSpaces)
          {
            int len = ptr - start - trimSize - 1;
            trimSize += countTrailingSpaces (buffer, start, len, (byte) 0x20);
          }
        }
        records
            .add (new Record (buffer, start, ptr - start - trimSize - 1, recordNumber++));
        start = ptr + 1;
      }
    }

    if (start < max)
    {
      // ignore 0x1A on the end - added by IND$FILE
      if (start != max - 1 || buffer[max - 1] != 0x1A)
        records.add (new Record (buffer, start, max - start, recordNumber++));
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
      bufferLength += record.length + 2;

    byte[] buffer = new byte[bufferLength];

    int ptr = 0;
    for (Record record : records)
    {
      System.arraycopy (record.buffer, record.offset, buffer, ptr, record.length);
      ptr += record.length;
      buffer[ptr++] = 0x0D;
      buffer[ptr++] = 0x0A;
    }

    assert ptr == buffer.length;

    return buffer;
  }
}