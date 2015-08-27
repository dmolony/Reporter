package com.bytezone.reporter.file;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.bytezone.reporter.application.TreePanel.FileNode;
import com.bytezone.reporter.record.CrRecordMaker;
import com.bytezone.reporter.record.CrlfRecordMaker;
import com.bytezone.reporter.record.FbRecordMaker;
import com.bytezone.reporter.record.LfRecordMaker;
import com.bytezone.reporter.record.NvbRecordMaker;
import com.bytezone.reporter.record.RavelRecordMaker;
import com.bytezone.reporter.record.RdwRecordMaker;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.record.SingleRecordMaker;
import com.bytezone.reporter.record.VbRecordMaker;
import com.bytezone.reporter.reports.AsaReport;
import com.bytezone.reporter.reports.HexReport;
import com.bytezone.reporter.reports.NatloadReport;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.reports.TextReport;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

public class ReportData
{
  private final List<RecordMaker> recordMakers;
  private final List<TextMaker> textMakers;
  private final List<ReportMaker> reportMakers;
  private final List<ReportScore> scores;

  private ReportScore selectedReportScore;

  private byte[] buffer;

  public ReportData ()
  {
    recordMakers = new ArrayList<> (Arrays
        .asList (new SingleRecordMaker (), new CrlfRecordMaker (), new CrRecordMaker (),
                 new LfRecordMaker (), new VbRecordMaker (), new RdwRecordMaker (),
                 new NvbRecordMaker (), new RavelRecordMaker (), new FbRecordMaker (63),
                 new FbRecordMaker (80), new FbRecordMaker (132),
                 new FbRecordMaker (252)));
    textMakers =
        new ArrayList<> (Arrays.asList (new AsciiTextMaker (), new EbcdicTextMaker ()));
    reportMakers = new ArrayList<> (
        Arrays.asList (new HexReport (true, true), new TextReport (false, false),
                       new AsaReport (false, true), new NatloadReport (false, false)));
    scores = new ArrayList<> ();
  }

  public boolean hasData ()
  {
    return buffer != null;
  }

  public void addBuffer (FileNode fileNode)
  {
    //    buffer = fileNode.getBuffer ();
    if (buffer == null)
      try
      {
        if (fileNode.getFile ().exists ())
          buffer = Files.readAllBytes (fileNode.getFile ().toPath ());
        else
          buffer = new byte[0];
        //        fileNode.setBuffer (buffer);
        System.out.printf ("reading %d bytes%n", buffer.length);
      }
      catch (IOException e)
      {
        System.out.println (e.toString ());
        buffer = new byte[0];
      }

    for (RecordMaker recordMaker : recordMakers)
      recordMaker.setBuffer (buffer);

    List<RecordTester> testers = new ArrayList<> ();

    for (RecordMaker recordMaker : recordMakers)
      if (recordMaker instanceof FbRecordMaker)
      {
        int recordLength = ((FbRecordMaker) recordMaker).getRecordLength ();
        int fileLength = recordMaker.getBuffer ().length;
        if (fileLength % recordLength == 0)
          testers.add (new RecordTester (recordMaker, 15 * recordLength));
      }
      else if (recordMaker instanceof SingleRecordMaker)
        testers.add (new RecordTester (recordMaker, 1024));
      else
      {
        RecordTester recordTester = new RecordTester (recordMaker, 1024);
        if (recordTester.getSampleSize () > 2)
          testers.add (recordTester);
      }

    for (RecordTester recordTester : testers)
    {
      for (TextMaker textMaker : textMakers)
        recordTester.testTextMaker (textMaker);

      TextMaker textMaker = recordTester.getPreferredTextMaker ();

      for (ReportMaker reportMaker : reportMakers)
        scores.add (recordTester.testReportMaker (reportMaker, textMaker));
    }

    assert scores.size () > 0;
    Collections.sort (scores);
    Collections.reverse (scores);

    if (false)
      for (ReportScore rs : scores)
        System.out.println (rs);
  }

  public List<RecordMaker> getRecordMakers ()
  {
    return recordMakers;
  }

  public List<TextMaker> getTextMakers ()
  {
    return textMakers;
  }

  public List<ReportMaker> getReportMakers ()
  {
    return reportMakers;
  }

  public List<ReportScore> getPerfectScores ()
  {
    List<ReportScore> perfectScores = new ArrayList<> ();
    for (ReportScore score : scores)
      if (score.isPerfectScore ())
        perfectScores.add (score);
      else
        break;

    return perfectScores;
  }

  private ReportScore getBestReportScore ()
  {
    assert scores.size () > 0;
    return scores.get (0);
  }

  public ReportScore getSelectedReportScore ()
  {
    if (selectedReportScore == null)
      selectedReportScore = getBestReportScore ();
    return selectedReportScore;
  }

  public ReportScore setReportScore (RecordMaker recordMaker, TextMaker textMaker,
      ReportMaker reportMaker)
  {
    for (ReportScore score : scores)
      if (score.matches (recordMaker, textMaker, reportMaker))
      {
        selectedReportScore = score;
        break;
      }

    return selectedReportScore;
  }

  public byte[] getBuffer ()
  {
    return buffer;
  }

  public boolean isAscii ()
  {
    if (selectedReportScore == null)
      return false;

    return selectedReportScore.textMaker.toString ().equals ("ASCII");// fix this
  }
}