package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.application.Report;
import com.bytezone.reporter.record.Record;

public class HexReport extends Report
{
  static final int HEX_LINE_SIZE = 16;
  private final boolean newline;

  public HexReport (List<Record> records)
  {
    super (records);
    newline = true;
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    if (record.length == 0)
      return String.format ("%06X%s", record.offset, newline ? "\n" : "");

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

    if (text.length () > 0 && !newline)
      text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }
}