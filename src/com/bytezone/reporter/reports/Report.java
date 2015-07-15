package com.bytezone.reporter.reports;

import java.awt.print.Printable;

import com.bytezone.reporter.text.TextMaker;

import javafx.scene.control.Pagination;

public interface Report extends Printable
{
  public void setTextMaker (TextMaker textMaker);

  public void setNewlineBetweenRecords (boolean value);

  public void setAllowSplitRecords (boolean value);

  public Pagination getPagination ();

  public boolean test ();
}