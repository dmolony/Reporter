package com.bytezone.reporter.application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.prefs.Preferences;

import com.bytezone.reporter.record.CrlfRecordMaker;

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

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    Path currentPath = Paths.get (home + files[0]);

    byte[] bufferAll = Files.readAllBytes (currentPath);
    byte[] buffer = new byte[2048];
    System.arraycopy (bufferAll, 0, buffer, 0, buffer.length);

    Font font = Font.font (fontNames[18], FontWeight.NORMAL, 14);
    textArea.setFont (font);

    CrlfRecordMaker crlf = new CrlfRecordMaker (buffer);
    List<byte[]> records = crlf.getRecords ();

    for (byte[] record : records)
    {
      textArea.appendText (Utility.toHex (record));
      textArea.appendText ("\n\n");
    }

    if (textArea.getLength () > 1)
    {
      textArea.deletePreviousChar ();
      textArea.deletePreviousChar ();
    }

    textArea.positionCaret (0);
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
    EventHandler<ActionEvent> evt1 = e -> setRecordMaker ();

    RadioButton btnCrlf = addRadioButton ("CRLF", group1, evt1);
    btnCrlf.setSelected (true);
    RadioButton btnCR = addRadioButton ("CR", group1, evt1);
    RadioButton btnLF = addRadioButton ("LF", group1, evt1);
    RadioButton btnVB = addRadioButton ("VB", group1, evt1);
    RadioButton btnRDW = addRadioButton ("RDW", group1, evt1);
    RadioButton btnRavel = addRadioButton ("Ravel", group1, evt1);
    RadioButton btnFb80 = addRadioButton ("FB80", group1, evt1);
    RadioButton btnFbOther = addRadioButton ("Other", group1, evt1);

    vbox.getChildren ().addAll (lblSplit, btnCrlf, btnCR, btnLF, btnVB, btnRDW, btnRavel,
                                btnFb80, btnFbOther);
    // vbox.getChildren ().add (new Separator ());

    ToggleGroup group2 = new ToggleGroup ();
    EventHandler<ActionEvent> evt2 = e -> setFormatter ();

    RadioButton btnAscii = addRadioButton ("ASCII", group2, evt2);
    btnAscii.setToggleGroup (group2);
    btnAscii.setSelected (true);
    RadioButton btnEbcdic = addRadioButton ("EBCDIC", group2, evt2);
    btnEbcdic.setToggleGroup (group2);
    vbox.getChildren ().addAll (lblFormat, btnAscii, btnEbcdic);
    vbox.getChildren ().add (new Separator ());

    ToggleGroup group3 = new ToggleGroup ();
    RadioButton btnFormatted = addRadioButton ("Formatted", group3, evt2);
    RadioButton btnHex = addRadioButton ("Hex", group3, evt2);
    btnHex.setSelected (true);
    vbox.getChildren ().addAll (btnHex, btnFormatted);
    vbox.getChildren ().add (new Separator ());

    ToggleGroup group4 = new ToggleGroup ();
    EventHandler<ActionEvent> evt3 = e -> setPageMaker ();

    RadioButton btnNone = addRadioButton ("None", group4, evt3);
    btnNone.setSelected (true);
    RadioButton btn66 = addRadioButton ("66", group4, evt3);
    RadioButton btnOther = addRadioButton ("Other", group4, evt3);
    vbox.getChildren ().addAll (lblPrint, btnNone, btn66, btnOther);

    CheckBox chkAsa = new CheckBox ("ASA");
    chkAsa.setOnAction (evt3);
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

  private void setRecordMaker ()
  {
    System.out.println ("rebuild");
  }

  private void setFormatter ()
  {
    System.out.println ("format");
  }

  private void setPageMaker ()
  {
    System.out.println ("page");
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