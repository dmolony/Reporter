package com.bytezone.reporter.reports;

import java.awt.print.Printable;

import com.bytezone.reporter.file.ReportScore;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

// -----------------------------------------------------------------------------------//
public interface ReportMaker extends Printable
// -----------------------------------------------------------------------------------//
{
  public boolean test (Record record, TextMaker textMaker);

  public void createPages (ReportScore reportScore);

  public String getFormattedRecord (ReportScore reportScore, Record record);

  public String getFormattedRecord (ReportScore reportScore, Record record, int offset,
      int length);

  public boolean newlineBetweenRecords ();

  public boolean allowSplitRecords ();

  public double weight ();
}