package com.bytezone.reporter.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.text.TextMaker;

public class MainframeFile
{
  private final String name;
  private byte[] buffer;
  private File file;

  private TextMaker textMaker;
  private RecordMaker recordMaker;
  private ReportMaker reportMaker;

  public MainframeFile (String name, byte[] buffer)
  {
    this.name = name;
    this.buffer = buffer;
  }

  public MainframeFile (File file)
  {
    this.file = file;
    this.name = file.getName ();
  }

  private void readFile ()
  {
    assert buffer == null;
    try
    {
      buffer = Files.readAllBytes (file.toPath ());
    }
    catch (IOException e)
    {
      e.printStackTrace ();
      buffer = new byte[0];
    }
  }

  private void analyse ()
  {
    if (buffer == null)
      readFile ();
  }

  @Override
  public String toString ()
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("File name ....... %s%n", name));
    text.append (String.format ("Size ............ %,d%n",
                                buffer == null ? file.length () : buffer.length));

    return text.toString ();
  }
}