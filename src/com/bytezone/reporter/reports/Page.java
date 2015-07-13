package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.record.Record;

public class Page
{
  final List<Record> records;
  final int firstRecordIndex;
  final int firstRecordOffset;
  final int lastRecordIndex;
  final int lastRecordOffset;

  public Page (List<Record> records, int first, int last)
  {
    this.records = records;
    firstRecordIndex = first;
    lastRecordIndex = last;

    lastRecordOffset = 0;
    firstRecordOffset = 0;
  }
}