package com.bytezone.reporter.application;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import com.bytezone.reporter.record.Record;
import com.bytezone.reporter.reports.ReportMaker;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Reporter extends Application implements FileSelectionListener
{
  private final static String OS = System.getProperty ("os.name");
  private final static boolean SYSTEM_MENUBAR = OS != null && OS.startsWith ("Mac");

  //  private List<RecordMaker> recordMakers;
  //  private List<TextMaker> textMakers;
  //  private List<ReportMaker> reportMakers;

  private final FormatBox formatBox = new FormatBox ();
  private final BorderPane borderPane = new BorderPane ();
  private final MenuBar menuBar = new MenuBar ();

  private WindowSaver windowSaver;
  private Preferences prefs;

  private List<Record> records;
  private ReportData reportData;

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    prefs = Preferences.userNodeForPackage (this.getClass ());

    EventHandler<ActionEvent> rebuild = e -> createRecords ();

    TreePanel treePanel = new TreePanel (prefs);
    treePanel.addFileSelectionListener (this);
    StackPane stackPane = new StackPane ();
    stackPane.setPrefWidth (200);
    stackPane.getChildren ().add (treePanel.getTree ());
    borderPane.setLeft (stackPane);

    VBox formatVBox = formatBox.getFormattingBox ();
    formatVBox.setPrefWidth (180);
    borderPane.setRight (formatVBox);

    menuBar.getMenus ().addAll (getFileMenu ());

    borderPane.setTop (menuBar);
    if (SYSTEM_MENUBAR)
      menuBar.useSystemMenuBarProperty ().set (true);

    Scene scene = new Scene (borderPane, 800, 592);
    primaryStage.setTitle ("Reporter");
    primaryStage.setScene (scene);
    primaryStage.setOnCloseRequest (e -> closeWindow ());

    windowSaver = new WindowSaver (prefs, primaryStage, "Reporter");
    if (!windowSaver.restoreWindow ())
      primaryStage.centerOnScreen ();

    primaryStage.show ();
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
    System.out.println (records.size ());

    //    recordMakers = reportData.getRecordMakers ();
    //    textMakers = reportData.getTextMakers ();
    List<ReportMaker> reportMakers = reportData.getReportMakers ();

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
    try
    {
      byte[] buffer = Files.readAllBytes (file.toPath ());
      reportData = new ReportData (buffer);

      formatBox.setData (reportData, e -> createRecords ());
      formatBox.test (buffer);
      createRecords ();
    }
    catch (IOException e)
    {
      e.printStackTrace ();
    }
  }
}