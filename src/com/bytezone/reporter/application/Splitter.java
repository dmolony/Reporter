package com.bytezone.reporter.application;

import java.util.List;

import com.bytezone.reporter.record.CrRecordMaker;
import com.bytezone.reporter.record.CrlfRecordMaker;
import com.bytezone.reporter.record.FbRecordMaker;
import com.bytezone.reporter.record.LfRecordMaker;
import com.bytezone.reporter.record.NoRecordMaker;
import com.bytezone.reporter.record.RavelRecordMaker;
import com.bytezone.reporter.record.RdwRecordMaker;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.VbRecordMaker;

public class Splitter
{
  private final byte[] buffer;

  private CrlfRecordMaker crlf;
  private CrRecordMaker cr;
  private LfRecordMaker lf;
  private FbRecordMaker fb80;
  private FbRecordMaker fb132;
  private FbRecordMaker fbxx;
  private VbRecordMaker vb;
  private RdwRecordMaker rdw;
  private RavelRecordMaker ravel;
  private NoRecordMaker none;

  enum RecordType
  {
    CR, CRLF, LF, RDW, VB, RVL, FB80, FB132, FBXX, NONE
  }

  public Splitter (byte[] buffer)
  {
    this.buffer = buffer;
  }

  List<Record> getFastRecords (RecordType recordType)
  {
    switch (recordType)
    {
      case CRLF:
        if (crlf == null)
          crlf = new CrlfRecordMaker (buffer);
        return crlf.getFastRecords ();

      case CR:
        if (cr == null)
          cr = new CrRecordMaker (buffer);
        return cr.getFastRecords ();

      case LF:
        if (lf == null)
          lf = new LfRecordMaker (buffer);
        return lf.getFastRecords ();

      case VB:
        if (vb == null)
          vb = new VbRecordMaker (buffer);
        return vb.getFastRecords ();

      case RDW:
        if (rdw == null)
          rdw = new RdwRecordMaker (buffer);
        return rdw.getFastRecords ();

      case RVL:
        if (ravel == null)
          ravel = new RavelRecordMaker (buffer);
        return ravel.getFastRecords ();

      case FB80:
        if (fb80 == null)
          fb80 = new FbRecordMaker (buffer, 80);
        return fb80.getFastRecords ();

      case FB132:
        if (fb132 == null)
          fb132 = new FbRecordMaker (buffer, 132);
        return fb132.getFastRecords ();

      case FBXX:
        if (fbxx == null)
          fbxx = new FbRecordMaker (buffer, 252);       // how to specify this?
        return fbxx.getFastRecords ();

      case NONE:
        if (none == null)
          none = new NoRecordMaker (buffer);
        return none.getFastRecords ();

      default:
        return null;
    }
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

      case VB:
        if (vb == null)
          vb = new VbRecordMaker (buffer);
        return vb.getRecords ();

      case RDW:
        if (rdw == null)
          rdw = new RdwRecordMaker (buffer);
        return rdw.getRecords ();

      case RVL:
        if (ravel == null)
          ravel = new RavelRecordMaker (buffer);
        return ravel.getRecords ();

      case FB80:
        if (fb80 == null)
          fb80 = new FbRecordMaker (buffer, 80);
        return fb80.getRecords ();

      case FB132:
        if (fb132 == null)
          fb132 = new FbRecordMaker (buffer, 132);
        return fb132.getRecords ();

      case FBXX:
        if (fbxx == null)
          fbxx = new FbRecordMaker (buffer, 252);       // how to specify this?
        return fbxx.getRecords ();

      case NONE:
        if (none == null)
          none = new NoRecordMaker (buffer);
        return none.getRecords ();

      default:
        return null;
    }
  }
}