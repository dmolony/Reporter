package com.bytezone.reporter.reports;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

public class NatloadReport extends DefaultReport
{
  @Override
  protected void paginate ()
  {
    pages.clear ();

    int firstRecord = 0;
    int lineCount = 0;

    for (int i = 0; i < records.size (); i++)
    {
      Record record = records.get (i);
      if (record.buffer[record.offset] == (byte) 0xFF
          || record.buffer[record.offset + 1] == (byte) 0xFF)
        continue;

      if (record.buffer[record.offset] == 0 && record.buffer[record.offset + 1] == 0)
        continue;

      // test for Module Header Record
      if ((record.buffer[record.offset] & 0xFF) > 0x95
          || (record.buffer[record.offset + 1] & 0xFF) > 0x95)
      {
        int sequence = (record.buffer[record.offset + 16] & 0xFF) << 8;
        sequence |= record.buffer[record.offset + 17] & 0xFF;
        if (sequence > 1)
          continue;
        if (lineCount > 0)
        {
          pages.add (new Page (records, firstRecord, i - 1));
          firstRecord = i;
          lineCount = 0;
        }
      }
      ++lineCount;
      if (lineCount > pageSize)
      {
        pages.add (new Page (records, firstRecord, i - 1));
        firstRecord = i;
        lineCount = 0;
      }
    }

    if (firstRecord < records.size () - 1)
      pages.add (new Page (records, firstRecord, records.size () - 1));
  }

  @Override
  public String getFormattedRecord (Record record)
  {
    if (record.buffer[record.offset] == (byte) 0xFF
        || record.buffer[record.offset + 1] == (byte) 0xFF)
      return null;

    if (record.buffer[record.offset] == 0 && record.buffer[record.offset + 1] == 0)
      return null;

    // test for Module Header Record
    if ((record.buffer[record.offset] & 0xFF) > 0x95
        || (record.buffer[record.offset + 1] & 0xFF) > 0x95)
    {
      String library = textMaker.getText (record.buffer, record.offset, 8);
      String program = textMaker.getText (record.buffer, record.offset + 8, 8);
      int sequence = (record.buffer[record.offset + 16] & 0xFF) << 8;
      sequence |= record.buffer[record.offset + 17] & 0xFF;
      if (sequence != 1)
        return null;
      String lines = textMaker.getText (record.buffer, record.offset + 18, 3);
      return String.format ("Library: %-8s  Program: %-8s  Seq: %2d  Lines: %s", library,
                            program, sequence, lines);
    }

    int length = record.length - record.countTrailingNulls ();
    return String.format ("%02X%02X %s", record.buffer[record.offset] & 0xFF,
                          record.buffer[record.offset + 1] & 0xFF, textMaker
                              .getText (record.buffer, record.offset + 2, length - 2))
        .trim ();
  }

  @Override
  public boolean test (Record record, TextMaker textMaker)
  {
    Record firstRecord = records.get (0);
    System.out.println (firstRecord);
    return false;
  }
}