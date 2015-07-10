package com.bytezone.reporter.format;

import com.bytezone.reporter.text.TextMaker;

public abstract class DefaultFormatter implements RecordFormatter
{
  protected TextMaker textMaker;

  protected boolean newline;

  @Override
  public void setTextMaker (TextMaker textMaker)
  {
    this.textMaker = textMaker;
  }

  @Override
  public void setNewline (boolean newline)
  {
    this.newline = newline;
  }
}