package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.file.ReportScore;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

// -----------------------------------------------------------------------------------//
public class TextReport extends DefaultReportMaker
// -----------------------------------------------------------------------------------//
{
  // ---------------------------------------------------------------------------------//
  public TextReport (boolean newLine, boolean split)
  // ---------------------------------------------------------------------------------//
  {
    super ("Text", newLine, split);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void createPages (ReportScore reportScore)
  // ---------------------------------------------------------------------------------//
  {
    List<Page> pages = reportScore.getPages ();
    List<Record> records = reportScore.recordMaker.getRecords ();

    pages.clear ();

    for (int i = 0; i < records.size (); i += pageSize)
      pages.add (new Page (records, i, Math.min (i + pageSize - 1, records.size () - 1)));
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getFormattedRecord (ReportScore reportScore, Record record)
  // ---------------------------------------------------------------------------------//
  {
    return reportScore.textMaker.getText (record);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getFormattedRecord (ReportScore reportScore, Record record, int offset,
      int length)
  // ---------------------------------------------------------------------------------//
  {
    return reportScore.textMaker.getText (record).substring (offset, offset + length);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean test (Record record, TextMaker textMaker)
  // ---------------------------------------------------------------------------------//
  {
    boolean result = record.length > 200 ? false : textMaker.test (record);
    //    System.out.printf ("%s  %s%n", record, result);
    return result;
  }
}