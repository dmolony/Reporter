package com.bytezone.reporter.reports;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Report
{
  protected final List<Record> records;
  protected TextMaker textMaker;
  protected int pageSize = 66;
  protected int pages;
  protected Pagination pagination = new Pagination ();

  public Report (List<Record> records)
  {
    this.records = records;
    pages = (records.size () - 1) / pageSize + 1;
    pagination.setPageFactory ( (Integer pageIndex) -> getFormattedPage (pageIndex));
    pagination.setPageCount (pages);

    //    if (false)
    //      for (Record record : records)
    //      {
    //        if (record.length > 0)
    //        {
    //          boolean ascii = asciiTextMaker.test (record);
    //          boolean ebcdic = ebcdicTextMaker.test (record);
    //          System.out.printf ("%-6s %-6s %n", ascii ? "ascii" : "",
    //                             ebcdic ? "ebcdic" : "");
    //        }
    //      }
  }

  public Pagination getPagination ()
  {
    return pagination;
  }

  public void setTextMaker (TextMaker textMaker)
  {
    this.textMaker = textMaker;
  }

  //  private String getFormattedText ()
  //  {
  //    StringBuilder text = new StringBuilder ();
  //    for (String record : getFormattedRecords ())
  //    {
  //      text.append (record);
  //      text.append ('\n');
  //    }
  //
  //    // remove trailing newlines
  //    while (text.length () > 0 && text.charAt (text.length () - 1) == '\n')
  //      text.deleteCharAt (text.length () - 1);
  //
  //    return text.toString ();
  //  }

  public TextArea getFormattedPage (int page)
  {
    StringBuilder text = new StringBuilder ();
    for (String record : getPageRecords (page))
    {
      text.append (record);
      text.append ('\n');
    }

    // remove trailing newlines
    while (text.length () > 0 && text.charAt (text.length () - 1) == '\n')
      text.deleteCharAt (text.length () - 1);

    TextArea textArea = new TextArea (text.toString ());

    textArea.setFont (Font.font ("Ubuntu Mono", FontWeight.NORMAL, 14));
    textArea.setEditable (false);

    return textArea;
  }

  protected List<String> getPageRecords (int page)
  {
    List<String> formattedRecords = new ArrayList<> (records.size ());
    int first = page * pageSize;
    int last = first + pageSize;

    for (int line = first; line < last; line++)
      if (line < records.size ())
        formattedRecords.add (getFormattedRecord (records.get (line)));

    return formattedRecords;
  }

  protected List<String> getFormattedRecords ()
  {
    List<String> formattedRecords = new ArrayList<> (records.size ());

    for (Record record : records)
      formattedRecords.add (getFormattedRecord (record));

    return formattedRecords;
  }

  public String getFormattedRecord (Record record)
  {
    return textMaker.getText (record.buffer, record.offset, record.length);
  }
}