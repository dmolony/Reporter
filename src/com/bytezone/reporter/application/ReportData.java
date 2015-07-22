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
  final List<RecordMaker> recordMakers;
  final List<TextMaker> textMakers;
  final List<ReportMaker> reportMakers;

  private final RecordMaker crlf = new CrlfRecordMaker ();
  private final RecordMaker cr = new CrRecordMaker ();
  private final RecordMaker lf = new LfRecordMaker ();
  private final RecordMaker fb63 = new FbRecordMaker (63);
  private final RecordMaker fb80 = new FbRecordMaker (80);
  private final RecordMaker fb132 = new FbRecordMaker (132);
  private final RecordMaker fb252 = new FbRecordMaker (252);
  private final RecordMaker vb = new VbRecordMaker ();
  private final RecordMaker nvb = new NvbRecordMaker ();
  private final RecordMaker rdw = new RdwRecordMaker ();
  private final RecordMaker ravel = new RavelRecordMaker ();
  private final RecordMaker none = new NoRecordMaker ();

  private final TextMaker asciiTextMaker = new AsciiTextMaker ();
  private final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();

  private final ReportMaker hexReport = new HexReport ();
  private final ReportMaker textReport = new TextReport ();
  private final ReportMaker natloadReport = new NatloadReport ();
  private final ReportMaker asaReport = new AsaReport ();

  private List<Score> scores;

  public ReportData (byte[] buffer)
  {
    hexReport.setNewlineBetweenRecords (true);
    hexReport.setAllowSplitRecords (true);
    asaReport.setAllowSplitRecords (true);

    recordMakers = new ArrayList<> (Arrays.asList (none, crlf, cr, lf, vb, rdw, nvb,
                                                   ravel, fb63, fb80, fb132, fb252));
    textMakers = new ArrayList<> (Arrays.asList (asciiTextMaker, ebcdicTextMaker));
    reportMakers =
        new ArrayList<> (Arrays.asList (hexReport, textReport, asaReport, natloadReport));
  }

  private void setBuffer (byte[] buffer)
  {
    for (RecordMaker recordMaker : recordMakers)
      recordMaker.setBuffer (buffer);
  }

  void setSelections (List<Record> records, TextMaker textMaker)
  {
    for (ReportMaker reportMaker : reportMakers)
    {
      reportMaker.setRecords (records);
      reportMaker.setTextMaker (textMaker);
    }
  }

  void test (byte[] buffer)
  {
    setBuffer (buffer);

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
    return recordMakers;
  }

  List<TextMaker> getTextMakers ()
  {
    return textMakers;
  }

  List<ReportMaker> getReportMakers ()
  {
    return reportMakers;
  }
}