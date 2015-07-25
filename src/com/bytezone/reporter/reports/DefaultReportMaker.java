package com.bytezone.reporter.reports;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class DefaultReportMaker implements ReportMaker
{
  protected List<PaginationData> paginationDataList = new ArrayList<> ();
  protected PaginationData currentPaginationData;

  protected final String name;
  protected final TextArea textArea;

  protected List<Record> records;

  protected TextMaker textMaker;
  protected RecordMaker recordMaker;
  protected boolean newlineBetweenRecords;
  protected boolean allowSplitRecords;

  protected int pageSize = 66;

  private LineMetrics lineMetrics;
  private int lineHeight;

  private final java.awt.Font plainFont;

  public DefaultReportMaker (String name)
  {
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
  public void setRecordMaker (RecordMaker recordMaker)
  {
    this.recordMaker = recordMaker;
    records = recordMaker.getRecords ();
    setCurrentPaginationData ();
  }

  @Override
  public void setTextMaker (TextMaker textMaker)
  {
    this.textMaker = textMaker;
    setCurrentPaginationData ();
  }

  @Override
  public void setNewlineBetweenRecords (boolean value)
  {
    newlineBetweenRecords = value;
    setCurrentPaginationData ();
  }

  @Override
  public void setAllowSplitRecords (boolean value)
  {
    allowSplitRecords = value;
    setCurrentPaginationData ();
  }

  private void setCurrentPaginationData ()
  {
    currentPaginationData = null;
    if (recordMaker == null || textMaker == null)
      return;

    for (PaginationData paginationData : paginationDataList)
    {
      if (recordMaker == paginationData.recordMaker
          && textMaker == paginationData.textMaker
          && newlineBetweenRecords == paginationData.newlineBetweenRecords
          && allowSplitRecords == paginationData.allowSplitRecords)
      {
        currentPaginationData = paginationData;
        break;
      }
    }

    if (currentPaginationData == null)
    {
      currentPaginationData = new PaginationData ();
      paginationDataList.add (currentPaginationData);
      currentPaginationData.recordMaker = recordMaker;
      currentPaginationData.textMaker = textMaker;
      currentPaginationData.newlineBetweenRecords = newlineBetweenRecords;
      currentPaginationData.allowSplitRecords = allowSplitRecords;
    }
  }

  @Override
  public Pagination getPagination ()
  {
    // this will be different for each combination of RecordMaker/TextMaker
    if (currentPaginationData.pagination == null)
    {
      currentPaginationData.pagination = new Pagination ();
      currentPaginationData.pagination.setPageFactory (i -> getFormattedPage (i));
      paginate ();
      currentPaginationData.pagination.setPageCount (currentPaginationData.pages.size ());
    }
    return currentPaginationData.pagination;
  }

  public TextArea getFormattedPage (int pageNumber)
  {
    StringBuilder text = new StringBuilder ();
    if (pageNumber < 0 || pageNumber >= currentPaginationData.pages.size ())
    {
      textArea.clear ();
      return textArea;
    }

    Page page = currentPaginationData.pages.get (pageNumber);
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
    currentPaginationData.pages.add (page);

    if (currentPaginationData.pages.size () > 1)
    {
      Page previousPage =
          currentPaginationData.pages.get (currentPaginationData.pages.size () - 2);
      page.setFirstRecordOffset (previousPage.lastRecordOffset);
    }

    return page;
  }

  @Override
  public int print (Graphics graphics, PageFormat pageFormat, int pageIndex)
      throws PrinterException
  {
    if (pageIndex >= currentPaginationData.pages.size ())
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
  protected abstract void paginate ();

  protected abstract String getFormattedRecord (Record record);

  @Override
  public String toString ()
  {
    return name;
  }

  class PaginationData
  {
    private RecordMaker recordMaker;
    private TextMaker textMaker;
    private boolean newlineBetweenRecords;
    private boolean allowSplitRecords;

    protected final List<Page> pages = new ArrayList<> ();
    private Pagination pagination;
  }
}