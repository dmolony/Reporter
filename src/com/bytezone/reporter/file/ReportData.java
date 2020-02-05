package com.bytezone.reporter.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

// -----------------------------------------------------------------------------------//
public class ReportData
// -----------------------------------------------------------------------------------//
{
  private static final int SAMPLE_SIZE = 1024;

  private final List<RecordMaker> recordMakers;
  private final List<TextMaker> textMakers;
  private final List<ReportMaker> reportMakers;
  private final List<ReportScore> scores;

  private ReportScore selectedReportScore;

  private byte[] buffer;

  // ---------------------------------------------------------------------------------//
  public ReportData ()
  // ---------------------------------------------------------------------------------//
  {
    recordMakers = new ArrayList<> (Arrays.asList (new SingleRecordMaker (),
        new CrlfRecordMaker (), new CrRecordMaker (), new LfRecordMaker (),
        new VbRecordMaker (), new RdwRecordMaker (), new NvbRecordMaker (),
        new RavelRecordMaker (), new FbRecordMaker (63), new FbRecordMaker (80),
        new FbRecordMaker (132), new FbRecordMaker (252)));
    textMakers =
        new ArrayList<> (Arrays.asList (new AsciiTextMaker (), new EbcdicTextMaker ()));
    reportMakers = new ArrayList<> (
        Arrays.asList (new HexReport (true, true), new TextReport (false, false),
            new AsaReport (false, true), new NatloadReport (false, false)));
    scores = new ArrayList<> ();
  }

  // used for file transfers
  // ---------------------------------------------------------------------------------//
  public ReportData (byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this ();
    this.buffer = buffer;
  }

  // ---------------------------------------------------------------------------------//
  public byte[] getBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  public boolean hasData ()
  // ---------------------------------------------------------------------------------//
  {
    return buffer != null;
  }

  // ---------------------------------------------------------------------------------//
  public boolean hasScores ()
  // ---------------------------------------------------------------------------------//
  {
    return scores.size () > 0;
  }

  // ---------------------------------------------------------------------------------//
  public void fillBuffer (File file)
  // ---------------------------------------------------------------------------------//
  {
    assert buffer == null;
    try
    {
      buffer = Files.readAllBytes (file.toPath ());
    }
    catch (IOException e)
    {
      System.out.println (e.toString ());
      buffer = new byte[0];
    }
  }

  // ---------------------------------------------------------------------------------//
  public void createScores ()
  // ---------------------------------------------------------------------------------//
  {
    assert buffer != null;
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
        int length = SAMPLE_SIZE;
        // avoid splitting between 0x0D and 0x0A
        if (recordMaker instanceof CrlfRecordMaker && buffer.length > length
            && buffer[length - 1] == 0x0D)
          length++;

        RecordTester recordTester = new RecordTester (recordMaker, length);
        if (recordTester.countSampleRecords () > 0)
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

  // ---------------------------------------------------------------------------------//
  public List<RecordMaker> getRecordMakers ()
  // ---------------------------------------------------------------------------------//
  {
    return recordMakers;
  }

  // ---------------------------------------------------------------------------------//
  public List<TextMaker> getTextMakers ()
  // ---------------------------------------------------------------------------------//
  {
    return textMakers;
  }

  // ---------------------------------------------------------------------------------//
  public List<ReportMaker> getReportMakers ()
  // ---------------------------------------------------------------------------------//
  {
    return reportMakers;
  }

  // ---------------------------------------------------------------------------------//
  public List<ReportScore> getPerfectScores ()
  // ---------------------------------------------------------------------------------//
  {
    List<ReportScore> perfectScores = new ArrayList<> ();
    for (ReportScore score : scores)
      if (score.isPerfectScore ())
        perfectScores.add (score);
      else
        break;

    return perfectScores;
  }

  // ---------------------------------------------------------------------------------//
  private ReportScore getBestReportScore ()
  // ---------------------------------------------------------------------------------//
  {
    assert scores.size () > 0;
    return scores.get (0);
  }

  // ---------------------------------------------------------------------------------//
  public ReportScore getSelectedReportScore ()
  // ---------------------------------------------------------------------------------//
  {
    if (selectedReportScore == null)
      selectedReportScore = getBestReportScore ();
    return selectedReportScore;
  }

  // ---------------------------------------------------------------------------------//
  public ReportScore setReportScore (RecordMaker recordMaker, TextMaker textMaker,
      ReportMaker reportMaker)
  // ---------------------------------------------------------------------------------//
  {
    selectedReportScore = null;

    for (ReportScore score : scores)
      if (score.matches (recordMaker, textMaker, reportMaker))
      {
        selectedReportScore = score;
        break;
      }

    return selectedReportScore;
  }

  // ---------------------------------------------------------------------------------//
  public boolean isAscii ()
  // ---------------------------------------------------------------------------------//
  {
    if (selectedReportScore == null)
      return false;

    return selectedReportScore.textMaker.toString ().equals ("ASCII");      // fix this
  }
}