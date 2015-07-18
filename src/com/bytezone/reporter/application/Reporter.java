package com.bytezone.reporter.application;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

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
import com.bytezone.reporter.reports.AsaReport;
import com.bytezone.reporter.reports.HexReport;
import com.bytezone.reporter.reports.NatloadReport;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.reports.TextReport;
import com.bytezone.reporter.tests.RecordTester;
import com.bytezone.reporter.tests.Score;
import com.bytezone.reporter.text.AsciiTextMaker;
import com.bytezone.reporter.text.EbcdicTextMaker;
import com.bytezone.reporter.text.TextMaker;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Reporter extends Application
{
  private final static String OS = System.getProperty ("os.name");
  private final static boolean SYSTEM_MENUBAR = OS != null && OS.startsWith ("Mac");

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

  private final ReportMaker hexReport = new HexReport ();
  private final ReportMaker textReport = new TextReport ();
  private final ReportMaker natloadReport = new NatloadReport ();
  private final ReportMaker asaReport = new AsaReport ();
  private ReportMaker currentReport;

  private final RecordMaker crlf = new CrlfRecordMaker ();
  private final RecordMaker cr = new CrRecordMaker ();
  private final RecordMaker lf = new LfRecordMaker ();
  private final RecordMaker fb80 = new FbRecordMaker (80);
  private final RecordMaker fb132 = new FbRecordMaker (132);
  private final RecordMaker fb252 = new FbRecordMaker (252);
  private final RecordMaker vb = new VbRecordMaker ();
  private final RecordMaker nvb = new NvbRecordMaker ();
  private final RecordMaker rdw = new RdwRecordMaker ();
  private final RecordMaker ravel = new RavelRecordMaker ();
  private final RecordMaker none = new NoRecordMaker ();

  private final BorderPane borderPane = new BorderPane ();
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

  private List<Record> records;
  private final MenuBar menuBar = new MenuBar ();

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles/";
    int choice = 10;
    Path currentPath = Paths.get (home + files[choice]);

    long fileLength = currentPath.toFile ().length ();
    byte[] buffer = Files.readAllBytes (currentPath);

    System.out.println ("-----------------------------------------------------");
    for (int i = 0; i < files.length; i++)
    {
      Path path = Paths.get (home + files[i]);
      long length = path.toFile ().length ();
      System.out.printf ("%s %2d  %-5s  %s  %s  %-22s %,11d%n", (choice == i) ? "*" : " ",
                         i, types[i], encodings[i], formats[i], files[i], length);
    }
    System.out.println ("-----------------------------------------------------");

    EventHandler<ActionEvent> rebuild = e -> createRecords ();
    EventHandler<ActionEvent> paginate = e -> paginate ();

    VBox vbox1 = new VBox (10);
    vbox1.setPadding (new Insets (10));

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

    vbox1.getChildren ().addAll (btnNoSplit, btnCrlf, btnCr, btnLf, btnVB, btnNvb, btnRDW,
                                 btnRavel, btnFb80, btnFb132, btnFb252);

    VBox vbox2 = new VBox (10);
    vbox2.setPadding (new Insets (10));

    btnAscii =
        addEncodingTypeButton ("ASCII", encodingGroup, paginate, EncodingType.ASCII);
    btnEbcdic =
        addEncodingTypeButton ("EBCDIC", encodingGroup, paginate, EncodingType.EBCDIC);
    vbox2.getChildren ().addAll (btnAscii, btnEbcdic);

    VBox vbox3 = new VBox (10);
    vbox3.setPadding (new Insets (10));

    btnHex = addFormatTypeButton ("Binary", formattingGroup, paginate, FormatType.HEX);
    btnText = addFormatTypeButton ("Text", formattingGroup, paginate, FormatType.TEXT);
    btnAsa =
        addFormatTypeButton ("ASA Printer", formattingGroup, paginate, FormatType.ASA);
    btnNatload =
        addFormatTypeButton ("NatLoad", formattingGroup, paginate, FormatType.NATLOAD);
    vbox3.getChildren ().addAll (btnHex, btnText, btnAsa, btnNatload);

    VBox vbox = new VBox ();

    addTitledPane ("Records", vbox1, vbox);
    addTitledPane ("Encoding", vbox2, vbox);
    addTitledPane ("Formatting", vbox3, vbox);

    borderPane.setRight (vbox);

    crlf.setBuffer (buffer);
    cr.setBuffer (buffer);
    lf.setBuffer (buffer);
    fb80.setBuffer (buffer);
    fb132.setBuffer (buffer);
    fb252.setBuffer (buffer);
    vb.setBuffer (buffer);
    nvb.setBuffer (buffer);
    rdw.setBuffer (buffer);
    ravel.setBuffer (buffer);
    none.setBuffer (buffer);

    selectButtons (buffer, fileLength);
    createRecords ();

    menuBar.getMenus ().addAll (getFileMenu ());

    borderPane.setTop (menuBar);
    if (SYSTEM_MENUBAR)
      menuBar.useSystemMenuBarProperty ().set (true);

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

  private void selectButtons (byte[] buffer, long fileLength)
  {
    RecordMaker probableRecordMaker = null;

    RadioButton[] testableButtons =
        { btnCrlf, btnLf, btnCr, btnVB, btnRDW, btnNvb, btnRavel };
    RadioButton[] recordMakerButtons = { btnCrlf, btnLf, btnCr, btnVB, btnRDW, btnNvb,
                                         btnRavel, btnFb80, btnFb132, btnFb252 };
    int maxRecords = 0;
    for (RadioButton button : testableButtons)
    {
      RecordMaker recordMaker = (RecordMaker) button.getUserData ();
      List<Record> records = recordMaker.test (buffer, 0, 1024);
      if (records.size () <= 2)
        button.setDisable (true);
      else if (records.size () > maxRecords)
      {
        probableRecordMaker = recordMaker;
        maxRecords = records.size ();
      }
    }

    if (fileLength % 80 != 0)
      btnFb80.setDisable (true);
    else if (probableRecordMaker == null)
      probableRecordMaker = (RecordMaker) btnFb80.getUserData ();

    if (fileLength % 132 != 0)
      btnFb132.setDisable (true);
    else if (probableRecordMaker == null)
      probableRecordMaker = (RecordMaker) btnFb132.getUserData ();

    if (fileLength % 252 != 0)
      btnFb252.setDisable (true);
    else//if (probableRecordMaker == null)
      probableRecordMaker = (RecordMaker) btnFb252.getUserData ();

    for (RadioButton button : recordMakerButtons)
      if (probableRecordMaker == button.getUserData ())
      {
        button.setSelected (true);
        break;
      }

    List<RecordTester> testers = new ArrayList<> ();
    testers.add (new RecordTester (crlf, buffer, 1024));
    testers.add (new RecordTester (cr, buffer, 1024));
    testers.add (new RecordTester (lf, buffer, 1024));
    testers.add (new RecordTester (vb, buffer, 1024));
    testers.add (new RecordTester (rdw, buffer, 1024));
    testers.add (new RecordTester (nvb, buffer, 1024));
    testers.add (new RecordTester (ravel, buffer, 1024));
    testers.add (new RecordTester (fb80, buffer, 800));
    testers.add (new RecordTester (fb132, buffer, 1320));
    testers.add (new RecordTester (fb252, buffer, 2520));

    List<TextMaker> textMakers = new ArrayList<> ();
    textMakers.add (asciiTextMaker);
    textMakers.add (ebcdicTextMaker);

    List<ReportMaker> reportMakers = new ArrayList<> ();
    reportMakers.add (textReport);
    reportMakers.add (asaReport);
    reportMakers.add (natloadReport);

    RecordTester preferredRecordTester = null;
    List<Score> scores = new ArrayList<> ();

    for (RecordTester tester : testers)
      if (tester.getTotalRecords () > 1)
      {
        for (TextMaker textMaker : textMakers)
          tester.testTextMaker (textMaker);

        TextMaker textMaker = tester.getPreferredTextMaker ();

        for (ReportMaker reportMaker : reportMakers)
          scores.add (tester.testReportMaker (reportMaker, textMaker));
      }

    for (RecordTester tester : testers)
      System.out.println (tester);

    Collections.sort (scores);
    Collections.reverse (scores);
    for (Score score : scores)
      System.out.println (score);

    if (preferredRecordTester != null)
    {
      TextMaker textMaker = preferredRecordTester.getPreferredTextMaker ();
      if (textMaker == asciiTextMaker)
        btnAscii.setSelected (true);
      else
        btnEbcdic.setSelected (true);

      ReportMaker reportMaker = preferredRecordTester.getPreferredReportMaker ();
      if (reportMaker == natloadReport)
        btnNatload.setSelected (true);
      else if (reportMaker == asaReport)
        btnAsa.setSelected (true);
      else if (reportMaker == textReport)
        btnText.setSelected (true);
      else
        btnHex.setSelected (true);
    }
    else
    {
      btnEbcdic.setSelected (true);
      btnHex.setSelected (true);
    }
  }

  private Menu getFileMenu ()
  {
    Menu menuFile = new Menu ("File");

    MenuItem menuItemOpen = getMenuItem ("Open...", e -> openFile (), KeyCode.O);
    MenuItem menuItemSave = getMenuItem ("Save...", e -> saveFile (), KeyCode.S);
    MenuItem menuItemPrint = getMenuItem ("Page setup", e -> pageSetup (), null);
    MenuItem menuItemPageSetup = getMenuItem ("Print", e -> printFile (), KeyCode.P);
    MenuItem menuItemClose = getMenuItem ("Close window", e -> closeWindow (), KeyCode.W);

    menuFile.getItems ().addAll (menuItemOpen, menuItemSave, menuItemPageSetup,
                                 menuItemPrint, menuItemClose);

    return menuFile;
  }

  private MenuItem getMenuItem (String text, EventHandler<ActionEvent> eventHandler,
      KeyCode keyCode)
  {
    MenuItem menuItem = new MenuItem (text);
    menuItem.setOnAction (eventHandler);
    if (keyCode != null)
      menuItem.setAccelerator (new KeyCodeCombination (keyCode,
          KeyCombination.SHORTCUT_DOWN));
    return menuItem;
  }

  private void openFile ()
  {
    System.out.println ("Open");
  }

  private void pageSetup ()
  {
    SwingUtilities.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        PrinterJob printerJob = PrinterJob.getPrinterJob ();// AWT

        PageFormat pageFormat = printerJob.defaultPage ();
        printerJob.pageDialog (pageFormat);
      }
    });
  }

  private void printFile ()
  {
    SwingUtilities.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        PrinterJob printerJob = PrinterJob.getPrinterJob ();// AWT

        if (printerJob.printDialog ())
        {
          printerJob.setPrintable (currentReport);
          try
          {
            printerJob.print ();
          }
          catch (PrinterException e)
          {
            e.printStackTrace ();
          }
        }
      }
    });
  }

  private void saveFile ()
  {
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

    hexReport.setRecords (records);
    hexReport.setNewlineBetweenRecords (true);
    hexReport.setAllowSplitRecords (true);

    textReport.setRecords (records);
    natloadReport.setRecords (records);

    asaReport.setRecords (records);
    asaReport.setAllowSplitRecords (true);

    spaceReport ();
    paginate ();
  }

  private void paginate ()
  {
    RadioButton btn = (RadioButton) encodingGroup.getSelectedToggle ();
    EncodingType encodingType = (EncodingType) btn.getUserData ();

    btn = (RadioButton) formattingGroup.getSelectedToggle ();
    FormatType formatType = (FormatType) btn.getUserData ();

    TextMaker textMaker =
        encodingType == EncodingType.EBCDIC ? ebcdicTextMaker : asciiTextMaker;

    hexReport.setTextMaker (textMaker);
    textReport.setTextMaker (textMaker);
    natloadReport.setTextMaker (textMaker);
    asaReport.setTextMaker (textMaker);

    switch (formatType)
    {
      case HEX:
        currentReport = hexReport;
        break;
      case TEXT:
        currentReport = textReport;
        break;
      case NATLOAD:
        currentReport = natloadReport;
        break;
      case ASA:
        currentReport = asaReport;
        break;
    }

    borderPane.setCenter (currentReport.getPagination ());
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