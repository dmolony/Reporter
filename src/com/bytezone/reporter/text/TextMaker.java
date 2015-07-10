package com.bytezone.reporter.text;

import com.bytezone.reporter.record.Record;

public interface TextMaker
{
  public String getText (byte[] buffer, int offset, int length);

  public boolean test (byte[] buffer, int offset, int length);

  public boolean test (Record record);
}