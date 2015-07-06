package com.bytezone.reporter.application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import com.bytezone.reporter.application.Formatter.FormatType;
import com.bytezone.reporter.application.Splitter.RecordType;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;

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
      { "DENIS-000.src", "DBALIB.src", "MOLONYD.NCD", "denis-005.src" };

  private final TextArea textArea = new TextArea ();
  private WindowSaver windowSaver;
  private Preferences prefs;
  private Splitter splitter;
  private final Formatter formatter = new Formatter ();

  // private final TextMaker asciiTextMaker = new AsciiTextMaker ();
  // private final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();
  private byte[] buffer;

  private final ToggleGroup splitterGroup = new ToggleGroup ();
  private RadioButton btnCrlf;
  private RadioButton btnCr;
  private RadioButton btnLf;
  private RadioButton btnFb80;
  private RadioButton btnFb132;
  private RadioButton btnFbOther;
  private RadioButton btnVB;
  private RadioButton btnRDW;
  private RadioButton btnRavel;
  private RadioButton btnNoSplit;

  private final ToggleGroup encodingGroup = new ToggleGroup ();
  private RadioButton btnAscii;
  private RadioButton btnEbcdic;

  private final ToggleGroup formattingGroup = new ToggleGroup ();
  private RadioButton btnText;
  private RadioButton btnHex;

  private final ToggleGroup pagingGroup = new ToggleGroup ();
  private RadioButton btnNoPaging;
  private RadioButton btn66;
  private RadioButton btnOther;

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    Path currentPath = Paths.get (home + files[3]);

    byte[] bufferAll = Files.readAllBytes (currentPath);
    buffer = new byte[2048];
    System.arraycopy (bufferAll, 0, buffer, 0, buffer.length);

    // hexEbcdicFormatter = new HexFormatter (new EbcdicTextMaker ());
    // hexAsciiFormatter = new HexFormatter (new AsciiTextMaker ());

    splitter = new Splitter (buffer);
    // formatter = new Formatter ();

    Font font = Font.font (fontNames[18], FontWeight.NORMAL, 14);
    textArea.setFont (font);

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

    vbox.getChildren ().addAll (lblSplit, btnNoSplit, btnCrlf, btnCr, btnLf, btnVB,
                                btnRDW, btnRavel, btnFb80, btnFb132, btnFbOther);

    btnAscii = addRadioButton ("ASCII", encodingGroup, rebuild);
    btnAscii.setToggleGroup (encodingGroup);
    btnAscii.setSelected (true);
    btnEbcdic = addRadioButton ("EBCDIC", encodingGroup, rebuild);
    btnEbcdic.setToggleGroup (encodingGroup);
    vbox.getChildren ().addAll (lblEncode, btnAscii, btnEbcdic);

    btnText = addRadioButton ("Text", formattingGroup, rebuild);
    btnHex = addRadioButton ("Hex", formattingGroup, rebuild);
    btnText = addRadioButton ("Text", formattingGroup, rebuild);
    btnHex.setSelected (true);
    vbox.getChildren ().addAll (lblFormat, btnHex, btnText);

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

    List<byte[]> records = setRecordMaker ();
    setFormatter (records);
    setPageMaker ();

    textArea.positionCaret (0);
  }

  private List<byte[]> setRecordMaker ()
  {
    RadioButton btn = (RadioButton) splitterGroup.getSelectedToggle ();
    RecordType recordType = (RecordType) btn.getUserData ();

    return splitter.getRecords (recordType);
  }

  private void setFormatter (List<byte[]> records)
  {
    RadioButton btn2 = (RadioButton) formattingGroup.getSelectedToggle ();
    if (btn2 == btnText)
    {
      formatter.setFormatter (FormatType.TEXT);
    }
    else if (btn2 == btnHex)
    {
      formatter.setFormatter (FormatType.HEX);
    }

    RadioButton btn = (RadioButton) encodingGroup.getSelectedToggle ();
    if (btn == btnAscii)
      formatter.setTextMaker (new AsciiTextMaker ());
    else
      formatter.setTextMaker (new EbcdicTextMaker ());

    for (byte[] record : records)
    {
      textArea.appendText (formatter.getFormattedRecord (record));
      textArea.appendText ("\n");
    }

    if (records.size () > 0)
    {
      int last = textArea.getLength ();
      textArea.deleteText (last - 2, last);
    }
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