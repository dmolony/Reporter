package com.bytezone.reporter.file;

import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

// -----------------------------------------------------------------------------------//
public class TextTester
// -----------------------------------------------------------------------------------//
{
  private final TextMaker textMaker;
  private int alphanumericBytes;
  private int bytesTested;

  // ---------------------------------------------------------------------------------//
  public TextTester (TextMaker textMaker)
  // ---------------------------------------------------------------------------------//
  {
    this.textMaker = textMaker;
  }

  // ---------------------------------------------------------------------------------//
  public void testRecords (List<Record> records)
  // ---------------------------------------------------------------------------------//
  {
    for (Record record : records)
    {
      alphanumericBytes += textMaker.countAlphanumericBytes (record);
      bytesTested += record.length;
    }
  }

  // ---------------------------------------------------------------------------------//
  public double getAlphanumericRatio ()
  // ---------------------------------------------------------------------------------//
  {
    return (double) alphanumericBytes / bytesTested * 100;
  }

  // ---------------------------------------------------------------------------------//
  public TextMaker getTextMaker ()
  // ---------------------------------------------------------------------------------//
  {
    return textMaker;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("%-6.6s %6.2f", textMaker, getAlphanumericRatio ());
  }
}