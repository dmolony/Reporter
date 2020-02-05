package com.bytezone.reporter.reports;

import java.util.List;

import com.bytezone.reporter.file.ReportScore;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

// -----------------------------------------------------------------------------------//
public class HexReport extends DefaultReportMaker
// -----------------------------------------------------------------------------------//
{
  static final int HEX_LINE_SIZE = 16;
  boolean trial = true;

  // ---------------------------------------------------------------------------------//
  public HexReport (boolean newLine, boolean split)
  // ---------------------------------------------------------------------------------//
  {
    super ("HEX", newLine, split);
    weight = 0.1;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void createPages (ReportScore reportScore)
  // ---------------------------------------------------------------------------------//
  {
    List<Page> pages = reportScore.getPages ();
    List<Record> records = reportScore.recordMaker.getRecords ();

    pages.clear ();

    int recordNumber = 0;
    int firstRecord = 0;
    int ptr = 0;
    int lineNo = 0;

    while (recordNumber < records.size ())
    {
      //      System.out.printf ("%d %d%n", recordNumber, ptr);
      int recordLength = records.get (recordNumber).length;
      int max = Math.min (16, recordLength - ptr);
      lineNo++;
      ptr += max;

      // check for end of record
      if (ptr == recordLength)
      {
        ptr = 0;
        ++recordNumber;

        if (newlineBetweenRecords)
          ++lineNo;
      }

      if (lineNo >= pageSize)
      {
        if (ptr == 0)
          reportScore.addPage (firstRecord, recordNumber - 1);
        else
        {
          Page page = reportScore.addPage (firstRecord, recordNumber);
          page.setLastRecordOffset (ptr);
        }

        firstRecord = recordNumber;
        lineNo = 0;
      }
    }

    if (lineNo > 0)
      reportScore.addPage (firstRecord, recordNumber - 1);
  }

  //  @Override
  //  public void createPages (ReportScore reportScore)
  //  {
  //    if (trial)
  //    {
  //      createPages2 (reportScore);
  //      return;
  //    }
  //
  //    List<Page> pages = reportScore.getPages ();
  //    List<Record> records = reportScore.recordMaker.getRecords ();
  //
  //    pages.clear ();
  //
  //    int firstRecord = 0;
  //    int lineCount = 0;
  //
  //    for (int recordNumber = 0; recordNumber < records.size (); recordNumber++)
  //    {
  //      int recordLength = records.get (recordNumber).length;
  //      int lines = recordLength == 0 ? 1 : (recordLength - 1) / 16 + 1;
  //      //      System.out.printf ("lines: %d%n", lines);
  //
  //      if (lineCount + lines > pageSize)
  //      {
  //        int linesLeft = pageSize - lineCount;
  //        if (allowSplitRecords && linesLeft > 0)
  //        {
  //          Page page = reportScore.addPage (firstRecord, recordNumber);
  //          page.setLastRecordOffset (linesLeft * 74);
  //          lineCount = lines - linesLeft;
  //        }
  //        else
  //        {
  //          reportScore.addPage (firstRecord, recordNumber - 1);
  //          lineCount = lines;
  //        }
  //        firstRecord = recordNumber;
  //      }
  //      else
  //        lineCount += lines;
  //
  //      if (newlineBetweenRecords)
  //        lineCount++;
  //    }
  //
  //    reportScore.addPage (firstRecord, records.size () - 1);
  //  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String getFormattedRecord (ReportScore reportScore, Record record, int offset,
      int length)
  // ---------------------------------------------------------------------------------//
  {
    TextMaker textMaker = reportScore.textMaker;
    StringBuilder text = new StringBuilder ();
    int max = record.offset + offset + length;

    for (int ptr = record.offset + offset; ptr < max; ptr += HEX_LINE_SIZE)
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

  // ---------------------------------------------------------------------------------//
  @Override
  public String getFormattedRecord (ReportScore reportScore, Record record)
  // ---------------------------------------------------------------------------------//
  {
    return getFormattedRecord (reportScore, record, 0, record.length);
    //    TextMaker textMaker = reportScore.textMaker;
    //
    //    if (record.length == 0)
    //      return String.format ("%06X", record.offset);
    //
    //    StringBuilder text = new StringBuilder ();
    //
    //    int max = record.offset + record.length;
    //    for (int ptr = record.offset; ptr < max; ptr += HEX_LINE_SIZE)
    //    {
    //      StringBuilder hexLine = new StringBuilder ();
    //
    //      int lineMax = Math.min (ptr + HEX_LINE_SIZE, max);
    //      for (int linePtr = ptr; linePtr < lineMax; linePtr++)
    //        hexLine.append (String.format ("%02X ", record.buffer[linePtr] & 0xFF));
    //
    //      text.append (String.format ("%06X  %-48s %s%n", ptr, hexLine.toString (),
    //                                  textMaker.getText (record.buffer, ptr, lineMax - ptr)));
    //    }
    //
    //    if (text.length () > 0)
    //      text.deleteCharAt (text.length () - 1);
    //
    //    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean test (Record record, TextMaker textMaker)
  // ---------------------------------------------------------------------------------//
  {
    return true;
  }
}