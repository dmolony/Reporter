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
    int start = 0;

    while (ptr < buffer.length)
    {
      byte firstByte = buffer[ptr++];
      if (firstByte == (byte) 0xFF)
      {
        byte nextByte = buffer[ptr++];
        if (nextByte == 0x02)                            // EOF
          break;
        if (nextByte == 0x01)                            // EOR
        {
          // if there are no 0xFF bytes in the record, just use the original buffer
          // otherwise copy to a new buffer
          int len = ptr - start - 2;
          if (len > tempPtr)
          {
            byte[] record = new byte[tempPtr];
            System.arraycopy (temp, 0, record, 0, tempPtr);
            records.add (new Record (record, 0, tempPtr, 0, tempPtr));
          }
          else
            records.add (new Record (buffer, start, tempPtr, start, tempPtr));

          tempPtr = 0;
          start = ptr;
          continue;
        }
        assert nextByte == (byte) 0xFF;
      }
      if (tempPtr < temp.length)
        temp[tempPtr++] = firstByte;
    }
  }
}