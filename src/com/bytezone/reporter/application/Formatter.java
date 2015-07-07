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

  private final RecordFormatter hexFormatter = new HexFormatter ();
  private final RecordFormatter stringFormatter = new StringFormatter ();
  private final RecordFormatter natloadFormatter = new NatloadFormatter ();

  private final TextMaker asciiTextMaker = new AsciiTextMaker ();
  private final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();

  private List<byte[]> records;
  private List<Record> fastRecords;

  enum FormatType
  {
    HEX, TEXT, NATLOAD
  }

  enum EncodingType
  {
    ASCII, EBCDIC
  }

  public void setRecords (List<byte[]> records)
  {
    this.records = records;
  }

  public void setFastRecords (List<Record> fastRecords)
  {
    this.fastRecords = fastRecords;
  }

  // public List<String> getFormattedRecords ()
  // {
  // List<String> formattedRecords = new ArrayList<> (records.size ());
  //
  // for (byte[] record : records)
  // formattedRecords.add (recordFormatter.getFormattedRecord (record));
  //
  // return formattedRecords;
  // }

  public List<String> getFormattedFastRecords ()
  {
    List<String> formattedRecords = new ArrayList<> (fastRecords.size ());

    for (Record fastRecord : fastRecords)
      formattedRecords.add (recordFormatter.getFormattedFastRecord (fastRecord));

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

  // public String getFormattedRecord (byte[] record)
  // {
  // return recordFormatter.getFormattedRecord (record);
  // }
}