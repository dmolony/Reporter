package com.bytezone.reporter.tests;

import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.text.TextMaker;

public class Score implements Comparable<Score>
{
  public final RecordMaker recordMaker;
  public final TextMaker textMaker;
  public final ReportMaker reportMaker;
  public final double score;
  public final int sampleSize;

  public Score (RecordMaker recordMaker, TextMaker textMaker, ReportMaker reportMaker,
      double score, int sampleSize)
  {
    this.reportMaker = reportMaker;
    this.textMaker = textMaker;
    this.recordMaker = recordMaker;
    this.score = score;
    this.sampleSize = sampleSize;
  }

  @Override
  public int compareTo (Score o)
  {
    return Double.compare (this.score, o.score);
  }

  @Override
  public String toString ()
  {
    return String.format ("%-10s %-10s %-10s %6.2f %3d", recordMaker, textMaker,
                          reportMaker, score, sampleSize);
  }
}