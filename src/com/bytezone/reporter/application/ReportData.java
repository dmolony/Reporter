package com.bytezone.reporter.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bytezone.reporter.record.CrRecordMaker;
import com.bytezone.reporter.record.CrlfRecordMaker;
import com.bytezone.reporter.record.FbRecordMaker;
import com.bytezone.reporter.record.LfRecordMaker;
import com.bytezone.reporter.record.NoRecordMaker;
import com.bytezone.reporter.record.NvbRecordMaker;
import com.bytezone.reporter.record.RavelRecordMaker;
import com.bytezone.reporter.record.RdwRecordMaker;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.record.VbRecordMaker;
import com.bytezone.reporter.reports.AsaReport;
import com.bytezone.reporter.reports.HexReport;
import com.bytezone.reporter.reports.NatloadReport;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.reports.TextReport;
import com.bytezone.reporter.tests.RecordTester;
import com.bytezone.reporter.tests.Score;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

public class ReportData
{
  private List<RecordMaker> recordMakers;
  private List<TextMaker> textMakers;
  private List<ReportMaker> reportMakers;

  private List<Score> scores;
  private byte[] buffer;

  private void createMakers ()
  {
    ReportMaker hexReport = new HexReport ();
    ReportMaker textReport = new TextReport ();
    ReportMaker natloadReport = new NatloadReport ();
    ReportMaker asaReport = new AsaReport ();

    hexReport.setNewlineBetweenRecords (true);
    hexReport.setAllowSplitRecords (true);
    asaReport.setAllowSplitRecords (true);

    recordMakers = new ArrayList<> (
        Arrays.asList (new NoRecordMaker (), new CrlfRecordMaker (), new CrRecordMaker (),
                       new LfRecordMaker (), new VbRecordMaker (), new RdwRecordMaker (),
                       new NvbRecordMaker (), new RavelRecordMaker (),
                       new FbRecordMaker (63), new FbRecordMaker (80),
                       new FbRecordMaker (132), new FbRecordMaker (252)));
    textMakers =
        new ArrayList<> (Arrays.asList (new AsciiTextMaker (), new EbcdicTextMaker ()));
    reportMakers =
        new ArrayList<> (Arrays.asList (hexReport, textReport, asaReport, natloadReport));
  }

  void setBuffer (byte[] buffer)
  {
    this.buffer = buffer;

    if (recordMakers == null)
      createMakers ();

    for (RecordMaker recordMaker : recordMakers)
      recordMaker.setBuffer (buffer);
  }

  byte[] getBuffer ()
  {
    return buffer;
  }

  void setSelections (List<Record> records, TextMaker textMaker)
  {
    for (ReportMaker reportMaker : reportMakers)
    {
      reportMaker.setRecords (records);
      reportMaker.setTextMaker (textMaker);
    }
  }

  void test ()
  {
    List<RecordTester> testers = new ArrayList<> ();
    for (RecordMaker recordMaker : recordMakers)
      if (recordMaker instanceof FbRecordMaker)
      {
        int length = ((FbRecordMaker) recordMaker).getRecordLength ();
        if (recordMaker.getBuffer ().length % length == 0)
          testers.add (new RecordTester (recordMaker, buffer, 10 * length));
      }
      else
        testers.add (new RecordTester (recordMaker, buffer, 1024));

    scores = new ArrayList<> ();

    for (RecordTester tester : testers)
      if (tester.getTotalRecords () > 1)
      {
        for (TextMaker textMaker : textMakers)
          tester.testTextMaker (textMaker);

        TextMaker textMaker = tester.getPreferredTextMaker ();

        for (ReportMaker reportMaker : reportMakers)
          scores.add (tester.testReportMaker (reportMaker, textMaker));
      }
  }

  public List<Score> getScores ()
  {
    return scores;
  }

  List<RecordMaker> getRecordMakers ()
  {
    if (recordMakers == null)
      createMakers ();
    return recordMakers;
  }

  List<TextMaker> getTextMakers ()
  {
    if (textMakers == null)
      createMakers ();
    return textMakers;
  }

  List<ReportMaker> getReportMakers ()
  {
    if (reportMakers == null)
      createMakers ();
    return reportMakers;
  }
}