package com.bytezone.reporter.reports;

import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;

public interface Report
{
  public void setTextMaker (TextMaker textMaker);

  public void setNewlineBetweenRecords (boolean value);

  public void setAllowSplitRecords (boolean value);

  public Pagination getPagination ();
}