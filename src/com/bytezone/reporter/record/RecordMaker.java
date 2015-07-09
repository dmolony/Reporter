package com.bytezone.reporter.record;

import java.util.List;

public interface RecordMaker
{
  public List<Record> getRecords ();

  public byte[] getBuffer ();

  public List<Record> test (int testBufferSize);

  //  public boolean test ();
}