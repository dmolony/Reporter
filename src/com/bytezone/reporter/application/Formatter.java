package com.bytezone.reporter.application;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.format.HexFormatter;
import com.bytezone.reporter.format.NatloadFormatter;
import com.bytezone.reporter.format.RecordFormatter;
import com.bytezone.reporter.format.StringFormatter;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

public class Formatter
{
  private RecordFormatter recordFormatter;

  private final TextMaker asciiTextMaker = new AsciiTextMaker ();
  private final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();

  private final RecordFormatter hexFormatter = new HexFormatter ();
  private final RecordFormatter stringFormatter = new StringFormatter ();
  private final RecordFormatter natloadFormatter = new NatloadFormatter ();

  private List<Record> records;

  enum EncodingType// TextMaker
  {
    ASCII, EBCDIC
  }

  enum FormatType// RecordFormatter
  {
    HEX, TEXT, NATLOAD
  }

  public void setRecords (List<Record> records)
  {
    this.records = records;

    for (Record record : records)
    {
      if (record.length > 0)
      {
        boolean ascii = asciiTextMaker.test (record);
        boolean ebcdic = ebcdicTextMaker.test (record);
        System.out.printf ("%-6s %-6s %n", ascii ? "ascii" : "", ebcdic ? "ebcdic" : "");
      }
    }
  }

  public String getFormattedText ()
  {
    StringBuilder text = new StringBuilder ();
    for (String record : getFormattedRecords ())
    {
      text.append (record);
      text.append ('\n');
    }

    // remove trailing newlines
    while (text.length () > 0 && text.charAt (text.length () - 1) == '\n')
      text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }

  private List<String> getFormattedRecords ()
  {
    List<String> formattedRecords = new ArrayList<> (records.size ());

    for (Record record : records)
      formattedRecords.add (recordFormatter.getFormattedRecord (record));

    return formattedRecords;
  }

  public void setTextMaker (EncodingType encodingType)
  {
    switch (encodingType)
    {
      case ASCII:
        setTextMaker (asciiTextMaker);
        break;

      case EBCDIC:
        setTextMaker (ebcdicTextMaker);
        break;
    }
  }

  private void setTextMaker (TextMaker textMaker)
  {
    hexFormatter.setTextMaker (textMaker);
    stringFormatter.setTextMaker (textMaker);
    natloadFormatter.setTextMaker (textMaker);
  }

  public void setFormatter (FormatType formatType)
  {
    switch (formatType)
    {
      case HEX:
        recordFormatter = hexFormatter;
        break;
      case TEXT:
        recordFormatter = stringFormatter;
        break;
      case NATLOAD:
        recordFormatter = natloadFormatter;
        break;
    }
  }
}