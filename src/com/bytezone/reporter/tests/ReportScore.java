package com.bytezone.reporter.tests;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.Page;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReportScore implements Comparable<ReportScore>
{
  public final RecordMaker recordMaker;
  public final TextMaker textMaker;
  public final ReportMaker reportMaker;
  public final double score;
  public final int sampleSize;

  private final List<Page> pages;
  private Pagination pagination;
  protected final TextArea textArea;

  public ReportScore (RecordMaker recordMaker, TextMaker textMaker,
      ReportMaker reportMaker, double score, int sampleSize)
  {
    this.recordMaker = recordMaker;
    this.textMaker = textMaker;
    this.reportMaker = reportMaker;
    this.score = score;
    this.sampleSize = sampleSize;
    pages = new ArrayList<> ();
    textArea = new TextArea ();
    textArea.setFont (Font.font ("Ubuntu Mono", FontWeight.NORMAL, 14));
    textArea.setEditable (false);
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

  public TextArea getFormattedPage (int pageNumber)
  {
    //    List<Page> pages = currentReportScore.getPages ();
    List<Record> records = recordMaker.getRecords ();

    StringBuilder text = new StringBuilder ();
    if (pageNumber < 0 || pageNumber >= pages.size ())
    {
      textArea.clear ();
      return textArea;
    }

    Page page = pages.get (pageNumber);
    for (int i = page.getFirstRecordIndex (); i <= page.getLastRecordIndex (); i++)
    {
      Record record = records.get (i);
      String formattedRecord = reportMaker.getFormattedRecord (record);
      if (formattedRecord == null)
        continue;

      if (page.getFirstRecordOffset () > 0 && i == page.getFirstRecordIndex ())
        formattedRecord = formattedRecord.substring (page.getFirstRecordOffset ());
      else if (page.getLastRecordOffset () > 0 && i == page.getLastRecordIndex ())
        formattedRecord = formattedRecord.substring (0, page.getLastRecordOffset ());

      text.append (formattedRecord);
      text.append ('\n');

      if (reportMaker.newlineBetweenRecords ())
        text.append ('\n');
    }

    // remove trailing newlines
    while (text.length () > 0 && text.charAt (text.length () - 1) == '\n')
      text.deleteCharAt (text.length () - 1);

    textArea.setText (text.toString ());

    return textArea;
  }

  @Override
  public String toString ()
  {
    return String.format ("%-10s %-10s %-10s %6.2f %3d  %s", recordMaker, textMaker,
                          reportMaker, score, sampleSize, pagination);
  }
}