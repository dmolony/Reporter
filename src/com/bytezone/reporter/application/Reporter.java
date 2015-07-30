package com.bytezone.reporter.application;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Reporter extends Application
//    implements PaginationChangeListener, FileSelectionListener
{
  //  private final static String OS = System.getProperty ("os.name");
  //  private final static boolean MAC_MENUBAR = OS != null && OS.startsWith ("Mac");
  //
  //  private FormatBox formatBox;
  //  private ReportData reportData;
  //
  private final BorderPane borderPane = new BorderPane ();
  //  private final MenuBar menuBar = new MenuBar ();
  //
  private WindowSaver windowSaver;
  private final Preferences prefs = Preferences.userNodeForPackage (this.getClass ());

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    String home = System.getProperty ("user.home") + "/Dropbox/testfiles";

    //    Scene scene = getScene (home, this);
    Scene scene = new ReporterScene (borderPane);
    primaryStage.setTitle ("Reporter");
    primaryStage.setScene (scene);
    primaryStage.setOnCloseRequest (e -> closeWindow ());

    windowSaver = new WindowSaver (prefs, primaryStage, "Reporter");
    if (!windowSaver.restoreWindow ())
      primaryStage.centerOnScreen ();

    primaryStage.show ();
  }

  //  public Scene getScene (String home, FileSelectionListener listener)
  //  {
  //    TreePanel treePanel = new TreePanel (prefs);
  //    treePanel.addFileSelectionListener (listener);
  //    StackPane stackPane = new StackPane ();
  //    stackPane.setPrefWidth (180);
  //
  //    TreeView<FileNode> tree = treePanel.getTree (home);
  //    stackPane.getChildren ().add (tree);
  //
  //    borderPane.setLeft (stackPane);
  //    borderPane.setTop (menuBar);
  //
  //    menuBar.getMenus ().addAll (getFileMenu ());
  //
  //    if (MAC_MENUBAR)
  //      menuBar.useSystemMenuBarProperty ().set (true);
  //
  //    Scene scene = new Scene (borderPane, 800, 592);
  //    tree.requestFocus ();
  //
  //    return scene;
  //  }

  //  @Override
  //  public void fileSelected (FileNode fileNode)
  //  {
  //    formatBox = fileNode.formatBox;
  //    borderPane.setRight (formatBox.getFormattingBox ());
  //    formatBox.setFileNode (fileNode, this);
  //  }

  //  private Menu getFileMenu ()
  //  {
  //    Menu menuFile = new Menu ("File");
  //
  //    MenuItem menuItemOpen = getMenuItem ("Open...", e -> openFile (), KeyCode.O);
  //    MenuItem menuItemSave = getMenuItem ("Save...", e -> saveFile (), KeyCode.S);
  //    MenuItem menuItemPrint = getMenuItem ("Page setup", e -> pageSetup (), null);
  //    MenuItem menuItemPageSetup = getMenuItem ("Print", e -> printFile (), KeyCode.P);
  //    MenuItem menuItemClose = getMenuItem ("Close window", e -> closeWindow (), KeyCode.W);
  //
  //    menuFile.getItems ().addAll (menuItemOpen, menuItemSave, menuItemPageSetup,
  //                                 menuItemPrint, menuItemClose);
  //    return menuFile;
  //  }

  //  private MenuItem getMenuItem (String text, EventHandler<ActionEvent> eventHandler,
  //      KeyCode keyCode)
  //  {
  //    MenuItem menuItem = new MenuItem (text);
  //    menuItem.setOnAction (eventHandler);
  //    if (keyCode != null)
  //      menuItem.setAccelerator (new KeyCodeCombination (keyCode,
  //          KeyCombination.SHORTCUT_DOWN));
  //    return menuItem;
  //  }
  //
  //  private void openFile ()
  //  {
  //    System.out.println ("Open");
  //  }
  //
  //  private void pageSetup ()
  //  {
  //    SwingUtilities.invokeLater (new Runnable ()
  //    {
  //      @Override
  //      public void run ()
  //      {
  //        PrinterJob printerJob = PrinterJob.getPrinterJob ();// AWT
  //        PageFormat pageFormat = printerJob.defaultPage ();
  //        printerJob.pageDialog (pageFormat);
  //      }
  //    });
  //  }
  //
  //  private void printFile ()
  //  {
  //    SwingUtilities.invokeLater (new Runnable ()
  //    {
  //      @Override
  //      public void run ()
  //      {
  //        PrinterJob printerJob = PrinterJob.getPrinterJob ();// AWT
  //
  //        if (printerJob.printDialog ())
  //        {
  //          try
  //          {
  //            printerJob.setPrintable (formatBox.getSelectedReportMaker ());
  //            printerJob.print ();
  //          }
  //          catch (PrinterException e)
  //          {
  //            e.printStackTrace ();
  //          }
  //        }
  //      }
  //    });
  //  }
  //
  //  private void saveFile ()
  //  {
  //  }
  //
  private void closeWindow ()
  {
    windowSaver.saveWindow ();
  }

  public static void main (String[] args)
  {
    launch (args);
  }

  //  @Override
  //  public void paginationChanged (Pagination pagination)
  //  {
  //    //    System.out.println (pagination);
  //    borderPane.setCenter (pagination);
  //  }
}