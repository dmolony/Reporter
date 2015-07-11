package com.bytezone.reporter.application;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

public abstract class Report
{
  //  private RecordFormatter recordFormatter;
  protected TextMaker textMaker;

  private final List<Record> records;

  public Report (List<Record> records)
  {

    this.records = records;

    //    if (false)
    //      for (Record record : records)
    //      {
    //        if (record.length > 0)
    //        {
    //          boolean ascii = asciiTextMaker.test (record);
    //          boolean ebcdic = ebcdicTextMaker.test (record);
    //          System.out.printf ("%-6s %-6s %n", ascii ? "ascii" : "",
    //                             ebcdic ? "ebcdic" : "");
    //        }
    //      }
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
      formattedRecords.add (getFormattedRecord (record));

    return formattedRecords;
  }

  //  public void setTextMaker (EncodingType encodingType)
  //  {
  //    switch (encodingType)
  //    {
  //      case ASCII:
  //        setTextMaker (asciiTextMaker);
  //        break;
  //
  //      case EBCDIC:
  //        setTextMaker (ebcdicTextMaker);
  //        break;
  //    }
  //  }

  public void setTextMaker (TextMaker textMaker)
  {
    //    hexFormatter.setTextMaker (textMaker);
    //    stringFormatter.setTextMaker (textMaker);
    //    natloadFormatter.setTextMaker (textMaker);
    this.textMaker = textMaker;
  }

  //  public void setFormatter (FormatType formatType)
  //  {
  //    switch (formatType)
  //    {
  //      case HEX:
  //        recordFormatter = hexFormatter;
  //        break;
  //      case TEXT:
  //        recordFormatter = stringFormatter;
  //        break;
  //      case NATLOAD:
  //        recordFormatter = natloadFormatter;
  //        break;
  //    }
  //  }

  public abstract String getFormattedRecord (Record record);
}