package com.bytezone.reporter.reports;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.tests.ReportScore;
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class DefaultReportMaker implements ReportMaker
{
  //  protected List<PaginationData> paginationDataList = new ArrayList<> ();
  //  protected PaginationData currentPaginationData;

  protected final String name;
  protected final TextArea textArea;

  protected TextMaker textMaker;
  protected RecordMaker recordMaker;
  protected boolean newlineBetweenRecords;
  protected boolean allowSplitRecords;
  protected List<Record> records;
  protected List<Page> pages;

  protected int pageSize = 66;

  private LineMetrics lineMetrics;
  private int lineHeight;

  private final java.awt.Font plainFont;

  public DefaultReportMaker (String name)
  {
    // it would help if the constructor had the 2 booleans
    this.name = name;
    textArea = new TextArea ();
    textArea.setFont (Font.font ("Ubuntu Mono", FontWeight.NORMAL, 14));
    textArea.setEditable (false);

    plainFont = new java.awt.Font ("Ubuntu Mono", java.awt.Font.PLAIN, 8);
    //    boldFont = new java.awt.Font (plainFont.getFontName (), java.awt.Font.BOLD,
    //        plainFont.getSize ());
    //    headerFont = new java.awt.Font ("Dialog", java.awt.Font.PLAIN, 14);
  }

  @Override
  public void setNewlineBetweenRecords (boolean value)
  {
    newlineBetweenRecords = value;// should only be set in the constructor
  }

  @Override
  public void setAllowSplitRecords (boolean value)
  {
    allowSplitRecords = value;// should only be set in the constructor
  }

  @Override
  public Pagination getPagination (ReportScore reportScore)
  {
    Pagination pagination = reportScore.getPagination ();
    if (pagination == null)
    {
      pagination = new Pagination ();
      pagination.setPageFactory (i -> getFormattedPage (i));
      recordMaker = reportScore.recordMaker;
      textMaker = reportScore.textMaker;
      records = recordMaker.getRecords ();
      pages = reportScore.getPages ();
      createPages ();
      pagination.setPageCount (pages.size ());
      reportScore.setPagination (pagination);
    }
    return pagination;
  }

  public TextArea getFormattedPage (int pageNumber)
  {
    StringBuilder text = new StringBuilder ();
    if (pageNumber < 0 || pageNumber >= pages.size ())
    {
      textArea.clear ();
      return textArea;
    }

    Page page = pages.get (pageNumber);
    for (int i = page.firstRecordIndex; i <= page.lastRecordIndex; i++)
    {
      Record record = records.get (i);
      String formattedRecord = getFormattedRecord (record);
      if (formattedRecord == null)
        continue;

      if (page.firstRecordOffset > 0 && i == page.firstRecordIndex)
        formattedRecord = formattedRecord.substring (page.firstRecordOffset);
      else if (page.lastRecordOffset > 0 && i == page.lastRecordIndex)
        formattedRecord = formattedRecord.substring (0, page.lastRecordOffset);

      text.append (formattedRecord);
      text.append ('\n');

      if (newlineBetweenRecords)
        text.append ('\n');
    }

    // remove trailing newlines
    while (text.length () > 0 && text.charAt (text.length () - 1) == '\n')
      text.deleteCharAt (text.length () - 1);

    textArea.setText (text.toString ());

    return textArea;
  }

  protected Page addPage (int firstRecord, int lastRecord)
  {
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

    //    if (fileStructure.lineSize > 80)
    //      pageFormat.setOrientation (PageFormat.LANDSCAPE);

    //    if (pageFormat.getOrientation () == PageFormat.PORTRAIT)
    //    {
    //      g2.setFont (headerFont);
    //      g2.drawString (name, x, y);
    //      g2.drawLine (x, y + 3, g2.getClipBounds ().width - x, y + 3);
    //      y += 30;
    //    }

    g2.setFont (plainFont);

    String[] lines = getFormattedPage (pageIndex).getText ().split ("\n");
    for (String line : lines)
    {
      g2.drawString (line, x, y);
      y += lineHeight;
    }

    return (Printable.PAGE_EXISTS);
  }

  @Override
  public boolean test (Record record, TextMaker textMaker)
  {
    return false;
  }

  // fill pages with records
  protected abstract void createPages ();

  protected abstract String getFormattedRecord (Record record);

  @Override
  public String toString ()
  {
    return name;
  }

  //  class PaginationData
  //  {
  //    protected RecordMaker recordMaker;
  //    protected TextMaker textMaker;
  //    protected boolean newlineBetweenRecords;
  //    protected boolean allowSplitRecords;
  //
  //    protected final List<Page> pages = new ArrayList<> ();
  //    protected Pagination pagination;
  //    protected List<Record> records;
  //
  //    @Override
  //    public String toString ()
  //    {
  //      return String.format ("%-12s %-6s %s %s", recordMaker, textMaker,
  //                            newlineBetweenRecords ? "NEWLINE" : "newline",
  //                            allowSplitRecords ? "SPLIT" : "split");
  //    }
  //  }
}