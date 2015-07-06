package com.bytezone.reporter.format;

import com.bytezone.reporter.text.TextMaker;

public abstract class DefaultFormatter implements RecordFormatter
{
  TextMaker textMaker;

  @Override
  public void setTextMaker (TextMaker textMaker)
  {
    this.textMaker = textMaker;
  }
}