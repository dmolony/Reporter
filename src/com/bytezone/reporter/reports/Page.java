package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.record.Record;

// -----------------------------------------------------------------------------------//
public class Page
// -----------------------------------------------------------------------------------//
{
  final List<Record> records;
  final int firstRecordIndex;
  final int lastRecordIndex;

  int firstRecordOffset;
  int lastRecordOffset;

  // ---------------------------------------------------------------------------------//
  public Page (List<Record> records, int first, int last)
  // ---------------------------------------------------------------------------------//
  {
    this.records = records;
    firstRecordIndex = first;
    lastRecordIndex = last;
  }

  // ---------------------------------------------------------------------------------//
  public int getFirstRecordIndex ()
  // ---------------------------------------------------------------------------------//
  {
    return firstRecordIndex;
  }

  // ---------------------------------------------------------------------------------//
  public int getLastRecordIndex ()
  // ---------------------------------------------------------------------------------//
  {
    return lastRecordIndex;
  }

  // ---------------------------------------------------------------------------------//
  public void setFirstRecordOffset (int offset)
  // ---------------------------------------------------------------------------------//
  {
    firstRecordOffset = offset;
  }

  // ---------------------------------------------------------------------------------//
  public int getFirstRecordOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return firstRecordOffset;
  }

  // ---------------------------------------------------------------------------------//
  public void setLastRecordOffset (int offset)
  // ---------------------------------------------------------------------------------//
  {
    lastRecordOffset = offset;
  }

  // ---------------------------------------------------------------------------------//
  public int getLastRecordOffset ()
  // ---------------------------------------------------------------------------------//
  {
    return lastRecordOffset;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("Records: %4d, first: %4d (%5d), last: %4d (%5d)",
        records.size (), firstRecordIndex, firstRecordOffset, lastRecordIndex,
        lastRecordOffset);
  }
}