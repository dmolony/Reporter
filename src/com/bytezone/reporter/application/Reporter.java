package com.bytezone.reporter.application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import com.bytezone.reporter.format.HexFormatter;
import com.bytezone.reporter.format.RecordFormatter;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
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

  private static final String[] files = { "DENIS-000.src", "DBALIB.src" };

  private final TextArea textArea = new TextArea ();
  private WindowSaver windowSaver;
  private Preferences prefs;
  private Splitter splitter;
  private RecordFormatter hexEbcdicFormatter;
  private RecordFormatter hexAsciiFormatter;
  private byte[] buffer;

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    Path currentPath = Paths.get (home + files[0]);

    byte[] bufferAll = Files.readAllBytes (currentPath);
    buffer = new byte[2048];
    System.arraycopy (bufferAll, 0, buffer, 0, buffer.length);

    splitter = new Splitter (buffer);
    hexEbcdicFormatter = new HexFormatter (new EbcdicTextMaker ());
    hexAsciiFormatter = new HexFormatter (new AsciiTextMaker ());
    rebuild ();

    Font font = Font.font (fontNames[18], FontWeight.NORMAL, 14);
    textArea.setFont (font);

    textArea.setEditable (false);

    VBox vbox = new VBox (10);
    vbox.setPadding (new Insets (10));

    Label lblSplit = new Label ("Records");
    Label lblFormat = new Label ("Format");
    Label lblPrint = new Label ("Paging");

    // lblSplit.setAlignment (Pos.CENTER);
    // lblFormat.setAlignment (Pos.CENTER);
    // lblPrint.setAlignment (Pos.CENTER);

    ToggleGroup group1 = new ToggleGroup ();

    RadioButton btnCrlf = addRadioButton ("CRLF", group1, e -> rebuild ());
    btnCrlf.setSelected (true);
    RadioButton btnCR = addRadioButton ("CR", group1, e -> rebuild ());
    RadioButton btnLF = addRadioButton ("LF", group1, e -> rebuild ());
    RadioButton btnVB = addRadioButton ("VB", group1, e -> rebuild ());
    RadioButton btnRDW = addRadioButton ("RDW", group1, e -> rebuild ());
    RadioButton btnRavel = addRadioButton ("Ravel", group1, e -> rebuild ());
    RadioButton btnFb80 = addRadioButton ("FB80", group1, e -> rebuild ());
    RadioButton btnFbOther = addRadioButton ("Other", group1, e -> rebuild ());

    vbox.getChildren ().addAll (lblSplit, btnCrlf, btnCR, btnLF, btnVB, btnRDW, btnRavel,
                                btnFb80, btnFbOther);

    ToggleGroup group2 = new ToggleGroup ();

    RadioButton btnAscii = addRadioButton ("ASCII", group2, e -> rebuild ());
    btnAscii.setToggleGroup (group2);
    btnAscii.setSelected (true);
    RadioButton btnEbcdic = addRadioButton ("EBCDIC", group2, e -> rebuild ());
    btnEbcdic.setToggleGroup (group2);
    vbox.getChildren ().addAll (lblFormat, btnAscii, btnEbcdic);
    vbox.getChildren ().add (new Separator ());

    ToggleGroup group3 = new ToggleGroup ();
    RadioButton btnFormatted = addRadioButton ("Formatted", group3, e -> rebuild ());
    RadioButton btnHex = addRadioButton ("Hex", group3, e -> rebuild ());
    btnHex.setSelected (true);
    vbox.getChildren ().addAll (btnHex, btnFormatted);
    vbox.getChildren ().add (new Separator ());

    ToggleGroup group4 = new ToggleGroup ();

    RadioButton btnNone = addRadioButton ("None", group4, e -> rebuild ());
    btnNone.setSelected (true);
    RadioButton btn66 = addRadioButton ("66", group4, e -> rebuild ());
    RadioButton btnOther = addRadioButton ("Other", group4, e -> rebuild ());
    vbox.getChildren ().addAll (lblPrint, btnNone, btn66, btnOther);

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
    return splitter.getRecords (Splitter.RecordType.CRLF);
  }

  private void setFormatter (List<byte[]> records)
  {
    for (byte[] record : records)
    {
      textArea.appendText (hexEbcdicFormatter.getFormattedRecord (record));
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