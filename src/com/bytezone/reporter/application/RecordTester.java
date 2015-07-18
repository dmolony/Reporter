package com.bytezone.reporter.application;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.text.TextMaker;

public class RecordTester
{
  final RecordMaker recordMaker;
  final byte[] buffer;
  final int testSize;
  final List<Record> records;

  final List<TextTester> textTesters = new ArrayList<> ();
  final List<ReportTester> reportTesters = new ArrayList<> ();

  public RecordTester (String name, RecordMaker recordMaker, byte[] buffer, int testSize)
  {
    this.recordMaker = recordMaker;
    this.buffer = buffer;
    this.testSize = testSize;

    records = recordMaker.test (buffer, 0, testSize);
  }

  public void testTextMaker (TextMaker textMaker)
  {
    TextTester textTester = new TextTester (textMaker);
    textTesters.add (textTester);

    for (Record record : records)
      textTester.testRecord (record);
  }

  public void testReportMaker (ReportMaker reportMaker, TextMaker textMaker)
  {
    ReportTester reportTester = new ReportTester (reportMaker, textMaker);
    reportTesters.add (reportTester);

    for (Record record : records)
      reportTester.testRecord (record);
  }

  public TextMaker getPreferredTextMaker ()
  {
    double max = Double.MIN_VALUE;
    TextMaker preferredTextMaker = null;
    for (TextTester textTester : textTesters)
    {
      if (textTester.getAlphanumericRatio () > max)
      {
        max = textTester.getAlphanumericRatio ();
        preferredTextMaker = textTester.textMaker;
      }
    }
    return preferredTextMaker;
  }

  @Override
  public String toString ()
  {
    StringBuilder text = new StringBuilder ();
    text.append (String.format ("%-8s %,5d", recordMaker, records.size ()));

    for (TextTester textTester : textTesters)
      text.append (String.format ("  %s", textTester));

    TextMaker preferredTextMaker = getPreferredTextMaker ();
    String textMaker = preferredTextMaker == null ? "" : preferredTextMaker.toString ();
    text.append ("  " + textMaker);

    for (ReportTester reportTester : reportTesters)
      text.append (String.format (" %s %3d", reportTester.reportMaker,
                                  reportTester.validRecords));

    return text.toString ();
  }
}