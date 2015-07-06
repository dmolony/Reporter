package com.bytezone.reporter.format;

public class NatloadFormatter extends DefaultFormatter
{

  @Override
  public String getFormattedRecord (byte[] buffer)
  {
    if (buffer[0] == (byte) 0xFF || buffer[1] == (byte) 0xFF)
      return ".";

    if (buffer[0] == 0 && buffer[1] == 0)
      return "..";

    if ((buffer[0] & 0xFF) > 0x95 || (buffer[1] & 0xFF) > 0x95)
    {
      String library = textMaker.getText (buffer, 0, 8);
      String program = textMaker.getText (buffer, 8, 8);
      int sequence = (buffer[16] & 0xFF) << 8;
      sequence |= buffer[17] & 0xFF;
      String lines = textMaker.getText (buffer, 18, 3);
      return String.format ("Library: %-8s  Program: %-8s  Seq: %2d  Lines: %s", library,
                            program, sequence, lines);
    }

    return String.format ("%02X%02X %s", buffer[0] & 0xFF, buffer[1] & 0xFF,
                          textMaker.getText (buffer, 2, buffer.length - 2))
        .trim ();
  }
}