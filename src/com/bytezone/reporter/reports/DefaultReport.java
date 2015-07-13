package com.bytezone.reporter.reports;

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

  public DefaultReport (List<Record> records)
  {
    this.records = records;
    pages = new ArrayList<> ();

    textArea = new TextArea ();
    textArea.setFont (Font.font ("Ubuntu Mono", FontWeight.NORMAL, 14));
    textArea.setEditable (false);

    pagination.setPageFactory ( (Integer pageIndex) -> getFormattedPage (pageIndex));
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
    if (pageNumber < pages.size ())
    {
      Page page = pages.get (pageNumber);
      for (int i = 0; i < page.records.size (); i++)
      {
        Record record = records.get (i);
        String formattedRecord = getFormattedRecord (record);

        if (i == 0 && page.firstRecordSplit > 0)
        {
          String[] lines = formattedRecord.split ("\n");
          for (int j = 0; j < lines.length; j++)
            if (j > page.firstRecordSplit)
            {
              text.append (formattedRecord);
              text.append ('\n');
            }
        }
        else if (page.lastRecordSplit > 0 && i == page.records.size () - 1)
        {
          String[] lines = formattedRecord.split ("\n");
          for (int j = 0; j < lines.length; j++)
            if (j <= page.lastRecordSplit)
            {
              text.append (formattedRecord);
              text.append ('\n');
            }
        }
        else
        {
          text.append (formattedRecord);
          text.append ('\n');
        }

        if (newlineBetweenRecords)
          text.append ('\n');
      }
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
    int firstRecordSplit;
    int lastRecordSplit;
  }
}