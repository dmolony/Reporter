package com.bytezone.reporter.reports;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;

public class HexReport extends Report
{
  static final int HEX_LINE_SIZE = 16;
  List<Integer> pageStarts;

  public HexReport (List<Record> records)
  {
    super (records);

    pageStarts = new ArrayList<> ();
    pageStarts.add (0);
    int lineCount = 0;

    for (int i = 0; i < records.size (); i++)
    {
      Record record = records.get (i);
      int lines = (record.length - 1) / 16 + 1;
      lineCount += lines;
      if (lineCount > pageSize)
      {
        lineCount = lines;
        pageStarts.add (i);
      }
    }
    pagination.setPageCount (pageStarts.size ());
  }

  @Override
  protected List<Record> getPageRecords (int page)
  {
    List<Record> pageRecords = new ArrayList<> (records.size ());
    if (page < 0 || page >= pageStarts.size ())
      return pageRecords;

    int first = pageStarts.get (page);
    int last =
        page == pageStarts.size () ? pageStarts.size () : pageStarts.get (page + 1);

    for (int line = first; line < last; line++)
      pageRecords.add (records.get (line));

    return pageRecords;
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
}