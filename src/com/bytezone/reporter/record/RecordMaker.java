package com.bytezone.reporter.record;

import java.util.List;

public interface RecordMaker
{
  enum RecordType
  {
    CR, CRLF, LF, RDW, VB, RVL, FB80, FB132, FB252, NONE, NVB
  }

  List<Record> getRecords ();

  byte[] getBuffer ();
}