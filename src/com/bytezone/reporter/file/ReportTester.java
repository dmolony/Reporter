package com.bytezone.reporter.file;

import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.text.TextMaker;

// -----------------------------------------------------------------------------------//
public class ReportTester
// -----------------------------------------------------------------------------------//
{
  final ReportMaker reportMaker;
  int validRecords;
  int recordsTested;
  final TextMaker textMaker;

  // ---------------------------------------------------------------------------------//
  public ReportTester (ReportMaker reportMaker, TextMaker textMaker)
  // ---------------------------------------------------------------------------------//
  {
    this.reportMaker = reportMaker;
    this.textMaker = textMaker;
  }

  // ---------------------------------------------------------------------------------//
  public void testRecords (List<Record> records)
  // ---------------------------------------------------------------------------------//
  {
    for (Record record : records)
    {
      if (reportMaker.test (record, textMaker))
        ++validRecords;
      ++recordsTested;
    }
  }

  // ---------------------------------------------------------------------------------//
  public double getRatio ()
  // ---------------------------------------------------------------------------------//
  {
    return (double) validRecords / recordsTested * 100;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%-6.6s %6.2f", reportMaker, getRatio ());
  }
}