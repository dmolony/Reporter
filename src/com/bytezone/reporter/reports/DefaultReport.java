package com.bytezone.reporter.reports;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public abstract class DefaultReport
{
  protected final List<Record> records;
  protected final List<Page> pages;
  protected final Pagination pagination = new Pagination ();
  protected final TextArea textArea;

  protected TextMaker textMaker;
  protected int pageSize = 66;
  protected boolean newlineBetweenRecords;

  public DefaultReport (List<Record> records)
  {
    this.records = records;
    pages = new ArrayList<> ();

    textArea = new TextArea ();
    textArea.setFont (Font.font ("Ubuntu Mono", FontWeight.NORMAL, 14));
    textArea.setEditable (false);

    pagination.setPageFactory ( (Integer pageIndex) -> getFormattedPage (pageIndex));
  }

  public void setTextMaker (TextMaker textMaker)
  {
    this.textMaker = textMaker;
    paginate ();
    pagination.setPageCount (pages.size ());
  }

  public void setNewlineBetweenRecords (boolean value)
  {
    newlineBetweenRecords = value;
    paginate ();
    pagination.setPageCount (pages.size ());
  }

  public Pagination getPagination ()
  {
    return pagination;
  }

  public TextArea getFormattedPage (int page)
  {
    StringBuilder text = new StringBuilder ();
    if (page < pages.size ())
      for (Record record : pages.get (page).records)
      {
        text.append (getFormattedRecord (record));
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

  // fill pages with records
  protected abstract void paginate ();

  protected abstract String getFormattedRecord (Record record);

  protected class Page
  {
    final List<Record> records = new ArrayList<> ();
  }
}