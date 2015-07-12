package com.bytezone.reporter.reports;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;

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

  public AsaReport (List<Record> records)
  {
    super (records);
  }

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
    int lineCount = 0;
    Page page = new Page ();
    pages.clear ();

    for (int i = 0; i < records.size (); i++)
    {
      Record record = records.get (i);

      char c = textMaker.getChar (record.buffer[record.offset] & 0xFF);
      int lines = 0;
      if (c == ' ')
        lines = 1;
      else if (c == '0')
        lines = 2;
      else if (c == '-')
        lines = 3;

      lineCount += lines;
      if (lineCount > pageSize || (c == '1' && lineCount > 0))
      {
        pages.add (page);
        page = new Page ();
        lineCount = lines;
      }

      if (newlineBetweenRecords)
        lineCount++;

      page.records.add (record);
    }

    if (page.records.size () > 0)
      pages.add (page);
  }

  @Override
  protected String getFormattedRecord (Record record)
  {
    char c = textMaker.getChar (record.buffer[record.offset] & 0xFF);
    String prefix = c == ' ' ? "" : c == '0' ? "\n" : c == '-' ? "\n\n" : "";
    return prefix
        + textMaker.getText (record.buffer, record.offset + 1, record.length - 1);
  }
}