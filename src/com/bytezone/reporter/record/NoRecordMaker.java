package com.bytezone.reporter.record;

public class NoRecordMaker extends DefaultRecordMaker
{
  public NoRecordMaker (byte[] buffer)
  {
    super (buffer);
  }

  @Override
  protected void split ()
  {
    records.add (new Record (buffer, 0, buffer.length, 0, buffer.length));
  }
}