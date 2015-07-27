package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.tests.ReportScore;
import com.bytezone.reporter.text.TextMaker;

/*
 * ' ' - One line
 * '0' - Two lines
 * '-' - Three lines
 * '+' - No lines
 * '1' : 'C' - Skip to channel 1-12 (channel 1 is top of page)
 */

public class AsaReport extends DefaultReportMaker
{
  public AsaReport (boolean newLine, boolean split)
  {
    super ("ASA", newLine, split);
  }

  @Override
  protected void createPages (ReportScore reportScore)
  {
    List<Page> pages = reportScore.getPages ();
    List<Record> records = reportScore.recordMaker.getRecords ();

    pages.clear ();

    int firstRecord = 0;
    int lineCount = 0;

    for (int recordNumber = 0; recordNumber < records.size (); recordNumber++)
    {
      Record record = records.get (recordNumber);

      char c = reportScore.textMaker.getChar (record.buffer[record.offset] & 0xFF);
      int lines = 0;
      if (c == ' ')
        lines = 1;
      else if (c == '0')
        lines = 2;
      else if (c == '-')
        lines = 3;

      if (c == '1' && lineCount > 0)
      {
        addPage (reportScore, firstRecord, recordNumber - 1);
        lineCount = 0;
        firstRecord = recordNumber;
      }
      else if (lineCount + lines > pageSize)
      {
        int linesLeft = pageSize - lineCount;
        if (allowSplitRecords && linesLeft > 0)
        {
          Page page = addPage (reportScore, firstRecord, recordNumber);
          page.setLastRecordOffset (linesLeft);
          lineCount = lines - linesLeft;
        }
        else
        {
          addPage (reportScore, firstRecord, recordNumber - 1);
          lineCount = lines;
        }
        firstRecord = recordNumber;
      }
      else
        lineCount += lines;
    }

    addPage (reportScore, firstRecord, records.size () - 1);
  }

  @Override
  public String getFormattedRecord (ReportScore reportScore, Record record)
  {
    TextMaker textMaker = reportScore.textMaker;

    char c = textMaker.getChar (record.buffer[record.offset] & 0xFF);
    String prefix = c == ' ' ? "" : c == '0' ? "\n" : c == '-' ? "\n\n" : "";
    return prefix
        + textMaker.getText (record.buffer, record.offset + 1, record.length - 1);
  }

  @Override
  public boolean test (Record record, TextMaker textMaker)
  {
    if (record.length == 0)
      return true;
    char c = textMaker.getChar (record.buffer[record.offset] & 0xFF);
    if (c != ' ' && c != '0' && c != '1' && c != '-')
      return false;

    // check for program listing
    if (record.recordNumber == 0 && record.length > 4)
    {
      int digits = 0;
      for (int i = 0; i < 4; i++)
      {
        c = textMaker.getChar (record.buffer[record.offset + i] & 0xFF);
        if (c == '0' || c == '1')
          ++digits;
      }
      if (digits == 4)
        return false;
    }

    return textMaker.test (record);
  }
}