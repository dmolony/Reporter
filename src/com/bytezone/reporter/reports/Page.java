package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.record.Record;

public class Page
{
  final List<Record> records;
  final int firstRecordIndex;
  final int lastRecordIndex;

  int firstRecordOffset;
  int lastRecordOffset;

  public Page (List<Record> records, int first, int last)
  {
    this.records = records;
    firstRecordIndex = first;
    lastRecordIndex = last;
  }

  public void setFirstRecordOffset (int offset)
  {
    firstRecordOffset = offset;
  }

  public void setLastRecordOffset (int offset)
  {
    lastRecordOffset = offset;
  }

  @Override
  public String toString ()
  {
    return String.format ("Records: %4d, first: %4d (%4d), last: %4d (%4d)",
                          records.size (), firstRecordIndex, firstRecordOffset,
                          lastRecordIndex, lastRecordOffset);
  }
}