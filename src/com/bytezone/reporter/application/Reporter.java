package com.bytezone.reporter.application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import com.bytezone.reporter.application.Splitter.RecordType;
import com.bytezone.reporter.format.HexFormatter;
import com.bytezone.reporter.format.RecordFormatter;
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
  private Formatter formatter;

  private RecordFormatter hexEbcdicFormatter;
  private RecordFormatter hexAsciiFormatter;
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
  private RadioButton btnFormatted;
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

    hexEbcdicFormatter = new HexFormatter (new EbcdicTextMaker ());
    hexAsciiFormatter = new HexFormatter (new AsciiTextMaker ());

    splitter = new Splitter (buffer);
    formatter = new Formatter ();

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

    btnNoSplit = addRadioButton ("None", splitterGroup, rebuild);
    btnNoSplit.setSelected (true);
    btnCrlf = addRadioButton ("CRLF", splitterGroup, rebuild);
    btnCr = addRadioButton ("CR", splitterGroup, rebuild);
    btnLf = addRadioButton ("LF", splitterGroup, rebuild);
    btnVB = addRadioButton ("VB", splitterGroup, rebuild);
    btnRDW = addRadioButton ("RDW", splitterGroup, rebuild);
    btnRavel = addRadioButton ("Ravel", splitterGroup, rebuild);
    btnFb80 = addRadioButton ("FB80", splitterGroup, rebuild);
    btnFb132 = addRadioButton ("FB132", splitterGroup, rebuild);
    btnFbOther = addRadioButton ("Other", splitterGroup, rebuild);

    vbox.getChildren ().addAll (lblSplit, btnNoSplit, btnCrlf, btnCr, btnLf, btnVB,
                                btnRDW, btnRavel, btnFb80, btnFb132, btnFbOther);

    btnAscii = addRadioButton ("ASCII", encodingGroup, rebuild);
    btnAscii.setToggleGroup (encodingGroup);
    btnAscii.setSelected (true);
    btnEbcdic = addRadioButton ("EBCDIC", encodingGroup, rebuild);
    btnEbcdic.setToggleGroup (encodingGroup);
    vbox.getChildren ().addAll (lblEncode, btnAscii, btnEbcdic);

    btnFormatted = addRadioButton ("Formatted", formattingGroup, rebuild);
    btnHex = addRadioButton ("Hex", formattingGroup, rebuild);
    btnHex.setSelected (true);
    vbox.getChildren ().addAll (lblFormat, btnHex, btnFormatted);

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
    RecordType recordType = null;

    if (btn == btnCrlf)
      recordType = RecordType.CRLF;
    else if (btn == btnCr)
      recordType = RecordType.CR;
    else if (btn == btnLf)
      recordType = RecordType.LF;
    else if (btn == btnFb80)
      recordType = RecordType.FB80;
    else if (btn == btnFb132)
      recordType = RecordType.FB132;
    else if (btn == btnFbOther)
      recordType = RecordType.FBXX;
    else if (btn == btnRavel)
      recordType = RecordType.RVL;
    else if (btn == btnRDW)
      recordType = RecordType.RDW;
    else if (btn == btnVB)
      recordType = RecordType.VB;
    else if (btn == btnNoSplit)
      recordType = RecordType.NONE;
    else
      System.out.println ("Unknown record type");

    return splitter.getRecords (recordType);
  }

  private void setFormatter (List<byte[]> records)
  {
    RadioButton btn = (RadioButton) encodingGroup.getSelectedToggle ();
    if (btn == btnAscii)
      formatter.setFormatter (hexAsciiFormatter);
    else
      formatter.setFormatter (hexEbcdicFormatter);

    for (byte[] record : records)
    {
      textArea.appendText (formatter.getFormattedRecord (record));
      textArea.appendText ("\n\n");
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