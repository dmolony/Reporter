package com.bytezone.reporter.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.record.VbRecordMaker;
import com.bytezone.reporter.reports.AsaReport;
import com.bytezone.reporter.reports.HexReport;
import com.bytezone.reporter.reports.NatloadReport;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.reports.TextReport;
import com.bytezone.reporter.tests.RecordTester;
import com.bytezone.reporter.tests.ReportScore;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

public class ReportData
{
  private byte[] buffer;

  private List<RecordMaker> recordMakers;
  private List<TextMaker> textMakers;
  private List<ReportMaker> reportMakers;
  private List<ReportScore> scores;

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

  void readFile (File file) throws IOException
  {
    this.buffer = Files.readAllBytes (file.toPath ());

    if (recordMakers == null)
      createMakers ();

    for (RecordMaker recordMaker : recordMakers)
      recordMaker.setBuffer (buffer);

    List<RecordTester> testers = new ArrayList<> ();
    for (RecordMaker recordMaker : recordMakers)
      if (recordMaker instanceof FbRecordMaker)
      {
        int length = ((FbRecordMaker) recordMaker).getRecordLength ();
        if (recordMaker.getBuffer ().length % length == 0)
          testers.add (new RecordTester (recordMaker, 10 * length));
      }
      else
        testers.add (new RecordTester (recordMaker, 1024));

    scores = new ArrayList<> ();

    for (RecordTester tester : testers)
      if (tester.getTotalRecords () > 2)
      {
        for (TextMaker textMaker : textMakers)
          tester.testTextMaker (textMaker);

        TextMaker textMaker = tester.getPreferredTextMaker ();

        for (ReportMaker reportMaker : reportMakers)
        {
          ReportScore score = tester.testReportMaker (reportMaker, textMaker);
          scores.add (score);
        }
      }
  }

  public boolean hasData ()
  {
    return buffer != null;
  }

  //  void setSelections (RecordMaker recordMaker, TextMaker textMaker)
      //  {
      //    for (ReportMaker reportMaker : reportMakers)
      //      reportMaker.setMakers (recordMaker, textMaker);
      //  }

  List<ReportScore> getScores ()
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