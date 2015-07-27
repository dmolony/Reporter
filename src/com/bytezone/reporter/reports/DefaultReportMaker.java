package com.bytezone.reporter.reports;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.tests.ReportScore;
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;

public abstract class DefaultReportMaker implements ReportMaker
{
  protected final String name;
  //  protected final TextArea textArea;
  protected final boolean newlineBetweenRecords;
  protected final boolean allowSplitRecords;

  protected ReportScore currentReportScore;

  protected int pageSize = 66;

  private LineMetrics lineMetrics;
  private int lineHeight;

  private java.awt.Font plainFont;
  private java.awt.Font boldFont;
  private java.awt.Font headerFont;

  public DefaultReportMaker (String name, boolean newLine, boolean split)
  {
    this.name = name;
    this.newlineBetweenRecords = newLine;
    this.allowSplitRecords = split;

    //    textArea = new TextArea ();
    //    textArea.setFont (Font.font ("Ubuntu Mono", FontWeight.NORMAL, 14));
    //    textArea.setEditable (false);

    if (false)
    {
      plainFont = new java.awt.Font ("Ubuntu Mono", java.awt.Font.PLAIN, 8);
      boldFont = new java.awt.Font (plainFont.getFontName (), java.awt.Font.BOLD,
          plainFont.getSize ());
      headerFont = new java.awt.Font ("Dialog", java.awt.Font.PLAIN, 14);
    }
  }

  @Override
  public void setPagination (ReportScore reportScore)
  {
    currentReportScore = reportScore;
    Pagination pagination = reportScore.getPagination ();
    if (pagination == null)
    {
      pagination = new Pagination ();
      pagination.setPageFactory (i -> reportScore.getFormattedPage (i));
      reportScore.setPagination (pagination);

      createPages ();
      pagination.setPageCount (reportScore.getPages ().size ());
    }
  }

  //  public TextArea getFormattedPage (int pageNumber)
  //  {
  //    List<Page> pages = currentReportScore.getPages ();
  //    List<Record> records = currentReportScore.recordMaker.getRecords ();
  //
  //    StringBuilder text = new StringBuilder ();
  //    if (pageNumber < 0 || pageNumber >= pages.size ())
  //    {
  //      textArea.clear ();
  //      return textArea;
  //    }
  //
  //    Page page = pages.get (pageNumber);
  //    for (int i = page.firstRecordIndex; i <= page.lastRecordIndex; i++)
  //    {
  //      Record record = records.get (i);
  //      String formattedRecord = getFormattedRecord (record);
  //      if (formattedRecord == null)
  //        continue;
  //
  //      if (page.firstRecordOffset > 0 && i == page.firstRecordIndex)
  //        formattedRecord = formattedRecord.substring (page.firstRecordOffset);
  //      else if (page.lastRecordOffset > 0 && i == page.lastRecordIndex)
  //        formattedRecord = formattedRecord.substring (0, page.lastRecordOffset);
  //
  //      text.append (formattedRecord);
  //      text.append ('\n');
  //
  //      if (newlineBetweenRecords)
  //        text.append ('\n');
  //    }
  //
  //    // remove trailing newlines
  //    while (text.length () > 0 && text.charAt (text.length () - 1) == '\n')
  //      text.deleteCharAt (text.length () - 1);
  //
  //    textArea.setText (text.toString ());
  //
  //    return textArea;
  //  }

  protected Page addPage (int firstRecord, int lastRecord)
  {
    List<Page> pages = currentReportScore.getPages ();
    List<Record> records = currentReportScore.recordMaker.getRecords ();

    Page page = new Page (records, firstRecord, lastRecord);
    pages.add (page);

    if (pages.size () > 1)
    {
      Page previousPage = pages.get (pages.size () - 2);
      page.setFirstRecordOffset (previousPage.lastRecordOffset);
    }

    return page;
  }

  @Override
  public int print (Graphics graphics, PageFormat pageFormat, int pageIndex)
      throws PrinterException
  {
    List<Page> pages = currentReportScore.getPages ();
    //    List<Record> records = currentReportScore.recordMaker.getRecords ();

    if (pageIndex >= pages.size ())
    {
      lineMetrics = null;
      return Printable.NO_SUCH_PAGE;
    }

    Graphics2D g2 = (Graphics2D) graphics;

    if (lineMetrics == null)
    {
      lineMetrics = plainFont.getLineMetrics ("crap", g2.getFontRenderContext ());
      lineHeight = (int) lineMetrics.getHeight () + 1;
    }

    int x = 50;
    int y = 10;

    g2.translate (pageFormat.getImageableX (), pageFormat.getImageableY ());

    if (false)
      pageFormat.setOrientation (PageFormat.LANDSCAPE);

    if (pageFormat.getOrientation () == PageFormat.PORTRAIT)
    {
      g2.setFont (headerFont);
      g2.drawString (name, x, y);
      g2.drawLine (x, y + 3, g2.getClipBounds ().width - x, y + 3);
      y += 30;
    }

    g2.setFont (plainFont);

    //    String[] lines = getFormattedPage (pageIndex).getText ().split ("\n");
    //    for (String line : lines)
    //    {
    //      g2.drawString (line, x, y);
    //      y += lineHeight;
    //    }

    // page number
    g2.setFont (boldFont);

    return (Printable.PAGE_EXISTS);
  }

  @Override
  public boolean test (Record record, TextMaker textMaker)
  {
    return false;
  }

  @Override
  public boolean newlineBetweenRecords ()
  {
    return newlineBetweenRecords;
  }

  @Override
  public boolean allowSplitRecords ()
  {
    return allowSplitRecords;
  }

  // fill List<Page> with Page records
  protected abstract void createPages ();

  @Override
  public abstract String getFormattedRecord (Record record);

  @Override
  public String toString ()
  {
    return name;
  }
}