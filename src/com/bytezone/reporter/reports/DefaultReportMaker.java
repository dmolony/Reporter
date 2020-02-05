package com.bytezone.reporter.reports;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import com.bytezone.reporter.file.ReportScore;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

// -----------------------------------------------------------------------------------//
public abstract class DefaultReportMaker implements ReportMaker
// -----------------------------------------------------------------------------------//
{
  protected final String name;
  protected final boolean newlineBetweenRecords;
  protected final boolean allowSplitRecords;
  protected double weight = 1.0;

  protected int pageSize = 66;

  //  private LineMetrics lineMetrics;
  //  private int lineHeight;

  //  private static java.awt.Font plainFont =
  //      new java.awt.Font ("Ubuntu Mono", java.awt.Font.PLAIN, 8);
  //  private static java.awt.Font boldFont = new java.awt.Font (plainFont.getFontName (),
  //      java.awt.Font.BOLD, plainFont.getSize ());
  //  private static java.awt.Font headerFont =
  //      new java.awt.Font ("Dialog", java.awt.Font.PLAIN, 14);

  // ---------------------------------------------------------------------------------//
  public DefaultReportMaker (String name, boolean newLine, boolean split)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
    this.newlineBetweenRecords = newLine;
    this.allowSplitRecords = split;

    //    if (true)
    //    {
    //      plainFont = new java.awt.Font ("Ubuntu Mono", java.awt.Font.PLAIN, 8);
    //      boldFont = new java.awt.Font (plainFont.getFontName (), java.awt.Font.BOLD,
    //          plainFont.getSize ());
    //      headerFont = new java.awt.Font ("Dialog", java.awt.Font.PLAIN, 14);
    //    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getFormattedRecord (ReportScore reportScore, Record record)
  // ---------------------------------------------------------------------------------//
  {
    return "Not possible";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getFormattedRecord (ReportScore reportScore, Record record, int offset,
      int length)
  // ---------------------------------------------------------------------------------//
  {
    return "Not possible";
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean test (Record record, TextMaker textMaker)
  // ---------------------------------------------------------------------------------//
  {
    return false;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public int print (Graphics graphics, PageFormat pageFormat, int pageIndex)
      throws PrinterException
  // ---------------------------------------------------------------------------------//
  {
    //    List<Page> pages = currentReportScore.getPages ();
    //    List<Record> records = currentReportScore.recordMaker.getRecords ();

    //    if (pageIndex >= pages.size ())
    //    {
    //      lineMetrics = null;
    //      return Printable.NO_SUCH_PAGE;
    //    }

    Graphics2D g2 = (Graphics2D) graphics;

    //    if (lineMetrics == null)
    //    {
    //      lineMetrics = plainFont.getLineMetrics ("crap", g2.getFontRenderContext ());
    //      lineHeight = (int) lineMetrics.getHeight () + 1;
    //    }

    int x = 50;
    int y = 10;

    g2.translate (pageFormat.getImageableX (), pageFormat.getImageableY ());

    if (false)
      pageFormat.setOrientation (PageFormat.LANDSCAPE);

    if (pageFormat.getOrientation () == PageFormat.PORTRAIT)
    {
      //      g2.setFont (headerFont);
      g2.drawString (name, x, y);
      g2.drawLine (x, y + 3, g2.getClipBounds ().width - x, y + 3);
      y += 30;
    }

    //    g2.setFont (plainFont);

    //    String[] lines = getFormattedPage (pageIndex).getText ().split ("\n");
    //    for (String line : lines)
    //    {
    //      g2.drawString (line, x, y);
    //      y += lineHeight;
    //    }

    // page number
    //    g2.setFont (boldFont);

    return (Printable.PAGE_EXISTS);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean newlineBetweenRecords ()
  // ---------------------------------------------------------------------------------//
  {
    return newlineBetweenRecords;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean allowSplitRecords ()
  // ---------------------------------------------------------------------------------//
  {
    return allowSplitRecords;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public double weight ()
  // ---------------------------------------------------------------------------------//
  {
    return weight;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return name;
  }
}