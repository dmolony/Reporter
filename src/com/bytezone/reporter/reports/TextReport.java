package com.bytezone.reporter.reports;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

public class TextReport extends DefaultReportMaker
{
  public TextReport ()
  {
    super ("Text");
  }

  @Override
  protected void paginate ()
  {
    currentPaginationData.pages.clear ();

    for (int i = 0; i < records.size (); i += pageSize)
      currentPaginationData.pages
          .add (new Page (records, i, Math.min (i + pageSize - 1, records.size () - 1)));
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