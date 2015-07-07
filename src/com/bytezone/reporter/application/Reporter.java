package com.bytezone.reporter.application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import com.bytezone.reporter.application.Formatter.EncodingType;
import com.bytezone.reporter.application.Formatter.FormatType;
import com.bytezone.reporter.application.Splitter.RecordType;
import com.bytezone.reporter.record.Record;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
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
        "smutlib001.src", "DBALIB.SRC" };
  private final String[] types = { "FB252", "LF", "CRLF", "RAV", "VB", "RDW", "NVB" };

  private final TextArea textArea = new TextArea ();
  private WindowSaver windowSaver;
  private Preferences prefs;

  private Splitter splitter;
  private final Formatter formatter = new Formatter ();

  private final ToggleGroup splitterGroup = new ToggleGroup ();
  private RadioButton btnCrlf;
  private RadioButton btnCr;
  private RadioButton btnLf;
  private RadioButton btnFb80;
  private RadioButton btnFb132;
  private RadioButton btnFbOther;
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

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    int choice = 6;
    Path currentPath = Paths.get (home + files[choice]);

    long fileLength = currentPath.toFile ().length ();
    byte[] buffer = Files.readAllBytes (currentPath);
    System.out.printf ("File size: %,d%n", buffer.length);
    int max = 300_000;
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

    splitter = new Splitter (buffer);

    textArea.setFont (Font.font (fontNames[18], FontWeight.NORMAL, 14));
    textArea.setEditable (false);

    VBox vbox = new VBox (10);
    vbox.setPadding (new Insets (10));

    Label lblSplit = addLabel ("Records", 20, 120);
    Label lblFormat = addLabel ("Formatting", 20, 120);
    Label lblEncode = addLabel ("Encoding", 20, 120);
    Label lblPrint = addLabel ("Paging", 20, 120);

    EventHandler<ActionEvent> rebuild = e -> rebuild ();

    btnNoSplit = addRecordTypeButton ("None", splitterGroup, rebuild, RecordType.NONE);
    btnNoSplit.setSelected (true);
    btnCrlf = addRecordTypeButton ("CRLF", splitterGroup, rebuild, RecordType.CRLF);
    btnCr = addRecordTypeButton ("CR", splitterGroup, rebuild, RecordType.CR);
    btnLf = addRecordTypeButton ("LF", splitterGroup, rebuild, RecordType.LF);
    btnVB = addRecordTypeButton ("VB", splitterGroup, rebuild, RecordType.VB);
    btnRDW = addRecordTypeButton ("RDW", splitterGroup, rebuild, RecordType.RDW);
    btnRavel = addRecordTypeButton ("Ravel", splitterGroup, rebuild, RecordType.RVL);
    btnFb80 = addRecordTypeButton ("FB80", splitterGroup, rebuild, RecordType.FB80);
    btnFb132 = addRecordTypeButton ("FB132", splitterGroup, rebuild, RecordType.FB132);
    btnFbOther = addRecordTypeButton ("Other", splitterGroup, rebuild, RecordType.FBXX);
    btnNvb = addRecordTypeButton ("NVB", splitterGroup, rebuild, RecordType.NVB);

    vbox.getChildren ().addAll (lblSplit, btnNoSplit, btnCrlf, btnCr, btnLf, btnVB,
                                btnNvb, btnRDW, btnRavel, btnFb80, btnFb132, btnFbOther);

    btnAscii =
        addEncodingTypeButton ("ASCII", encodingGroup, rebuild, EncodingType.ASCII);
    btnAscii.setSelected (true);
    btnEbcdic =
        addEncodingTypeButton ("EBCDIC", encodingGroup, rebuild, EncodingType.EBCDIC);
    vbox.getChildren ().addAll (lblEncode, btnAscii, btnEbcdic);

    btnText = addFormatTypeButton ("Text", formattingGroup, rebuild, FormatType.TEXT);
    btnHex = addFormatTypeButton ("Hex", formattingGroup, rebuild, FormatType.HEX);
    btnNatload =
        addFormatTypeButton ("NatLoad", formattingGroup, rebuild, FormatType.NATLOAD);
    btnHex.setSelected (true);
    vbox.getChildren ().addAll (lblFormat, btnHex, btnText, btnNatload);

    btnNoPaging = addRadioButton ("None", pagingGroup, rebuild);
    btnNoPaging.setSelected (true);
    btn66 = addRadioButton ("66", pagingGroup, rebuild);
    btnOther = addRadioButton ("Other", pagingGroup, rebuild);
    vbox.getChildren ().addAll (lblPrint, btnNoPaging, btn66, btnOther);

    CheckBox chkAsa = new CheckBox ("ASA");
    chkAsa.setOnAction (e -> rebuild ());
    vbox.getChildren ().addAll (chkAsa);

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

    rebuild ();
    primaryStage.show ();
  }

  private RadioButton addRecordTypeButton (String text, ToggleGroup group,
      EventHandler<ActionEvent> evt, RecordType recordType)
  {
    RadioButton button = addRadioButton (text, group, evt);
    button.setUserData (recordType);
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

  private Label addLabel (String text, double height, double width)
  {
    Label label = new Label (text);

    label.setPrefWidth (width);
    label.setAlignment (Pos.CENTER);
    label.setPrefHeight (height);

    return label;
  }

  private void rebuild ()
  {
    textArea.clear ();

    List<Record> records = setRecordMaker ();
    System.out.printf ("%,d records%n", records.size ());
    setFormatter (records);

    setPageMaker ();

    textArea.positionCaret (0);
  }

  private List<Record> setRecordMaker ()
  {
    RadioButton btn = (RadioButton) splitterGroup.getSelectedToggle ();
    RecordType recordType = (RecordType) btn.getUserData ();

    return splitter.getRecords (recordType);
  }

  private void setFormatter (List<Record> records)
  {
    RadioButton btn2 = (RadioButton) formattingGroup.getSelectedToggle ();
    FormatType formatType = (FormatType) btn2.getUserData ();
    formatter.setFormatter (formatType);

    RadioButton btn = (RadioButton) encodingGroup.getSelectedToggle ();
    EncodingType encodingType = (EncodingType) btn.getUserData ();
    formatter.setTextMaker (encodingType);

    formatter.setRecords (records);

    StringBuilder text = new StringBuilder ();
    for (String record : formatter.getFormattedRecords ())
    {
      text.append (record);
      text.append ('\n');
    }

    while (text.length () > 0 && text.charAt (text.length () - 1) == '\n')
      text.deleteCharAt (text.length () - 1);

    textArea.setText (text.toString ());
  }

  private void setPageMaker ()
  {
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