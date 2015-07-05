package com.bytezone.reporter.application;

import java.util.List;

import com.bytezone.reporter.record.CrRecordMaker;
import com.bytezone.reporter.record.CrlfRecordMaker;
import com.bytezone.reporter.record.LfRecordMaker;

public class Splitter
{
  private final byte[] buffer;
  private CrlfRecordMaker crlf;
  private CrRecordMaker cr;
  private LfRecordMaker lf;

  enum RecordType
  {
    CR, CRLF, LF, RDW, VB, RVL, FB
  }

  public Splitter (byte[] buffer)
  {
    this.buffer = buffer;
  }

  List<byte[]> getRecords (RecordType recordType)
  {
    switch (recordType)
    {
      case CRLF:
        if (crlf == null)
          crlf = new CrlfRecordMaker (buffer);
        return crlf.getRecords ();

      case CR:
        if (cr == null)
          cr = new CrRecordMaker (buffer);
        return crlf.getRecords ();

      case LF:
        if (lf == null)
          lf = new LfRecordMaker (buffer);
        return lf.getRecords ();

      default:
        return null;
    }
  }
}