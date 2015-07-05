package com.bytezone.reporter.application;

import java.util.List;

import com.bytezone.reporter.record.CrRecordMaker;
import com.bytezone.reporter.record.CrlfRecordMaker;
import com.bytezone.reporter.record.FbRecordMaker;
import com.bytezone.reporter.record.LfRecordMaker;

public class Splitter
{
  private final byte[] buffer;
  private CrlfRecordMaker crlf;
  private CrRecordMaker cr;
  private LfRecordMaker lf;
  private FbRecordMaker fb80;
  private FbRecordMaker fb132;

  enum RecordType
  {
    CR, CRLF, LF, RDW, VB, RVL, FB80, FB132, FBXX
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
        return cr.getRecords ();

      case LF:
        if (lf == null)
          lf = new LfRecordMaker (buffer);
        return lf.getRecords ();

      case FB80:
        if (fb80 == null)
          fb80 = new FbRecordMaker (buffer, 80);
        return fb80.getRecords ();

      case FB132:
        if (fb132 == null)
          fb132 = new FbRecordMaker (buffer, 132);
        return fb132.getRecords ();

      default:
        return null;
    }
  }
}