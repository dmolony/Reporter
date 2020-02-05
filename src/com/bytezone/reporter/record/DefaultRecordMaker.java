package com.bytezone.reporter.record;

import java.util.List;

// -----------------------------------------------------------------------------------//
public abstract class DefaultRecordMaker implements RecordMaker
// -----------------------------------------------------------------------------------//
{
  protected byte[] buffer;
  protected List<Record> records;
  protected final String name;
  protected double weight = 1.0;

  // ---------------------------------------------------------------------------------//
  public DefaultRecordMaker (String name)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void setBuffer (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this.buffer = buffer;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void setRecords (List<Record> records)
  // ---------------------------------------------------------------------------------//
  {
    this.records = records;
    System.out.printf ("given %d records%n", records.size ());
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public List<Record> createSampleRecords (int length)
  // ---------------------------------------------------------------------------------//
  {
    if (length >= buffer.length)
      return getRecords ();

    return split (length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public List<Record> getRecords ()
  // ---------------------------------------------------------------------------------//
  {
    if (records == null)
      records = split (buffer.length);          // use the entire buffer

    return records;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null)
      buffer = join (records);

    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public double weight ()
  // ---------------------------------------------------------------------------------//
  {
    return weight;
  }

  // ---------------------------------------------------------------------------------//
  protected abstract List<Record> split (int length);
  // ---------------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------------//
  protected abstract byte[] join (List<Record> records);
  // ---------------------------------------------------------------------------------//

  // ---------------------------------------------------------------------------------//
  protected boolean hasNumbers (byte[] buffer, int offset, int length, int min, int max)
  // ---------------------------------------------------------------------------------//
  {
    for (int i = offset; length > 0; i++, length--)
    {
      int value = buffer[i] & 0xFF;
      if (value < min || value > max)
        return false;
    }
    return true;
  }

  // ---------------------------------------------------------------------------------//
  protected int countTrailingSpaces (byte[] buffer, int offset, int length, byte space)
  // ---------------------------------------------------------------------------------//
  {
    int ptr = offset + length - 1;
    int spaces = 0;
    while (ptr >= offset && buffer[ptr--] == space)
      ++spaces;

    return spaces;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return name;
  }
}