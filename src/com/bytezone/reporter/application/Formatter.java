package com.bytezone.reporter.application;

import com.bytezone.reporter.format.HexFormatter;
import com.bytezone.reporter.format.NatloadFormatter;
import com.bytezone.reporter.format.RecordFormatter;
import com.bytezone.reporter.format.StringFormatter;
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

  enum FormatType
  {
    HEX, TEXT, NATLOAD
  }

  enum EncodingType
  {
    ASCII, EBCDIC
  }

  public void setTextMaker (EncodingType encodingType)
  {
    switch (encodingType)
    {
      case ASCII:
        hexFormatter.setTextMaker (asciiTextMaker);
        stringFormatter.setTextMaker (asciiTextMaker);
        natloadFormatter.setTextMaker (asciiTextMaker);
        break;

      case EBCDIC:
        hexFormatter.setTextMaker (ebcdicTextMaker);
        stringFormatter.setTextMaker (ebcdicTextMaker);
        natloadFormatter.setTextMaker (ebcdicTextMaker);
        break;
    }
  }

  private void setTextMaker (TextMaker textMaker)
  {

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

  public String getFormattedRecord (byte[] record)
  {
    return recordFormatter.getFormattedRecord (record);
  }
}