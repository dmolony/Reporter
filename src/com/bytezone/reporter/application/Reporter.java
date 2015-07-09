package com.bytezone.reporter.application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import com.bytezone.reporter.application.Formatter.EncodingType;
import com.bytezone.reporter.application.Formatter.FormatType;
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

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
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
        "smutlib001.src", "DBALIB.SRC", "test1.txt", "test2.txt" };

  private final String[] types =
      { "FB252", "LF", "CRLF", "RAV", "VB", "RDW", "NVB", "FB132", "FB132" };

  private final TextArea textArea = new TextArea ();
  private WindowSaver windowSaver;
  private Preferences prefs;

  private final Formatter formatter = new Formatter ();

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

  private final ToggleGroup pagingGroup = new ToggleGroup ();
  private RadioButton btnNoPaging;
  private RadioButton btn66;
  private RadioButton btnOther;

  private List<Record> records;

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    int choice = 8;
    Path currentPath = Paths.get (home + files[choice]);

    long fileLength = currentPath.toFile ().length ();
    byte[] buffer = Files.readAllBytes (currentPath);
    System.out.printf ("File size: %,d%n", buffer.length);
    int max = 100_000;
    if (fileLength > max)
    {
      System.out.printf ("Reducing buffer to %,d%n", max);
      byte[] shortBuffer = new byte[max];
      System.arraycopy (buffer, 0, shortBuffer, 0, max);
      buffer = shortBuffer;
    }

    System.out.println ("------------------------");
    for (int i = 0; i < files.length; i++)
      System.out.printf ("%d  %-5s %s%n", i, types[i], files[i]);
    System.out.println ("------------------------");
    System.out.printf ("Using %-5s %s%n", types[choice], files[choice]);

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
    else
      btnFb80.setSelected (true);

    if (fileLength % 132 != 0)
      btnFb132.setDisable (true);
    else
      btnFb132.setSelected (true);

    if (fileLength % 252 != 0)
      btnFb252.setDisable (true);
    else
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
    btnHex = addFormatTypeButton ("Hex", formattingGroup, setText, FormatType.HEX);
    btnNatload =
        addFormatTypeButton ("NatLoad", formattingGroup, setText, FormatType.NATLOAD);
    btnHex.setSelected (true);
    vbox3.getChildren ().addAll (btnHex, btnText, btnNatload);

    VBox vbox4 = new VBox (10);
    vbox4.setPadding (new Insets (10));

    btnNoPaging = addRadioButton ("None", pagingGroup, setText);
    btnNoPaging.setSelected (true);
    btn66 = addRadioButton ("66", pagingGroup, setText);
    btnOther = addRadioButton ("Other", pagingGroup, setText);
    vbox4.getChildren ().addAll (btnNoPaging, btn66, btnOther);

    CheckBox chkAsa = new CheckBox ("ASA");
    chkAsa.setOnAction (setText);
    vbox4.getChildren ().addAll (chkAsa);

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
    TitledPane titledPane = new TitledPane ("Records", contents);
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
    formatter.setRecords (records);
    spaceReport ();
    setText ();
  }

  private void setText ()
  {
    RadioButton btn = (RadioButton) encodingGroup.getSelectedToggle ();
    EncodingType encodingType = (EncodingType) btn.getUserData ();

    btn = (RadioButton) formattingGroup.getSelectedToggle ();
    FormatType formatType = (FormatType) btn.getUserData ();

    formatter.setTextMaker (encodingType);
    formatter.setFormatter (formatType);// hex/text/other

    textArea.setText (formatter.getFormattedText ());
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