package com.bytezone.reporter.application;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import com.bytezone.reporter.record.FbRecordMaker;
import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.record.RecordMaker;
import com.bytezone.reporter.reports.ReportMaker;
import com.bytezone.reporter.tests.RecordTester;
import com.bytezone.reporter.tests.Score;
import com.bytezone.reporter.text.TextMaker;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Reporter extends Application implements FileSelectionListener
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
    int choice = 12;

    Path currentPath = Paths.get (home + files[choice]);
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

    borderPane.setRight (formatBox.getFormattingBox (rebuild));

    recordMakers = formatBox.getRecordMakers ();
    textMakers = formatBox.getTextMakers ();
    reportMakers = formatBox.getReportMakers ();

    for (RecordMaker recordMaker : recordMakers)
      recordMaker.setBuffer (buffer);

    test (buffer);
    createRecords ();

    TreePanel treePanel = new TreePanel (prefs);
    treePanel.addFileSelectionListener (this);
    borderPane.setLeft (treePanel.getTree ());

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

  private void test (byte[] buffer)
  {
    List<RecordTester> testers = new ArrayList<> ();
    for (RecordMaker recordMaker : recordMakers)
      if (recordMaker instanceof FbRecordMaker)
      {
        int length = ((FbRecordMaker) recordMaker).getRecordLength ();
        testers.add (new RecordTester (recordMaker, buffer, 10 * length));
      }
      else
        testers.add (new RecordTester (recordMaker, buffer, 1024));

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

    formatBox.process (scores);
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
          try
          {
            printerJob.setPrintable (formatBox.getSelectedReportMaker ());
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

    TextMaker textMaker = formatBox.getSelectedTextMaker ();
    for (ReportMaker reportMaker : reportMakers)
      reportMaker.setTextMaker (textMaker);

    borderPane.setCenter (formatBox.getSelectedReportMaker ().getPagination ());
  }

  private void closeWindow ()
  {
    windowSaver.saveWindow ();
  }

  public static void main (String[] args)
  {
    launch (args);
  }

  @Override
  public void fileSelected (File file)
  {
    System.out.println (file);
    try
    {
      byte[] buffer = Files.readAllBytes (file.toPath ());
      for (RecordMaker recordMaker : recordMakers)
        recordMaker.setBuffer (buffer);

      test (buffer);
      createRecords ();
    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
  }
}