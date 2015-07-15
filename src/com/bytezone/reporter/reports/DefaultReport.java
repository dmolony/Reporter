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
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class DefaultReport implements Report
{
  protected final List<Record> records;
  protected final List<Page> pages;
  protected final Pagination pagination = new Pagination ();
  protected final TextArea textArea;

  protected TextMaker textMaker;
  protected int pageSize = 66;
  protected boolean newlineBetweenRecords;
  protected boolean allowSplitRecords;

  private LineMetrics lineMetrics;
  private int lineHeight;

  private final java.awt.Font plainFont, boldFont, headerFont;

  public DefaultReport (List<Record> records)
  {
    this.records = records;
    pages = new ArrayList<> ();

    textArea = new TextArea ();
    textArea.setFont (Font.font ("Ubuntu Mono", FontWeight.NORMAL, 14));
    textArea.setEditable (false);

    pagination.setPageFactory ( (Integer pageIndex) -> getFormattedPage (pageIndex));

    plainFont = new java.awt.Font ("Ubuntu Mono", java.awt.Font.PLAIN, 8);
    boldFont = new java.awt.Font (plainFont.getFontName (), java.awt.Font.BOLD,
        plainFont.getSize ());
    headerFont = new java.awt.Font ("Dialog", java.awt.Font.PLAIN, 14);
  }

  @Override
  public void setTextMaker (TextMaker textMaker)
  {
    this.textMaker = textMaker;
    paginate ();
    pagination.setPageCount (pages.size ());
  }

  @Override
  public void setNewlineBetweenRecords (boolean value)
  {
    newlineBetweenRecords = value;
    //    paginate ();
    //    pagination.setPageCount (pages.size ());
  }

  @Override
  public void setAllowSplitRecords (boolean value)
  {
    allowSplitRecords = value;
    //    paginate ();
    //    pagination.setPageCount (pages.size ());
  }

  @Override
  public Pagination getPagination ()
  {
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
      if (formattedRecord == null || formattedRecord.isEmpty ())
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

  // fill pages with records
  protected abstract void paginate ();

  protected abstract String getFormattedRecord (Record record);
}