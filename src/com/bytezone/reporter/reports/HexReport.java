package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.record.Record;

public class HexReport extends DefaultReport
{
  static final int HEX_LINE_SIZE = 16;

  public HexReport (List<Record> records)
  {
    super (records);
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    if (record.length == 0)
      return String.format ("%06X", record.offset);

    StringBuilder text = new StringBuilder ();

    int max = record.offset + record.length;
    for (int ptr = record.offset; ptr < max; ptr += HEX_LINE_SIZE)
    {
      StringBuilder hexLine = new StringBuilder ();

      int lineMax = Math.min (ptr + HEX_LINE_SIZE, max);
      for (int linePtr = ptr; linePtr < lineMax; linePtr++)
        hexLine.append (String.format ("%02X ", record.buffer[linePtr] & 0xFF));

      text.append (String.format ("%06X  %-48s %s%n", ptr, hexLine.toString (),
                                  textMaker.getText (record.buffer, ptr, lineMax - ptr)));
    }

    if (text.length () > 0)
      text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }

  @Override
  protected void paginate ()
  {
    pages.clear ();

    int firstRecord = 0;
    int lineCount = 0;

    for (int recordNumber = 0; recordNumber < records.size (); recordNumber++)
    {
      int recordLength = records.get (recordNumber).length;
      int lines = recordLength == 0 ? 1 : (recordLength - 1) / 16 + 1;
      if (lineCount + lines > pageSize)
      {
        int linesLeft = pageSize - lineCount;
        if (allowSplitRecords && linesLeft > 0)
        {
          Page page = addPage (firstRecord, recordNumber);
          page.setLastRecordOffset (linesLeft * 74);
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

      if (newlineBetweenRecords)
        lineCount++;
    }

    addPage (firstRecord, records.size () - 1);

    for (Page page2 : pages)
      System.out.println (page2);
  }

  private Page addPage (int firstRecord, int lastRecord)
  {
    Page page = new Page (records, firstRecord, lastRecord);
    pages.add (page);

    if (pages.size () > 1)
    {
      Page previousPage = pages.get (pages.size () - 2);
      page.setFirstRecordOffset (previousPage.lastRecordOffset);
    }

    return page;
  }
}