package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.record.Record;

public class TextReport extends DefaultReport
{
  public TextReport (List<Record> records)
  {
    super (records);

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

  @Override
  protected void paginate ()
  {
    Page page = new Page ();
    pages.clear ();

    for (int i = 0; i < records.size (); i++)
    {
      Record record = records.get (i);
      page.records.add (record);
      if (page.records.size () == pageSize)
      {
        pages.add (page);
        page = new Page ();
      }
    }

    if (page.records.size () > 0)
      pages.add (page);
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    return textMaker.getText (record.buffer, record.offset, record.length);
  }
}