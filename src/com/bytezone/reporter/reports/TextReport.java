package com.bytezone.reporter.reports;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

public class TextReport extends DefaultReportMaker
{
  public TextReport (boolean newLine, boolean split)
  {
    super ("Text", newLine, split);
  }

  @Override
  protected void createPages ()
  {
    pages.clear ();

    for (int i = 0; i < records.size (); i += pageSize)
      pages.add (new Page (records, i, Math.min (i + pageSize - 1, records.size () - 1)));
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    return textMaker.getText (record);
  }

  @Override
  public boolean test (Record record, TextMaker textMaker)
  {
    return textMaker.test (record);
  }
}