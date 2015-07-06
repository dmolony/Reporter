package com.bytezone.reporter.record;

public class RavelRecordMaker extends DefaultRecordMaker
{
  byte[] temp = new byte[2048];

  public RavelRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  @Override
  protected void split ()
  {
    int ptr = 0;
    int tempPtr = 0;

    while (ptr < buffer.length)
    {
      byte firstByte = buffer[ptr++];
      if (firstByte == (byte) 0xFF)
      {
        byte nextByte = buffer[ptr++];
        if (nextByte == 0x02)                           // EOF
          break;
        if (nextByte == 0x01)                           // EOR
        {
          addRecord (tempPtr);
          tempPtr = 0;
          continue;
        }
        assert nextByte == (byte) 0xFF;
      }
      if (tempPtr < temp.length)
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