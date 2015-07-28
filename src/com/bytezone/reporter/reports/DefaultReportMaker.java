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

public abstract class DefaultReportMaker implements ReportMaker
{
  protected final String name;
  protected final boolean newlineBetweenRecords;
  protected final boolean allowSplitRecords;

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

    if (false)
    {
      plainFont = new java.awt.Font ("Ubuntu Mono", java.awt.Font.PLAIN, 8);
      boldFont = new java.awt.Font (plainFont.getFontName (), java.awt.Font.BOLD,
          plainFont.getSize ());
      headerFont = new java.awt.Font ("Dialog", java.awt.Font.PLAIN, 14);
    }
  }

  //  @Override
  //  public void setPagination (ReportScore reportScore)
  //  {
  //    Pagination pagination = reportScore.getPagination ();
  //    if (pagination == null)
  //    {
  //      pagination = new Pagination ();
  //      pagination.setPageFactory (i -> reportScore.getFormattedPage (i));
  //      reportScore.setPagination (pagination);
  //
  //      createPages (reportScore);
  //      pagination.setPageCount (reportScore.getPages ().size ());
  //    }
  //  }

  protected Page addPage (ReportScore reportScore, int firstRecord, int lastRecord)
  {
    List<Page> pages = reportScore.getPages ();
    List<Record> records = reportScore.recordMaker.getRecords ();

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
    //    List<Page> pages = currentReportScore.getPages ();
    //    List<Record> records = currentReportScore.recordMaker.getRecords ();

    //    if (pageIndex >= pages.size ())
    //    {
    //      lineMetrics = null;
    //      return Printable.NO_SUCH_PAGE;
    //    }

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
  //  protected abstract void createPages (ReportScore reportScore);

  @Override
  public String toString ()
  {
    return name;
  }
}