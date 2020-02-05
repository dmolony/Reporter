package com.bytezone.reporter.application;

import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import com.bytezone.reporter.file.ReportData;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

// -----------------------------------------------------------------------------------//
public class ReporterNode extends BorderPane
    implements PaginationChangeListener, NodeSelectionListener
// -----------------------------------------------------------------------------------//
{
  private static final String PREFS_SAVE_LOCATION = "SaveLocation";
  private final Set<NodeSelectionListener> nodeSelectionListeners = new HashSet<> ();
  private final FormatBox formatBox;
  private final TreePanel treePanel;
  private final MenuBar menuBar = new MenuBar ();
  private final Preferences prefs;
  private File lastSaveLocation;
  private FileNode currentFileNode;

  // ---------------------------------------------------------------------------------//
  public ReporterNode (Preferences prefs)
  // ---------------------------------------------------------------------------------//
  {
    this.prefs = prefs;
    String saveLocation = prefs.get (PREFS_SAVE_LOCATION, "");
    if (!saveLocation.isEmpty ())
      lastSaveLocation = new File (saveLocation);

    formatBox = new FormatBox (this);
    Path path = Paths.get (System.getProperty ("user.home"), "dm3270", "files");

    treePanel = new TreePanel (prefs);
    treePanel.addNodeSelectionListener (this);

    StackPane stackPane = new StackPane ();
    stackPane.getChildren ().add (treePanel.getTree (path));

    stackPane.setPrefWidth (280);
    setLeft (stackPane);
    setRight (formatBox.getPanel ());

    menuBar.getMenus ().addAll (getFileMenu ());
  }

  // ---------------------------------------------------------------------------------//
  public MenuBar getMenuBar ()
  // ---------------------------------------------------------------------------------//
  {
    return menuBar;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void requestFocus ()
  // ---------------------------------------------------------------------------------//
  {
    treePanel.getTree ().requestFocus ();
  }

  // ---------------------------------------------------------------------------------//
  public void addBuffer (String name, byte[] buffer)
  // ---------------------------------------------------------------------------------//
  {
    treePanel.addBuffer (name, buffer);
  }
  //
  //  public void addBuffer (String name, byte[] buffer, String folderName)
  //  {
  //    treePanel.addBuffer (name, buffer, folderName);
  //  }

  // ---------------------------------------------------------------------------------//
  public void addFile (File file, String siteFolderName)
  // ---------------------------------------------------------------------------------//
  {
    treePanel.addFile (file, siteFolderName);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void nodeSelected (FileNode fileNode)
  // ---------------------------------------------------------------------------------//
  {
    currentFileNode = fileNode;

    ReportData reportData = fileNode.getReportData ();
    if (!reportData.hasData ())
      reportData.fillBuffer (fileNode.getFile ());
    if (!reportData.hasScores ())
      reportData.createScores ();
    formatBox.setReportData (reportData);

    fireNodeSelected (fileNode);
  }

  // ---------------------------------------------------------------------------------//
  public FileNode getSelectedNode ()
  // ---------------------------------------------------------------------------------//
  {
    return currentFileNode;
  }

  // ---------------------------------------------------------------------------------//
  private Menu getFileMenu ()
  // ---------------------------------------------------------------------------------//
  {
    Menu menuFile = new Menu ("File");

    getMenuItem (menuFile, "Open...", e -> openFile (), KeyCode.O);
    getMenuItem (menuFile, "Save...", e -> saveFile (), KeyCode.S);

    menuFile.getItems ().add (new SeparatorMenuItem ());

    getMenuItem (menuFile, "Page setup", e -> pageSetup (), null);
    getMenuItem (menuFile, "Print", e -> printFile (), KeyCode.P);

    return menuFile;
  }

  // ---------------------------------------------------------------------------------//
  private MenuItem getMenuItem (Menu menu, String text,
      EventHandler<ActionEvent> eventHandler, KeyCode keyCode)
  // ---------------------------------------------------------------------------------//
  {
    MenuItem menuItem = new MenuItem (text);

    menuItem.setOnAction (eventHandler);
    if (keyCode != null)
      menuItem.setAccelerator (
          new KeyCodeCombination (keyCode, KeyCombination.SHORTCUT_DOWN));
    menu.getItems ().add (menuItem);

    return menuItem;
  }

  // ---------------------------------------------------------------------------------//
  private void openFile ()
  // ---------------------------------------------------------------------------------//
  {
    System.out.println ("Open not written yet");
  }

  // ---------------------------------------------------------------------------------//
  private void pageSetup ()
  // ---------------------------------------------------------------------------------//
  {
    SwingUtilities.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        PrinterJob printerJob = PrinterJob.getPrinterJob ();        // AWT
        PageFormat pageFormat = printerJob.defaultPage ();
        printerJob.pageDialog (pageFormat);
      }
    });
  }

  // ---------------------------------------------------------------------------------//
  private void printFile ()
  // ---------------------------------------------------------------------------------//
  {
    SwingUtilities.invokeLater (new Runnable ()
    {
      @Override
      public void run ()
      {
        PrinterJob printerJob = PrinterJob.getPrinterJob ();        // AWT!!!

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
      }
    });
  }

  // ---------------------------------------------------------------------------------//
  private void saveFile ()
  // ---------------------------------------------------------------------------------//
  {
    FileChooser fileChooser = new FileChooser ();
    fileChooser.setInitialFileName (currentFileNode.getDatasetName ());
    if (lastSaveLocation != null)
      fileChooser.setInitialDirectory (lastSaveLocation);

    File file = fileChooser.showSaveDialog (null);

    if (file != null && !file.isDirectory ())
      try
      {
        ReportData reportData = currentFileNode.getReportData ();
        Files.write (file.toPath (), reportData.getBuffer ());
        lastSaveLocation = fileChooser.getInitialDirectory ();
        lastSaveLocation = file.getParentFile ();
        prefs.put (PREFS_SAVE_LOCATION, lastSaveLocation.getAbsolutePath ());
      }
      catch (IOException e)
      {
        e.printStackTrace ();
      }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public void paginationChanged (Pagination pagination)
  // ---------------------------------------------------------------------------------//
  {
    pagination.setPrefWidth (18000);            // need this to make it expand
    setCenter (pagination);
  }

  // ---------------------------------------------------------------------------------//
  private void fireNodeSelected (FileNode fileNode)
  // ---------------------------------------------------------------------------------//
  {
    nodeSelectionListeners.forEach (l -> l.nodeSelected (fileNode));
  }

  // ---------------------------------------------------------------------------------//
  public void addNodeSelectionListener (NodeSelectionListener listener)
  // ---------------------------------------------------------------------------------//
  {
    if (!nodeSelectionListeners.contains (listener))
      nodeSelectionListeners.add (listener);
  }

  // ---------------------------------------------------------------------------------//
  public void removeNodeSelectionListener (NodeSelectionListener listener)
  // ---------------------------------------------------------------------------------//
  {
    if (nodeSelectionListeners.contains (listener))
      nodeSelectionListeners.remove (listener);
  }
}