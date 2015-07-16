package com.bytezone.reporter.reports;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

public class TextReport extends DefaultReport
{
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
  public boolean test (Record record, TextMaker textMaker)
  {
    return false;
  }
}