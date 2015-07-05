package com.bytezone.reporter.application;

public class Splitter
{
  private final byte[] buffer;

  enum RecordType
  {
    CR, CRLF, LF, RDW, VB, RVL, FB
  }

  public Splitter (byte[] buffer)
  {
    this.buffer = buffer;
  }
}