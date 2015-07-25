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

    for (int i = 0; i < currentPaginationData.records.size (); i += pageSize)
      currentPaginationData.pages.add (new Page (currentPaginationData.records, i,
          Math.min (i + pageSize - 1, currentPaginationData.records.size () - 1)));
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    return currentPaginationData.textMaker.getText (record);
  }

  @Override
  public boolean test (Record record, TextMaker textMaker)
  {
    return textMaker.test (record);
  }
}