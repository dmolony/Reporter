package com.bytezone.reporter.record;

import java.util.ArrayList;
import java.util.List;

public class RavelRecordMaker implements RecordMaker
{
  private final byte[] buffer;
  private List<byte[]> records;
  byte[] temp = new byte[1024];

  public RavelRecordMaker (byte[] buffer)
  {
    this.buffer = buffer;
  }

  @Override
  public List<byte[]> getRecords ()
  {
    if (records == null)
    {
      records = new ArrayList<> ();
      split ();
    }
    return records;
  }

  private void split ()
  {
    int ptr = 0;
    int tempPtr = 0;

    while (ptr < buffer.length)
    {
      byte firstByte = buffer[ptr++];
      if (firstByte == (byte) 0xFF)
      {
        byte nextByte = buffer[ptr++];
        if (nextByte == 0x02)                    // EOF
          break;
        if (nextByte == 0x01)                    // EOR
        {
          addRecord (tempPtr);
          tempPtr = 0;
          continue;
        }
        assert nextByte == (byte) 0xFF;
      }
      temp[tempPtr++] = firstByte;
    }
    if (tempPtr > 0)
    {
      System.out.println ("Unfinished ravel record");
      addRecord (tempPtr);
    }
  }

  private void addRecord (int ptr)
  {
    byte[] record = new byte[ptr];
    System.arraycopy (temp, 0, record, 0, ptr);
    records.add (record);
  }
}