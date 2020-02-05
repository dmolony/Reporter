package com.bytezone.reporter.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

// -----------------------------------------------------------------------------------//
public class MainframeFile
// -----------------------------------------------------------------------------------//
{
  private final String name;
  private byte[] buffer;
  private File file;
  //  private List<ReportScore> scores;

  // ---------------------------------------------------------------------------------//
  public MainframeFile (String name, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    this.name = name;
    this.buffer = buffer;
  }

  // ---------------------------------------------------------------------------------//
  public MainframeFile (File file)
  // ---------------------------------------------------------------------------------//
  {
    this.file = file;
    this.name = file.getName ();
  }

  // ---------------------------------------------------------------------------------//
  private void analyse ()
  // ---------------------------------------------------------------------------------//
  {
    if (buffer == null && file != null)
      readFile ();
  }

  // ---------------------------------------------------------------------------------//
  private void readFile ()
  // ---------------------------------------------------------------------------------//
  {
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

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("File name ....... %s%n", name));
    text.append (String.format ("Size ............ %,d%n",
        buffer == null ? file.length () : buffer.length));

    return text.toString ();
  }
}