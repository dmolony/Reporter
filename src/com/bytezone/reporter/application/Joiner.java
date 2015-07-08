package com.bytezone.reporter.application;

import java.util.List;

import com.bytezone.reporter.record.CrRecordMaker;
import com.bytezone.reporter.record.CrlfRecordMaker;
import com.bytezone.reporter.record.FbRecordMaker;
import com.bytezone.reporter.record.LfRecordMaker;
import com.bytezone.reporter.record.NoRecordMaker;
import com.bytezone.reporter.record.NvbRecordMaker;
import com.bytezone.reporter.record.RavelRecordMaker;
import com.bytezone.reporter.record.RdwRecordMaker;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker.RecordType;
import com.bytezone.reporter.record.VbRecordMaker;

// this won't be needed

public class Joiner
{
  private final List<Record> records;

  private CrlfRecordMaker crlf;
  private CrRecordMaker cr;
  private LfRecordMaker lf;
  private FbRecordMaker fb80;
  private FbRecordMaker fb132;
  private FbRecordMaker fb252;
  private VbRecordMaker vb;
  private NvbRecordMaker nvb;
  private RdwRecordMaker rdw;
  private RavelRecordMaker ravel;
  private NoRecordMaker none;

  public Joiner (List<Record> records)
  {
    this.records = records;
  }

  public byte[] getBuffer (RecordType recordType)
  {
    switch (recordType)
    {
      case CRLF:
        if (crlf == null)
          crlf = new CrlfRecordMaker (records);
        return crlf.getBuffer ();

      case CR:
        if (cr == null)
          cr = new CrRecordMaker (records);
        return cr.getBuffer ();

      case LF:
        if (lf == null)
          lf = new LfRecordMaker (records);
        return lf.getBuffer ();

      case FB80:
        if (fb80 == null)
          fb80 = new FbRecordMaker (records, 80);
        return fb80.getBuffer ();

      case FB132:
        if (fb132 == null)
          fb132 = new FbRecordMaker (records, 132);
        return fb132.getBuffer ();

      case FB252:
        if (fb252 == null)
          fb252 = new FbRecordMaker (records, 252);
        return fb252.getBuffer ();

    }
    return null;
  }
}