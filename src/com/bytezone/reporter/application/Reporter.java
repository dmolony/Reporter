package com.bytezone.reporter.application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.bytezone.reporter.format.ASAReport;
import com.bytezone.reporter.format.HexReport;
import com.bytezone.reporter.format.NatloadReport;
import com.bytezone.reporter.format.TextReport;
import com.bytezone.reporter.record.CrRecordMaker;
import com.bytezone.reporter.record.CrlfRecordMaker;
import com.bytezone.reporter.record.FbRecordMaker;
import com.bytezone.reporter.record.LfRecordMaker;
import com.bytezone.reporter.record.NoRecordMaker;
import com.bytezone.reporter.record.NvbRecordMaker;
import com.bytezone.reporter.record.RavelRecordMaker;
import com.bytezone.reporter.record.RdwRecordMaker;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.record.VbRecordMaker;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Reporter extends Application
{
  private static final String[] fontNames =
      { "Andale Mono", "Anonymous Pro", "Consolas", "Courier New", "DejaVu Sans Mono",
        "Hermit", "IBM 3270", "IBM 3270 Narrow", "Inconsolata", "Input Mono",
        "Input Mono Narrow", "Luculent", "Menlo", "Monaco", "M+ 1m", "Panic Sans",
        "PT Mono", "Source Code Pro", "Ubuntu Mono", "Monospaced" };

  private static final String[] files =
      { "MOLONYD.NCD", "password.txt", "denis-000.src", "denis-005.src", "SMLIB-001.src",
        "smutlib001.src", "DBALIB.SRC", "listcat.txt", "iehlist2.txt", "iehlist3.txt",
        "out-idcams-listcat.txt", "jcl-idcams-listcat.txt", "iehlist.txt" };

  private static final String[] types =
      { "FB252", "LF", "CRLF", "RAV", "VB", "RDW", "NVB", "FB132", "FB132", "CRLF",
        "FB132", "FB80", "FB132" };

  private static final String[] encodings =
      { "E", "A", "E", "E", "E", "E", "E", "E", "E", "A", "E", "E", "E" };

  private static final String[] formats =
      { "N", "T", "N", "N", "N", "N", "N", "T", "T", "T", "T", "T", "T" };

  enum EncodingType
  {
    ASCII, EBCDIC
  }

  enum FormatType
  {
    HEX, TEXT, NATLOAD, ASA
  }
  private final TextMaker asciiTextMaker = new AsciiTextMaker ();
  private final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();

  private Report hexReport;
  private Report textReport;
  private Report natloadReport;
  private Report asaReport;

  private final TextArea textArea = new TextArea ();
  private WindowSaver windowSaver;
  private Preferences prefs;

  private final ToggleGroup splitterGroup = new ToggleGroup ();
  private RadioButton btnCrlf;
  private RadioButton btnCr;
  private RadioButton btnLf;
  private RadioButton btnFb80;
  private RadioButton btnFb132;
  private RadioButton btnFb252;
  private RadioButton btnVB;
  private RadioButton btnNvb;
  private RadioButton btnRDW;
  private RadioButton btnRavel;
  private RadioButton btnNoSplit;

  private final ToggleGroup encodingGroup = new ToggleGroup ();
  private RadioButton btnAscii;
  private RadioButton btnEbcdic;

  private final ToggleGroup formattingGroup = new ToggleGroup ();
  private RadioButton btnText;
  private RadioButton btnHex;
  private RadioButton btnNatload;
  private RadioButton btnAsa;

  private final ToggleGroup pagingGroup = new ToggleGroup ();
  private RadioButton btnNoPaging;
  private RadioButton btn66;
  private RadioButton btnOther;

  private List<Record> records;

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    int choice = 10;
    Path currentPath = Paths.get (home + files[choice]);

    long fileLength = currentPath.toFile ().length ();
    byte[] buffer = Files.readAllBytes (currentPath);

    int max = 120_000;
    if (fileLength > max)
    {
      System.out.printf ("Reducing buffer to %,d%n", max);
      byte[] shortBuffer = new byte[max];
      System.arraycopy (buffer, 0, shortBuffer, 0, max);
      buffer = shortBuffer;
    }

    System.out.println ("-----------------------------------------------------");
    for (int i = 0; i < files.length; i++)
    {
      Path path = Paths.get (home + files[i]);
      long length = path.toFile ().length ();
      System.out.printf ("%s %2d  %-5s  %s  %s  %-22s %,11d%n", (choice == i) ? "*" : " ",
                         i, types[i], encodings[i], formats[i], files[i], length);
    }
    System.out.println ("-----------------------------------------------------");

    textArea.setFont (Font.font (fontNames[18], FontWeight.NORMAL, 14));
    textArea.setEditable (false);

    EventHandler<ActionEvent> rebuild = e -> createRecords ();
    EventHandler<ActionEvent> setText = e -> setText ();

    VBox vbox1 = new VBox (10);
    vbox1.setPadding (new Insets (10));

    RecordMaker crlf = new CrlfRecordMaker (buffer);
    RecordMaker cr = new CrRecordMaker (buffer);
    RecordMaker lf = new LfRecordMaker (buffer);
    RecordMaker fb80 = new FbRecordMaker (buffer, 80);
    RecordMaker fb132 = new FbRecordMaker (buffer, 132);
    RecordMaker fb252 = new FbRecordMaker (buffer, 252);
    RecordMaker vb = new VbRecordMaker (buffer);
    RecordMaker nvb = new NvbRecordMaker (buffer);
    RecordMaker rdw = new RdwRecordMaker (buffer);
    RecordMaker ravel = new RavelRecordMaker (buffer);
    RecordMaker none = new NoRecordMaker (buffer);

    btnNoSplit = addRecordTypeButton (none, "None", splitterGroup, rebuild);
    btnNoSplit.setSelected (true);
    btnCrlf = addRecordTypeButton (crlf, "CRLF", splitterGroup, rebuild);
    btnCr = addRecordTypeButton (cr, "CR", splitterGroup, rebuild);
    btnLf = addRecordTypeButton (lf, "LF", splitterGroup, rebuild);
    btnVB = addRecordTypeButton (vb, "VB", splitterGroup, rebuild);
    btnRDW = addRecordTypeButton (rdw, "RDW", splitterGroup, rebuild);
    btnRavel = addRecordTypeButton (ravel, "Ravel", splitterGroup, rebuild);
    btnFb80 = addRecordTypeButton (fb80, "FB80", splitterGroup, rebuild);
    btnFb132 = addRecordTypeButton (fb132, "FB132", splitterGroup, rebuild);
    btnFb252 = addRecordTypeButton (fb252, "FB252", splitterGroup, rebuild);
    btnNvb = addRecordTypeButton (nvb, "NVB", splitterGroup, rebuild);

    RadioButton[] testableButtons =
        { btnCrlf, btnLf, btnCr, btnVB, btnRDW, btnNvb, btnRavel };
    int maxRecords = 0;
    for (RadioButton button : testableButtons)
    {
      int recordsFound = ((RecordMaker) button.getUserData ()).test (1024).size ();
      if (recordsFound <= 2)
        button.setDisable (true);
      else
      {
        if (recordsFound > maxRecords)
        {
          button.setSelected (true);
          maxRecords = recordsFound;
        }
      }
    }

    if (fileLength % 80 != 0)
      btnFb80.setDisable (true);
    else if (maxRecords < 2)
      btnFb80.setSelected (true);

    if (fileLength % 132 != 0)
      btnFb132.setDisable (true);
    else if (maxRecords < 2)
      btnFb132.setSelected (true);

    if (fileLength % 252 != 0)
      btnFb252.setDisable (true);
    else if (maxRecords < 2)
      btnFb252.setSelected (true);

    max = Math.min (1000, buffer.length);
    int hex20 = 0;
    int hex40 = 0;
    for (int ptr = 0; ptr < max; ptr++)
    {
      if (buffer[ptr] == 0x20)
        ++hex20;
      else if (buffer[ptr] == 0x40)
        ++hex40;
    }

    vbox1.getChildren ().addAll (btnNoSplit, btnCrlf, btnCr, btnLf, btnVB, btnNvb, btnRDW,
                                 btnRavel, btnFb80, btnFb132, btnFb252);

    VBox vbox2 = new VBox (10);
    vbox2.setPadding (new Insets (10));

    btnAscii =
        addEncodingTypeButton ("ASCII", encodingGroup, setText, EncodingType.ASCII);
    btnEbcdic =
        addEncodingTypeButton ("EBCDIC", encodingGroup, setText, EncodingType.EBCDIC);
    vbox2.getChildren ().addAll (btnAscii, btnEbcdic);

    if (hex20 > hex40)
      btnAscii.setSelected (true);
    else
      btnEbcdic.setSelected (true);

    VBox vbox3 = new VBox (10);
    vbox3.setPadding (new Insets (10));

    btnText = addFormatTypeButton ("Text", formattingGroup, setText, FormatType.TEXT);
    btnHex = addFormatTypeButton ("Binary", formattingGroup, setText, FormatType.HEX);
    btnNatload =
        addFormatTypeButton ("NatLoad", formattingGroup, setText, FormatType.NATLOAD);
    btnAsa = addFormatTypeButton ("ASA", formattingGroup, setText, FormatType.ASA);
    vbox3.getChildren ().addAll (btnHex, btnText, btnNatload, btnAsa);

    btnHex.setSelected (true);

    VBox vbox4 = new VBox (10);
    vbox4.setPadding (new Insets (10));

    btnNoPaging = addRadioButton ("None", pagingGroup, setText);
    btnNoPaging.setSelected (true);
    btn66 = addRadioButton ("66", pagingGroup, setText);
    btnOther = addRadioButton ("Other", pagingGroup, setText);
    vbox4.getChildren ().addAll (btnNoPaging, btn66, btnOther);

    VBox vbox = new VBox ();

    addTitledPane ("Records", vbox1, vbox);
    addTitledPane ("Encoding", vbox2, vbox);
    addTitledPane ("Formatting", vbox3, vbox);
    addTitledPane ("Paging", vbox4, vbox);

    BorderPane borderPane = new BorderPane ();
    borderPane.setCenter (textArea);
    borderPane.setRight (vbox);

    Scene scene = new Scene (borderPane, 800, 592);
    primaryStage.setTitle ("Reporter");
    primaryStage.setScene (scene);
    primaryStage.setOnCloseRequest (e -> closeWindow ());

    prefs = Preferences.userNodeForPackage (this.getClass ());
    windowSaver = new WindowSaver (prefs, primaryStage, "Reporter");
    if (!windowSaver.restoreWindow ())
      primaryStage.centerOnScreen ();

    createRecords ();
    primaryStage.show ();
  }

  private TitledPane addTitledPane (String text, Node contents, VBox parent)
  {
    TitledPane titledPane = new TitledPane (text, contents);
    titledPane.setCollapsible (false);
    parent.getChildren ().add (titledPane);
    return titledPane;
  }

  private RadioButton addRecordTypeButton (RecordMaker recordMaker, String text,
      ToggleGroup group, EventHandler<ActionEvent> evt)
  {
    RadioButton button = addRadioButton (text, group, evt);
    button.setUserData (recordMaker);
    return button;
  }

  private RadioButton addEncodingTypeButton (String text, ToggleGroup group,
      EventHandler<ActionEvent> evt, EncodingType encodingType)
  {
    RadioButton button = addRadioButton (text, group, evt);
    button.setUserData (encodingType);
    return button;
  }

  private RadioButton addFormatTypeButton (String text, ToggleGroup group,
      EventHandler<ActionEvent> evt, FormatType formatType)
  {
    RadioButton button = addRadioButton (text, group, evt);
    button.setUserData (formatType);
    return button;
  }

  private RadioButton addRadioButton (String text, ToggleGroup group,
      EventHandler<ActionEvent> evt)
  {
    RadioButton button = new RadioButton (text);
    button.setToggleGroup (group);
    button.setOnAction (evt);
    return button;
  }

  private void createRecords ()
  {
    RadioButton btn = (RadioButton) splitterGroup.getSelectedToggle ();
    records = ((RecordMaker) btn.getUserData ()).getRecords ();
    //    formatter = new Report (records);
    hexReport = new HexReport (records);
    textReport = new TextReport (records);
    natloadReport = new NatloadReport (records);
    asaReport = new ASAReport (records);
    spaceReport ();
    setText ();
  }

  private void setText ()
  {
    RadioButton btn = (RadioButton) encodingGroup.getSelectedToggle ();
    EncodingType encodingType = (EncodingType) btn.getUserData ();

    btn = (RadioButton) formattingGroup.getSelectedToggle ();
    FormatType formatType = (FormatType) btn.getUserData ();

    switch (encodingType)
    {
      case EBCDIC:
        hexReport.setTextMaker (ebcdicTextMaker);
        textReport.setTextMaker (ebcdicTextMaker);
        natloadReport.setTextMaker (ebcdicTextMaker);
        asaReport.setTextMaker (ebcdicTextMaker);
        break;

      case ASCII:
        hexReport.setTextMaker (asciiTextMaker);
        textReport.setTextMaker (asciiTextMaker);
        natloadReport.setTextMaker (asciiTextMaker);
        asaReport.setTextMaker (asciiTextMaker);
        break;
    }

    switch (formatType)
    {
      case HEX:
        textArea.setText (hexReport.getFormattedText ());
        break;
      case TEXT:
        textArea.setText (textReport.getFormattedText ());
        break;
      case NATLOAD:
        textArea.setText (natloadReport.getFormattedText ());
        break;
      case ASA:
        textArea.setText (asaReport.getFormattedText ());
        break;
    }

    textArea.positionCaret (0);
  }

  private void spaceReport ()
  {
    List<byte[]> buffers = new ArrayList<> ();
    int totalData = 0;
    int bufferLength = 0;
    for (Record record : records)
    {
      totalData += record.length;
      if (!buffers.contains (record.buffer))
      {
        buffers.add (record.buffer);
        bufferLength += record.buffer.length;
      }
    }

    System.out.println ("--------------------------");
    System.out.printf ("Records     : %,8d%n", records.size ());
    System.out.printf ("Buffer space: %,8d%nrecord space: %,8d%n", bufferLength,
                       totalData);
    System.out.printf ("Utilisation :      %6.2f%%%n",
                       ((float) totalData / bufferLength * 100));
    System.out.println ("--------------------------");
  }

  private void closeWindow ()
  {
    windowSaver.saveWindow ();
  }

  public static void main (String[] args)
  {
    launch (args);
  }
}