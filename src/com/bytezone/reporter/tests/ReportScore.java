package com.bytezone.reporter.tests;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.Page;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;

public class ReportScore implements Comparable<ReportScore>
{
  public final RecordMaker recordMaker;
  public final TextMaker textMaker;
  public final ReportMaker reportMaker;
  public final double score;
  public final int sampleSize;

  private final List<Page> pages;
  private Pagination pagination;

  public ReportScore (RecordMaker recordMaker, TextMaker textMaker,
      ReportMaker reportMaker, double score, int sampleSize)
  {
    this.reportMaker = reportMaker;
    this.textMaker = textMaker;
    this.recordMaker = recordMaker;
    this.score = score;
    this.sampleSize = sampleSize;

    if (score == 100.0)
    {
      pages = new ArrayList<> ();
    }
    else
    {
      pages = null;
    }
  }

  public boolean matches (RecordMaker recordMaker, TextMaker textMaker,
      ReportMaker reportMaker)
  {
    if (this.recordMaker == recordMaker && this.textMaker == textMaker
        && this.reportMaker == reportMaker)
      return true;
    return false;
  }

  public List<Page> getPages ()
  {
    return pages;
  }

  public void setPagination (Pagination pagination)
  {
    this.pagination = pagination;
  }

  public Pagination getPagination ()
  {
    return pagination;
  }

  @Override
  public int compareTo (ReportScore o)
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