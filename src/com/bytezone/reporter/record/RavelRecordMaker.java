package com.bytezone.reporter.record;

import java.util.List;

public class RavelRecordMaker extends DefaultRecordMaker
{
  byte[] temp = new byte[2048];

  public RavelRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  public RavelRecordMaker (List<Record> records)
  {
    super (records);
  }

  @Override
  protected void split ()
  {
    int ptr = 0;
    int tempPtr = 0;
    int start = 0;
    int recordNumber = 0;

    while (ptr < buffer.length)
    {
      byte firstByte = buffer[ptr++];
      if (firstByte == (byte) 0xFF)
      {
        byte nextByte = buffer[ptr++];
        if (nextByte == 0x02) // EOF
          break;
        if (nextByte == 0x01) // EOR
        {
          // if there are no 0xFF bytes in the record, just use the original buffer
          // otherwise copy to a new buffer
          int len = ptr - start - 2;
          if (len > tempPtr)
          {
            byte[] record = new byte[tempPtr];
            System.arraycopy (temp, 0, record, 0, tempPtr);
            records.add (new Record (record, 0, tempPtr, recordNumber++));
          }
          else
            records.add (new Record (buffer, start, tempPtr, recordNumber++));

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

  @Override
  protected byte[] join ()
  {
    int bufferLength = 0;
    int[] ravelLengths = new int[records.size ()];

    int count = 0;
    for (Record record : records)
    {
      int length = getRavelLength (record);
      ravelLengths[count++] = length;
      bufferLength += length + 2;
    }

    byte[] buffer = new byte[bufferLength + 2];
    int ptr = 0;
    for (int i = 0; i < records.size (); i++)
    {
      Record record = records.get (i);
      int ravelLength = ravelLengths[i];
      if (ravelLength == record.length)
      {
        System.arraycopy (record.buffer, record.offset, buffer, ptr, ravelLength);
        ptr += ravelLength;
      }
      else
        ptr = packRecord (record, ptr);
      buffer[ptr++] = (byte) 0xFF;
      buffer[ptr++] = (byte) 0x01;
    }
    buffer[ptr++] = (byte) 0xFF;
    buffer[ptr++] = (byte) 0x02;

    assert ptr == buffer.length;

    return buffer;
  }

  private int getRavelLength (Record record)
  {
    int length = record.length;
    for (int i = record.offset, max = record.offset + record.length; i < max; i++)
    {
      if (record.buffer[i] == (byte) 0xFF)
        length++;
    }
    return length;
  }

  private int packRecord (Record record, int ptr)
  {
    for (int i = record.offset, max = record.offset + record.length; i < max; i++)
    {
      if (record.buffer[i] == (byte) 0xFF)
        buffer[ptr++] = (byte) 0xFF;
      buffer[ptr++] = record.buffer[i];
    }
    return ptr;
  }
}