package com.bytezone.reporter.reports;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

/*
 * ' ' - One line
 * '0' - Two lines
 * '-' - Three lines
 * '+' - No lines
 * '1' : 'C' - Skip to channel 1-12 (channel 1 is top of page)
 */

public class AsaReport extends DefaultReport
{
  private int currentLine;
  private final int maxLines = 66;

  protected List<String> getFormattedRecords ()
  {
    List<String> formattedRecords = new ArrayList<> (records.size ());
    String line;
    currentLine = 0;

    for (Record record : records)
    {
      char c = textMaker.getChar (record.buffer[record.offset] & 0xFF);
      switch (c)
      {
        case '-':
          lineFeedTo (currentLine + 2, formattedRecords);
          break;

        case '0':
          lineFeedTo (currentLine + 1, formattedRecords);
          break;

        case ' ':
          break;

        case '+':// merge line?
          break;

        case '1':
          lineFeedTo (0, formattedRecords);
          break;

        default:
          line = "";
      }

      line = textMaker.getText (record.buffer, record.offset + 1, record.length - 1);
      formattedRecords.add (line);
      ++currentLine;
    }

    return formattedRecords;
  }

  private void lineFeedTo (int line, List<String> formattedRecords)
  {
    line %= maxLines;

    while (currentLine != line)
    {
      formattedRecords.add ("");
      ++currentLine;
      if (currentLine >= maxLines)
        currentLine = 0;
    }
  }

  @Override
  protected void paginate ()
  {
    pages.clear ();

    int firstRecord = 0;
    int lineCount = 0;

    for (int recordNumber = 0; recordNumber < records.size (); recordNumber++)
    {
      Record record = records.get (recordNumber);

      char c = textMaker.getChar (record.buffer[record.offset] & 0xFF);
      int lines = 0;
      if (c == ' ')
        lines = 1;
      else if (c == '0')
        lines = 2;
      else if (c == '-')
        lines = 3;

      if (c == '1' && lineCount > 0)
      {
        addPage (firstRecord, recordNumber - 1);
        lineCount = 0;
        firstRecord = recordNumber;
      }
      else if (lineCount + lines > pageSize)
      {
        int linesLeft = pageSize - lineCount;
        if (allowSplitRecords && linesLeft > 0)
        {
          Page page = addPage (firstRecord, recordNumber);
          page.setLastRecordOffset (linesLeft);
          lineCount = lines - linesLeft;
        }
        else
        {
          addPage (firstRecord, recordNumber - 1);
          lineCount = lines;
        }
        firstRecord = recordNumber;
      }
      else
        lineCount += lines;
    }

    addPage (firstRecord, records.size () - 1);

    //    for (Page page2 : pages)
    //      System.out.println (page2);
  }

  @Override
  protected String getFormattedRecord (Record record)
  {
    char c = textMaker.getChar (record.buffer[record.offset] & 0xFF);
    String prefix = c == ' ' ? "" : c == '0' ? "\n" : c == '-' ? "\n\n" : "";
    return prefix
        + textMaker.getText (record.buffer, record.offset + 1, record.length - 1);
  }

  @Override
  public boolean test (Record record, TextMaker textMaker)
  {
    char c = textMaker.getChar (record.buffer[record.offset] & 0xFF);
    if (c != ' ' && c != '0' && c != '1' && c != '-')
      return false;

    return true;
  }
}