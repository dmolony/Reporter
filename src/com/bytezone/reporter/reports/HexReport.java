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
    int firstRecordOffset = 0;
    int lineCount = 0;
    System.out.printf ("Split: %s%n", allowSplitRecords);

    for (int i = 0; i < records.size (); i++)
    {
      Record record = records.get (i);
      int lines = record.length == 0 ? 1 : (record.length - 1) / 16 + 1;
      if (lineCount + lines > pageSize)
      {
        int linesLeft = pageSize - lineCount;
        if (allowSplitRecords && linesLeft > 0)
        {
          int offset = linesLeft * 74;// includes the linefeed

          Page page = addPage (firstRecord, firstRecordOffset, i);
          firstRecordOffset = 0;
          page.setLastRecordOffset (offset);

          lineCount = lines - linesLeft;
          firstRecord = i;
          firstRecordOffset = offset;
        }
        else
        {
          addPage (firstRecord, firstRecordOffset, i - 1);
          firstRecordOffset = 0;

          lineCount = lines;
          firstRecord = i;
        }
      }
      else
        lineCount += lines;

      if (newlineBetweenRecords)
        lineCount++;
    }

    addPage (firstRecord, firstRecordOffset, records.size () - 1);

    for (Page page2 : pages)
      System.out.println (page2);
  }

  private Page addPage (int firstRecord, int firstRecordOffset, int lastRecord)
  {
    Page page = new Page (records, firstRecord, lastRecord);
    pages.add (page);

    if (firstRecordOffset > 0)
      page.setFirstRecordOffset (firstRecordOffset);

    return page;
  }
}