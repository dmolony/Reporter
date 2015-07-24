package com.bytezone.reporter.tests;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.text.TextMaker;

public class RecordTester
{
  private final RecordMaker recordMaker;
  private final List<Record> records;

  private final List<TextTester> textTesters = new ArrayList<> ();
  private final List<ReportTester> reportTesters = new ArrayList<> ();

  public RecordTester (RecordMaker recordMaker, int testSize)
  {
    this.recordMaker = recordMaker;
    records = recordMaker.test (testSize);
  }

  public int getTotalRecords ()
  {
    return records.size ();
  }

  public void testTextMaker (TextMaker textMaker)
  {
    TextTester textTester = new TextTester (textMaker);
    textTesters.add (textTester);

    textTester.testRecords (records);
  }

  public ReportScore testReportMaker (ReportMaker reportMaker, TextMaker textMaker)
  {
    ReportTester reportTester = new ReportTester (reportMaker, textMaker);
    reportTesters.add (reportTester);

    reportTester.testRecords (records);

    return new ReportScore (recordMaker, textMaker, reportMaker, reportTester.getRatio (),
        records.size ());
  }

  public TextMaker getPreferredTextMaker ()
  {
    double max = Double.MIN_VALUE;
    TextTester bestTextTester = null;

    for (TextTester textTester : textTesters)
      if (textTester.getAlphanumericRatio () > max)
      {
        max = textTester.getAlphanumericRatio ();
        bestTextTester = textTester;
      }

    return bestTextTester.getTextMaker ();
  }
  //
  //  public ReportMaker getPreferredReportMaker ()
  //  {
  //    double max = Double.MIN_VALUE;
  //    ReportTester bestReportTester = null;
  //
  //    for (ReportTester reportTester : reportTesters)
  //      if (reportTester.getRatio () >= max)
  //      {
  //        max = reportTester.getRatio ();
  //        bestReportTester = reportTester;
  //      }
  //
  //    return bestReportTester.reportMaker;
  //  }
  //
  //  @Override
  //  public String toString ()
  //  {
  //    StringBuilder text = new StringBuilder ();
  //    text.append (String.format ("%-8s %,5d", recordMaker, records.size ()));
  //
  //    for (TextTester textTester : textTesters)
  //      text.append (String.format ("  %s", textTester));
  //
  //    TextMaker preferredTextMaker = getPreferredTextMaker ();
  //    String textMaker = preferredTextMaker == null ? "" : preferredTextMaker.toString ();
  //    text.append ("  " + textMaker);
  //
  //    for (ReportTester reportTester : reportTesters)
  //      text.append (String.format (" %s", reportTester));
  //
  //    ReportMaker preferredReportMaker = getPreferredReportMaker ();
  //    String reportMaker =
  //        preferredReportMaker == null ? "" : preferredReportMaker.toString ();
  //    text.append ("  " + reportMaker);
  //
  //    return text.toString ();
  //  }
}