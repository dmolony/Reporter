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
    records.add (buffer);
  }
}