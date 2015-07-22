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

  private RecordMaker crlf;
  private RecordMaker cr;
  private RecordMaker lf;
  private RecordMaker fb63;
  private RecordMaker fb80;
  private RecordMaker fb132;
  private RecordMaker fb252;
  private RecordMaker vb;
  private RecordMaker nvb;
  private RecordMaker rdw;
  private RecordMaker ravel;
  private RecordMaker none;

  private TextMaker asciiTextMaker;
  private TextMaker ebcdicTextMaker;

  private ReportMaker hexReport;
  private ReportMaker textReport;
  private ReportMaker natloadReport;
  private ReportMaker asaReport;

  private List<Score> scores;
  private byte[] buffer;

  private void initialise ()
  {
    crlf = new CrlfRecordMaker ();
    cr = new CrRecordMaker ();
    lf = new LfRecordMaker ();
    fb63 = new FbRecordMaker (63);
    fb80 = new FbRecordMaker (80);
    fb132 = new FbRecordMaker (132);
    fb252 = new FbRecordMaker (252);
    vb = new VbRecordMaker ();
    nvb = new NvbRecordMaker ();
    rdw = new RdwRecordMaker ();
    ravel = new RavelRecordMaker ();
    none = new NoRecordMaker ();

    asciiTextMaker = new AsciiTextMaker ();
    ebcdicTextMaker = new EbcdicTextMaker ();

    hexReport = new HexReport ();
    textReport = new TextReport ();
    natloadReport = new NatloadReport ();
    asaReport = new AsaReport ();

    hexReport.setNewlineBetweenRecords (true);
    hexReport.setAllowSplitRecords (true);
    asaReport.setAllowSplitRecords (true);

    recordMakers = new ArrayList<> (Arrays.asList (none, crlf, cr, lf, vb, rdw, nvb,
                                                   ravel, fb63, fb80, fb132, fb252));
    textMakers = new ArrayList<> (Arrays.asList (asciiTextMaker, ebcdicTextMaker));
    reportMakers =
        new ArrayList<> (Arrays.asList (hexReport, textReport, asaReport, natloadReport));
  }

  void setBuffer (byte[] buffer)
  {
    this.buffer = buffer;

    if (reportMakers == null)
      initialise ();

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