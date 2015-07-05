package com.bytezone.reporter.application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.bytezone.record.CrlfRecordMaker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    lblSplit.setAlignment (Pos.CENTER);
    lblFormat.setAlignment (Pos.CENTER);
    lblPrint.setAlignment (Pos.CENTER);

    ToggleGroup group1 = new ToggleGroup ();
    RadioButton btnCrlf = new RadioButton ("CRLF");
    btnCrlf.setToggleGroup (group1);
    btnCrlf.setSelected (true);
    RadioButton btnCR = new RadioButton ("CR");
    btnCR.setToggleGroup (group1);
    RadioButton btnLF = new RadioButton ("LF");
    btnLF.setToggleGroup (group1);
    RadioButton btnVB = new RadioButton ("VB");
    btnVB.setToggleGroup (group1);
    RadioButton btnRDW = new RadioButton ("RDW");
    btnRDW.setToggleGroup (group1);
    RadioButton btnRavel = new RadioButton ("Ravel");
    btnRavel.setToggleGroup (group1);
    RadioButton btnFb80 = new RadioButton ("FB80");
    btnLF.setToggleGroup (group1);
    RadioButton btnFbOther = new RadioButton ("Other");
    btnFbOther.setToggleGroup (group1);

    vbox.getChildren ().addAll (lblSplit, btnCrlf, btnCR, btnLF, btnVB, btnRDW, btnRavel,
                                btnFb80, btnFbOther);
    // vbox.getChildren ().add (new Separator ());

    ToggleGroup group2 = new ToggleGroup ();
    RadioButton btnAscii = new RadioButton ("ASCII");
    btnAscii.setToggleGroup (group2);
    btnAscii.setSelected (true);
    RadioButton btnEbcdic = new RadioButton ("EBCDIC");
    btnEbcdic.setToggleGroup (group2);
    vbox.getChildren ().addAll (lblFormat, btnAscii, btnEbcdic);
    vbox.getChildren ().add (new Separator ());

    ToggleGroup group3 = new ToggleGroup ();
    RadioButton btnFormatted = new RadioButton ("Formatted");
    btnFormatted.setToggleGroup (group3);
    RadioButton btnHex = new RadioButton ("Hex");
    btnHex.setToggleGroup (group3);
    btnHex.setSelected (true);
    vbox.getChildren ().addAll (btnHex, btnFormatted);
    vbox.getChildren ().add (new Separator ());

    ToggleGroup group4 = new ToggleGroup ();
    RadioButton btnNone = new RadioButton ("None");
    btnNone.setToggleGroup (group4);
    btnNone.setSelected (true);
    RadioButton btn66 = new RadioButton ("66");
    btn66.setToggleGroup (group4);
    RadioButton btnOther = new RadioButton ("Other");
    btnOther.setToggleGroup (group4);
    vbox.getChildren ().addAll (lblPrint, btnNone, btn66, btnOther);
    // vbox.getChildren ().add (new Separator ());

    CheckBox chkAsa = new CheckBox ("ASA");
    vbox.getChildren ().addAll (chkAsa);

    BorderPane borderPane = new BorderPane ();
    borderPane.setCenter (textArea);
    borderPane.setRight (vbox);
    Scene scene = new Scene (borderPane, 800, 592);

    primaryStage.setTitle ("Reporter");
    primaryStage.setScene (scene);
    primaryStage.show ();
  }

  public static void main (String[] args)
  {
    launch (args);
  }
}