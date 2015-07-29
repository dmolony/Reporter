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
  private final List<Record> sampleRecords;

  private final List<TextTester> textTesters = new ArrayList<> ();
  private final List<ReportTester> reportTesters = new ArrayList<> ();

  public RecordTester (RecordMaker recordMaker, int testSize)
  {
    this.recordMaker = recordMaker;
    sampleRecords = recordMaker.createSampleRecords (testSize);
  }

  public int getSampleSize ()
  {
    return sampleRecords.size ();
  }

  public void testTextMaker (TextMaker textMaker)
  {
    TextTester textTester = new TextTester (textMaker);
    textTesters.add (textTester);

    textTester.testRecords (sampleRecords);
  }

  public TextMaker getPreferredTextMaker ()
  {
    double max = Double.MIN_VALUE;
    TextTester bestTextTester = null;

    for (TextTester textTester : textTesters)
    {
      System.out.println (textTester);
      if (textTester.getAlphanumericRatio () > max)
      {
        max = textTester.getAlphanumericRatio ();
        bestTextTester = textTester;
      }
    }

    return bestTextTester.getTextMaker ();
  }

  public ReportScore testReportMaker (ReportMaker reportMaker, TextMaker textMaker)
  {
    ReportTester reportTester = new ReportTester (reportMaker, textMaker);
    reportTesters.add (reportTester);

    reportTester.testRecords (sampleRecords);

    return new ReportScore (recordMaker, textMaker, reportMaker, reportTester.getRatio (),
        sampleRecords.size ());
  }
}