package com.bytezone.reporter.record;

import java.util.List;

public interface RecordMaker
{
  public void setRecords (List<Record> records);

  public void setBuffer (byte[] buffer);

  public List<Record> getRecords ();

  public byte[] getBuffer ();

  //  public List<Record> test (byte[] buffer);
  public List<Record> test ();

  //  public List<Record> test (byte[] buffer, int offset, int length);
  public List<Record> test (int length);
}