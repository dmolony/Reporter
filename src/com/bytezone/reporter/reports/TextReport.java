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
    pages.clear ();

    for (int i = 0; i < records.size (); i += pageSize)
      pages.add (new Page (records, i, Math.min (i + pageSize - 1, records.size () - 1)));
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    return textMaker.getText (record.buffer, record.offset, record.length);
  }

  @Override
  public boolean test ()
  {
    return true;
  }
}