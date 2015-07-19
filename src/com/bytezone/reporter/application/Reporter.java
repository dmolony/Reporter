package com.bytezone.reporter.application;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Reporter extends Application
{
  private final static String OS = System.getProperty ("os.name");
  private final static boolean SYSTEM_MENUBAR = OS != null && OS.startsWith ("Mac");

  //  private static final String[] fontNames =
  //      { "Andale Mono", "Anonymous Pro", "Consolas", "Courier New", "DejaVu Sans Mono",
  //        "Hermit", "IBM 3270", "IBM 3270 Narrow", "Inconsolata", "Input Mono",
  //        "Input Mono Narrow", "Luculent", "Menlo", "Monaco", "M+ 1m", "Panic Sans",
  //        "PT Mono", "Source Code Pro", "Ubuntu Mono", "Monospaced" };

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

  private List<RecordMaker> recordMakers;
  private List<TextMaker> textMakers;
  private List<ReportMaker> reportMakers;

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

  private final TextMaker asciiTextMaker = new AsciiTextMaker ();
  private final TextMaker ebcdicTextMaker = new EbcdicTextMaker ();

  private final ReportMaker hexReport = new HexReport ();
  private final ReportMaker textReport = new TextReport ();
  private final ReportMaker natloadReport = new NatloadReport ();
  private final ReportMaker asaReport = new AsaReport ();

  private final FormatBox formatBox = new FormatBox ();
  private final BorderPane borderPane = new BorderPane ();
  private final MenuBar menuBar = new MenuBar ();

  private WindowSaver windowSaver;
  private Preferences prefs;

  private List<Record> records;

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

    hexReport.setNewlineBetweenRecords (true);
    hexReport.setAllowSplitRecords (true);
    asaReport.setAllowSplitRecords (true);

    recordMakers = new ArrayList<> (
        Arrays.asList (none, crlf, cr, lf, vb, rdw, nvb, ravel, fb80, fb132, fb252));
    textMakers = new ArrayList<> (Arrays.asList (asciiTextMaker, ebcdicTextMaker));
    reportMakers =
        new ArrayList<> (Arrays.asList (hexReport, textReport, asaReport, natloadReport));

    borderPane.setRight (formatBox.getFormattingBox (rebuild, paginate, recordMakers,
                                                     textMakers, reportMakers));

    for (RecordMaker recordMaker : recordMakers)
      recordMaker.setBuffer (buffer);

    selectButtons (buffer, fileLength);
    createRecords ();

    //    TreePanel treePanel = new TreePanel ();
    //    treePanel.initialise ();

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

    Collections.sort (scores);
    Collections.reverse (scores);
    for (Score score : scores)
      System.out.println (score);

    formatBox.select (scores.get (0));
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
          RadioButton btn = (RadioButton) formatBox.reportsGroup.getSelectedToggle ();
          ReportMaker reportMaker = (ReportMaker) btn.getUserData ();
          printerJob.setPrintable (reportMaker);
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

  private void createRecords ()
  {
    records = formatBox.getSelectedRecordMaker ().getRecords ();

    for (ReportMaker reportMaker : reportMakers)
      reportMaker.setRecords (records);

    spaceReport ();
    paginate ();
  }

  private void paginate ()
  {
    TextMaker textMaker = formatBox.getSelectedTextMaker ();
    for (ReportMaker reportMaker : reportMakers)
      reportMaker.setTextMaker (textMaker);

    ReportMaker reportMaker = formatBox.getSelectedReportMaker ();
    borderPane.setCenter (reportMaker.getPagination ());
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