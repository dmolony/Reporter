package com.bytezone.reporter.application;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

public class TextTester
{
  TextMaker textMaker;
  int badBytes;

  public TextTester (TextMaker textMaker)
  {
    this.textMaker = textMaker;
  }

  public void testRecord (Record record)
  {
    badBytes += textMaker.countBadBytes (record);
  }
}