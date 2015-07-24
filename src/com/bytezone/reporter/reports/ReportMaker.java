package com.bytezone.reporter.reports;

import java.awt.print.Printable;
import java.util.List;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;

public interface ReportMaker extends Printable
{
  public boolean test (Record record, TextMaker textMaker);

  public void setRecords (List<Record> records);

  public void setTextMaker (TextMaker textMaker);

  public void setNewlineBetweenRecords (boolean value);

  public void setAllowSplitRecords (boolean value);

  public Pagination createPagination ();
}