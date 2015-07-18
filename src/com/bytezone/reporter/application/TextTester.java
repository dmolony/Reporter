package com.bytezone.reporter.application;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

public class TextTester
{
  TextMaker textMaker;
  int alphanumericBytes;
  int bytesTested;

  public TextTester (TextMaker textMaker)
  {
    this.textMaker = textMaker;
  }

  public void testRecord (Record record)
  {
    alphanumericBytes += textMaker.countAlphanumericBytes (record);
    bytesTested += record.length;
  }

  public double getAlphanumericRatio ()
  {
    return (double) alphanumericBytes / bytesTested * 100;
  }

  @Override
  public String toString ()
  {
    return String.format ("%-6.6s %6.2f", textMaker, getAlphanumericRatio ());
  }
}