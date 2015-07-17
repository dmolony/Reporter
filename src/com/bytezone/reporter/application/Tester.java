package com.bytezone.reporter.application;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.text.TextMaker;

public class Tester
{
  String name;
  RecordMaker recordMaker;
  byte[] buffer;
  int testSize;

  List<Record> records;
  List<TextTester> textTesters = new ArrayList<> ();

  public Tester (String name, RecordMaker recordMaker, byte[] buffer, int testSize)
  {
    this.name = name;
    this.recordMaker = recordMaker;
    this.buffer = buffer;
    this.testSize = testSize;

    records = recordMaker.test (buffer, 0, testSize);
  }

  public int countBadBytes (TextMaker textMaker)
  {
    TextTester textTester = new TextTester (textMaker);
    textTesters.add (textTester);

    for (Record record : records)
      textTester.badBytes += textMaker.countBadBytes (record);

    return textTester.badBytes;
  }

  public TextMaker getPreferredTextMaker ()
  {
    int max = Integer.MAX_VALUE;
    TextMaker preferredTextMaker = null;
    for (TextTester textTester : textTesters)
    {
      if (textTester.badBytes < max)
      {
        max = textTester.badBytes;
        preferredTextMaker = textTester.textMaker;
      }
    }
    return preferredTextMaker;
  }

  @Override
  public String toString ()
  {
    StringBuilder text = new StringBuilder ();
    text.append (String.format ("%-8s %,5d", name, records.size ()));
    for (TextTester textTester : textTesters)
      text.append (String.format ("  %,5d", textTester.badBytes));

    TextMaker preferredTextMaker = getPreferredTextMaker ();
    String textMaker = preferredTextMaker == null ? "" : preferredTextMaker.toString ();
    text.append ("  " + textMaker);
    return text.toString ();
  }
}